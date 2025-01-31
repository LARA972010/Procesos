package org.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class Nodo {

    private int puertoEscucha;
    private String siguienteHost;
    private int siguientePuerto;
    private boolean usaCifradoAscii;

    public Nodo(int puertoEscucha, String siguienteHost, int siguientePuerto, boolean usaCifradoAscii) {
        this.puertoEscucha = puertoEscucha;
        this.siguienteHost = siguienteHost;
        this.siguientePuerto = siguientePuerto;
        this.usaCifradoAscii = usaCifradoAscii;
    }

    // Cifrado ASCII + 3
    public String cifrarAscii(String mensaje) {
        StringBuilder cifrado = new StringBuilder();
        for (char c : mensaje.toCharArray()) {
            cifrado.append((char) (c + 3));
        }
        return cifrado.toString();
    }

    // Descifrado ASCII - 3
    public String descifrarAscii(String mensaje) {
        StringBuilder descifrado = new StringBuilder();
        for (char c : mensaje.toCharArray()) {
            descifrado.append((char) (c - 3));
        }
        return descifrado.toString();
    }

    // Cifrado por desplazamiento
    public String cifrarDesplazamiento(String mensaje, int desplazamiento) {
        StringBuilder cifrado = new StringBuilder();
        for (char c : mensaje.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isLowerCase(c) ? 'a' : 'A';
                cifrado.append((char) ((c - base + desplazamiento) % 26 + base));
            } else {
                cifrado.append(c); // No ciframos los caracteres no alfabéticos
            }
        }
        return cifrado.toString();
    }

    // Descifrado por desplazamiento
    public String descifrarDesplazamiento(String mensaje, int desplazamiento) {
        return cifrarDesplazamiento(mensaje, 26 - desplazamiento);
    }

    // Enviar mensaje a través de socket
    public void enviar(String mensaje) {
        try (Socket socket = new Socket(siguienteHost, siguientePuerto);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(mensaje);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Escuchar mensajes entrantes
    public void escuchar() {
        try (ServerSocket serverSocket = new ServerSocket(puertoEscucha)) {
            while (true) {
                try (Socket socket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String mensaje = in.readLine();
                    String mensajeDescifrado = usaCifradoAscii ? descifrarAscii(mensaje) : descifrarDesplazamiento(mensaje, 3);
                    System.out.println("Mensaje recibido y descifrado: " + mensajeDescifrado);

                    // Preguntar al usuario si desea cifrar y reenviar el mensaje
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("¿Deseas cifrar el mensaje y enviarlo al siguiente nodo? (s/n)");
                    String respuesta = scanner.nextLine();

                    if ("s".equalsIgnoreCase(respuesta)) {
                        System.out.println("Introduce el método de cifrado (1 para ASCII +3, 2 para Desplazamiento): ");
                        int metodo = scanner.nextInt();
                        scanner.nextLine(); // Consumir línea sobrante

                        String mensajeCifrado = "";
                        if (metodo == 1) {
                            mensajeCifrado = cifrarAscii(mensajeDescifrado);
                        } else if (metodo == 2) {
                            System.out.print("Introduce el desplazamiento: ");
                            int desplazamiento = scanner.nextInt();
                            scanner.nextLine(); // Consumir línea sobrante
                            mensajeCifrado = cifrarDesplazamiento(mensajeDescifrado, desplazamiento);
                        }

                        enviar(mensajeCifrado);
                        System.out.println("Mensaje cifrado y enviado al siguiente nodo.");
                    } else {
                        System.out.println("Flujo detenido.");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Introduce el puerto donde este nodo escuchará:");
        int puertoEscucha = scanner.nextInt();

        System.out.println("Introduce la IP del siguiente nodo:");
        String siguienteHost = scanner.next();

        System.out.println("Introduce el puerto del siguiente nodo:");
        int siguientePuerto = scanner.nextInt();

        System.out.println("¿Este nodo usa cifrado ASCII? (true/false):");
        boolean usaCifradoAscii = scanner.nextBoolean();
        scanner.nextLine(); // Consumir línea sobrante

        Nodo nodo = new Nodo(puertoEscucha, siguienteHost, siguientePuerto, usaCifradoAscii);

        System.out.println("¿Quieres introducir un mensaje (1) o escuchar un mensaje (2)?");
        int opcion = scanner.nextInt();
        scanner.nextLine(); // Consumir línea sobrante

        if (opcion == 1) {
            // Iniciar flujo introduciendo un mensaje
            System.out.print("Introduce el mensaje inicial: ");
            String mensaje = scanner.nextLine();

            // Cifrar el mensaje según el método elegido
            String mensajeCifrado = usaCifradoAscii ? nodo.cifrarAscii(mensaje) : nodo.cifrarDesplazamiento(mensaje, 3);
            nodo.enviar(mensajeCifrado);
            System.out.println("Mensaje cifrado y enviado al siguiente nodo.");
        } else {
            // Escuchar mensajes entrantes
            nodo.escuchar();
        }
    }
}
