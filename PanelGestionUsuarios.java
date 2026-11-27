/*
Laboratorio 3: DSOO(Lab F)
Docente: EDWAR ABRIL SAIRE PERALTA
Integrantes:
-MELANY YARITZA GOMEZ HUAMAN
-KAROL ESTEFANY MAMANI CUBA

Panel de gestion de usuarios del sistema (EXCLUSIVO del Administrador):
registrar usuarios (Cliente, Empleado o Administrador), activar/desactivar,
eliminar y listar.

Seguridad en dos capas:
 1) VentanaPrincipal solo habilita esta pestana para un UsuarioAdministrador.
 2) Cada accion de este panel vuelve a comprobar el rol antes de ejecutarse
    (validacion de operaciones), por si alguien llegara aqui sin permiso.
Ademas protege la integridad: no se puede desactivar ni eliminar al usuario que
tiene la sesion abierta, ni dejar al sistema sin ningun administrador activo.
*/

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Supplier;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PanelGestionUsuarios extends JPanel {

    private Banco banco;                       // instancia compartida con los demas paneles
    private Supplier<Usuario> usuarioActual;   // quien tiene la sesion abierta (lo da VentanaPrincipal)

    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JComboBox<String> cmbTipo;
    private JCheckBox chkActivo;
    private JButton btnRegistrar, btnCambiarEstado, btnEliminar;
    private DefaultTableModel modelo;
    private JTable tabla;

    public PanelGestionUsuarios(Banco banco, Supplier<Usuario> usuarioActual) {
        this.banco = banco;
        this.usuarioActual = usuarioActual;

        txtUsuario = new JTextField(18);
        txtContrasena = new JPasswordField(18);
        cmbTipo = new JComboBox<>(new String[]{"Cliente", "Empleado", "Administrador"});
        chkActivo = new JCheckBox("Activo", true);
        btnRegistrar = new JButton("Registrar usuario");
        btnCambiarEstado = new JButton("Activar / Desactivar seleccionado");
        btnEliminar = new JButton("Eliminar seleccionado");

        // Formulario 4 filas x 2 columnas
        JPanel formulario = new JPanel(new GridLayout(4, 2, 8, 6));
        formulario.add(new JLabel("Nombre de usuario:"));
        formulario.add(txtUsuario);
        formulario.add(new JLabel("Contraseña:"));
        formulario.add(txtContrasena);
        formulario.add(new JLabel("Tipo de usuario:"));
        formulario.add(cmbTipo);
        formulario.add(new JLabel("Estado inicial:"));
        formulario.add(chkActivo);

        JPanel centroFormulario = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centroFormulario.add(formulario);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        botones.add(btnRegistrar);
        botones.add(btnCambiarEstado);
        botones.add(btnEliminar);

        JPanel norte = new JPanel(new BorderLayout(0, 6));
        norte.add(centroFormulario, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);

        // Tabla con la lista completa de usuarios (nunca se muestra la contrasena)
        modelo = new DefaultTableModel(new String[]{"Usuario", "Tipo", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) actualizarEstadoControles();
        });

        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Eventos
        btnRegistrar.addActionListener(e -> registrar());
        btnCambiarEstado.addActionListener(e -> cambiarEstado());
        btnEliminar.addActionListener(e -> eliminar());

        actualizar();
    }

    /** Vuelve a leer los usuarios del Banco y refresca la tabla (listar). */
    public void actualizar() {
        modelo.setRowCount(0);
        // copia defensiva: la tabla nunca modifica la lista interna del Banco
        for (Usuario u : new ArrayList<>(banco.getUsuarios())) {
            modelo.addRow(new Object[]{u.getNombreUsuario(), tipoDe(u), u.isEstado() ? "Activo" : "Inactivo"});
        }
        actualizarEstadoControles();
    }

    private void actualizarEstadoControles() {
        boolean haySeleccion = tabla.getSelectedRow() >= 0;
        btnCambiarEstado.setEnabled(haySeleccion);
        btnEliminar.setEnabled(haySeleccion);
    }

    //  Acciones (todas verifican primero que quien actua sea Administrador)
  

    private void registrar() {
        if (!esAdministrador()) return;

        String nombre = txtUsuario.getText().trim();
        String pass = new String(txtContrasena.getPassword());

        // Validaciones de entrada
        if (nombre.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete usuario y contraseña.");
            return;
        }
        if (nombre.contains(" ")) {
            JOptionPane.showMessageDialog(this, "El nombre de usuario no puede tener espacios.");
            return;
        }
        if (pass.length() < 4) {
            JOptionPane.showMessageDialog(this, "La contraseña debe tener al menos 4 caracteres.");
            return;
        }

        // Se crea el tipo de usuario elegido; el resto del sistema lo trata como Usuario
        boolean activo = chkActivo.isSelected();
        Usuario nuevo;
        switch (cmbTipo.getSelectedIndex()) {
            case 0:
                nuevo = new UsuarioCliente(nombre, pass, activo);
                break;
            case 1:
                nuevo = new UsuarioEmpleado(nombre, pass, activo);
                break;
            default:
                nuevo = new UsuarioAdministrador(nombre, pass, activo);
                break;
        }

        if (!banco.agregarUsuario(nuevo)) {
            JOptionPane.showMessageDialog(this, "Ya existe un usuario con ese nombre.");
            return;
        }
        JOptionPane.showMessageDialog(this, "Usuario registrado correctamente.");
        txtUsuario.setText("");
        txtContrasena.setText("");
        cmbTipo.setSelectedIndex(0);
        chkActivo.setSelected(true);
        actualizar();
    }

    private void cambiarEstado() {
        if (!esAdministrador()) return;
        Usuario u = usuarioSeleccionado();
        if (u == null) return;

        // Si se va a DESACTIVAR, se protegen la sesion actual y al ultimo administrador
        if (u.isEstado() && !puedeQuedarInactivoOEliminado(u, "desactivar")) return;

        u.setEstado(!u.isEstado());
        JOptionPane.showMessageDialog(this, "El usuario " + u.getNombreUsuario() + " ahora está "
                + (u.isEstado() ? "ACTIVO." : "INACTIVO."));
        actualizar();
        seleccionarUsuario(u.getNombreUsuario());   // se mantiene la fila elegida para poder revertir
    }

    private void eliminar() {
        if (!esAdministrador()) return;
        Usuario u = usuarioSeleccionado();
        if (u == null) return;

        if (u.isEstado() && !puedeQuedarInactivoOEliminado(u, "eliminar")) return;
        // (un administrador inactivo si se puede eliminar: ya no cuenta como administrador activo)
        if (esElUsuarioActual(u)) {
            JOptionPane.showMessageDialog(this, "No puede eliminar al usuario con el que inició sesión.");
            return;
        }

        int resp = JOptionPane.showConfirmDialog(this,
                "¿Eliminar al usuario " + u.getNombreUsuario() + " (" + tipoDe(u) + ")?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) return;

        if (banco.eliminarUsuario(u.getNombreUsuario())) {
            JOptionPane.showMessageDialog(this, "Usuario eliminado.");
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar el usuario.");
        }
        actualizar();
    }

    
    //  Auxiliares
    

    /** Control de acceso: solo un UsuarioAdministrador con sesion abierta puede continuar. */
    private boolean esAdministrador() {
        if (!(usuarioActual.get() instanceof UsuarioAdministrador)) {
            JOptionPane.showMessageDialog(this,
                    "Acceso denegado: solo el Administrador puede gestionar usuarios.");
            return false;
        }
        return true;
    }

    private Usuario usuarioSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla.");
            return null;
        }
        Usuario u = banco.buscarUsuario((String) modelo.getValueAt(fila, 0));
        if (u == null) {
            JOptionPane.showMessageDialog(this, "El usuario ya no existe.");
            actualizar();
        }
        return u;
    }

    /** Vuelve a marcar en la tabla la fila del usuario indicado (si sigue existiendo). */
    private void seleccionarUsuario(String nombre) {
        for (int i = 0; i < modelo.getRowCount(); i++) {
            if (nombre.equals(modelo.getValueAt(i, 0))) {
                tabla.setRowSelectionInterval(i, i);
                return;
            }
        }
    }

    private boolean esElUsuarioActual(Usuario u) {
        Usuario actual = usuarioActual.get();
        return actual != null && actual.getNombreUsuario().equals(u.getNombreUsuario());
    }

    /**
     * Comprueba que desactivar/eliminar a "u" no deje al sistema inutilizable.
     * Muestra el motivo y devuelve false si no se debe permitir.
     */
    private boolean puedeQuedarInactivoOEliminado(Usuario u, String accion) {
        if (esElUsuarioActual(u)) {
            JOptionPane.showMessageDialog(this,
                    "No puede " + accion + " al usuario con el que inició sesión.");
            return false;
        }
        if (u instanceof UsuarioAdministrador && contarAdministradoresActivos() <= 1) {
            JOptionPane.showMessageDialog(this,
                    "No puede " + accion + " al único administrador activo del sistema.");
            return false;
        }
        return true;
    }

    private int contarAdministradoresActivos() {
        int cantidad = 0;
        for (Usuario u : new ArrayList<>(banco.getUsuarios())) {
            if (u instanceof UsuarioAdministrador && u.isEstado()) cantidad++;
        }
        return cantidad;
    }

    /** Nombre legible del tipo real del usuario. */
    private static String tipoDe(Usuario u) {
        if (u instanceof UsuarioAdministrador) return "Administrador";
        if (u instanceof UsuarioEmpleado) return "Empleado";
        if (u instanceof UsuarioCliente) return "Cliente";
        return "Usuario";
    }
}