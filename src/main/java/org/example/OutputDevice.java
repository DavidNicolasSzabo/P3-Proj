package org.example;

import java.io.IOException;
import java.io.OutputStream;

public class OutputDevice {
    public OutputDevice(OutputStream out) {
        this.outputStream = out;
    }
    private OutputStream outputStream;
    public void writeMessage(String message) {
        try {
            outputStream.write(message.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
