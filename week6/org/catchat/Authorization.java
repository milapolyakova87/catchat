package org.catchat;
// Авторизация
import java.io.*;
import java.net.Socket;
public class Authorization {

    private final BufferedReader in;
    private final PrintWriter out;

    private int autCounter = 0;

    public Authorization(Socket socket) throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
    }

    public String autClient() throws IOException {
        String clientName = requestAndValidateName();
        if (clientName == null) {
            clientName = "user"; // Если имя не валидно, то присваиваем имя по умолчанию
            out.println("Вы не прошли авторизацию на сервере, вам присвоено имя " + clientName + autCounter);
            autCounter++;
        } else {
            out.println("Вы успешно авторизовались на сервере с именем " + clientName +". Введите команду /catfact для получения котофакта");
        }
        return clientName;
    }

    private String requestAndValidateName() throws IOException {
        int AUTHORIZATION_ATTEMPTS = 5;

        while (AUTHORIZATION_ATTEMPTS > 0) {
            out.println("Введите свое имя:");
            String name = in.readLine();

            if (isNameValid(name)) {
                return name;
            } else {
                AUTHORIZATION_ATTEMPTS--;
                if (AUTHORIZATION_ATTEMPTS > 0) {
                    out.println("Неверное имя. Попыток осталось: " + AUTHORIZATION_ATTEMPTS);
                }
            }
        }
        return null;
    }

    private boolean isNameValid(String name) {
        return name != null && !name.trim().isEmpty();
    }
}