package cder.rplace;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientConnector
{

    private final String baseUrl;
    private final String user;
    private final String password;
    private final HttpClient client;
    private final String params;

    public ClientConnector(String baseUrl, String user, String password)
    {
        this.baseUrl = baseUrl;
        this.user = user;
        this.password = password;
        this.client = HttpClient.newHttpClient();
        this.params = "?user=" + user + "&password=" + password;
    }

    private Map<String, Object> parseErrorResponse(String json) {
        try {
            // Gson for the student client
            // Gson gson = new Gson();
            // Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            // Map<String, Object> result = gson.fromJson(json, mapType);
            
            // Jackson ObjectMapper
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> result = mapper.readValue(json, Map.class);
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse error response", e);
        }
    }

    public boolean setColor(int row, int col, String color)
    {
        String url = baseUrl + "/rplace/setColor" + params + "&row=" + row + "&col=" + col + "&color=" + color;
        // Make HTTP request to set color
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();
        //System.out.println("Request: " + request);
        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return true;
            }
            // Handle error responses
            // parse the response body for error details
            Map<String, Object> errorDetails = parseErrorResponse(response.body());
            String errorMessage = (String) errorDetails.get("error");
            int errorCode = response.statusCode();

            System.err.println("Error setting color at (" + row + ", " + col + ")");
            System.err.println("Error message: " + errorMessage);
            System.err.println("Error code: " + errorCode);

            return false;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getTimeToNextPixel()
    {
        String url = baseUrl + "/rplace/countdown" + params;
        // Make HTTP request to get next pixel time
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();
        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return Integer.parseInt(response.body());
            }
            // Handle error responses
            Map<String, Object> errorDetails = parseErrorResponse(response.body());
            String errorMessage = (String) errorDetails.get("error");
            int errorCode = response.statusCode();

            System.err.println("Error getting next pixel time for user " + user);
            System.err.println("Error message: " + errorMessage);
            System.err.println("Error code: " + errorCode);

            return -1;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getStats()
    {
        String url = baseUrl + "/rplace/stats" + params;
        // Make HTTP request to get stats
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();
        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            }
            // Handle error responses
            Map<String, Object> errorDetails = parseErrorResponse(response.body());
            String errorMessage = (String) errorDetails.get("error");
            int errorCode = response.statusCode();

            System.err.println("Error getting stats for user " + user);
            System.err.println("Error message: " + errorMessage);
            System.err.println("Error code: " + errorCode);

            return null;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}