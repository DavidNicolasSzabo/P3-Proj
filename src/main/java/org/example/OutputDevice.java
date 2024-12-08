package org.example;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class OutputDevice {
    private OutputStream outputStream;

    // New constructor to handle RandomAccessFile as an OutputStream
    public OutputDevice(RandomAccessFile randomAccessFile) throws IOException {
        this.outputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                randomAccessFile.write(b);
            }
        };
    }

    public void writeMessage(String message) {
        try {
            outputStream.write(message.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
