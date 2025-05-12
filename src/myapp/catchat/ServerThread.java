package myapp.catchat;
// Класс для обработки сообщений от клиента
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class ServerThread extends Thread {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final String clientName;
    public ServerThread(Socket socket, String clientName) throws IOException {
        this.socket = socket;
        this.clientName = clientName;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }
    @Override
    public void run() {
        String message;
        try {
            while (Server.running && (message = in.readLine()) != null) {
                System.out.println("Клиент " + clientName + " прислал сообщение: " + message);
                if (!processClientMessage(message)) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении сообщения от клиента: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Ошибка при закрытии сокета: " + e.getMessage());
            }
            Server.clientList.remove(this);
            System.out.println("Клиент " + clientName + " отключился.");
        }
    }
    private boolean processClientMessage(String message) {
        if (message.equalsIgnoreCase("stop")) {
            out.println("Вы вышли из чата.");
            return false;
            /*
            Интеграция с Web API для фактов о котах
             */
        } else if (message.equalsIgnoreCase("/catfact")) {
            out.println("Забавный котофакт:");
            try {
                CatFact catFact = WebApiCatFacts.getFact();
                /*
                Рассылка факта:
                После получения факта о коте сервер отправляет его всем подключенным клиентам.
                 */
                if (catFact != null && catFact.getFact() != null) {
                    out.println(catFact.getFact());
                } else {
                    out.println("Не удалось получить котофакт.");
                }
            } catch (Exception e) {
                out.println("Произошла ошибка при получении котофакта: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            broadcastMessage(clientName + ": " + message);
        }
        return true;
    }

    /*
    Отправка сообщения всем подключенным клиентам
     */
    private void broadcastMessage(String message) {
        for (ServerThread client : Server.clientList) {
            if (client != this) {
                client.sendMessage(message);
            }
        }
    }
    private void sendMessage(String message) {
        out.println(message);
    }
}