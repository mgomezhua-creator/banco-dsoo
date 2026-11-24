/*
Laboratorio 2: DSOO(Lab F)
Docente: EDWAR ABRIL SAIRE PERALTA
Integrantes:
-MELANY YARITZA GOMEZ HUAMAN
-KAROL ESTEFANY MAMANI CUBA

Panel de gestion de clientes: registrar, editar, eliminar y listar.
Toda la logica se delega en Banco; este panel solo captura datos y muestra resultados.
*/

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PanelRegistroCliente extends JPanel {

    private Banco banco;   // instancia compartida con los demas paneles

    private JTextField txtCodigo, txtNombre, txtDni, txtDireccion, txtTelefono;
    private JButton btnRegistrar, btnActualizar, btnEliminar, btnLimpiar;
    private DefaultTableModel modelo;
    private JTable tabla;

    public PanelRegistroCliente(Banco banco) {
        this.banco = banco;

        txtCodigo = new JTextField(20);
        txtNombre = new JTextField(20);
        txtDni = new JTextField(20);
        txtDireccion = new JTextField(20);
        txtTelefono = new JTextField(20);
        btnRegistrar = new JButton("Registrar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        // Formulario 5 filas x 2 columnas
        JPanel formulario = new JPanel(new GridLayout(5, 2, 8, 6));
        formulario.add(new JLabel("Codigo de cliente:"));
        formulario.add(txtCodigo);
        formulario.add(new JLabel("Nombre completo:"));
        formulario.add(txtNombre);
        formulario.add(new JLabel("DNI:"));
        formulario.add(txtDni);
        formulario.add(new JLabel("Direccion:"));
        formulario.add(txtDireccion);
        formulario.add(new JLabel("Telefono:"));
        formulario.add(txtTelefono);

        JPanel centroFormulario = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centroFormulario.add(formulario);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        botones.add(btnRegistrar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        JPanel norte = new JPanel(new BorderLayout(0, 6));
        norte.add(centroFormulario, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);

        // Tabla (listado de clientes)
        modelo = new DefaultTableModel(
                new String[]{"Codigo", "Nombre", "DNI", "Direccion", "Telefono", "N° cuentas"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarSeleccion();
        });

        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Eventos
        btnRegistrar.addActionListener(e -> registrar());
        btnActualizar.addActionListener(e -> actualizarCliente());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        actualizar();
    }

    /** Vuelve a leer los clientes del Banco y refresca la tabla (listar). */
    public void actualizar() {
        modelo.setRowCount(0);
        for (Cliente c : banco.getClientes()) {
            modelo.addRow(new Object[]{c.getCodigoCliente(), c.getNombre(), c.getDni(),
                    c.getDireccion(), c.getTelefono(), c.getCuentas().size()});
        }
        actualizarEstadoControles();
    }

    /** Copia la fila seleccionada al formulario para poder editarla. */
    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila >= 0) {
            txtCodigo.setText((String) modelo.getValueAt(fila, 0));
            txtNombre.setText((String) modelo.getValueAt(fila, 1));
            txtDni.setText((String) modelo.getValueAt(fila, 2));
            txtDireccion.setText((String) modelo.getValueAt(fila, 3));
            txtTelefono.setText((String) modelo.getValueAt(fila, 4));
        }
        actualizarEstadoControles();
    }

    private void actualizarEstadoControles() {
        boolean haySeleccion = tabla.getSelectedRow() >= 0;
        btnActualizar.setEnabled(haySeleccion);
        btnEliminar.setEnabled(haySeleccion);
        btnRegistrar.setEnabled(!haySeleccion);
        txtCodigo.setEditable(!haySeleccion);   // el codigo identifica al cliente y no se edita
    }

    /** Valida los datos del formulario. Devuelve false (y avisa) si hay algun problema. */
    private boolean validarFormulario(boolean validarCodigo) {
        if ((validarCodigo && txtCodigo.getText().trim().isEmpty()) || txtNombre.getText().trim().isEmpty()
                || txtDni.getText().trim().isEmpty() || txtDireccion.getText().trim().isEmpty()
                || txtTelefono.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.");
            return false;
        }
        if (!txtDni.getText().trim().matches("\\d{8}")) {
            JOptionPane.showMessageDialog(this, "El DNI debe tener 8 digitos numericos.");
            return false;
        }
        return true;
    }

    private void registrar() {
        if (!validarFormulario(true)) return;
        try {
            banco.registrarCliente(txtCodigo.getText(), txtNombre.getText(), txtDni.getText(),
                    txtDireccion.getText(), txtTelefono.getText());
            JOptionPane.showMessageDialog(this, "Cliente registrado correctamente.");
            limpiar();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void actualizarCliente() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || !validarFormulario(false)) return;
        String dniOriginal = (String) modelo.getValueAt(fila, 2);
        try {
            banco.editarCliente(dniOriginal, txtNombre.getText(), txtDni.getText(),
                    txtDireccion.getText(), txtTelefono.getText());
            JOptionPane.showMessageDialog(this, "Cliente actualizado correctamente.");
            limpiar();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        String dni = (String) modelo.getValueAt(fila, 2);
        String nombre = (String) modelo.getValueAt(fila, 1);
        int resp = JOptionPane.showConfirmDialog(this,
                "¿Eliminar al cliente " + nombre + " (DNI " + dni + ") y sus cuentas?",
                "Confirmar eliminacion", JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) return;
        try {
            banco.eliminarCliente(dni);
            JOptionPane.showMessageDialog(this, "Cliente eliminado.");
            limpiar();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void limpiar() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtDni.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
        tabla.clearSelection();
        actualizar();
    }
}
