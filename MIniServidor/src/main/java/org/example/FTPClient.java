package org.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class FTPClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 2121;
    private static final String DOWNLOAD_DIR = "downloads";

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            //creamos el directorio si no existe
            File downloadDir = new File(DOWNLOAD_DIR);
            if (!downloadDir.exists()) {
                downloadDir.mkdir();
            }

            //Inicio de usuario
            System.out.println(in.readLine());
            String username = scanner.nextLine();
            out.println(username);
            System.out.println(in.readLine());
            String password = scanner.nextLine();
            out.println(password);

            //mostrar la respuesta:
            System.out.println(in.readLine());

            String command;
            while (true) {
                System.out.print("ftp> ");
                command = scanner.nextLine();
                out.println(command);

                if (command.toUpperCase().startsWith("STOR")) {
                    String fileName = command.substring(5).trim();
                    File file = new File(fileName);
                    if (!file.exists()) {
                        System.out.println("File not found!");
                        continue;
                    }

                    //esperamos al servidor
                    if (in.readLine().equals("READY")) {
                        int dataPort = Integer.parseInt(in.readLine());
                        try (Socket dataSocket = new Socket(SERVER_ADDRESS, dataPort);
                             DataOutputStream dos = new DataOutputStream(dataSocket.getOutputStream());
                             BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {

                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = bis.read(buffer)) != -1) {
                                dos.write(buffer, 0, bytesRead);
                            }
                        }
                        System.out.println(in.readLine());
                    }
                } else if (command.toUpperCase().startsWith("RETR")) {
                    String fileName = command.substring(5).trim();
                    if (in.readLine().equals("READY")) {
                        int dataPort = Integer.parseInt(in.readLine());
                        try (Socket dataSocket = new Socket(SERVER_ADDRESS, dataPort);
                             DataInputStream dis = new DataInputStream(dataSocket.getInputStream());
                             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(DOWNLOAD_DIR + "/" + fileName))) {

                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = dis.read(buffer)) != -1) {
                                bos.write(buffer, 0, bytesRead);
                            }
                        }
                        System.out.println("Descarga completada. Guardada en " + DOWNLOAD_DIR + "/" + fileName);
                    }
                } else if (command.equalsIgnoreCase("EXIT")) {
                    System.out.println(in.readLine());
                    break;
                } else {
                    String serverResponse;
                    while ((serverResponse = in.readLine()) != null) {
                        System.out.println(serverResponse);
                        if (serverResponse.equals("Fin")) break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
