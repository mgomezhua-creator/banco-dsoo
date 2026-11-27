/*
Subclase UsuarioAdministrador: posee acceso completo al sistema bancario,
incluyendo la gestión de usuarios y empleados.
*/
public class UsuarioAdministrador extends Usuario {

    public UsuarioAdministrador(String nombreUsuario, String contrasena, boolean estado) {
        super(nombreUsuario, contrasena, estado);
    }

    @Override
    public String mostrarPermisos() {
        return "ROL: ADMINISTRADOR (" + nombreUsuario + ")\n"
             + "- Permitido: Acceso total al sistema (gestionar usuarios, activar/desactivar cuentas de acceso, "
             + "registrar/eliminar empleados, clientes, cuentas y supervisar transacciones).";
    }
}