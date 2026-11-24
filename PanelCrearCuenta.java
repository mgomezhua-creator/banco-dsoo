/*
Laboratorio 2: DSOO(Lab F)
Docente: EDWAR ABRIL SAIRE PERALTA
Integrantes:
-MELANY YARITZA GOMEZ HUAMAN
-KAROL ESTEFANY MAMANI CUBA

Panel para crear una cuenta (Ahorro o Corriente) con saldo inicial a un cliente registrado,
listar las cuentas del banco y cerrar una cuenta (solo si su saldo es 0).
*/

import java.awt.*;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PanelCrearCuenta extends JPanel {

    private Banco banco;   // instancia compartida con los demas paneles

    private JTextField txtDniCliente, txtNumCuenta, txtSaldoInicial;
    private JComboBox<String> cmbTipo;
    private JButton btnCrear, btnCerrar;
    private DefaultTableModel modelo;
    private JTable tabla;

    public PanelCrearCuenta(Banco banco) {
        this.banco = banco;

        txtDniCliente = new JTextField(20);
        txtNumCuenta = new JTextField(20);
        txtSaldoInicial = new JTextField(20);
        cmbTipo = new JComboBox<>(new String[]{"Ahorro", "Corriente"});
        btnCrear = new JButton("Crear cuenta");
        btnCerrar = new JButton("Cerrar cuenta seleccionada");

        // Formulario 4 filas x 2 columnas
        JPanel formulario = new JPanel(new GridLayout(4, 2, 8, 6));
        formulario.add(new JLabel("DNI del cliente:"));
        formulario.add(txtDniCliente);
        formulario.add(new JLabel("Numero de cuenta:"));
        formulario.add(txtNumCuenta);
        formulario.add(new JLabel("Tipo de cuenta:"));
        formulario.add(cmbTipo);
        formulario.add(new JLabel("Saldo inicial (S/):"));
        formulario.add(txtSaldoInicial);

        JPanel centroFormulario = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centroFormulario.add(formulario);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        botones.add(btnCrear);
        botones.add(btnCerrar);

        JPanel norte = new JPanel(new BorderLayout(0, 6));
        norte.add(centroFormulario, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);

        modelo = new DefaultTableModel(
                new String[]{"N° cuenta", "Tipo", "Titular", "DNI titular", "Saldo (S/.)", "Movimientos"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Eventos
        btnCrear.addActionListener(e -> crear());
        btnCerrar.addActionListener(e -> cerrar());

        actualizar();
    }

    /** Vuelve a leer las cuentas del Banco y refresca la tabla (listar). */
    public void actualizar() {
        modelo.setRowCount(0);
        for (Cuenta c : banco.getCuentas()) {
            modelo.addRow(new Object[]{c.getNumeroCuenta(), c.getTipo(), c.getCliente().getNombre(),
                    c.getCliente().getDni(), String.format(Locale.US, "%.2f", c.getSaldo()),
                    c.getMovimientos().size()});
        }
    }

    /** Valida los datos y crea la cuenta para el cliente indicado. */
    private void crear() {
        String dni = txtDniCliente.getText().trim();
        String num = txtNumCuenta.getText().trim();
        String tipo = (String) cmbTipo.getSelectedItem();
        String saldoTxt = txtSaldoInicial.getText().trim();

        // Validacion: campos obligatorios
        if (dni.isEmpty() || num.isEmpty() || saldoTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.");
            return;
        }

        // Validacion: el saldo debe ser un numero no negativo
        double saldo;
        try {
            saldo = Double.parseDouble(saldoTxt);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El saldo inicial debe ser un numero valido.");
            return;
        }
        if (!(saldo >= 0) || Double.isInfinite(saldo)) {
            JOptionPane.showMessageDialog(this, "El saldo inicial no puede ser negativo.");
            return;
        }

        try {
            // Banco valida que el cliente exista y que el numero de cuenta no se repita
            banco.crearCuenta(num, tipo, saldo, dni);
            JOptionPane.showMessageDialog(this, "Cuenta creada correctamente.");
            txtDniCliente.setText("");
            txtNumCuenta.setText("");
            txtSaldoInicial.setText("");
            cmbTipo.setSelectedIndex(0);
            actualizar();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void cerrar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una cuenta de la tabla.");
            return;
        }
        String num = (String) modelo.getValueAt(fila, 0);
        int resp = JOptionPane.showConfirmDialog(this, "¿Cerrar la cuenta " + num + "?",
                "Confirmar cierre", JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) return;
        try {
            banco.cerrarCuenta(num);
            JOptionPane.showMessageDialog(this, "Cuenta cerrada.");
            actualizar();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
