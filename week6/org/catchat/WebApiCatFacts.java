package org.catchat;
// Класс для получения котофакта из внешнего API
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.*;

public class WebApiCatFacts {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static CatFact getFact() {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://catfact.ninja/fact"))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            CatFact catFact = objectMapper.readValue(response.body(), CatFact.class);

            // Сохранение объекта в двоичном формате (Java-сериализация)
            saveCatFactAsBinary(catFact, "catfacts.dat");

            // Сохранение объекта в JSON формате
            saveCatFactAsJson(catFact, "catfacts.json");

            return catFact;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch cat fact: " + e.getMessage(), e);
        }
    }

    private static void saveCatFactAsBinary(CatFact catFact, String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName, true))) {
            oos.writeObject(catFact);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении котофакта в бинарном формате: " + e.getMessage());
        }
    }

    private static void saveCatFactAsJson(CatFact catFact, String fileName) {
        try (FileWriter fw = new FileWriter(fileName, true)) {
            fw.write(objectMapper.writeValueAsString(catFact));
            fw.write("\n");
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении котофакта в JSON: " + e.getMessage());
        }
    }
}