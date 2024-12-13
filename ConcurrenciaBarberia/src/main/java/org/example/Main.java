package org.example;

import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    // Semáforo para sillas de espera
    private static final Semaphore sillas = new Semaphore(3); // 3 sillas disponibles
    // Semáforo para la silla del barbero
    private static final Semaphore sillaBarbero = new Semaphore(1); // 1 silla del barbero
    // Semáforo para el barbero (dormir/despertar)
    private static final Semaphore barbero = new Semaphore(0); // Barbero está inicialmente durmiendo

    public static void main(String[] args) {


        // Crear y ejecutar el hilo del barbero
        Thread hiloBarbero = new Thread(new Barbero());
        hiloBarbero.start();

        // Crear y ejecutar hilos para los clientes
        Thread[] hilosClientes = new Thread[8];
        for (int i = 1; i <= 8; i++) {
            try {
                // Espera aleatoria entre clientes
                Thread.sleep((int) (Math.random() * 2000));
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Se produjo un error: ", e);
            }
            hilosClientes[i - 1] = new Thread(new Cliente(i));
            hilosClientes[i - 1].start();
        }

        // Esperar a que todos los clientes terminen
        for (Thread cliente : hilosClientes) {
            try {
                cliente.join(); // Espera a que cada cliente termine
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Se produjo un error: ", e);
            }
        }
        System.out.println("Todos los clientes han sido atendidos o se han ido. Cerrando la peluquería.");
        System.exit(0);
    }

    // Clase Barbero
    static class Barbero implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    // Esperar a que llegue un cliente (dormir)
                    barbero.acquire();

                    // Cortar el cabello del cliente
                    System.out.println("Barbero: Comenzando a cortar el pelo...");
                    Thread.sleep(3000); // Simular tiempo de corte
                    System.out.println("Barbero: Terminé de cortar el pelo.");
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Se produjo un error: ", e);
                } finally {
                    // Liberar la silla del barbero
                    sillaBarbero.release();
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
                // Intentar sentarse en una silla de espera
                if (sillas.tryAcquire()) {
                    System.out.println("Cliente " + id + ": Me senté en una silla de espera.");

                    // Despertar al barbero si está durmiendo
                    barbero.release();

                    // Esperar a que la silla del barbero esté libre
                    sillaBarbero.acquire();
                    sillas.release(); // Dejar la silla de espera

                    System.out.println("Cliente " + id + ": Estoy en la silla del barbero.");
                    Thread.sleep(3000); // Simular tiempo en la silla del barbero
                    System.out.println("Cliente " + id + ": Terminé y me voy.");

                } else {
                    // No hay sillas disponibles, el cliente se va
                    System.out.println("Cliente " + id + ": No hay sillas libres, me voy.");
                }
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Se produjo un error: ", e);
            }
        }
    }
}
