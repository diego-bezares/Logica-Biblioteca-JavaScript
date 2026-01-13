import java.util.ArrayList;
import java.util.List;

public class Usuario_Bezares_218606334 {
    private int id;
    private String nombre;
    private int deuda;
    private String estado; // "ACTIVO" o "SUSPENDIDO"
    private List<Prestamo_Bezares_218606334> prestamos; // Lista de préstamos del usuario

    // Contador para ID autoincremental
    private static int contadorId = 0;

    // Constructor
    public Usuario_Bezares_218606334(String nombre) {
        this.id = contadorId++;
        this.nombre = nombre;
        this.deuda = 0; // RF03: Deuda inicial 0
        this.estado = "ACTIVO"; // RF03: Estado inicial activo
        this.prestamos = new ArrayList<>();
    }

    // Métodos (Getters)
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getDeuda() { return deuda; }
    public String getEstado() { return estado; }
    public List<Prestamo_Bezares_218606334> getPrestamos() { return prestamos; }

    // Métodos de lógica (RF20 y RF21)
    public void suspender() {
        this.estado = "SUSPENDIDO";
    }

    public void activar() {
        this.estado = "ACTIVO";
    }

    public void agregarDeuda(int monto) {
        this.deuda += monto;
    }

    // Método para validar si puede pedir (RF18)
    public boolean puedePedirPrestamo() {
        return this.estado.equals("ACTIVO");
    }

    // Agregar un préstamo al historial
    public void agregarPrestamo(Prestamo_Bezares_218606334 prestamo) {
        this.prestamos.add(prestamo);
    }
}
