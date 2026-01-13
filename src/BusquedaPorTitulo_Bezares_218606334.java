import java.util.List;

public class BusquedaPorTitulo_Bezares_218606334 implements IBusqueda_Bezares_218606334 {
    @Override
    public Libro_Bezares_218606334 buscar(List<Libro_Bezares_218606334> libros, String criterio) {
        // Case-insensitive (minúsculas)
        String tituloBuscado = criterio.toLowerCase();
        for (Libro_Bezares_218606334 libro : libros) {
            if (libro.getTitulo().contains(tituloBuscado)) { // Búsqueda parcial
                return libro;
            }
        }
        return null;
    }
}