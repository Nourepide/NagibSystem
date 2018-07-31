package net.nourepide.nagibSystem.backend;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

class Client {
    private String nickname = "UNREGISTERED";

    private Server server;

    private DataInputStream input;
    private DataOutputStream output;

    Client(Socket socket, Server server) throws IOException {
        this.server = server;
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());

        new Thread(() -> {

            try {
                while (true) {
                    String message = input.readUTF();

                    if (message.equals("!exit")) {
                        server.clients.remove(this);
                        break;
                    }

                    if (message.contains("!nickname")) {
                        String[] strings = message.split(" ", 2);
                        String rawNickname = strings[1];

                        boolean isAvailable = true;

                        for (Client client : server.clients) {
                            if (client.nickname.equals(rawNickname)) {
                                output.writeUTF("!nickname" + " " + "denied");
                                isAvailable = false;
                                break;
                            }
                        }

                        if (isAvailable) {
                            output.writeUTF("!nickname" + " " + "confirmed");
                            nickname = rawNickname;
                        }

                        continue;
                    }

                    if (message.indexOf("!w ") == 0 || message.indexOf("/w ") == 0) {
                        String messagePrivate;

                        messagePrivate = message.replace("!w ", "");
                        messagePrivate = messagePrivate.replace("/w ", "");

                        String[] strings = messagePrivate.split(" ", 2);

                        if (strings.length != 2) {
                            continue;
                        }

                        String nicknameUser = strings[0];
                        String messageUser = strings[1];

                        sendMessagePrivate(nicknameUser, messageUser);
                        continue;
                    }

                    if (!message.equals("")) {
                        sendMessageMass(message);
                        System.out.println(nickname + " " + message);
                    }
                }

            } catch (IOException e) {
                System.out.println(nickname + " disconnected");
            } finally {
                server.clients.remove(this);

                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }).start();
    }

    void sendMessage(String nickname, String message) {
        try {
            output.writeUTF(nickname + " " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageMass(String message) {
        Object clone = server.clients.clone();

        if (clone instanceof Vector) {

            ((Vector) clone).remove(this);

            for (Client client : (Vector<Client>) clone) {
                client.sendMessage(nickname, message);
            }
        }
    }

    private void sendMessagePrivate(String nickname, String message) {
        Client clientFounded = null;

        for (Client client : server.clients) {
            if (client.nickname.equals(nickname)) {
                clientFounded = client;
                break;
            }
        }

        if (clientFounded != null) {
            clientFounded.sendMessage(this.nickname, message);
        } else {
            sendMessage("Server", "User not found");
        }
    }
}
