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
