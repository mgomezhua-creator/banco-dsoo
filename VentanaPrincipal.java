import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/*
Laboratorio 2: DSOO(Lab F)
Docente: EDWAR ABRIL SAIRE PERALTA
Integrantes:
-MELANY YARITZA GOMEZ HUAMAN
-KAROL ESTEFANY MAMANI CUBA
*/
public class VentanaPrincipal extends JFrame {
    private Banco banco;

    private PanelRegistroCliente panelClientes;
    private PanelRegistroEmpleado panelEmpleados;
    private PanelCrearCuenta panelCuentas;

    public VentanaPrincipal() {
        banco = new Banco();
        setTitle("Sistema Bancario - POO Swing");
        setSize(960, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        panelClientes = new PanelRegistroCliente(banco);
        panelEmpleados = new PanelRegistroEmpleado(banco);
        panelCuentas = new PanelCrearCuenta(banco);

        JTabbedPane pestanas = new JTabbedPane();

        // --- Gestion de personas y cuentas ---
        pestanas.addTab("Clientes", panelClientes);
        pestanas.addTab("Empleados", panelEmpleados);
        pestanas.addTab("Cuentas", panelCuentas);

        // --- Operaciones del empleado (ventanilla) ---
        pestanas.addTab("Depósitos y Retiros", crearPanelTransacciones());
        pestanas.addTab("Gestión de Transacciones", crearPanelGestionTransacciones());

        // --- Operaciones del cliente y consultas ---
        pestanas.addTab("Operaciones del Cliente", crearPanelOperacionesCliente());
        pestanas.addTab("Saldo y Movimientos", crearPanelConsultas());
        pestanas.addTab("Buscar", new PanelBusqueda(banco));

        // Al entrar a una pestaña con tabla, se vuelve a leer el Banco para mostrar datos al día
        pestanas.addChangeListener(e -> {
            Component sel = pestanas.getSelectedComponent();
            if (sel == panelClientes) panelClientes.actualizar();
            else if (sel == panelEmpleados) panelEmpleados.actualizar();
            else if (sel == panelCuentas) panelCuentas.actualizar();
        });

        add(pestanas);
    }

    // =====================================================================
    //  Auxiliares
    // =====================================================================

    /** Ejecuta una operacion del Banco y muestra el error (si lo hay) con el Consumer indicado. */
    private void ejecutar(Consumer<String> mostrarError, Runnable operacion) {
        try {
            operacion.run();
        } catch (NumberFormatException ex) {          // debe ir antes: es subclase de IllegalArgumentException
            mostrarError.accept("Error: monto inválido.");
        } catch (IllegalArgumentException ex) {
            mostrarError.accept("Error: " + ex.getMessage());
        }
    }

    private static String dinero(double valor) {
        return String.format(Locale.US, "%.2f", valor);
    }

    // =====================================================================
    //  Pestaña: Depósitos y Retiros (el empleado atiende en ventanilla)
    // =====================================================================
    private JPanel crearPanelTransacciones() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formulario = new JPanel(new GridLayout(7, 2, 8, 8));

        JTextField txtIdTrans = new JTextField();
        JTextField txtNumCuenta = new JTextField();
        JTextField txtMonto = new JTextField();
        JTextField txtFecha = new JTextField();
        JTextField txtHora = new JTextField();
        JTextField txtCodEmp = new JTextField();

        JButton btnDepositar = new JButton("Realizar Depósito");
        JButton btnRetirar = new JButton("Realizar Retiro");
        JLabel lblEstado = new JLabel("Estado: Esperando operación...");

        formulario.add(new JLabel("ID Transacción (vacío = automático):")); formulario.add(txtIdTrans);
        formulario.add(new JLabel("Número de Cuenta:")); formulario.add(txtNumCuenta);
        formulario.add(new JLabel("Monto (S/.):")); formulario.add(txtMonto);
        formulario.add(new JLabel("Fecha dd/MM/aaaa (vacío = hoy):")); formulario.add(txtFecha);
        formulario.add(new JLabel("Hora HH:mm (vacío = ahora):")); formulario.add(txtHora);
        formulario.add(new JLabel("Código Empleado:")); formulario.add(txtCodEmp);
        formulario.add(btnDepositar); formulario.add(btnRetirar);

        panel.add(formulario, BorderLayout.CENTER);
        panel.add(lblEstado, BorderLayout.SOUTH);

        btnDepositar.addActionListener(e -> ejecutar(lblEstado::setText, () -> {
            if (txtCodEmp.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Ingrese el código del empleado que atiende.");
            }
            double monto = Double.parseDouble(txtMonto.getText().trim());
            banco.realizarDeposito(txtIdTrans.getText(), txtNumCuenta.getText(), monto,
                    txtFecha.getText(), txtHora.getText(), txtCodEmp.getText());
            lblEstado.setText("Depósito registrado con éxito. " + banco.consultarSaldo(txtNumCuenta.getText()).replace("\n", " | "));
        }));

        btnRetirar.addActionListener(e -> ejecutar(lblEstado::setText, () -> {
            if (txtCodEmp.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Ingrese el código del empleado que atiende.");
            }
            double monto = Double.parseDouble(txtMonto.getText().trim());
            banco.realizarRetiro(txtIdTrans.getText(), txtNumCuenta.getText(), monto,
                    txtFecha.getText(), txtHora.getText(), txtCodEmp.getText());
            lblEstado.setText("Retiro exitoso. " + banco.consultarSaldo(txtNumCuenta.getText()).replace("\n", " | "));
        }));

        return panel;
    }

    // =====================================================================
    //  Pestaña: Gestión de Transacciones (listar, modificar y eliminar)
    // =====================================================================
    private JPanel crearPanelGestionTransacciones() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Filtros para listar
        JTextField txtCuenta = new JTextField(10);
        JTextField txtEmp = new JTextField(8);
        JComboBox<String> cmbTipo = new JComboBox<>(new String[]{Transaccion.RETIRO, Transaccion.DEPOSITO, "Todos"});
        JButton btnListar = new JButton("Listar");

        JPanel filaFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filaFiltros.add(new JLabel("N° Cuenta:"));
        filaFiltros.add(txtCuenta);
        filaFiltros.add(new JLabel("Cód. Empleado:"));
        filaFiltros.add(txtEmp);
        filaFiltros.add(new JLabel("Tipo:"));
        filaFiltros.add(cmbTipo);
        filaFiltros.add(btnListar);

        // Datos a modificar
        JTextField txtMonto = new JTextField(8);
        JTextField txtFecha = new JTextField(9);
        JTextField txtHora = new JTextField(6);
        JButton btnModificar = new JButton("Modificar seleccionada");
        JButton btnEliminar = new JButton("Eliminar seleccionada");

        JPanel filaEdicion = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filaEdicion.add(new JLabel("Nuevo monto:"));
        filaEdicion.add(txtMonto);
        filaEdicion.add(new JLabel("Fecha:"));
        filaEdicion.add(txtFecha);
        filaEdicion.add(new JLabel("Hora:"));
        filaEdicion.add(txtHora);

        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filaBotones.add(btnModificar);
        filaBotones.add(btnEliminar);

        JPanel norte = new JPanel(new GridLayout(3, 1));
        norte.add(filaFiltros);
        norte.add(filaEdicion);
        norte.add(filaBotones);

        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"ID", "Cuenta", "Tipo", "Monto (S/.)", "Fecha", "Hora", "Empleado"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        JTable tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JLabel lblEstado = new JLabel("Use el listado para elegir una transacción. (Para agregar un retiro use la pestaña Depósitos y Retiros)");

        panel.add(norte, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(lblEstado, BorderLayout.SOUTH);

        // Al elegir una fila, sus datos pasan a los campos de edicion
        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0) {
                txtMonto.setText((String) modelo.getValueAt(fila, 3));
                txtFecha.setText((String) modelo.getValueAt(fila, 4));
                txtHora.setText((String) modelo.getValueAt(fila, 5));
            }
        });

        // Listar con los filtros indicados
        Runnable listar = () -> {
            modelo.setRowCount(0);
            String tipo = "Todos".equals(cmbTipo.getSelectedItem()) ? null : (String) cmbTipo.getSelectedItem();
            ejecutar(lblEstado::setText, () -> {
                List<Transaccion> lista = banco.listarTransacciones(txtCuenta.getText(), txtEmp.getText(), tipo);
                for (Transaccion t : lista) {
                    String emp = (t.getEmpleado() == null) ? "Sistema"
                            : t.getEmpleado().getCodigoEmpleado() + " - " + t.getEmpleado().getNombre();
                    modelo.addRow(new Object[]{t.getIdTransaccion(), banco.numeroCuentaDe(t), t.getTipo(),
                            dinero(t.getMonto()), t.getFecha(), t.getHora(), emp});
                }
                lblEstado.setText(lista.size() + " transacción(es) encontrada(s).");
            });
        };

        btnListar.addActionListener(e -> listar.run());

        btnModificar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                lblEstado.setText("Error: seleccione una transacción de la tabla.");
                return;
            }
            String id = (String) modelo.getValueAt(fila, 0);
            ejecutar(lblEstado::setText, () -> {
                double monto = Double.parseDouble(txtMonto.getText().trim());
                banco.modificarTransaccion(id, monto, txtFecha.getText(), txtHora.getText());
                listar.run();
                lblEstado.setText("Transacción " + id + " modificada. El saldo de la cuenta fue ajustado.");
            });
        });

        btnEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                lblEstado.setText("Error: seleccione una transacción de la tabla.");
                return;
            }
            String id = (String) modelo.getValueAt(fila, 0);
            int resp = JOptionPane.showConfirmDialog(panel,
                    "¿Eliminar la transacción " + id + "? Se deshará su efecto en el saldo.",
                    "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            if (resp != JOptionPane.YES_OPTION) return;
            ejecutar(lblEstado::setText, () -> {
                banco.eliminarTransaccion(id);
                listar.run();
                lblEstado.setText("Transacción " + id + " eliminada. El saldo de la cuenta fue ajustado.");
            });
        });

        return panel;
    }

    // =====================================================================
    //  Pestaña: Operaciones del Cliente (autoservicio: sin empleado)
    // =====================================================================
    private JPanel crearPanelOperacionesCliente() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField txtDni = new JTextField(10);
        JButton btnCargar = new JButton("Cargar mis cuentas");
        JComboBox<String> cmbCuentas = new JComboBox<>();
        cmbCuentas.setPrototypeDisplayValue("0000000000000000");

        JTextField txtMonto = new JTextField(8);
        JButton btnDepositar = new JButton("Depositar");
        JButton btnRetirar = new JButton("Retirar");

        JTextField txtDestino = new JTextField(12);
        JButton btnTransferir = new JButton("Transferir");

        JButton btnSaldo = new JButton("Consultar saldo");
        JButton btnMovs = new JButton("Ver movimientos");

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        fila1.add(new JLabel("DNI del cliente:"));
        fila1.add(txtDni);
        fila1.add(btnCargar);
        fila1.add(new JLabel("Cuenta:"));
        fila1.add(cmbCuentas);

        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        fila2.add(new JLabel("Monto (S/.):"));
        fila2.add(txtMonto);
        fila2.add(btnDepositar);
        fila2.add(btnRetirar);

        JPanel fila3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        fila3.add(new JLabel("Transferir el monto a la cuenta:"));
        fila3.add(txtDestino);
        fila3.add(btnTransferir);

        JPanel fila4 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        fila4.add(btnSaldo);
        fila4.add(btnMovs);

        JPanel norte = new JPanel(new GridLayout(4, 1));
        norte.add(fila1);
        norte.add(fila2);
        norte.add(fila3);
        norte.add(fila4);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        panel.add(norte, BorderLayout.NORTH);
        panel.add(new JScrollPane(area), BorderLayout.CENTER);

        // Devuelve la cuenta elegida o avisa que hay que cargarlas primero
        java.util.function.Supplier<String> cuentaElegida = () -> {
            Object sel = cmbCuentas.getSelectedItem();
            if (sel == null) {
                throw new IllegalArgumentException("Primero cargue las cuentas del cliente con su DNI.");
            }
            return (String) sel;
        };

        btnCargar.addActionListener(e -> ejecutar(area::setText, () -> {
            Cliente c = banco.buscarCliente(txtDni.getText());
            if (c == null) {
                throw new IllegalArgumentException("No existe un cliente con ese DNI.");
            }
            cmbCuentas.removeAllItems();
            for (Cuenta cu : c.getCuentas()) {
                cmbCuentas.addItem(cu.getNumeroCuenta());
            }
            area.setText(c.getCuentas().isEmpty()
                    ? "Bienvenido/a " + c.getNombre() + ". Aún no tiene cuentas (créelas en la pestaña Cuentas)."
                    : "Bienvenido/a " + c.getNombre() + ". Elija una cuenta y realice su operación.");
        }));

        btnDepositar.addActionListener(e -> ejecutar(area::setText, () -> {
            String cuenta = cuentaElegida.get();
            banco.realizarDeposito("", cuenta, Double.parseDouble(txtMonto.getText().trim()), "", "", "");
            area.setText("Depósito realizado.\n\n" + banco.consultarSaldo(cuenta));
        }));

        btnRetirar.addActionListener(e -> ejecutar(area::setText, () -> {
            String cuenta = cuentaElegida.get();
            banco.realizarRetiro("", cuenta, Double.parseDouble(txtMonto.getText().trim()), "", "", "");
            area.setText("Retiro realizado.\n\n" + banco.consultarSaldo(cuenta));
        }));

        btnTransferir.addActionListener(e -> ejecutar(area::setText, () -> {
            String cuenta = cuentaElegida.get();
            banco.transferir(cuenta, txtDestino.getText(), Double.parseDouble(txtMonto.getText().trim()), "", "", "");
            area.setText("Transferencia realizada a la cuenta " + txtDestino.getText().trim() + ".\n\n"
                    + banco.consultarSaldo(cuenta));
        }));

        btnSaldo.addActionListener(e -> ejecutar(area::setText, () -> area.setText(banco.consultarSaldo(cuentaElegida.get()))));
        btnMovs.addActionListener(e -> ejecutar(area::setText, () -> area.setText(banco.mostrarMovimientos(cuentaElegida.get()))));

        return panel;
    }

    // =====================================================================
    //  Pestaña: Saldo y Movimientos (incluye filtro desde una fecha)
    // =====================================================================
    private JPanel crearPanelConsultas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel superior = new JPanel(new GridLayout(2, 1));
        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));

        JTextField txtCuenta = new JTextField(12);
        JButton btnSaldo = new JButton("Consultar Saldo");
        JButton btnMovs = new JButton("Ver Movimientos");
        JTextField txtDesde = new JTextField(10);
        JButton btnDesde = new JButton("Movimientos desde fecha");

        fila1.add(new JLabel("N° Cuenta:"));
        fila1.add(txtCuenta);
        fila1.add(btnSaldo);
        fila1.add(btnMovs);
        fila2.add(new JLabel("Desde (dd/MM/aaaa):"));
        fila2.add(txtDesde);
        fila2.add(btnDesde);
        superior.add(fila1);
        superior.add(fila2);

        JTextArea areaResultado = new JTextArea();
        areaResultado.setEditable(false);
        areaResultado.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        btnSaldo.addActionListener(e -> areaResultado.setText(banco.consultarSaldo(txtCuenta.getText())));
        btnMovs.addActionListener(e -> areaResultado.setText(banco.mostrarMovimientos(txtCuenta.getText())));
        btnDesde.addActionListener(e -> ejecutar(areaResultado::setText,
                () -> areaResultado.setText(banco.mostrarMovimientosDesde(txtCuenta.getText(), txtDesde.getText()))));

        panel.add(superior, BorderLayout.NORTH);
        panel.add(new JScrollPane(areaResultado), BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
