/*
Laboratorio 2: DSOO(Lab F)
Docente: EDWAR ABRIL SAIRE PERALTA
Integrantes:
-MELANY YARITZA GOMEZ HUAMAN
-KAROL ESTEFANY MAMANI CUBA

Panel de gestion de empleados: registrar, editar, eliminar y listar.
Toda la logica se delega en Banco.
*/

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PanelRegistroEmpleado extends JPanel {

    private Banco banco;

    private JTextField txtCodigo, txtNombre, txtDni, txtDireccion, txtTelefono;
    private JComboBox<String> cmbCargo;
    private JButton btnRegistrar, btnActualizar, btnEliminar, btnLimpiar;
    private DefaultTableModel modelo;
    private JTable tabla;

    public PanelRegistroEmpleado(Banco banco) {
        this.banco = banco;

        txtCodigo = new JTextField(20);
        txtNombre = new JTextField(20);
        txtDni = new JTextField(20);
        txtDireccion = new JTextField(20);
        txtTelefono = new JTextField(20);
        cmbCargo = new JComboBox<>(new String[]{"Cajero", "Asesor", "Gerente", "Supervisor"});
        btnRegistrar = new JButton("Registrar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        // Formulario 6 filas x 2 columnas
        JPanel formulario = new JPanel(new GridLayout(6, 2, 8, 6));
        formulario.add(new JLabel("Codigo de empleado:"));
        formulario.add(txtCodigo);
        formulario.add(new JLabel("Cargo:"));
        formulario.add(cmbCargo);
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

        modelo = new DefaultTableModel(
                new String[]{"Codigo", "Cargo", "Nombre", "DNI", "Direccion", "Telefono", "N° transacciones"}, 0) {
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

        btnRegistrar.addActionListener(e -> registrar());
        btnActualizar.addActionListener(e -> actualizarEmpleado());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        actualizar();
    }

    /** Vuelve a leer los empleados del Banco y refresca la tabla (listar). */
    public void actualizar() {
        modelo.setRowCount(0);
        for (Empleado emp : banco.getEmpleados()) {
            modelo.addRow(new Object[]{emp.getCodigoEmpleado(), emp.getCargo(), emp.getNombre(), emp.getDni(),
                    emp.getDireccion(), emp.getTelefono(), emp.getTransacciones().size()});
        }
        actualizarEstadoControles();
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila >= 0) {
            txtCodigo.setText((String) modelo.getValueAt(fila, 0));
            cmbCargo.setSelectedItem(modelo.getValueAt(fila, 1));
            txtNombre.setText((String) modelo.getValueAt(fila, 2));
            txtDni.setText((String) modelo.getValueAt(fila, 3));
            txtDireccion.setText((String) modelo.getValueAt(fila, 4));
            txtTelefono.setText((String) modelo.getValueAt(fila, 5));
        }
        actualizarEstadoControles();
    }

    private void actualizarEstadoControles() {
        boolean haySeleccion = tabla.getSelectedRow() >= 0;
        btnActualizar.setEnabled(haySeleccion);
        btnEliminar.setEnabled(haySeleccion);
        btnRegistrar.setEnabled(!haySeleccion);
        txtCodigo.setEditable(!haySeleccion);   // el codigo identifica al empleado y no se edita
    }

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
            banco.registrarEmpleado(txtCodigo.getText(), (String) cmbCargo.getSelectedItem(), txtNombre.getText(),
                    txtDni.getText(), txtDireccion.getText(), txtTelefono.getText());
            JOptionPane.showMessageDialog(this, "Empleado registrado correctamente.");
            limpiar();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void actualizarEmpleado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || !validarFormulario(false)) return;
        String codigo = (String) modelo.getValueAt(fila, 0);
        try {
            banco.editarEmpleado(codigo, (String) cmbCargo.getSelectedItem(), txtNombre.getText(),
                    txtDni.getText(), txtDireccion.getText(), txtTelefono.getText());
            JOptionPane.showMessageDialog(this, "Empleado actualizado correctamente.");
            limpiar();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        String codigo = (String) modelo.getValueAt(fila, 0);
        String nombre = (String) modelo.getValueAt(fila, 2);
        int resp = JOptionPane.showConfirmDialog(this,
                "¿Eliminar al empleado " + nombre + " (" + codigo + ")?\nSus transacciones pasadas se conservan en el historial.",
                "Confirmar eliminacion", JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) return;
        try {
            banco.eliminarEmpleado(codigo);
            JOptionPane.showMessageDialog(this, "Empleado eliminado.");
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
        cmbCargo.setSelectedIndex(0);
        tabla.clearSelection();
        actualizar();
    }
}
