package cder.rplace;


import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@Component
public class AccountManager 
{
    private final Map<String, String> accounts = new HashMap<>();

    @PostConstruct
    public void loadAccounts() throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/accounts.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains(":")) continue;
                String[] parts = line.split(":", 2);
                accounts.put(parts[0].trim(), parts[1].trim());
            }
        }
    }

    public boolean isValid(String username, String password) {
        //System.out.println("Checking credentials for user: " + username);
        return password.equals(accounts.get(username));
    }

    public Set<String> allUsers() {
        return accounts.keySet();
    }
}


