import java.util.*;
import java.util.concurrent.locks.*;

public class Cocina {
    private static final Queue<Orden> pedidos = new LinkedList<>();
    private static final Lock lock = new ReentrantLock();
    private static int platosPreparados = 0;
    private static double gananciasTotales = 0.0;

    public static Queue<Orden> getPedidos() {
        return pedidos;
    }

    public static void aumentarPlatosPreparados() {
        lock.lock();
        try {
            platosPreparados++;
        } finally {
            lock.unlock();
        }
    }

    public static void aumentarGanancias(double precio) {
        lock.lock();
        try {
            gananciasTotales += precio;
        } finally {
            lock.unlock();
        }
    }

    public static int getPlatosPreparados() {
        return platosPreparados;
    }

    public static double getGananciasTotales() {
        return gananciasTotales;
    }
}
