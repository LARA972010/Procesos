package org.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor {
    public static void main(String[] args) throws IOException {
        int puerto = 1234;
        ServerSocket serverSocket = new ServerSocket(puerto);
        System.out.println("Servidor iniciado en el puerto " + puerto);

        // Aceptar un cliente
        try (Socket clientSocket = serverSocket.accept();
             DataInputStream in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
             DataOutputStream out = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()))) {

            boolean jugar = true;

            while (jugar) {
                int numeroSecreto = new Random().nextInt(100) + 1;
                int intentos = 0;
                boolean acertado = false;


                out.writeUTF("¿En que número estoy pensando?¡Pista: el numero está entre 1 y 100!");
                out.flush();

                while (!acertado) {
                    out.writeUTF("¿Qué número crees que es?");
                    out.flush();

                    int suposicion = in.readInt();
                    intentos++;

                    if (suposicion < numeroSecreto) {
                        out.writeUTF("El número es mayor. Intenta de nuevo.");
                    } else if (suposicion > numeroSecreto) {
                        out.writeUTF("El número es menor. Intenta de nuevo.");
                    } else {
                        out.writeUTF("¡Felicidades! Adivinaste el número en " + intentos + " intentos.");
                        acertado = true;
                    }
                    out.flush();
                }

                // Preguntar si el jugador quiere jugar de nuevo
                out.writeUTF("¿Quieres jugar otra vez? (si/no)");
                out.flush();
                String respuesta = in.readUTF().trim().toLowerCase();

                if (respuesta.equals("no")) {
                    jugar = false;
                    out.writeUTF("Gracias por jugar. ¡Hasta luego!");
                    out.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            serverSocket.close();
        }
    }
}
