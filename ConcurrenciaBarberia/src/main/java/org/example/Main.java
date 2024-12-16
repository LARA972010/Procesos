package org.example;

import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    // Semáforos
    private static final Semaphore sillas = new Semaphore(3); // 3 sillas de espera
    private static final Semaphore sillasBarbero = new Semaphore(2); // 2 sillas de barbero
    private static final Semaphore barberos = new Semaphore(0); // Barberos inicialmente durmiendo

    // variables donde vasmoa ir guardabdo el dinero de cada barbero
    private static int gananciasBarbero1 = 0;
    private static int gananciasBarbero2 = 0;

    // booleano para mostrar el estado de la barberia:
    private static boolean abierto = true;

    public static void main(String[] args) {
        //Nos cremos los dos hilos de cada barbero y los inicializamos:
        Thread hiloBarbero1 = new Thread(new Barbero(1, 5000)); // Barbero que atiende a mas.
        Thread hiloBarbero2 = new Thread(new Barbero(2, 10000)); // BArbero que atiende a menos
        hiloBarbero1.start();
        hiloBarbero2.start();

        //La barbería solo recibe clientes dentro del minuto:
        new Thread(() -> {
            try {
                Thread.sleep(60000);
                abierto = false;
                System.out.println("La barbería está cerrada, ya no recibe mas clientes.");
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Se produjo un error: ", e);
            }
        }).start();

        // 20 clientes con tiempos diferentes
        for (int i = 1; i <= 20; i++) {
            if (abierto) {
                new Thread(new Cliente(i)).start();
                try {
                    Thread.sleep((int) (Math.random() * 5000)); // Tiempo aleatorio entre clientes (0-5 segundos)
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Se produjo un error: ", e);
                }
            } else {
                System.out.println("Cliente " + i + ": La barbería ya está cerrada.");
            }
        }

        // me creo un hilo para mostrar las ganancias totales:
        Thread gananciasHilo = new Thread(() -> {
            while (abierto) {
                try {
                    Thread.sleep(10000); 
                    System.out.println("Ganancias totales: Barbero 1 = $" + gananciasBarbero1 +
                            ", Barbero 2 = $" + gananciasBarbero2);
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Se produjo un error: ", e);
                }
            }
        });
        gananciasHilo.start();

        // Esperar a que los barberos terminen después de cerrar
        try {
            hiloBarbero1.join();
            hiloBarbero2.join();
            gananciasHilo.join();
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Se produjo un error: ", e);
        }

        // Mostrar ganancias finales
        System.out.println("Ganancias finales totales: Barbero 1 = $" + gananciasBarbero1 + ", Barbero 2 = $" + gananciasBarbero2);
        System.out.println("La barbería ha cerrado por completo.");
    }

    // Clase Barbero
    static class Barbero implements Runnable {
        private final int id;
        private final int tiempoCorte;

        public Barbero(int id, int tiempoCorte) {
            this.id = id;
            this.tiempoCorte = tiempoCorte;
        }

        @Override
        public void run() {
            while (abierto || barberos.availablePermits() > 0) {
                try {
                    barberos.acquire(); //esperar a que llegue un cliente
                    System.out.println("Barbero " + id + ": Comenzando a cortar el pelo...");
                    Thread.sleep(tiempoCorte); //Barbero tiene tiempo específico para cortar
                    System.out.println("Barbero " + id + ": Terminé de cortar el pelo.");
                    synchronized (this) {
                        if (id == 1) gananciasBarbero1 += 15; //Barbero 1 gana $10 por cliente
                        else gananciasBarbero2 += 10; //Barbero 2 gana $15 por cliente
                    }
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Se produjo un error: ", e);
                } finally {
                    sillasBarbero.release(); // Liberar la silla del barbero
                }
            }
        }
    }

    // Clase Cliente
    static class Cliente implements Runnable {
        private final int id;

        public Cliente(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                if (sillas.tryAcquire()) { //primero comprueba que hay silla de espera
                    System.out.println("Cliente " + id + ": Me senté en una silla de espera.");
                    barberos.release(); // Despertar a un barbero
                    sillasBarbero.acquire(); // Esperar una silla de barbero libre
                    sillas.release(); // Liberar la silla de espera
                    System.out.println("Cliente " + id + ": Estoy en la silla del barbero.");
                    Thread.sleep(3000); // Simular tiempo en la silla del barbero
                    System.out.println("Cliente " + id + ": Terminé y me voy.");
                } else {
                    System.out.println("Cliente " + id + ": No hay sillas libres, me voy.");
                }
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Se produjo un error: ", e);
            }
        }
    }
}
