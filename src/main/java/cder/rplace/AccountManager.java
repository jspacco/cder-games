package cder.rplace;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component
public class AccountManager 
{
    private static final Logger log = LoggerFactory.getLogger(AccountManager.class);

    private final Map<String, String> accounts = new HashMap<>();

    @PostConstruct
    public void loadAccounts() throws Exception {

        // 1. Try external config folder
        Path externalPath = Paths.get("config/accounts.txt");
        if (Files.exists(externalPath)) {
            try (BufferedReader reader = Files.newBufferedReader(externalPath)) {
                loadFromReader(reader);
                log.info("Loaded accounts from config/accounts.txt");
                return;
            }
        }

        // 2. Fallback to classpath
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/accounts.txt")))) {
            loadFromReader(reader);
            log.info("✅ Loaded accounts from classpath");
        }
    }

    private void loadFromReader(BufferedReader reader) throws Exception {
        String line;
        while ((line = reader.readLine()) != null) {
            // remove comments
            line = line.replaceAll("#.*", ""); 
            line = line.trim();
            // skip empty lines
            if (line.isEmpty()) continue;

            if (!line.contains(":")) continue;
            String[] parts = line.split(":", 2);
            accounts.put(parts[0].trim(), parts[1].trim());
        }
    }


    public boolean isValid(String username, String password) {
        log.trace("Checking credentials for user: " + username);
        return password.equals(accounts.get(username));
    }

    public Set<String> allUsers() {
        return accounts.keySet();
    }
}


