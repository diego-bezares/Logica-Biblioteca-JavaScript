import java.util.List;

public class BusquedaPorAutor_Bezares_218606334 implements IBusqueda_Bezares_218606334 {
    @Override
    public Libro_Bezares_218606334 buscar(List<Libro_Bezares_218606334> libros, String criterio) {
        String autorBuscado = criterio.toLowerCase();
        for (Libro_Bezares_218606334 libro : libros) {
            if (libro.getAutor().contains(autorBuscado)) {
                return libro;
            }
        }
        return null;
    }
}
