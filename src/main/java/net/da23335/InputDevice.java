package net.da23335;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class InputDevice {
    private RandomAccessFile inputStream;
    private Scanner scanner;
    private ObjectMapper objectMapper;

    public InputDevice(RandomAccessFile inputStream) {
        this.inputStream = inputStream;
        this.scanner = new Scanner(inputStream.getChannel());
        this.objectMapper = new ObjectMapper();
    }

    public Boolean getBoolean() {
        return scanner.nextBoolean();
    }

    public boolean isFileEmpty() {
        try {
            return inputStream.length() == 0;
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }
    }


    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }


    public Total_Inventory deserializeTotalInventory() throws IOException {
        StringBuilder content = new StringBuilder();

        // Read the entire file content
        while (scanner.hasNextLine()) {
            content.append(scanner.nextLine()).append("\n");
        }

        // Convert JSON content to Total_Inventory object
        return objectMapper.readValue(content.toString(), Total_Inventory.class);
    }
}
