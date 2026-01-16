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
    public boolean realizarPrestamo(int idUsuario, int idLibro, int diasSolicitados){
        Usuario_Bezares_218606334 u = buscarUsuarioPorId(idUsuario);

        Libro_Bezares_218606334 l = null;
        for(Libro_Bezares_218606334 libro : libros){
            if(libro.getId() == idLibro) {
                l = libro;
                break;
            }
        }

        // Validaciones
        if(u == null || l == null) return false; // No existe el usuario o el libro
        if(!l.estaDisponible()) return false; // El libro no está disponible
        if(!u.puedePedirPrestamo()) return false; // Usuario suspendido
        if(diasSolicitados > diasMaxPrestamo) return false; // Días solicitados exceden el máximo

        // Verificar cantidad de préstamos activos
        long prestamosActivos = u.getPrestamos().stream().filter(p -> p.getEstado().equals("ACTIVO")).count();
        if(prestamosActivos >= maxLibrosPorUsuario) return false;

        // Verificar deuda
        if(u.getDeuda() > limiteDeudaMax) return false;

        // Ejecución del préstamo
        l.setDisponible(false);
        Prestamo_Bezares_218606334 nuevoPrestamo = new Prestamo_Bezares_218606334(
                u.getId(),
                l.getId(),
                this.fechaActual,
                diasSolicitados
        );
        this.prestamos.add(nuevoPrestamo);
        u.agregarPrestamo(nuevoPrestamo);

        return true;
    }

    // RF19: Devolver libro
    public void devolverLibro(int idLibro){
        // Se busca el préstamo activo asociado al libro
        for(Prestamo_Bezares_218606334 p : prestamos){
            if(p.getIdLibro() == idLibro && p.getEstado().equals("ACTIVO")){
                long diasRetraso = p.calcularDiasRetraso(this.fechaActual);
                int multa = (int) (diasRetraso * tasaMultaDiaria);

                // Actualizar usuario
                Usuario_Bezares_218606334 usuario = buscarUsuarioPorId(p.getIdUsuario());
                if(usuario != null){
                    usuario.agregarDeuda(multa);
                    // RF20: Suspender automáticamente si excede el límite de deuda
                    if(usuario.getDeuda() > limiteDeudaMax){
                        usuario.suspender();
                    }
                }

                // Actualizar préstamo
                p.finalizarPrestamo(this.fechaActual);

                // Actualizar libro
                for(Libro_Bezares_218606334 l : libros){
                    if(l.getId() == idLibro) {
                        l.setDisponible(true);
                        break;
                    }
                }

                return;
            }
        }
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

    // Getter para fecha actual
    public LocalDate getFechaActual() {
        return fechaActual;
    }
}
