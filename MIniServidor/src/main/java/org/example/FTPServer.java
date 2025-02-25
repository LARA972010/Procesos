package org.example;

import java.io.*;
import java.net.*;

public class FTPServer {
    private static final int PORT = 2121;
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "1234";
    private static final String SERVER_DIR = "server_files";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("FTP Server started on port " + PORT);
            File dir = new File(SERVER_DIR);
            if (!dir.exists()) dir.mkdir();

            while (true) { // Espera conexiones entrantes
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Autenticación
                out.println("Username: ");
                String username = in.readLine();
                out.println("Password: ");
                String password = in.readLine();

                // Verificar usuario y contraseña
                if (USERNAME.equals(username) && PASSWORD.equals(password)) {
                    out.println("Authentication Successful");
                } else {
                    out.println("Authentication Failed");
                    socket.close();
                    return;
                }

                String command;
                while ((command = in.readLine()) != null) {

                    if (command.equalsIgnoreCase("LIST")) {
                        listFiles();
                    } else if (command.startsWith("STOR")) {
                        uploadFile(command.substring(5).trim());
                    } else if (command.startsWith("RETR")) {
                        downloadFile(command.substring(5).trim());
                    } else if (command.startsWith("DELE")) {
                        deleteFile(command.substring(5).trim());
                    } else if (command.equalsIgnoreCase("EXIT")) {
                        out.println("Goodbye!");
                        break;
                    } else {
                        out.println("Invalid Command");
                    }
                }

                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void listFiles() {
            File dir = new File(SERVER_DIR);
            String[] files = dir.list();
            if (files != null && files.length > 0) {
                for (String file : files) {
                    out.println(file);
                }
            } else {
                out.println("No files found.");
            }
        }

        private void uploadFile(String fileName) throws IOException {
            out.println("READY");
            try (ServerSocket dataSocket = new ServerSocket(0)) {
                out.println(dataSocket.getLocalPort());
                try (Socket transferSocket = dataSocket.accept();
                     BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(SERVER_DIR + "/" + fileName));
                     DataInputStream dis = new DataInputStream(transferSocket.getInputStream())) {

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = dis.read(buffer)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                    }
                    out.println("Upload complete");
                }
            }
        }

        private void downloadFile(String fileName) throws IOException {
            File file = new File(SERVER_DIR + "/" + fileName);
            if (!file.exists()) {
                out.println("File not found");
                return;
            }

            out.println("READY");
            try (ServerSocket dataSocket = new ServerSocket(0)) {
                out.println(dataSocket.getLocalPort());
                try (Socket transferSocket = dataSocket.accept();
                     BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                     DataOutputStream dos = new DataOutputStream(transferSocket.getOutputStream())) {

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        dos.write(buffer, 0, bytesRead);
                    }
                    out.println("Download complete");
                }
            }
        }

        private void deleteFile(String fileName) {
            File file = new File(SERVER_DIR + "/" + fileName);
            if (file.exists() && file.delete()) {
                out.println("File deleted successfully");
            } else {
                out.println("File not found or failed to delete");
            }
        }
    }
}
