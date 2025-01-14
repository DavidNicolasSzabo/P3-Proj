package Proj.Server;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;
import com.fasterxml.jackson.databind.ObjectMapper;
public class InputDevice{
    private RandomAccessFile inputStream;
    private Scanner scanner;
    private ObjectMapper objectMapper;
    public InputDevice(RandomAccessFile inputStream){
        this.inputStream = inputStream;
        scanner = new Scanner(inputStream.getChannel());
        this.objectMapper = new ObjectMapper();
    }
    public Boolean getBoolean()
    {
        Boolean info = scanner.nextBoolean();
        return info;
    }
    public boolean isFileEmpty()  {
        try {
            return inputStream.length() == 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }
    public Total_Inventory deserializeTotalInventory() throws IOException {
        StringBuilder content = new StringBuilder();
        while (scanner.hasNextLine()) {
            content.append(scanner.nextLine()).append("\n");
        }

        // Use Jackson's ObjectMapper to convert JSON to Total_Inventory
        return objectMapper.readValue(content.toString(), Total_Inventory.class);
    }
}
