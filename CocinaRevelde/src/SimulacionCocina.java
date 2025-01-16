import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SimulacionCocina {
    public static void main(String[] args) throws InterruptedException {
        Menu menu = new Menu();

        //pillamos 10 platos aleatorios para poder hacer pruebas con diferentes precios:
        List<String> platosAleatorios = menu.platosAleatorios();

        if (platosAleatorios.size() < 10) {
            System.out.println("No hay suficientes platos aleatorios para procesar las órdenes.");
            return;
        }

        //liosta con precios y platos aleatorios:
        List<Orden> listaOrdenes = Arrays.asList(
                new Orden(platosAleatorios.get(0), menu.getPrecio(platosAleatorios.get(0)), 8.5),
                new Orden(platosAleatorios.get(1), menu.getPrecio(platosAleatorios.get(1)), 10.0),
                new Orden(platosAleatorios.get(2), menu.getPrecio(platosAleatorios.get(2)), 5.5),
                new Orden(platosAleatorios.get(3), menu.getPrecio(platosAleatorios.get(3)), 6.0),
                new Orden(platosAleatorios.get(4), menu.getPrecio(platosAleatorios.get(4)), 7.0),
                new Orden(platosAleatorios.get(5), menu.getPrecio(platosAleatorios.get(5)), 4.5),
                new Orden(platosAleatorios.get(6), menu.getPrecio(platosAleatorios.get(6)), 9.0),
                new Orden(platosAleatorios.get(7), menu.getPrecio(platosAleatorios.get(7)), 6.5),
                new Orden(platosAleatorios.get(8), menu.getPrecio(platosAleatorios.get(8)), 7.5),
                new Orden(platosAleatorios.get(9), menu.getPrecio(platosAleatorios.get(9)), 8.0)
        );

        //órdenes a la cola
        synchronized (Cocina.getPedidos()) {
            Cocina.getPedidos().addAll(listaOrdenes);
        }

        //creamos cocineros
        Cocinero[] cocineros = new Cocinero[3];
        for (int i = 0; i < cocineros.length; i++) {
            cocineros[i] = new Cocinero(i + 1);
            cocineros[i].start();
        }

        //crea jefe cocina
        JefeDeCocina jefe = new JefeDeCocina();
        jefe.start();

        //esperamos a que terminen todos los cocineros con el join
        for (Cocinero cocinero : cocineros) {
            cocinero.join();
        }
        jefe.join();
    }
}
