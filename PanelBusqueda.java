/*
Laboratorio 2: DSOO(Lab F)
Docente: EDWAR ABRIL SAIRE PERALTA
Integrantes:
-MELANY YARITZA GOMEZ HUAMAN
-KAROL ESTEFANY MAMANI CUBA

Panel para buscar un cliente (por DNI), una cuenta (por numero) o un empleado (por codigo)
usando los metodos de busqueda del Banco, que recorren listas enlazadas (LinkedList / ArrayList).
*/

import java.awt.*;
import javax.swing.*;

public class PanelBusqueda extends JPanel {

    private Banco banco;   // instancia compartida con los demas paneles

    private JComboBox<String> cmbCriterio;
    private JTextField txtBusqueda;
    private JButton btnBuscar;
    private JTextArea areaResultado;

    public PanelBusqueda(Banco banco) {
        this.banco = banco;

        cmbCriterio = new JComboBox<>(new String[]{"Cliente (por DNI)", "Cuenta (por numero)", "Empleado (por codigo)"});
        txtBusqueda = new JTextField(15);
        btnBuscar = new JButton("Buscar");

        // Fila superior: FlowLayout (por defecto) mantiene el tamano natural de cada componente
        JPanel filtros = new JPanel();
        filtros.add(cmbCriterio);
        filtros.add(txtBusqueda);
        filtros.add(btnBuscar);

        // Zona de resultados (solo lectura) con salto de linea y barra de desplazamiento
        areaResultado = new JTextArea(12, 40);
        areaResultado.setEditable(false);
        areaResultado.setLineWrap(true);        // salta de linea al llegar al borde
        areaResultado.setWrapStyleWord(true);   // corta por palabras, no por letras

        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(filtros, BorderLayout.NORTH);
        add(new JScrollPane(areaResultado), BorderLayout.CENTER);

        // Evento del boton
        btnBuscar.addActionListener(e -> buscar());
    }

    /** Busca segun el criterio elegido y muestra el resultado. */
    private void buscar() {
        String clave = txtBusqueda.getText().trim();
        if (clave.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un valor a buscar.");
            return;
        }

        switch (cmbCriterio.getSelectedIndex()) {
            case 0:
                buscarCliente(clave);
                break;
            case 1:
                buscarCuenta(clave);
                break;
            default:
                buscarEmpleado(clave);
                break;
        }
    }

    // Busqueda de cliente por DNI (recorre la lista enlazada de clientes)
    private void buscarCliente(String clave) {
        Cliente c = banco.buscarCliente(clave);
        if (c == null) {
            areaResultado.setText("No se encontro ningun cliente con DNI " + clave + ".");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("CLIENTE ENCONTRADO\n");
        sb.append("Codigo   : ").append(c.getCodigo()).append("\n");
        sb.append("Nombre   : ").append(c.getNombre()).append("\n");
        sb.append("DNI      : ").append(c.getDni()).append("\n");
        sb.append("Direccion: ").append(c.getDireccion()).append("\n");
        sb.append("Telefono : ").append(c.getTelefono()).append("\n");
        sb.append("\nCuentas (").append(c.getCuentas().size()).append("):\n");
        for (Cuenta cu : c.getCuentas()) {
            sb.append("  - ").append(cu).append("\n");
        }
        areaResultado.setText(sb.toString());
    }

    // Busqueda de cuenta por numero (recorre la lista enlazada de cuentas)
    private void buscarCuenta(String clave) {
        Cuenta cu = banco.buscarCuenta(clave);
        if (cu == null) {
            areaResultado.setText("No se encontro ninguna cuenta con numero " + clave + ".");
        } else {
            areaResultado.setText(cu.toString());
        }
    }

    // Busqueda de empleado por codigo (recorre la lista de empleados)
    private void buscarEmpleado(String clave) {
        Empleado e = banco.buscarEmpleado(clave);
        if (e == null) {
            areaResultado.setText("No se encontro ningun empleado con codigo " + clave + ".");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("EMPLEADO ENCONTRADO\n");
        sb.append("Codigo   : ").append(e.getCodigo()).append("\n");
        sb.append("Rol      : ").append(e.getRol()).append("\n");
        sb.append("Nombre   : ").append(e.getNombre()).append("\n");
        sb.append("DNI      : ").append(e.getDni()).append("\n");
        sb.append("Direccion: ").append(e.getDireccion()).append("\n");
        sb.append("Telefono : ").append(e.getTelefono()).append("\n");
        sb.append("\nTransacciones gestionadas: ").append(e.getTransacciones().size()).append("\n");
        for (Transaccion t : e.getTransacciones()) {
            sb.append("  - ").append(t).append("\n");
        }
        areaResultado.setText(sb.toString());
    }
}
