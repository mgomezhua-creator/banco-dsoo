import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/*
Laboratorio 2: DSOO(Lab F)
Docente: EDWAR ABRIL SAIRE PERALTA
Integrantes:
-MELANY YARITZA GOMEZ HUAMAN
-KAROL ESTEFANY MAMANI CUBA

Clase principal que administra clientes, empleados y cuentas (Abstraccion).
Toda la logica de negocio esta aqui; la interfaz Swing solo llama a estos metodos.
Cuando una operacion no es valida lanza IllegalArgumentException con el motivo.
*/
public class Banco {

    private static final DateTimeFormatter FMT_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final LinkedList<Cliente> clientes;
    private final ArrayList<Empleado> empleados;
    private final LinkedList<Cuenta> cuentas;
    private int contadorTransacciones;
    private final ArrayList<Usuario> usuarios;

    public Banco() {
        this.clientes = new LinkedList<>();
        this.empleados = new ArrayList<>();
        this.usuarios = new ArrayList<>();
        this.cuentas = new LinkedList<>();
        this.contadorTransacciones = 0;
    }

    // =====================================================================
    //  CLIENTES: registrar, editar, eliminar, listar, buscar
    // =====================================================================

    public void registrarCliente(String cod, String nombre, String dni, String dir, String tel) {
        if (buscarClientePorCodigo(cod) != null) {
            throw new IllegalArgumentException("Ya existe un cliente con ese código.");
        }
        if (buscarCliente(dni) != null) {
            throw new IllegalArgumentException("Ya existe un cliente con ese DNI.");
        }
        clientes.add(new Cliente(cod, nombre, dni, dir, tel));
    }

    /** Edita los datos de un cliente identificado por su DNI actual (el codigo no cambia). */
    public void editarCliente(String dniActual, String nombre, String nuevoDni, String dir, String tel) {
        Cliente c = buscarCliente(dniActual);
        if (c == null) {
            throw new IllegalArgumentException("Cliente no encontrado.");
        }
        if (!texto(dniActual).equals(texto(nuevoDni)) && buscarCliente(nuevoDni) != null) {
            throw new IllegalArgumentException("Ya existe otro cliente con ese DNI.");
        }
        c.setNombre(nombre);
        c.setDni(nuevoDni);
        c.setDireccion(dir);
        c.setTelefono(tel);
    }

    /** Elimina un cliente y sus cuentas. Solo si todas sus cuentas tienen saldo 0. */
    public void eliminarCliente(String dni) {
        Cliente c = buscarCliente(dni);
        if (c == null) {
            throw new IllegalArgumentException("Cliente no encontrado.");
        }
        for (Cuenta cu : c.getCuentas()) {
            if (cu.getSaldo() > 0) {
                throw new IllegalArgumentException("No se puede eliminar: la cuenta " + cu.getNumeroCuenta()
                        + " aún tiene saldo (S/. " + formato(cu.getSaldo()) + "). Retire el dinero primero.");
            }
        }
        cuentas.removeAll(c.getCuentas());
        clientes.remove(c);
    }

    public List<Cliente> getClientes() {
        return Collections.unmodifiableList(clientes);
    }

    // Búsquedas sobre LinkedList
    public Cliente buscarCliente(String dni) {
        String clave = texto(dni);
        for (Cliente c : clientes) {
            if (c.getDni().equals(clave)) return c;
        }
        return null;
    }

    public Cliente buscarClientePorCodigo(String codigo) {
        String clave = texto(codigo);
        for (Cliente c : clientes) {
            if (c.getCodigoCliente().equals(clave)) return c;
        }
        return null;
    }

    // =====================================================================
    //  EMPLEADOS: registrar, editar, eliminar, listar, buscar
    // =====================================================================

    public void registrarEmpleado(String cod, String cargo, String nombre, String dni, String dir, String tel) {
        if (buscarEmpleado(cod) != null) {
            throw new IllegalArgumentException("Ya existe un empleado con ese código.");
        }
        if (buscarEmpleadoPorDni(dni) != null) {
            throw new IllegalArgumentException("Ya existe un empleado con ese DNI.");
        }
        empleados.add(new Empleado(cod, cargo, nombre, dni, dir, tel));
    }

    /** Edita un empleado identificado por su codigo (el codigo no cambia). */
    public void editarEmpleado(String cod, String cargo, String nombre, String nuevoDni, String dir, String tel) {
        Empleado e = buscarEmpleado(cod);
        if (e == null) {
            throw new IllegalArgumentException("Empleado no encontrado.");
        }
        Empleado otro = buscarEmpleadoPorDni(nuevoDni);
        if (otro != null && otro != e) {
            throw new IllegalArgumentException("Ya existe otro empleado con ese DNI.");
        }
        e.setCargo(cargo);
        e.setNombre(nombre);
        e.setDni(nuevoDni);
        e.setDireccion(dir);
        e.setTelefono(tel);
    }

    /** Elimina un empleado. Sus transacciones pasadas se conservan en el historial de las cuentas. */
    public void eliminarEmpleado(String cod) {
        Empleado e = buscarEmpleado(cod);
        if (e == null) {
            throw new IllegalArgumentException("Empleado no encontrado.");
        }
        empleados.remove(e);
    }

    public List<Empleado> getEmpleados() {
        return Collections.unmodifiableList(empleados);
    }

    public Empleado buscarEmpleado(String codEmpleado) {
        String clave = texto(codEmpleado);
        for (Empleado e : empleados) {
            if (e.getCodigoEmpleado().equals(clave)) return e;
        }
        return null;
    }

    public Empleado buscarEmpleadoPorDni(String dni) {
        String clave = texto(dni);
        for (Empleado e : empleados) {
            if (e.getDni().equals(clave)) return e;
        }
        return null;
    }

    // =====================================================================
    //  CUENTAS: crear, cerrar, listar, buscar
    // =====================================================================

    public void crearCuenta(String numCuenta, String tipo, double saldoInicial, String dniCliente) {
        Cliente cliente = buscarCliente(dniCliente);
        if (cliente == null) {
            throw new IllegalArgumentException("No existe un cliente registrado con ese DNI.");
        }
        if (buscarCuenta(numCuenta) != null) {
            throw new IllegalArgumentException("Ya existe una cuenta con ese número.");
        }
        if (!"Ahorro".equals(tipo) && !"Corriente".equals(tipo)) {
            throw new IllegalArgumentException("El tipo de cuenta debe ser Ahorro o Corriente.");
        }
        Cuenta nueva = new Cuenta(numCuenta, tipo, saldoInicial, cliente);
        cuentas.add(nueva);
        cliente.agregarCuenta(nueva);
    }

    /** Cierra (elimina) una cuenta. Solo si su saldo es 0. */
    public void cerrarCuenta(String numCuenta) {
        Cuenta c = exigirCuenta(numCuenta);
        if (c.getSaldo() > 0) {
            throw new IllegalArgumentException("No se puede cerrar: la cuenta tiene saldo (S/. "
                    + formato(c.getSaldo()) + "). Retire el dinero primero.");
        }
        c.getCliente().eliminarCuenta(c);
        cuentas.remove(c);
    }

    public List<Cuenta> getCuentas() {
        return Collections.unmodifiableList(cuentas);
    }

    public Cuenta buscarCuenta(String numCuenta) {
        String clave = texto(numCuenta);
        for (Cuenta c : cuentas) {
            if (c.getNumeroCuenta().equals(clave)) return c;
        }
        return null;
    }

    // =====================================================================
    //  OPERACIONES: depositar, retirar, transferir
    //  - idTrans vacio  -> se genera automaticamente (T-0001, T-0002, ...)
    //  - fecha/hora vacias -> se usa la fecha y hora actuales
    //  - codEmpleado vacio -> operacion hecha por el propio cliente ("Sistema")
    // =====================================================================

    public void realizarDeposito(String idTrans, String numCuenta, double monto, String fecha, String hora, String codEmpleado) {
        Cuenta cuenta = exigirCuenta(numCuenta);
        validarMonto(monto);
        Empleado emp = resolverEmpleado(codEmpleado);
        String f = normalizarFecha(fecha);
        String h = normalizarHora(hora);
        String id = resolverId(idTrans);
        ejecutar(new Deposito(id, monto, f, h, emp), cuenta, emp);
    }

    public void realizarRetiro(String idTrans, String numCuenta, double monto, String fecha, String hora, String codEmpleado) {
        Cuenta cuenta = exigirCuenta(numCuenta);
        validarMonto(monto);
        Empleado emp = resolverEmpleado(codEmpleado);
        String f = normalizarFecha(fecha);
        String h = normalizarHora(hora);
        String id = resolverId(idTrans);
        ejecutar(new Retiro(id, monto, f, h, emp), cuenta, emp);
    }

    /** Transferencia = un Retiro en la cuenta origen + un Deposito en la cuenta destino. */
    public void transferir(String numOrigen, String numDestino, double monto, String fecha, String hora, String codEmpleado) {
        Cuenta origen = exigirCuenta(numOrigen);
        Cuenta destino = exigirCuenta(numDestino);
        if (origen == destino) {
            throw new IllegalArgumentException("La cuenta origen y la cuenta destino deben ser distintas.");
        }
        validarMonto(monto);
        Empleado emp = resolverEmpleado(codEmpleado);
        String f = normalizarFecha(fecha);
        String h = normalizarHora(hora);

        ejecutar(new Retiro(generarIdTransaccion(), monto, f, h, emp), origen, emp);      // si falla, no se hace el deposito
        ejecutar(new Deposito(generarIdTransaccion(), monto, f, h, emp), destino, emp);
    }

    // =====================================================================
    //  TRANSACCIONES: listar, modificar, eliminar
    // =====================================================================

    /**
     * Lista transacciones (mas recientes primero dentro de cada cuenta).
     * Cada filtro es opcional: numCuenta vacio = todas las cuentas,
     * codEmpleado vacio = cualquier empleado, tipo null = Deposito y Retiro.
     */
    public List<Transaccion> listarTransacciones(String numCuenta, String codEmpleado, String tipo) {
        String num = texto(numCuenta);
        String codEmp = texto(codEmpleado);
        if (!codEmp.isEmpty() && buscarEmpleado(codEmp) == null) {
            throw new IllegalArgumentException("No existe un empleado con código " + codEmp + ".");
        }
        List<Cuenta> origen = new ArrayList<>();
        if (num.isEmpty()) {
            origen.addAll(cuentas);
        } else {
            origen.add(exigirCuenta(num));
        }
        List<Transaccion> resultado = new ArrayList<>();
        for (Cuenta c : origen) {
            List<Transaccion> movs = c.getMovimientos();
            for (int i = movs.size() - 1; i >= 0; i--) {
                Transaccion t = movs.get(i);
                if (tipo != null && !tipo.equals(t.getTipo())) continue;
                if (!codEmp.isEmpty()
                        && (t.getEmpleado() == null || !t.getEmpleado().getCodigoEmpleado().equals(codEmp))) continue;
                resultado.add(t);
            }
        }
        return resultado;
    }

    public Transaccion buscarTransaccion(String idTrans) {
        String id = texto(idTrans);
        for (Cuenta c : cuentas) {
            for (Transaccion t : c.getMovimientos()) {
                if (t.getIdTransaccion().equals(id)) return t;
            }
        }
        return null;
    }

    /** Numero de la cuenta a la que pertenece una transaccion ("-" si no se encuentra). */
    public String numeroCuentaDe(Transaccion t) {
        Cuenta c = cuentaDeTransaccion(t == null ? null : t.getIdTransaccion());
        return c == null ? "-" : c.getNumeroCuenta();
    }

    /** Modifica monto, fecha y hora de una transaccion (deposito o retiro) y corrige el saldo. */
    public void modificarTransaccion(String idTrans, double nuevoMonto, String nuevaFecha, String nuevaHora) {
        Cuenta cuenta = cuentaDeTransaccion(idTrans);
        if (cuenta == null) {
            throw new IllegalArgumentException("Transacción no encontrada.");
        }
        Transaccion t = buscarTransaccion(idTrans);
        validarMonto(nuevoMonto);
        String f = normalizarFecha(nuevaFecha);
        String h = normalizarHora(nuevaHora);
        if (!cuenta.modificarMonto(t, nuevoMonto)) {
            throw new IllegalArgumentException("No se pudo modificar: el saldo resultante sería negativo.");
        }
        t.setFecha(f);
        t.setHora(h);
    }

    /** Elimina una transaccion y deshace su efecto en el saldo de la cuenta. */
    public void eliminarTransaccion(String idTrans) {
        Cuenta cuenta = cuentaDeTransaccion(idTrans);
        if (cuenta == null) {
            throw new IllegalArgumentException("Transacción no encontrada.");
        }
        Transaccion t = buscarTransaccion(idTrans);
        if (!cuenta.eliminarMovimiento(t)) {
            throw new IllegalArgumentException("No se puede eliminar: el saldo de la cuenta quedaría negativo.");
        }
        if (t.getEmpleado() != null) {
            t.getEmpleado().quitarTransaccion(t);
        }
    }

    // =====================================================================
    //  USUARIOS Y PERMISOS: agregar, buscar, autenticar, eliminar, listar
    // =====================================================================

     public boolean agregarUsuario(Usuario u) {
        if (u == null) {
             throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }
        if (buscarUsuario(u.getNombreUsuario()) != null) {
             throw new IllegalArgumentException("Ya existe un usuario con ese nombre.");
         }
        usuarios.add(u);
        return true;
    }

    public Usuario buscarUsuario(String nombreUsuario) {
        String clave = texto(nombreUsuario);
        for (Usuario u : usuarios) {
            if (u.getNombreUsuario().equalsIgnoreCase(clave)) return u;
         }
         return null;
    }

    public Usuario autenticar(String user, String pass) {
        for (Usuario u : usuarios) {
            if (u.login(user, pass)) return u;
        }
        return null;
    }

    public boolean eliminarUsuario(String nombreUsuario) {
        Usuario encontrado = buscarUsuario(nombreUsuario);
        if (encontrado == null) {
             throw new IllegalArgumentException("Usuario no encontrado.");
        }
         return usuarios.remove(encontrado);
    }

    public List<Usuario> getUsuarios() {
        return Collections.unmodifiableList(usuarios);
    }

    // =====================================================================
    //  CONSULTAS (devuelven texto listo para mostrar)
    // =====================================================================

    public String consultarSaldo(String numCuenta) {
        Cuenta c = buscarCuenta(numCuenta);
        if (c != null) {
            return "Cuenta: " + c.getNumeroCuenta() + " (" + c.getTipo() + ")\n"
                 + "Titular: " + c.getCliente().getNombre() + "\n"
                 + "Saldo Actual: S/. " + formato(c.getSaldo());
        }
        return "Cuenta no encontrada.";
    }

    // Recorre la pila (Stack) de movimientos de la cuenta desde el más reciente
    public String mostrarMovimientos(String numCuenta) {
        Cuenta c = buscarCuenta(numCuenta);
        if (c == null) return "Cuenta no encontrada.";

        List<Transaccion> pila = c.getMovimientos();
        if (pila.isEmpty()) return "La cuenta no registra movimientos.";

        StringBuilder sb = new StringBuilder("Historial de Movimientos (Más reciente primero):\n");
        for (int i = pila.size() - 1; i >= 0; i--) {
            sb.append(pila.get(i).toString()).append("\n");
        }
        return sb.toString();
    }

    /** Movimientos desde una fecha (dd/MM/aaaa) hasta hoy, del mas reciente al mas antiguo. */
    public String mostrarMovimientosDesde(String numCuenta, String fechaDesde) {
        Cuenta c = buscarCuenta(numCuenta);
        if (c == null) return "Cuenta no encontrada.";
        LocalDate desde = parseFecha(fechaDesde);

        StringBuilder sb = new StringBuilder("Movimientos desde " + desde.format(FMT_FECHA)
                + " hasta " + LocalDate.now().format(FMT_FECHA) + " (más reciente primero):\n");
        List<Transaccion> pila = c.getMovimientos();
        int cantidad = 0;
        for (int i = pila.size() - 1; i >= 0; i--) {
            Transaccion t = pila.get(i);
            if (!parseFecha(t.getFecha()).isBefore(desde)) {
                sb.append(t).append("\n");
                cantidad++;
            }
        }
        if (cantidad == 0) return "No hay movimientos desde " + desde.format(FMT_FECHA) + ".";
        return sb.toString();
    }

    // =====================================================================
    //  Auxiliares privados
    // =====================================================================

    private Cuenta exigirCuenta(String numCuenta) {
        Cuenta c = buscarCuenta(numCuenta);
        if (c == null) {
            throw new IllegalArgumentException("Cuenta no encontrada: " + texto(numCuenta) + ".");
        }
        return c;
    }

    private Cuenta cuentaDeTransaccion(String idTrans) {
        if (idTrans == null) return null;
        String id = texto(idTrans);
        for (Cuenta c : cuentas) {
            for (Transaccion t : c.getMovimientos()) {
                if (t.getIdTransaccion().equals(id)) return c;
            }
        }
        return null;
    }

    private Empleado resolverEmpleado(String cod) {
        if (texto(cod).isEmpty()) return null;
        Empleado e = buscarEmpleado(cod);
        if (e == null) {
            throw new IllegalArgumentException("No existe un empleado con código " + texto(cod) + ".");
        }
        return e;
    }

    private String resolverId(String id) {
        if (texto(id).isEmpty()) return generarIdTransaccion();
        if (buscarTransaccion(id) != null) {
            throw new IllegalArgumentException("Ya existe una transacción con el ID " + texto(id) + ".");
        }
        return texto(id);
    }

    private String generarIdTransaccion() {
        String id;
        do {
            contadorTransacciones++;
            id = String.format("T-%04d", contadorTransacciones);
        } while (buscarTransaccion(id) != null);
        return id;
    }

    private void ejecutar(Transaccion t, Cuenta cuenta, Empleado emp) {
        if (!t.procesar(cuenta)) {
            throw new IllegalArgumentException("No se pudo procesar el " + t.getTipo().toLowerCase()
                    + ": monto inválido o saldo insuficiente.");
        }
        if (emp != null) {
            emp.agregarTransaccion(t);
        }
    }

    private static void validarMonto(double monto) {
        if (!(monto > 0) || Double.isInfinite(monto)) {
            throw new IllegalArgumentException("El monto debe ser un número mayor que cero.");
        }
    }

    private static LocalDate parseFecha(String f) {
        try {
            return LocalDate.parse(texto(f), FMT_FECHA);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Fecha inválida. Use el formato dd/MM/aaaa (ej. 24/11/2026).");
        }
    }

    private static String normalizarFecha(String f) {
        if (texto(f).isEmpty()) return LocalDate.now().format(FMT_FECHA);
        return parseFecha(f).format(FMT_FECHA);
    }

    private static String normalizarHora(String h) {
        if (texto(h).isEmpty()) return LocalTime.now().format(FMT_HORA);
        try {
            return LocalTime.parse(texto(h), FMT_HORA).format(FMT_HORA);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Hora inválida. Use el formato HH:mm (ej. 18:30).");
        }
    }

    private static String texto(String s) {
        return s == null ? "" : s.trim();
    }

    private static String formato(double valor) {
        return String.format(Locale.US, "%.2f", valor);
    }
}
