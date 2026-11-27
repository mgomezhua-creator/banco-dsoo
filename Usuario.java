/*
Laboratorio 3: DSOO
Docente: EDWAR ABRIL SAIRE PERALTA
Integrantes:
- MELANY YARITZA GOMEZ HUAMAN
- KAROL ESTEFANY MAMANI CUBA

Superclase abstracta que define los atributos comunes y el contrato de permisos
para los distintos tipos de usuarios del sistema bancario.
*/
public abstract class Usuario {

    protected String nombreUsuario;
    protected String contrasena;
    protected boolean estado; // true = activo, false = inactivo

    public Usuario(String nombreUsuario, String contrasena, boolean estado) {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
        }
        if (contrasena == null || contrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        this.nombreUsuario = nombreUsuario.trim();
        this.contrasena = contrasena;
        this.estado = estado;
    }

    /** Verifica credenciales y comprueba que el usuario se encuentre activo. */
    public boolean login(String user, String pass) {
        if (!this.estado || user == null || pass == null) {
            return false;
        }
        return this.nombreUsuario.equalsIgnoreCase(user.trim()) && this.contrasena.equals(pass);
    }

    /** Devuelve los datos generales del usuario. */
    public String mostrarDatos() {
        String estadoTexto = estado ? "Activo" : "Inactivo";
        return "Usuario: " + nombreUsuario + " | Estado: " + estadoTexto;
    }

    /** Método polimórfico sobrescrito por cada subclase según sus privilegios. */
    public abstract String mostrarPermisos();

    // Getters y Setters
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
        }
        this.nombreUsuario = nombreUsuario.trim();
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        if (contrasena == null || contrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        this.contrasena = contrasena;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return mostrarDatos();
    }
}