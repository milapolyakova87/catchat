package org.catchat;
import java.io.*;
import java.net.Socket;
class ServerThread extends Thread {
    public final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final String clientName;

    public ServerThread(Socket socket, String clientName) throws IOException {
        this.socket = socket;
        this.clientName = clientName;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        start();
    }

    @Override
    public void run() {
        String word;
        try {
            while (true) {
                word = in.readLine();
                System.out.println("Клиент прислал сообщение: " + word);
                if (!validateClientMessageAndGetCatFact(word)) {
                    break;
                }
                for (ServerThread smth : Server.clientList) {
                    smth.sendMessage(clientName + ": " + word);
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении сообщения от клиента: " + e.getMessage());
        }
    }

    private void sendMessage(String word) {
        out.println(word);
    }

    private boolean validateClientMessageAndGetCatFact(String message) {
        if (message.equals("stop")) {
            out.println("Вы вышли из чата");
            return false;
        } else if (message.equals("/catfact")) {
            out.println("Забавный котофакт:");
            CatFact catFact = WebApiCatFacts.getFact();
            if (catFact.getFact() != null && !catFact.getFact().isEmpty()) {
                sendMessage(catFact.getFact());
            } else {
                sendMessage("Не удалось получить котофакт.");
            }
        }
        return true;
    }
}
