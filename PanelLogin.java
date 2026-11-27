/*
Laboratorio 3: DSOO(Lab F)
Docente: EDWAR ABRIL SAIRE PERALTA
Integrantes:
-MELANY YARITZA GOMEZ HUAMAN
-KAROL ESTEFANY MAMANI CUBA

Panel de inicio de sesion. Pide usuario y contrasena, los valida con Banco.autenticar()
y muestra los datos y permisos del usuario que ingreso.

POLIMORFISMO: este panel solo conoce el tipo base Usuario. La misma llamada
u.mostrarPermisos() devuelve un texto distinto segun el objeto real
(UsuarioCliente, UsuarioEmpleado o UsuarioAdministrador).

El panel NO decide que pestanas se habilitan: avisa a VentanaPrincipal mediante
el callback "alCambiarSesion" (usuario al ingresar, null al cerrar sesion).
*/

import java.awt.*;
import java.util.function.Consumer;
import javax.swing.*;

public class PanelLogin extends JPanel {

    private Banco banco;                          // instancia compartida con los demas paneles
    private Consumer<Usuario> alCambiarSesion;    // callback hacia VentanaPrincipal
    private Usuario usuarioActual;                // null = nadie ha iniciado sesion

    private JTextField txtUsuario;
    private JPasswordField txtContrasena;         // oculta lo que se escribe
    private JButton btnIngresar, btnCerrarSesion;
    private JLabel lblEstado;
    private JTextArea areaInfo;

    public PanelLogin(Banco banco, Consumer<Usuario> alCambiarSesion) {
        this.banco = banco;
        this.alCambiarSesion = alCambiarSesion;

        txtUsuario = new JTextField(18);
        txtContrasena = new JPasswordField(18);
        btnIngresar = new JButton("Ingresar");
        btnCerrarSesion = new JButton("Cerrar Sesión");
        lblEstado = new JLabel("Ingrese sus credenciales para usar el sistema.");

        // Formulario 2 filas x 2 columnas
        JPanel formulario = new JPanel(new GridLayout(2, 2, 8, 8));
        formulario.add(new JLabel("Usuario:"));
        formulario.add(txtUsuario);
        formulario.add(new JLabel("Contraseña:"));
        formulario.add(txtContrasena);

        JPanel centroFormulario = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centroFormulario.add(formulario);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        botones.add(btnIngresar);
        botones.add(btnCerrarSesion);

        JPanel norte = new JPanel(new BorderLayout(0, 8));
        norte.add(centroFormulario, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);

        // Zona de datos y permisos del usuario autenticado (solo lectura)
        areaInfo = new JTextArea();
        areaInfo.setEditable(false);
        areaInfo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(areaInfo), BorderLayout.CENTER);
        add(lblEstado, BorderLayout.SOUTH);

        // Eventos (Enter en cualquiera de los dos campos tambien ingresa)
        btnIngresar.addActionListener(e -> ingresar());
        txtUsuario.addActionListener(e -> ingresar());
        txtContrasena.addActionListener(e -> ingresar());
        btnCerrarSesion.addActionListener(e -> cerrarSesion());

        actualizarControles();
    }

    /** Usuario que tiene la sesion abierta (null si no hay). */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /** Valida las credenciales y, si son correctas, abre la sesion. */
    private void ingresar() {
        String user = txtUsuario.getText().trim();
        String pass = new String(txtContrasena.getPassword());
        txtContrasena.setText("");   // la contrasena no se queda escrita en pantalla

        // Validacion: campos obligatorios
        if (user.isEmpty() || pass.isEmpty()) {
            lblEstado.setText("Error: ingrese usuario y contraseña.");
            return;
        }

        // Banco.autenticar() devuelve null si las credenciales no coinciden
        // o si el usuario esta inactivo (login() exige estado == true).
        Usuario u = banco.autenticar(user, pass);
        if (u == null) {
            usuarioActual = null;
            areaInfo.setText("");
            lblEstado.setText("Error: usuario o contraseña incorrectos, o el usuario está inactivo.");
            return;
        }

        usuarioActual = u;
        // POLIMORFISMO: mostrarPermisos() se comporta distinto segun el tipo real de u
        areaInfo.setText("= DATOS DEL USUARIO =\n" + u.mostrarDatos()
                + "\n\n= PERMISOS =\n" + u.mostrarPermisos());
        areaInfo.setCaretPosition(0);
        lblEstado.setText("Sesión iniciada como: " + u.getNombreUsuario());
        actualizarControles();
        alCambiarSesion.accept(u);   // VentanaPrincipal habilita/bloquea las pestanas
    }

    /** Cierra la sesion: limpia el panel y avisa para que se bloqueen las pestanas. */
    private void cerrarSesion() {
        usuarioActual = null;
        txtUsuario.setText("");
        txtContrasena.setText("");
        areaInfo.setText("");
        lblEstado.setText("Sesión cerrada. Ingrese sus credenciales para usar el sistema.");
        actualizarControles();
        alCambiarSesion.accept(null);
    }

    /** Con sesion abierta se bloquea el formulario; sin sesion se bloquea "Cerrar Sesión". */
    private void actualizarControles() {
        boolean haySesion = (usuarioActual != null);
        txtUsuario.setEnabled(!haySesion);
        txtContrasena.setEnabled(!haySesion);
        btnIngresar.setEnabled(!haySesion);
        btnCerrarSesion.setEnabled(haySesion);
    }
}