package org.catchat;
// Серверный класс
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PORT = 8080;
    private static volatile boolean running = true; // Флаг для управления состоянием сервера
    public static List<ServerThread> clientList = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен");

            // Запуск клиента в отдельном потоке
            new Thread(Client::startClient).start();

            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    Authorization authorization = new Authorization(socket);
                    String clientName = authorization.autClient();
                    ServerThread clientThread = new ServerThread(socket, clientName);
                    clientList.add(clientThread);
                } catch (IOException e) {
                    System.err.println("Ошибка при работе с сервером: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при создании сервера: " + e.getMessage());
        } finally {
            System.out.println("Сервер остановлен");
        }
    }

    // Метод для остановки сервера
    public static void stopServer() {
        running = false;
    }
}