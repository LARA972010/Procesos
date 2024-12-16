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

    // variables donde vasmoa ir guardando el dinero de cada barbero
    private static int gananciasBarbero1 = 0;
    private static int gananciasBarbero2 = 0;
    private static int numClient1 = 0;
    private static int numClient2 = 0;


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

        // me creo un hilo para mostrar las ganancias totales:
        Thread gananciasHilo = new Thread(() -> {
            while (abierto) {
                try {
                    Thread.sleep(5000);
                    System.out.println("Ganancias totales: Barbero 1 = $" + gananciasBarbero1 +
                            ", Barbero 2 = $" + gananciasBarbero2);
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Se produjo un error: ", e);
                }
            }
        });
        gananciasHilo.start();// este hilo mostrará las ganancias de cada barbero cada 5 segundos

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
        System.out.println("El barbero 1 ha cortado el pelo a :"+ numClient1+ " y el barbero 2 : "+ numClient2);
        System.out.println("La barbería ha cerrado no aceptamos a ningún cliente más.");
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
                    // despertamos al barbero
                    barberos.acquire();
                    System.out.println("Barbero " + id + ": cortándose el pelo...");
                    Thread.sleep(tiempoCorte); //Barbero tiene tiempo específico para cortar
                    System.out.println("Barbero " + id + ": Terminé de cortar el pelo.");
                    synchronized (this) {
                        // separo a cada barbero por si queremos que cada uno gane diferente en un futuro
                        if (id == 1) {
                            gananciasBarbero1 += 10;
                            numClient1 ++;
                        }
                        else {
                            gananciasBarbero2 += 10;
                            numClient2 ++;
                        }
                    }
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Se produjo un error: ", e);
                } finally {
                    // liberamos la silla del barbero:
                    sillasBarbero.release();
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
                // comprobamos que hay silla de espera:
                if (sillas.tryAcquire()) {
                    System.out.println("Cliente " + id + ": Me senté en una silla de espera.");
                    //despertar a un barbero
                    barberos.release();
                    //el cliente espera a que se quede una silla de barbero libre (desde la silla de espera)
                    sillasBarbero.acquire();
                    //liberar la silla de espera
                    sillas.release();
                    System.out.println("Cliente " + id + ": Estoy en la silla del barbero.");
                    Thread.sleep(3000);
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
