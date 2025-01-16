import java.util.Random;

public class JefeDeCocina extends Thread {
    private static final Random rand = new Random();

    @Override
    public void run() {
        long tiempoLimite = System.currentTimeMillis() + 30000; // 30 segundos de simulación

        while (System.currentTimeMillis() < tiempoLimite) {
            try {
                Thread.sleep(3000); // El jefe supervisa cada 3 segundos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (Cocina.getPedidos()) {
                System.out.println("Platos preparados: " + Cocina.getPlatosPreparados());
                System.out.println("Ganancias totales: " + Cocina.getGananciasTotales() + " €");

                //mostramos la orden con mas tiempo de preparación para verificar el cambio de prioridad
                Orden ordenConMasTiempo = null;
                for (Thread t : Thread.getAllStackTraces().keySet()) {
                    if (t instanceof Cocinero) {
                        Cocinero cocinero = (Cocinero) t;
                        Orden orden = cocinero.getOrdenMasLarga();
                        if (orden != null && (ordenConMasTiempo == null || orden.getTiempoPreparacion() > ordenConMasTiempo.getTiempoPreparacion())) {
                            ordenConMasTiempo = orden;
                        }
                    }
                }

                if (ordenConMasTiempo != null) {
                    System.out.println("Orden con más tiempo de preparación: " + ordenConMasTiempo);
                }

                // Supervisar el rendimiento de los cocineros
                for (Thread t : Thread.getAllStackTraces().keySet()) {
                    if (t instanceof Cocinero) {
                        Cocinero cocinero = (Cocinero) t;

                        // Comprobar que el cocinero ha preparado al menos un plato antes de calcular el tiempo promedio
                        if (cocinero.getPlatosPreparados() > 0) {
                            long tiempoPromedio = cocinero.getTiempoTotalPreparacion() / cocinero.getPlatosPreparados();
                            long tiempoEsperado = 3000; // Tiempo ideal por plato, en milisegundos

                            if (tiempoPromedio > tiempoEsperado) {
                                cocinero.setPrioridad(Thread.MAX_PRIORITY);  // Aumenta la prioridad
                                System.out.println("Jefe cambia prioridad de cocinero " + cocinero.getId() + " a alta.");
                            } else if (tiempoPromedio < tiempoEsperado) {
                                cocinero.setPrioridad(Thread.MIN_PRIORITY);  // Baja la prioridad
                                System.out.println("Jefe cambia prioridad de cocinero " + cocinero.getId() + " a baja.");
                            } else {
                                cocinero.setPrioridad(Thread.NORM_PRIORITY);  // Prioridad normal
                            }
                        } else {
                            System.out.println("Cocinero " + cocinero.getId() + " no ha preparado platos aún.");
                        }
                    }
                }
            }
        }
        System.out.println("Tiempo de cocina agotado.");
    }
}
