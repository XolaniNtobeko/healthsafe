package za.co.wethinkcode.healthsafe;

import io.javalin.Javalin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTest {
    private Javalin app;
    private int port;

    @BeforeEach
    public void setUp() {
        // Pass 0 to Javalin to automatically bind to any available free port
        app = Javalin.create().start(0);
        port = app.port(); // Retrieve the dynamically assigned port

        app.get("/api/vitals", ctx -> ctx.result("HealthSafe Vitals Service Active"));
        app.post("/api/vitals", ctx -> {
            ctx.status(201).result("Vital alert queued: " + ctx.body());
        });
    }

    @AfterEach
    public void tearDown() {
        if (app != null) {
            app.stop();
        }
    }

    @Test
    public void testGetVitalsEndpoint() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/vitals"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("HealthSafe Vitals Service Active", response.body());
    }

    @Test
    public void testPostVitalsEndpoint() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String jsonPayload = "{\"patientId\": \"P101\", \"heartRate\": 140}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/vitals"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }
}