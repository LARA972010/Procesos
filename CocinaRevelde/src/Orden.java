// Orden.java
public class Orden {
    private String nombre;
    private int tiempoPreparacion; // en milisegundos
    private double precio;

    public Orden(String nombre, int tiempoPreparacion, double precio) {
        this.nombre = nombre;
        this.tiempoPreparacion = tiempoPreparacion;
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public int getTiempoPreparacion() {
        return tiempoPreparacion;
    }

    public double getPrecio() {
        return precio;
    }

    @Override
    public String toString() {
        return "Orden: " + nombre + ", Tiempo: " + tiempoPreparacion + " ms, Precio: " + precio + " €";
    }
}
