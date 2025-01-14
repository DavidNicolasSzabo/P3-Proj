package Proj.Server;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CSVUtils {

    public static void saveMapToCSV(Map<String, String> map, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Key,Value");
            writer.newLine();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Map<String, String> loadMapFromCSV(String filePath) {
        Map<String, String> map = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    map.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
}
