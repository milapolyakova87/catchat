package myapp.catchat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class Server {
    private static final int PORT = 8080;
    static volatile boolean running = true;
    public static List<ServerThread> clientList = new ArrayList<>();
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        ClientStarter.start();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен");

            // Запуск клиента после успешной авторизации
            executorService.submit(Server::startClient);
            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    Authorization authorization = new Authorization(socket);
                    String clientName = authorization.autClient();
                    if (clientName != null) {
                        ServerThread clientThread = new ServerThread(socket, clientName);
                        clientList.add(clientThread);
                        clientThread.start();
                    }
                } catch (IOException e) {
                    System.err.println("Ошибка при работе с сервером: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при создании сервера: " + e.getMessage());
        } finally {
            executorService.shutdown();
            System.out.println("Сервер остановлен");
        }
    }
    // Метод для остановки сервера
    public static void stopServer() {
        running = false;
    }
    // Метод для запуска клиента
    private static void startClient() {
        try {
            Client.startClient();
        } catch (Exception e) {
            System.err.println("Ошибка при запуске клиента: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
