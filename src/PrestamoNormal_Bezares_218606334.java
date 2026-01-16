public class PrestamoNormal_Bezares_218606334 implements IOperacionPrestamo_Bezares_218606334 {

    @Override
    public boolean ejecutarPrestamo(Biblioteca_Bezares_218606334 b, int idUsuario, int idLibro, int diasSolicitados) {
        Usuario_Bezares_218606334 usuario = b.buscarUsuarioPorId(idUsuario);

        Libro_Bezares_218606334 libro = null;
        for (Libro_Bezares_218606334 l : b.getLibros()) {
            if (l.getId() == idLibro) {
                libro = l;
                break;
            }
        }

        // Validaciones
        if (usuario == null || libro == null) return false;
        if (!libro.estaDisponible()) return false;
        if (!usuario.puedePedirPrestamo()) return false;
        if (diasSolicitados > b.getDiasMaxPrestamo()) return false;

        // Verificar préstamos activos
        long prestamosActivos = usuario.getPrestamos().stream().filter(p -> p.getEstado().equals("ACTIVO")).count();
        if (prestamosActivos >= b.getMaxLibrosPorUsuario()) return false;
        if (usuario.getDeuda() > b.getLimiteDeudaMax()) return false;

        // Ejecución del préstamo
        libro.setDisponible(false);

        Prestamo_Bezares_218606334 nuevoPrestamo = new Prestamo_Bezares_218606334(
                usuario.getId(),
                libro.getId(),
                b.getFechaActual(),
                diasSolicitados
        );

        // Registramos en la biblioteca y en el usuario
        b.registrarNuevoPrestamo(nuevoPrestamo);
        usuario.agregarPrestamo(nuevoPrestamo);

        return true;
    }
}
