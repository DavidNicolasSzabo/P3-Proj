package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NameMC {

    // The URL to fetch player info
    private static final String API_URL = "https://api.mojang.com/users/profiles/minecraft/";

    public static String getUsernameFromUUID(String uuid) {
        try {
            // Construct the URL to query the Mojang API with UUID
            URL url = new URL(API_URL + uuid);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // Read the response from the input stream
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            // Close the stream
            in.close();
            connection.disconnect();

            // Parse the JSON response using Jackson
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(content.toString());
            String username = jsonResponse.get("name").asText();  // Access the "name" field

            return username;

        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Return null if there was an error
        }
    }

    public static void main(String[] args) {
        String uuid = "your-player-uuid-here"; // Replace with actual UUID
        String username = getUsernameFromUUID(uuid);

        if (username != null) {
            System.out.println("The player's name is: " + username);
        } else {
            System.out.println("Player not found or an error occurred.");
        }
    }
}
