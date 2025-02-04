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

    // Cifrado ASCII como números desplazados
    public String cifrarAscii(String mensaje) {
        StringBuilder cifrado = new StringBuilder();
        for (char c : mensaje.toCharArray()) {
            cifrado.append((int) c + 3).append(" ");
        }
        return cifrado.toString().trim();
    }

    // Descifrado ASCII desde números desplazados
    public String descifrarAscii(String mensaje) {
        StringBuilder descifrado = new StringBuilder();
        for (String num : mensaje.split(" ")) {
            descifrado.append((char) (Integer.parseInt(num) - 3));
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
                cifrado.append(c);
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

                    // Alternar cifrado para el reenvío
                    String mensajeCifrado = usaCifradoAscii ? cifrarDesplazamiento(mensajeDescifrado, 3) : cifrarAscii(mensajeDescifrado);
                    enviar(mensajeCifrado);
                    System.out.println("Mensaje cifrado y enviado al siguiente nodo.");
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
            System.out.print("Introduce el mensaje inicial: ");
            String mensaje = scanner.nextLine();

            String mensajeCifrado = usaCifradoAscii ? nodo.cifrarAscii(mensaje) : nodo.cifrarDesplazamiento(mensaje, 3);
            nodo.enviar(mensajeCifrado);
            System.out.println("Mensaje cifrado y enviado al siguiente nodo.");
        } else {
            nodo.escuchar();
        }
    }
}
