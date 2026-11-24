/*
Laboratorio 2: DSOO(Lab F)
Docente: EDWAR ABRIL SAIRE PERALTA
Integrantes:
-MELANY YARITZA GOMEZ HUAMAN
-KAROL ESTEFANY MAMANI CUBA

Representa una cuenta bancaria. Guarda el saldo y el historial de movimientos en una pila (Stack).
 - Encapsulamiento: todos los atributos son privados; el saldo SOLO cambia mediante
                    depositar / retirar / modificarMonto / eliminarMovimiento.
 - Abstraccion:     el resto del sistema usa la cuenta sin saber como guarda el historial.
*/
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

public class Cuenta {

    private final String numeroCuenta;
    private final String tipo;                      // "Ahorro" o "Corriente"
    private double saldo;
    private final Cliente cliente;                  // titular de la cuenta
    private final Stack<Transaccion> movimientos;   // historial - pila

    public Cuenta(String numeroCuenta, String tipo, double saldoInicial, Cliente cliente) {
        if (numeroCuenta == null || numeroCuenta.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de cuenta no puede estar vacío.");
        }
        if (cliente == null) {
            throw new IllegalArgumentException("La cuenta necesita un titular.");
        }
        if (!(saldoInicial >= 0) || Double.isInfinite(saldoInicial)) {
            throw new IllegalArgumentException("El saldo inicial debe ser un número no negativo.");
        }
        this.numeroCuenta = numeroCuenta.trim();
        this.tipo = tipo;
        this.saldo = saldoInicial;
        this.cliente = cliente;
        this.movimientos = new Stack<>();
    }

    // suma el monto al saldo (solo si el monto es positivo)
    public void depositar(double monto) {
        if (monto > 0) {
            saldo += monto;
        }
    }

    // retira el dinero si el saldo alcanza; devuelve true si se pudo
    public boolean retirar(double monto) {
        if (monto > 0 && saldo >= monto) {
            saldo -= monto;
            return true;
        }
        return false;
    }

    // guarda una transaccion en el historial (push: agrega arriba de la pila)
    public void registrarMovimiento(Transaccion t) {
        movimientos.push(t);
    }

    /**
     * Cambia el monto de un movimiento YA registrado y corrige el saldo
     * (usa el efecto polimorfico de cada tipo de transaccion).
     * Devuelve false (sin cambios) si el nuevo saldo quedaria negativo.
     */
    public boolean modificarMonto(Transaccion t, double nuevoMonto) {
        if (t == null || !movimientos.contains(t) || !(nuevoMonto > 0) || Double.isInfinite(nuevoMonto)) {
            return false;
        }
        double montoAnterior = t.getMonto();
        double nuevoSaldo = saldo - t.efectoEnSaldo();   // deshace el efecto anterior
        t.setMonto(nuevoMonto);
        nuevoSaldo += t.efectoEnSaldo();                 // aplica el efecto nuevo
        if (nuevoSaldo < -1e-9) {
            t.setMonto(montoAnterior);                   // se revierte
            return false;
        }
        saldo = Math.max(nuevoSaldo, 0);
        return true;
    }

    /**
     * Elimina un movimiento del historial y deshace su efecto en el saldo.
     * Devuelve false (sin cambios) si el saldo quedaria negativo.
     */
    public boolean eliminarMovimiento(Transaccion t) {
        if (t == null || !movimientos.contains(t)) {
            return false;
        }
        double nuevoSaldo = saldo - t.efectoEnSaldo();
        if (nuevoSaldo < -1e-9) {
            return false;
        }
        saldo = Math.max(nuevoSaldo, 0);
        movimientos.remove(t);
        return true;
    }

    /** Vista de solo lectura del historial (el ultimo elemento es el mas reciente). */
    public List<Transaccion> getMovimientos() {
        return Collections.unmodifiableList(movimientos);
    }

    /** Movimiento mas reciente (peek de la pila) o null si no hay. */
    public Transaccion ultimoMovimiento() {
        return movimientos.isEmpty() ? null : movimientos.peek();
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public String getTipo() {
        return tipo;
    }

    public double getSaldo() {
        return saldo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    @Override
    public String toString() {
        return "Cuenta [numeroCuenta=" + numeroCuenta + ", tipo=" + tipo
                + ", saldo=" + String.format(Locale.US, "%.2f", saldo)
                + ", titular=" + cliente.getNombre() + ", movimientos=" + movimientos.size() + "]";
    }
}
