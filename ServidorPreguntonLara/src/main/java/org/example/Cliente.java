package org.example;

import java.io.*;
import java.net.*;

public class Cliente {
    public static void main(String[] args) throws IOException {
        String host = "localhost";
        int puerto = 1234;

        try (Socket socket = new Socket(host, puerto);
             DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
             DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()))) {

            String mensaje;
            while (true) {
                //recibe mensajes del servidor:
                mensaje = in.readUTF();
                System.out.println(mensaje);

                //esperamos el mensaje del cliente:
                if (mensaje.contains("¿Cuál es tu suposición?")) {
                    System.out.print("Tu suposición: ");
                    int suposicion = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
                    out.writeInt(suposicion);
                    out.flush();
                }

                //si el cliente no quiere jugar de nuevo terminamos el juego:
                if (mensaje.contains("¿Quieres jugar otra vez?")) {
                    String respuesta = new BufferedReader(new InputStreamReader(System.in)).readLine();
                    out.writeUTF(respuesta);
                    out.flush();

                    if (respuesta.trim().equalsIgnoreCase("no")) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
