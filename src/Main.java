import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Inicialización: Max 3 libros, 7 días préstamo, $5000 deuda máx, $1000 multa diaria
        Biblioteca_Bezares_218606334 biblioteca = new Biblioteca_Bezares_218606334(
                LocalDate.now(), 3, 7, 5000, 1000
        );

        Scanner scanner = new Scanner(System.in);
        boolean ejecutando = true;

        System.out.println("=== SISTEMA DE BIBLIOTECA ===");

        while (ejecutando) {
            System.out.println("\n--- FECHA: " + biblioteca.getFechaActual() + " ---");
            System.out.println("1. Agregar Libro");
            System.out.println("2. Registrar Usuario");
            System.out.println("3. Buscar Libro");
            System.out.println("4. Realizar Préstamo");
            System.out.println("5. Devolver Libro");
            System.out.println("6. Avanzar día");
            System.out.println("7. Pagar deuda");
            System.out.println("8. Salir");
            System.out.print("Seleccione: ");

            int opcion = -1;
            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) { continue; }

            switch (opcion) {
                case 1: // Agregar Libro
                    System.out.print("Título: ");
                    String titulo = scanner.nextLine();
                    System.out.print("Autor: ");
                    String autor = scanner.nextLine();
                    Libro_Bezares_218606334 l = new Libro_Bezares_218606334(titulo, autor);
                    biblioteca.agregarLibro(l);
                    System.out.println("Libro ID: " + l.getId());
                    break;

                case 2: // Registrar Usuario
                    System.out.print("Nombre: ");
                    String nombre = scanner.nextLine();
                    Usuario_Bezares_218606334 u = new Usuario_Bezares_218606334(nombre);
                    biblioteca.registrarUsuario(u);
                    System.out.println("Usuario ID: " + u.getId());
                    break;

                case 3: // Buscar
                    System.out.println("a) ID  b) Título  c) Autor");
                    String tipo = scanner.nextLine();
                    System.out.print("Criterio: ");
                    String crit = scanner.nextLine();
                    IBusqueda_Bezares_218606334 est = null;
                    if (tipo.equalsIgnoreCase("a")) est = new BusquedaPorId_Bezares_218606334();
                    else if (tipo.equalsIgnoreCase("b")) est = new BusquedaPorTitulo_Bezares_218606334();
                    else if (tipo.equalsIgnoreCase("c")) est = new BusquedaPorAutor_Bezares_218606334();

                    if (est != null) {
                        Libro_Bezares_218606334 res = biblioteca.buscarLibro(est, crit);
                        if (res != null) System.out.println("Encontrado: " + res.getTitulo() + " (Disp: " + res.estaDisponible() + ")");
                        else System.out.println("No encontrado.");
                    }
                    break;

                case 4: // Prestar
                    try {
                        System.out.print("ID Usuario: ");
                        int idU = Integer.parseInt(scanner.nextLine());
                        System.out.print("ID Libro: ");
                        int idL = Integer.parseInt(scanner.nextLine());
                        System.out.print("Días: ");
                        int dias = Integer.parseInt(scanner.nextLine());
                        if (biblioteca.realizarPrestamo(idU, idL, dias)) System.out.println("✅ Préstamo OK");
                        else System.out.println("❌ Falló (Revise estado usuario o disponibilidad)");
                    } catch (Exception e) { System.out.println("Error de entrada"); }
                    break;

                case 5: // Devolver (CON CÁLCULO DE MULTA VISUAL)
                    try {
                        System.out.print("ID Libro a devolver: ");
                        int idLibDev = Integer.parseInt(scanner.nextLine());

                        // Truco para mostrar la multa: ver deuda antes y después
                        // Necesitamos saber quién tiene el libro para ver su deuda
                        // Como Main no sabe eso fácil, lo haremos genérico:
                        // Si quieres ser muy preciso, busca el préstamo en Main, pero es mucha lógica.
                        // Simplemente ejecutamos y avisamos que revise su estado.

                        // MEJORA: Buscar el usuario dueño del prestamo antes de devolver para comparar deuda
                        // Pero para no complicar tu codigo main con logica de negocio, confiaremos en biblioteca.

                        biblioteca.devolverLibro(idLibDev);
                        System.out.println("Libro procesado. Si hubo retraso, se aplicó multa. Revise estado del usuario.");

                    } catch (Exception e) { System.out.println("Error de entrada"); }
                    break;

                case 6: // Avanzar Día
                    biblioteca.procesarDia();
                    System.out.println(">>> Nuevo día: " + biblioteca.getFechaActual());
                    break;

                case 7: // PAGAR DEUDA (NUEVO)
                    try {
                        System.out.print("ID Usuario: ");
                        int idU = Integer.parseInt(scanner.nextLine());
                        Usuario_Bezares_218606334 userDeuda = biblioteca.buscarUsuarioPorId(idU);
                        if(userDeuda != null) {
                            System.out.println("Deuda Actual: $" + userDeuda.getDeuda());
                            System.out.print("Monto a pagar: ");
                            int monto = Integer.parseInt(scanner.nextLine());
                            String mensaje = biblioteca.pagarDeuda(idU, monto);
                            System.out.println(mensaje);
                        } else {
                            System.out.println("Usuario no existe");
                        }
                    } catch (Exception e) { System.out.println("Error entrada"); }
                    break;

                case 8: // Salir
                    ejecutando = false;
                    break;
            }
        }
    }
}