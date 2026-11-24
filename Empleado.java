/*
Laboratorio 2: DSOO(Lab F)
Docente: EDWAR ABRIL SAIRE PERALTA
Integrantes:
-MELANY YARITZA GOMEZ HUAMAN
-KAROL ESTEFANY MAMANI CUBA

Clase hija de Persona. Un empleado gestiona (atiende) varias transacciones.
*/
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Empleado extends Persona {

    private final String codigoEmpleado;
    private String cargo;
    private final ArrayList<Transaccion> transacciones;   // transacciones que ha gestionado

    public Empleado(String codigoEmpleado, String cargo, String nombre, String dni, String direccion, String telefono) {
        super(nombre, dni, direccion, telefono);
        if (codigoEmpleado == null || codigoEmpleado.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de empleado no puede estar vacío.");
        }
        this.codigoEmpleado = codigoEmpleado.trim();
        setCargo(cargo);
        this.transacciones = new ArrayList<>();
    }

    public void agregarTransaccion(Transaccion t) {
        if (t != null && !transacciones.contains(t)) {
            transacciones.add(t);
        }
    }

    public void quitarTransaccion(Transaccion t) {
        transacciones.remove(t);
    }

    /** Vista de solo lectura de las transacciones gestionadas. */
    public List<Transaccion> getTransacciones() {
        return Collections.unmodifiableList(transacciones);
    }

    public String getCodigoEmpleado() {
        return codigoEmpleado;
    }

    public String getCargo() {
        return cargo;
    }

    public final void setCargo(String cargo) {
        if (cargo == null || cargo.trim().isEmpty()) {
            throw new IllegalArgumentException("El cargo no puede estar vacío.");
        }
        this.cargo = cargo.trim();
    }

    // ---- Polimorfismo: redefine los métodos abstractos de Persona ----

    @Override
    public String getRol() {
        return "Empleado (" + cargo + ")";
    }

    @Override
    public String getCodigo() {
        return codigoEmpleado;
    }

    @Override
    public String toString() {
        return "Empleado [codigoEmpleado=" + codigoEmpleado + ", cargo=" + cargo + "] " + super.toString();
    }
}
