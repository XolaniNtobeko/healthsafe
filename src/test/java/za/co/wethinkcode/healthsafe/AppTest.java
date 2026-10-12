package za.co.wethinkcode.healthsafe;

import io.javalin.Javalin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTest {
    private static Javalin app;
    private static final int TEST_PORT = 7001;

    @BeforeAll
    public static void setUp() {
        // We start a basic app, but we haven't implemented the POST /api/vitals route yet!
        app = Javalin.create().start(TEST_PORT);
        app.get("/api/vitals", ctx -> ctx.result("HealthSafe Vitals Service Active"));
    }

    @AfterAll
    public static void tearDown() {
        if (app != null) {
            app.stop();
        }
    }

    @Test
    public void testGetVitalsEndpoint() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + TEST_PORT + "/api/vitals"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("HealthSafe Vitals Service Active", response.body());
    }

    @Test
    public void testPostVitalsShouldQueueMessage() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String jsonPayload = "{\"patientId\": \"P101\", \"heartRate\": 140, \"status\": \"CRITICAL\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + TEST_PORT + "/api/vitals"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // This will fail because POST /api/vitals is not defined yet (will return 404 Not Found)
        assertEquals(201, response.statusCode());
    }
}