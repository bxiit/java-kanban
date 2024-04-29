package handler;

import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.time.LocalDateTime;

import static java.nio.charset.StandardCharsets.UTF_8;

public class BaseHttpHandler {
    protected final GsonBuilder gsonBuilder;
    protected final Gson gson;

    public BaseHttpHandler() {
        this.gsonBuilder = new GsonBuilder();
        this.gson = gsonBuilder
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .setPrettyPrinting()
                .create();
    }

    public void sendText(HttpExchange h, int code, String text) {
        byte[] bytes = text.getBytes(UTF_8);

        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");

        try {
            h.sendResponseHeaders(200, bytes.length);
            OutputStream os = h.getResponseBody();
            os.write(bytes);
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendNotFound(HttpExchange h) {
        try {
            h.sendResponseHeaders(404, 0);
            h.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendHasInteractions(HttpExchange h) {
        try {
            h.sendResponseHeaders(406, 0);
            h.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected HttpMethods getMethod(HttpExchange h) {
        return HttpMethods.valueOf(h.getRequestMethod());
    }
}
