/*
Laboratorio 2: DSOO(Lab F)
Docente: EDWAR ABRIL SAIRE PERALTA
Integrantes:
-MELANY YARITZA GOMEZ HUAMAN
-KAROL ESTEFANY MAMANI CUBA

Clase base ABSTRACTA (no se puede hacer "new Persona(...)").
 - Abstraccion:      define lo comun (nombre, DNI, direccion, telefono) y declara
                     metodos abstractos que cada hija debe implementar.
 - Encapsulamiento:  atributos privados; solo se accede por getters/setters,
                     y los setters validan los datos.
 - Herencia:         Cliente y Empleado extienden esta clase.
*/
public abstract class Persona {

    private String nombre;
    private String dni;
    private String direccion;
    private String telefono;

    public Persona(String nombre, String dni, String direccion, String telefono) {
        this.nombre = validarTexto(nombre, "El nombre");
        this.dni = validarDni(dni);
        this.direccion = validarTexto(direccion, "La dirección");
        this.telefono = validarTexto(telefono, "El teléfono");
    }

    // ---- Métodos abstractos: cada subclase los redefine (polimorfismo) ----

    /** Rol de la persona dentro del banco ("Cliente", "Empleado"). */
    public abstract String getRol();

    /** Código propio de la subclase (codigoCliente / codigoEmpleado). */
    public abstract String getCodigo();

    // ---- Getters y setters (con validación) ----

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = validarTexto(nombre, "El nombre");
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = validarDni(dni);
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = validarTexto(direccion, "La dirección");
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = validarTexto(telefono, "El teléfono");
    }

    // ---- Validaciones privadas (detalle interno oculto) ----

    private static String validarTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío.");
        }
        return valor.trim();
    }

    private static String validarDni(String valor) {
        if (valor == null || !valor.trim().matches("\\d{8}")) {
            throw new IllegalArgumentException("El DNI debe tener 8 dígitos numéricos.");
        }
        return valor.trim();
    }

    @Override
    public String toString() {
        return "Nombre: " + nombre + "--DNI: " + dni + "--Direccion: " + direccion + "--Telefono: " + telefono;
    }
}
