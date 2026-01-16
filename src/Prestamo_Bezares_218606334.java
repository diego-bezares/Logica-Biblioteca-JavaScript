import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Prestamo_Bezares_218606334 {
    private int id;
    private int idUsuario;
    private int idLibro;
    private LocalDate fechaInicio;
    private LocalDate fechaVencimiento;
    private LocalDate fechaDevolucionReal; // Null si aún no lo devuelve
    private String estado; // "ACTIVO", "FINALIZADO"

    // Contador estático
    private static int contadorId = 0;

    // Constructor (RF05 y RF15)
    public Prestamo_Bezares_218606334(int idUsuario, int idLibro, LocalDate fechaInicio, int diasSolicitados) {
        this.id = contadorId++;
        this.idUsuario = idUsuario;
        this.idLibro = idLibro;
        this.fechaInicio = fechaInicio;
        // RF15: Calcular vencimiento sumando días
        this.fechaVencimiento = fechaInicio.plusDays(diasSolicitados);
        this.estado = "ACTIVO";
        this.fechaDevolucionReal = null;
    }

    // Getters
    public int getId() { return id; }
    public int getIdUsuario() { return idUsuario; }
    public int getIdLibro() { return idLibro; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public String getEstado() { return estado; }

    // Métodos de lógica
    public void finalizarPrestamo(LocalDate fechaDevolucion) {
        this.fechaDevolucionReal = fechaDevolucion;
        this.estado = "FINALIZADO";
    }
    public void extenderPlazo(int diasExtra) {
        this.fechaVencimiento = this.fechaVencimiento.plusDays(diasExtra);
    }

    // RF16: Calcular días de retraso
    public long calcularDiasRetraso(LocalDate fechaActual) {
        if (!estado.equals("ACTIVO")) return 0; // Si ya pagó, no hay retraso actual

        // Si fechaActual es mayor a vencimiento, retorna la diferencia. Si no, 0.
        long dias = ChronoUnit.DAYS.between(this.fechaVencimiento, fechaActual);
        return dias > 0 ? dias : 0;
    }
}
