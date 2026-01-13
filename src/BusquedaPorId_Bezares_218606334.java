import java.util.List;

public class BusquedaPorId_Bezares_218606334 implements IBusqueda_Bezares_218606334 {
    @Override
    public Libro_Bezares_218606334 buscar(List<Libro_Bezares_218606334> libros, String criterio) {
        try {
            int idBuscado = Integer.parseInt(criterio);

            for (Libro_Bezares_218606334 libro : libros) {
                if (libro.getId() == idBuscado) {
                    return libro;
                }
            }
        } catch (NumberFormatException e) {
            // Si el criterio no es un número, simplemente no encontramos nada.
            return null;
        }
        return null; // No encontrado
    }
}
