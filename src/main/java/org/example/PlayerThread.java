package org.example;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class PlayerThread {
    private static int number = 0;
    private int threadNumber;
    private String playerUUID;
    private String playerName;
    private RandomAccessFile file;

    public PlayerThread(String playerUUID, String playerName, RandomAccessFile fileName) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.file = fileName;
        this.threadNumber = number++;
    }

    public void run() {
        InputDevice input = new InputDevice(System.in);
        try {

            OutputDevice output = new OutputDevice(file);
            Application app = new Application(input, output, this.playerUUID, this.playerName);
            app.run();
            System.out.println();
            output.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
