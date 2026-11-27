/*
Subclase UsuarioCliente: representa a un cliente con permisos básicos
para consultar saldo, depositar, retirar y revisar movimientos.
*/
public class UsuarioCliente extends Usuario {

    public UsuarioCliente(String nombreUsuario, String contrasena, boolean estado) {
        super(nombreUsuario, contrasena, estado);
    }

    @Override
    public String mostrarPermisos() {
        return "ROL: CLIENTE (" + nombreUsuario + ")\n"
             + "- Permitido: Consultar saldo, realizar depósitos, retiros y ver movimientos.\n"
             + "- Restringido: Registrar clientes/empleados, abrir o cerrar cuentas y gestionar usuarios.";
    }
}