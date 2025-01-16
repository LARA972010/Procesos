import java.util.Random;

public class Cocinero extends Thread {
    private static final Random rand = new Random();
    private int id;
    private double eficiencia;
    private long tiempoTotalPreparacion;
    private int platosPreparados;
    private Orden ordenMasLarga;

    public Cocinero(int id) {
        this.id = id;
        this.eficiencia = rand.nextDouble() * 0.5 + 0.5; // 0.5 a 1.0
        this.tiempoTotalPreparacion = 0;
        this.platosPreparados = 0;
        this.ordenMasLarga = null;
    }

    @Override
    public void run() {
        while (true) {
            Orden orden;
            synchronized (Cocina.getPedidos()) {
                if (Cocina.getPedidos().isEmpty()) {
                    break;
                }
                orden = Cocina.getPedidos().poll();
            }

            if (orden != null) {
                try {
                    long startTime = System.currentTimeMillis();
                    int tiempoPreparacion = (int)(orden.getTiempoPreparacion() * eficiencia);
                    System.out.println("Cocinero " + id + " preparando: " + orden);
                    Thread.sleep(tiempoPreparacion);  // Simula el tiempo de preparación
                    long endTime = System.currentTimeMillis();


                    tiempoTotalPreparacion += (endTime - startTime);
                    platosPreparados++;


                    if (ordenMasLarga == null || orden.getTiempoPreparacion() > ordenMasLarga.getTiempoPreparacion()) {
                        ordenMasLarga = orden;
                    }

                    Cocina.aumentarPlatosPreparados();
                    Cocina.aumentarGanancias(orden.getPrecio());
                } catch (InterruptedException e) {
                    System.out.println("Cocinero " + id + " interrumpido.");
                }
            }
        }
    }

    // Cambiar la prioridad del hilo
    public void setPrioridad(int prioridad) {
        setPriority(prioridad);
    }

    // Métodos para obtener el rendimiento del cocinero
    public long getTiempoTotalPreparacion() {
        return tiempoTotalPreparacion;
    }

    public int getPlatosPreparados() {
        return platosPreparados;
    }

    public Orden getOrdenMasLarga() {
        return ordenMasLarga;
    }
}
