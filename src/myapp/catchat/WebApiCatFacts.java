package myapp.catchat;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.fasterxml.jackson.databind.ObjectMapper;

public class WebApiCatFacts {
    private static final String API_URL = "https://catfact.ninja/fact";
    private static final String BINARY_FILE_NAME = "catfact.bin";
    private static final String JSON_FILE_NAME = "catfact.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static CatFact getFact() throws Exception {
        SSLContext sslContext = setSSLworkower();

        // Настройка HttpClient
        var client = HttpClient.newBuilder()
                .sslContext(sslContext)
                .sslParameters(new SSLParameters())
                .build();

        var request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Парсинг ответа
        String body = response.body();
        CatFact catFact = objectMapper.readValue(body, CatFact.class);

        // Сохранение объекта в двоичном формате (Java-сериализация)
        saveCatFactAsBinary(catFact, BINARY_FILE_NAME);

        // Сохранение объекта в JSON формате
        saveCatFactAsJson(catFact, JSON_FILE_NAME);

        return catFact;
    }

    private static SSLContext setSSLworkower() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                }
        };

        // Install the all-trusting trust manager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new SecureRandom());
        return sslContext;
    }

    private static void saveCatFactAsBinary(CatFact catFact, String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName, true))) {
            oos.writeObject(catFact);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении котофакта в бинарном формате: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void saveCatFactAsJson(CatFact catFact, String fileName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
            String json = objectMapper.writeValueAsString(catFact);
            bw.write(json);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении котофакта в JSON: " + e.getMessage());
        }
    }
}