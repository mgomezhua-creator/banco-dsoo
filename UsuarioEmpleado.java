/*
Subclase UsuarioEmpleado: representa al personal operativo del banco con permisos
para gestionar clientes, cuentas, transacciones y búsquedas.
*/
public class UsuarioEmpleado extends Usuario {

    public UsuarioEmpleado(String nombreUsuario, String contrasena, boolean estado) {
        super(nombreUsuario, contrasena, estado);
    }

    @Override
    public String mostrarPermisos() {
        return "ROL: EMPLEADO (" + nombreUsuario + ")\n"
             + "- Permitido: Registrar/editar clientes, crear cuentas, atender depósitos/retiros/transferencias y buscar.\n"
             + "- Restringido: Registrar/eliminar empleados y administrar usuarios del sistema.";
    }
}