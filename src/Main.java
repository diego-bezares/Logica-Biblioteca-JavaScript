import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Inicialización
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
            System.out.println("6. Avanzar Día");
            System.out.println("7. Pagar Deuda");
            System.out.println("8. Renovar Préstamo");
            System.out.println("9. Ver Historial Usuario");
            System.out.println("10. Ver Historial Sistema");
            System.out.println("11. Buscar Usuario");
            System.out.println("12. Salir");
            System.out.print("Seleccione: ");

            int opcion = -1;
            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) { continue; }

            switch (opcion) {
                case 1:
                    System.out.print("Título: "); String t = scanner.nextLine();
                    System.out.print("Autor: "); String a = scanner.nextLine();
                    Libro_Bezares_218606334 l = new Libro_Bezares_218606334(t, a);
                    biblioteca.agregarLibro(l);
                    System.out.println("Libro ID: " + l.getId());
                    break;
                case 2:
                    System.out.print("Nombre: "); String n = scanner.nextLine();
                    Usuario_Bezares_218606334 u = new Usuario_Bezares_218606334(n);
                    biblioteca.registrarUsuario(u);
                    System.out.println("Usuario ID: " + u.getId());
                    break;
                case 3:
                    System.out.print("Tipo (a:ID, b:Titulo, c:Autor): "); String tipo = scanner.nextLine();
                    System.out.print("Criterio: "); String crit = scanner.nextLine();
                    IBusqueda_Bezares_218606334 est = null;
                    if(tipo.equalsIgnoreCase("a")) est = new BusquedaPorId_Bezares_218606334();
                    else if(tipo.equalsIgnoreCase("b")) est = new BusquedaPorTitulo_Bezares_218606334();
                    else if(tipo.equalsIgnoreCase("c")) est = new BusquedaPorAutor_Bezares_218606334();
                    if(est!=null) {
                        Libro_Bezares_218606334 res = biblioteca.buscarLibro(est, crit);
                        System.out.println(res != null ? "Encontrado: " + res.getTitulo() : "No encontrado");
                    }
                    break;

                case 4: // Prestar
                    try {
                        System.out.print("ID Usuario: "); int idU = Integer.parseInt(scanner.nextLine());
                        System.out.print("ID Libro: "); int idL = Integer.parseInt(scanner.nextLine());
                        System.out.print("Días: "); int dias = Integer.parseInt(scanner.nextLine());
                        if (biblioteca.realizarPrestamo(idU, idL, dias)) System.out.println("Préstamo exitoso");
                        else System.out.println("Error: No pudimos realizar el préstamo");
                    } catch (Exception e) { System.out.println("Error entrada"); }
                    break;

                case 5: // Devolver
                    try {
                        System.out.print("ID Libro a devolver: "); int idL = Integer.parseInt(scanner.nextLine());
                        biblioteca.devolverLibro(idL);
                        System.out.println("Proceso de devolución ejecutado.");
                    } catch (Exception e) {}
                    break;

                case 6: // Avanzar
                    biblioteca.procesarDia();
                    System.out.println(">>> Día avanzado.");
                    break;

                case 7: // Pagar
                    try {
                        System.out.print("ID Usuario: "); int idU = Integer.parseInt(scanner.nextLine());
                        System.out.print("Monto: "); int m = Integer.parseInt(scanner.nextLine());
                        System.out.println(biblioteca.pagarDeuda(idU, m));
                    } catch (Exception e) {}
                    break;

                case 8: // Renovar
                    try {
                        System.out.print("ID Préstamo a renovar: ");
                        int idP = Integer.parseInt(scanner.nextLine());
                        System.out.print("Días extra a sumar: ");
                        int diasEx = Integer.parseInt(scanner.nextLine());
                        System.out.println(biblioteca.renovarPrestamo(idP, diasEx));
                    } catch (Exception e) { System.out.println("Error entrada"); }
                    break;

                case 9: // Reporte Usuario
                    try {
                        System.out.print("ID Usuario: ");
                        int idU = Integer.parseInt(scanner.nextLine());
                        System.out.println(biblioteca.generarReporteUsuario(idU));
                    } catch (Exception e) {}
                    break;

                case 10: // Reporte Sistema
                    System.out.println(biblioteca.generarReporteSistema());
                    break;

                case 11: // Buscar Usuario
                    try {
                        System.out.print("ID Usuario a buscar: ");
                        int idBuscado = Integer.parseInt(scanner.nextLine());
                        Usuario_Bezares_218606334 usuario = biblioteca.buscarUsuarioPorId(idBuscado);
                        if (usuario != null) {
                            System.out.println("--- FICHA DE USUARIO ---");
                            System.out.println("ID: " + usuario.getId());
                            System.out.println("Nombre: " + usuario.getNombre());
                            System.out.println("Estado: " + usuario.getEstado());
                            System.out.println("Deuda Actual: $" + usuario.getDeuda());
                            System.out.println("Total Préstamos Históricos: " + usuario.getPrestamos().size());
                        } else {
                            System.out.println("Usuario no encontrado.");
                        }
                    } catch (Exception e) { System.out.println("Error entrada"); }
                    break;

                case 12:
                    ejecutando = false;
                    System.out.println("Cerrando sistema...");
                    break;
            }
        }
    }
}