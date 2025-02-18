package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class Controlador {

    @Autowired
    private Servicio servicio;

    // Endpoint para obtener una respuesta aleatoria
    @GetMapping("/pregunta")
    public Respuesta obtenerRespuesta(@RequestParam String pregunta) {
        return servicio.obtenerRespuesta(pregunta);
    }

    // Endpoint para obtener el historial de llamadas
    @GetMapping("/historial")
    public List<String> obtenerHistorial() {
        return servicio.obtenerHistorial();
    }

    // Endpoint para obtener las categorías disponibles
    @GetMapping("/categorias")
    public List<String> obtenerCategorias() {
        return servicio.obtenerCategorias();
    }

    // Endpoint para enviar una nueva pregunta (POST)
    @PostMapping("/pregunta")
    public Respuesta procesarPregunta(@RequestBody Pregunta pregunta) {
        return servicio.obtenerRespuesta(pregunta.getPregunta());
    }
}
