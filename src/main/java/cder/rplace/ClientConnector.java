package cder.rplace;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

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

    public boolean setColor(String user, String password, int row, int col, String color)
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
            } else {
                // System.out.println("Failed to set color: " + response.body());
                // System.out.println("Status code: " + response.statusCode());
                // System.out.println("URL: " + url);
                return false;
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}