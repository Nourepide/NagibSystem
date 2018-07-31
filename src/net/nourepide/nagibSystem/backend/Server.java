package net.nourepide.nagibSystem.backend;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.Vector;

public class Server {
    Vector<Client> clients = new Vector<>();
    private ServerSocket serverSocket;

    private Server() {
        try {
            serverSocket = new ServerSocket(7820);
            System.out.println("Server started");
            new Thread(() -> {
                while (true) {
                    try {
                        clients.add(new Client(serverSocket.accept(), this));
                        System.out.println("Client connected");
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }).start();

              Scanner scanner = new Scanner(System.in);

              while(true) {
                  String message = scanner.nextLine();

                  for (Client client : clients) {
                      client.sendMessage("Server", message);
                  }
              }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
