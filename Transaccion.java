/*
Laboratorio 2: DSOO(Lab F)
Docente: EDWAR ABRIL SAIRE PERALTA
Integrantes:
-MELANY YARITZA GOMEZ HUAMAN
-KAROL ESTEFANY MAMANI CUBA

Clase base ABSTRACTA de Deposito y Retiro.
 - Polimorfismo: procesar() y efectoEnSaldo() son abstractos; cada hija los redefine.
 - Encapsulamiento: atributos privados con getters.
*/
import java.util.Locale;

public abstract class Transaccion {

    public static final String DEPOSITO = "Depósito";
    public static final String RETIRO = "Retiro";

    private final String idTransaccion;
    private double monto;
    private String fecha;
    private String hora;
    private final String tipo;
    private final Empleado empleado;   // puede ser null si lo hizo el propio cliente ("Sistema")

    public Transaccion(String idTransaccion, double monto, String fecha, String hora, String tipo, Empleado empleado) {
        this.idTransaccion = idTransaccion;
        this.monto = monto;
        this.fecha = fecha;
        this.hora = hora;
        this.tipo = tipo;
        this.empleado = empleado;
    }

    // Métodos polimórficos que implementan Deposito y Retiro
    public abstract boolean procesar(Cuenta cuenta);

    /** Efecto de esta transaccion sobre el saldo: +monto (deposito) o -monto (retiro). */
    public abstract double efectoEnSaldo();

    public String getIdTransaccion() { return idTransaccion; }
    public double getMonto() { return monto; }
    public String getFecha() { return fecha; }
    public String getHora() { return hora; }
    public String getTipo() { return tipo; }
    public Empleado getEmpleado() { return empleado; }

    /**
     * Solo debe usarse a traves de Cuenta.modificarMonto(), que tambien corrige el saldo.
     * Es protected para no exponerlo al resto del sistema.
     */
    protected void setMonto(double monto) {
        this.monto = monto;
    }

    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setHora(String hora) { this.hora = hora; }

    @Override
    public String toString() {
        String nomEmp = (empleado != null) ? empleado.getNombre() : "Sistema";
        return "[" + fecha + " " + hora + "] " + tipo.toUpperCase() + " (ID: " + idTransaccion + ") - Monto: S/."
                + String.format(Locale.US, "%.2f", monto) + " | Atendido por: " + nomEmp;
    }
}
