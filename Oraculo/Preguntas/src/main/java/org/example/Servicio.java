package org.example;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class Servicio {

    private List<String> historial = new ArrayList<>();
    private Random rand = new Random();

    // Método para obtener una respuesta basada en palabras clave en la pregunta
    public Respuesta obtenerRespuesta(String pregunta) {
        historial.add(pregunta);

        // Respuesta que se va a devolver (con la categoría)
        Respuesta respuesta = new Respuesta();
        respuesta.setPregunta(pregunta);

        // Lógica para determinar la respuesta basada en la pregunta
        if (pregunta.toLowerCase().contains("tiempo")) {
            String[] respuestasTiempo = {"Hace buen tiempo", "Está lloviendo", "Hace frío", "Está soleado"};
            respuesta.setCategoria("tiempo");
            respuesta.setRespuestas(List.of(obtenerRespuestaAleatoria(respuestasTiempo)));
        } else if (pregunta.toLowerCase().contains("comida")) {
            String[] respuestasComida = {"La comida está buena", "La comida está mala", "Está un poco pasada", "Está deliciosa"};
            respuesta.setCategoria("comida");
            respuesta.setRespuestas(List.of(obtenerRespuestaAleatoria(respuestasComida)));
        } else if (pregunta.toLowerCase().contains("fútbol") || pregunta.toLowerCase().contains("jugador")) {
            respuesta.setCategoria("fútbol");
            respuesta.setRespuestas(List.of("¡El bicho siuuu!"));
        } else if (pregunta.toLowerCase().contains("nota")) {
            int nota = rand.nextInt(10) + 1; // Nota aleatoria entre 1 y 10
            respuesta.setCategoria("nota");
            respuesta.setRespuestas(List.of("Tu nota es: " + nota));
        } else {
            String[] respuestasGenericas = {"Sí", "No", "Tal vez", "Definitivamente sí", "Definitivamente no"};
            respuesta.setCategoria("general");
            respuesta.setRespuestas(List.of(obtenerRespuestaAleatoria(respuestasGenericas)));
        }

        return respuesta;
    }

    // Método para obtener una respuesta aleatoria de un array de respuestas
    private String obtenerRespuestaAleatoria(String[] respuestas) {
        int index = rand.nextInt(respuestas.length);
        return respuestas[index];
    }

    // Método para obtener el historial de preguntas
    public List<String> obtenerHistorial() {
        return historial;
    }

    // Método para obtener las categorías disponibles
    public List<String> obtenerCategorias() {
        List<String> categorias = new ArrayList<>();
        categorias.add("tiempo");
        categorias.add("comida");
        categorias.add("fútbol");
        categorias.add("nota");
        categorias.add("general");
        return categorias;
    }
}
