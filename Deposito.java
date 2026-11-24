/*
Laboratorio 2: DSOO(Lab F)
Docente: EDWAR ABRIL SAIRE PERALTA
Integrantes:
-MELANY YARITZA GOMEZ HUAMAN
-KAROL ESTEFANY MAMANI CUBA
*/
public class Deposito extends Transaccion {

    public Deposito(String idTransaccion, double monto, String fecha, String hora, Empleado empleado) {
        super(idTransaccion, monto, fecha, hora, Transaccion.DEPOSITO, empleado);
    }

    @Override
    public boolean procesar(Cuenta cuenta) {
        if (cuenta != null && getMonto() > 0) {
            cuenta.depositar(getMonto());
            cuenta.registrarMovimiento(this);
            return true;
        }
        return false;
    }

    @Override
    public double efectoEnSaldo() {
        return getMonto();      // un deposito suma
    }
}
