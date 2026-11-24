/*
Laboratorio 2: DSOO(Lab F)
Docente: EDWAR ABRIL SAIRE PERALTA
Integrantes:
-MELANY YARITZA GOMEZ HUAMAN
-KAROL ESTEFANY MAMANI CUBA

Clase hija de Persona. Un cliente puede tener una o varias cuentas bancarias.
*/
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cliente extends Persona {

    private final String codigoCliente;
    private final ArrayList<Cuenta> cuentas;

    public Cliente(String codigoCliente, String nombre, String dni, String direccion, String telefono) {
        super(nombre, dni, direccion, telefono);
        if (codigoCliente == null || codigoCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de cliente no puede estar vacío.");
        }
        this.codigoCliente = codigoCliente.trim();
        this.cuentas = new ArrayList<>();
    }

    public void agregarCuenta(Cuenta c) {
        if (c != null && !cuentas.contains(c)) {
            cuentas.add(c);
        }
    }

    public void eliminarCuenta(Cuenta c) {
        cuentas.remove(c);
    }

    /** Vista de solo lectura: la lista interna no se puede modificar desde fuera. */
    public List<Cuenta> getCuentas() {
        return Collections.unmodifiableList(cuentas);
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    // ---- Polimorfismo: redefine los métodos abstractos de Persona ----

    @Override
    public String getRol() {
        return "Cliente";
    }

    @Override
    public String getCodigo() {
        return codigoCliente;
    }

    @Override
    public String toString() {
        return "Cliente [" + codigoCliente + "] " + super.toString() + " | Cuentas: " + cuentas;
    }
}
