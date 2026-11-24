/*
Laboratorio 2: DSOO(Lab F)
Docente: EDWAR ABRIL SAIRE PERALTA
Integrantes:
-MELANY YARITZA GOMEZ HUAMAN
-KAROL ESTEFANY MAMANI CUBA
*/
public class Retiro extends Transaccion {

    public Retiro(String idTransaccion, double monto, String fecha, String hora, Empleado empleado) {
        super(idTransaccion, monto, fecha, hora, Transaccion.RETIRO, empleado);
    }

    @Override
    public boolean procesar(Cuenta cuenta) {
        if (cuenta != null && getMonto() > 0) {
            // Valida si hay saldo suficiente dentro de Cuenta
            if (cuenta.retirar(getMonto())) {
                cuenta.registrarMovimiento(this);
                return true;
            }
        }
        return false;
    }

    @Override
    public double efectoEnSaldo() {
        return -getMonto();     // un retiro resta
    }
}
