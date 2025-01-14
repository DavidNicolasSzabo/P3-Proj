package Proj.Server;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import com.fasterxml.jackson.databind.ObjectMapper;
public class OutputDevice {
    private OutputStream outputStream;
    private final ObjectMapper objectMapper;

    public OutputDevice(RandomAccessFile randomAccessFile) throws IOException {
        this.outputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                randomAccessFile.write(b);
            }
        };
        this.objectMapper = new ObjectMapper();
    }

    public void writeSerializedObject(Object obj) {
        try {
            String json = objectMapper.writeValueAsString(obj);
            outputStream.write((json + "\n").getBytes());
            outputStream.flush();
        } catch (IOException e) {
            server_mod.LOGGER.error(e.getMessage());
        }
    }

    public void close() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            server_mod.LOGGER.error(e.getMessage());
        }
    }
}
