package cder.rplace;

public class Driver 
{
    public static void main(String[] args) {
        String baseUrl = "http://localhost:8080";
        String user = "test";
        String password = "foobar";

        // This is a placeholder for the main method.
        // You can implement a simple test or run the Spring Boot application here.
        ClientConnector connector = new ClientConnector(baseUrl, user, password);

        for (int row = 0; row < 100; row++) {
            for (int col = 0; col < 100; col++) {
                String color = "red"; // or any other color you want to test
                boolean success = connector.setColor(user, password, row, col, color);
                if (!success) {
                    System.out.println("Failed to set color at (" + row + ", " + col + ")");
                    return;
                }
            }
        }
    }
    
}
