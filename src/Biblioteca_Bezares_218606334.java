import java.time.LocalDate;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class Biblioteca_Bezares_218606334 {
    private List<Libro_Bezares_218606334> libros;
    private List<Usuario_Bezares_218606334> usuarios;
    private List<Prestamo_Bezares_218606334> prestamos;

    private int maxLibrosPorUsuario;
    private int diasMaxPrestamo;
    private int limiteDeudaMax;
    private int tasaMultaDiaria;
    private LocalDate fechaActual;

    // Constructor (RF06)
    public Biblioteca_Bezares_218606334(LocalDate fechaInicial, int maxLibros, int diasMaxPrestamo, int limiteDeuda, int tasaMultaDiaria){
        this.libros = new ArrayList<>();
        this.usuarios = new ArrayList<>();
        this.prestamos = new ArrayList<>();

        this.fechaActual = fechaInicial;
        this.maxLibrosPorUsuario = maxLibros;
        this.diasMaxPrestamo = diasMaxPrestamo;
        this.tasaMultaDiaria = tasaMultaDiaria;
        this.limiteDeudaMax = limiteDeuda;
    }

    public List<Libro_Bezares_218606334> getLibros() { return libros; }
    public List<Usuario_Bezares_218606334> getUsuarios() { return usuarios; }
    public List<Prestamo_Bezares_218606334> getPrestamos() { return prestamos; }

    public int getMaxLibrosPorUsuario() { return maxLibrosPorUsuario; }
    public int getDiasMaxPrestamo() { return diasMaxPrestamo; }
    public int getLimiteDeudaMax() { return limiteDeudaMax; }
    public int getTasaMultaDiaria() { return tasaMultaDiaria; }
    public LocalDate getFechaActual() { return fechaActual; }
    public void registrarNuevoPrestamo(Prestamo_Bezares_218606334 p) {
        this.prestamos.add(p);
    }

    // RF07: Agregar Libro
    public boolean agregarLibro(Libro_Bezares_218606334 libro){
        return this.libros.add(libro);
    }

    // RF08: Registrar Usuario
    public boolean registrarUsuario(Usuario_Bezares_218606334 usuario){
        return this.usuarios.add(usuario);
    }

    // RF09: Buscar Usuario por ID
    public Usuario_Bezares_218606334 buscarUsuarioPorId(int id){
        for(Usuario_Bezares_218606334 u : usuarios){
            if(u.getId() == id)
                return u;
        }
        return null;
    }

    // RF10: Buscar Libro (POLIMORFISMO)
    public Libro_Bezares_218606334 buscarLibro(IBusqueda_Bezares_218606334 estrategia, String criterio){
        return estrategia.buscar(this.libros, criterio);
    }

    // RF18: Realizar préstamo
    public boolean realizarPrestamo(int idUsuario, int idLibro, int diasSolicitados) {
        // Instanciamos la estrategia
        IOperacionPrestamo_Bezares_218606334 operacion = new PrestamoNormal_Bezares_218606334();
        return operacion.ejecutarPrestamo(this, idUsuario, idLibro, diasSolicitados);
    }

    // RF19: Devolver libro
    public void devolverLibro(int idLibro) {
        IOperacionDevolucion_Bezares_218606334 operacion = new DevolucionNormal_Bezares_218606334();
        operacion.realizarDevolucion(this, idLibro);
    }

    // RF22: Renovar préstamo
    public String renovarPrestamo(int idPrestamo, int diasExtra) {
        // Buscar el préstamo
        Prestamo_Bezares_218606334 prestamo = null;
        for (Prestamo_Bezares_218606334 p : prestamos) {
            if (p.getId() == idPrestamo) {
                prestamo = p;
                break;
            }
        }

        // Validaciones
        if (prestamo == null) return "Préstamo no encontrado.";
        if (!prestamo.getEstado().equals("ACTIVO")) return "El préstamo no está activo.";

        Usuario_Bezares_218606334 usuario = buscarUsuarioPorId(prestamo.getIdUsuario());
        if (usuario == null) return "Usuario no existe.";

        // Usuario no suspendido
        if (!usuario.puedePedirPrestamo()) return "Usuario suspendido, no puede renovar.";

        // No hay retraso actual
        if (prestamo.calcularDiasRetraso(this.fechaActual) > 0) return "El préstamo ya tiene retraso. No se puede renovar.";

        // Días totales no exceden máximo
        long diasTotales = java.time.temporal.ChronoUnit.DAYS.between(
                prestamo.getFechaInicio(),
                prestamo.getFechaVencimiento().plusDays(diasExtra)
        );

        if (diasTotales > this.diasMaxPrestamo) {
            return "Renovación rechazada: Excede el tiempo máximo permitido de préstamo (" + this.diasMaxPrestamo + " días totales).";
        }

        // Ejecutar renovación
        prestamo.extenderPlazo(diasExtra);
        return "Renovación exitosa. Nuevo vencimiento: " + prestamo.getFechaVencimiento();
    }

    // RF23: Pagar Deuda
    public String pagarDeuda(int idUsuario, int monto) {
        Usuario_Bezares_218606334 usuario = buscarUsuarioPorId(idUsuario);
        if (usuario == null) return "Usuario no encontrado.";

        // Aplicar pago
        usuario.disminuirDeuda(monto);

        // Verificar reactivación
        if (usuario.getDeuda() == 0) {
            boolean tieneAtrasos = false;
            for (Prestamo_Bezares_218606334 p : prestamos) {
                // Buscamos prestamos activos de este usuario que tengan retraso hoy
                if (p.getIdUsuario() == idUsuario && p.getEstado().equals("ACTIVO")) {
                    if (p.calcularDiasRetraso(this.fechaActual) > 0) {
                        tieneAtrasos = true;
                        break;
                    }
                }
            }

            if (!tieneAtrasos) {
                usuario.activar(); // Reactivar usuario
                return "Pago exitoso. Deuda saldada y usuario REACTIVADO.";
            } else {
                return "Pago exitoso. Deuda saldada, pero usuario sigue SUSPENDIDO por libros atrasados sin devolver.";
            }
        }

        return "Pago parcial realizado. Nueva deuda: $" + usuario.getDeuda();
    }

    // RF24: Historial préstamos Usuario (Reporte String)
    public String generarReporteUsuario(int idUsuario) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Historial Usuario ID ").append(idUsuario).append(" ---\n");

        boolean tieneDatos = false;
        for (Prestamo_Bezares_218606334 p : prestamos) {
            if (p.getIdUsuario() == idUsuario) {
                sb.append("ID Préstamo: ").append(p.getId())
                        .append(" | ID Libro: ").append(p.getIdLibro())
                        .append(" | Fecha: ").append(p.getFechaInicio())
                        .append(" | Vence: ").append(p.getFechaVencimiento())
                        .append(" | Estado: ").append(p.getEstado()).append("\n");
                tieneDatos = true;
            }
        }

        if (!tieneDatos) return "El usuario no tiene historial de préstamos.";
        return sb.toString();
    }

    // RF25: Historial sistema
    public String generarReporteSistema() {
        if (prestamos.isEmpty()) return "No hay préstamos registrados en el sistema.";

        StringBuilder sb = new StringBuilder();
        sb.append("--- REPORTE GENERAL DEL SISTEMA ---\n");
        for (Prestamo_Bezares_218606334 p : prestamos) {
            sb.append("ID: ").append(p.getId())
                    .append(" | User: ").append(p.getIdUsuario())
                    .append(" | Libro: ").append(p.getIdLibro())
                    .append(" | Inicio: ").append(p.getFechaInicio())
                    .append(" | Estado: ").append(p.getEstado()).append("\n");
        }
        return sb.toString();
    }

    // RF26: Procesar día
    public void procesarDia() {
        this.fechaActual = this.fechaActual.plusDays(1);

        // Revisar si algún usuario debe ser suspendido por retrasos nuevos
        for (Prestamo_Bezares_218606334 p : prestamos) {
            if (p.getEstado().equals("ACTIVO")) {
                long retraso = p.calcularDiasRetraso(this.fechaActual);
                if (retraso > 0) {
                    Usuario_Bezares_218606334 u = buscarUsuarioPorId(p.getIdUsuario());
                    if (u != null) u.suspender();
                }
            }
        }
    }

}
