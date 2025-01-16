import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Menu {
    private HashMap<String, Integer> platos;

    // Constructor del menú
    Menu() {
        this.platos = new HashMap<>();
        this.platos.put("Tostada", 3000);
        this.platos.put("Sopa Instantánea", 5000);
        this.platos.put("Ensalada de Frutas", 2000);
        this.platos.put("Sándwich de Queso", 4000);
        this.platos.put("Huevos Revueltos", 3000);
        this.platos.put("Yogur con Granola", 1000);
        this.platos.put("Batido de Proteínas", 2000);
        this.platos.put("Café Instantáneo", 1000);
        this.platos.put("Avena Instantánea", 5000);
        this.platos.put("Té", 2000);
    }

    // Método para obtener platos aleatorios (pueden repetirse)
    public List<String> platosAleatorios() {
        List<String> pAleatorios = new ArrayList<>();
        List<String> listaPlatos = new ArrayList<>(platos.keySet());
        Random random = new Random();

        //se seleccionan 10 platos
        for (int i = 0; i < 10; i++) {
            int indiceAleatorio = random.nextInt(listaPlatos.size());
            String platoSeleccionado = listaPlatos.get(indiceAleatorio);
            pAleatorios.add(platoSeleccionado);  // Usamos una lista para permitir duplicados
        }

        return pAleatorios;
    }

    // Método para obtener el precio de un plato dado su nombre
    public int getPrecio(String nombrePlato) {
        return platos.getOrDefault(nombrePlato, 0);
    }
}
