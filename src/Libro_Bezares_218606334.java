public class Libro_Bezares_218606334{
    private int id;
    private String titulo;
    private String autor;
    private boolean disponible;

    // Contador estático para ID autoincremental
    private static int contadorId = 0;

    // Constructor (RF04)
    public Libro_Bezares_218606334(String titulo, String autor) {
        this.id = contadorId++;
        this.titulo = titulo.toLowerCase();
        this.autor = autor.toLowerCase();
        this.disponible = true;
    }

    // Getters necesarios
    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }

    // Método de comportamiento
    public boolean estaDisponible() {
        return this.disponible;
    }

    // Setter para cambiar disponibilidad al prestar/devolver
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}
