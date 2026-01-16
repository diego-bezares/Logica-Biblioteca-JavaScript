public class DevolucionNormal_Bezares_218606334 implements IOperacionDevolucion_Bezares_218606334 {

    @Override
    public void realizarDevolucion(Biblioteca_Bezares_218606334 b, int idLibro) {
        // Buscamos el préstamo activo asociado al libro
        for (Prestamo_Bezares_218606334 p : b.getPrestamos()) {
            if (p.getIdLibro() == idLibro && p.getEstado().equals("ACTIVO")) {

                // Calcula multa (RF17)
                long diasRetraso = p.calcularDiasRetraso(b.getFechaActual());
                int multa = (int) (diasRetraso * b.getTasaMultaDiaria());

                // Actualizar Usuario
                Usuario_Bezares_218606334 usuario = b.buscarUsuarioPorId(p.getIdUsuario());
                if (usuario != null) {
                    if (multa > 0) {
                        usuario.agregarDeuda(multa);
                    }

                    // RF20: Verificar si debe suspenderse
                    boolean excedeDeuda = usuario.getDeuda() > b.getLimiteDeudaMax();
                    boolean tieneRetrasoGrave = diasRetraso > b.getDiasMaxPrestamo();

                    if (excedeDeuda || tieneRetrasoGrave) {
                        // RF21: Suspender
                        usuario.suspender();
                    }
                }

                // Finalizar Préstamo y Liberar Libro
                p.finalizarPrestamo(b.getFechaActual());

                for(Libro_Bezares_218606334 l : b.getLibros()) {
                    if (l.getId() == idLibro) {
                        l.setDisponible(true);
                        break;
                    }
                }
                return;
            }
        }
    }
}
