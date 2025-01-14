package Proj.Server;



import net.minecraft.server.network.ServerPlayerEntity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;

public class PlayerThread implements Runnable {

    private String playerUUID;
    private String playerName;
    private RandomAccessFile file;
    private ServerPlayerEntity player;
    private boolean isServerRunning;
    private Collection<PlayerThread> threads ;
    private Application app;
    public PlayerThread(String playerUUID, String playerName, RandomAccessFile fileName, boolean isServerRunning,Collection<PlayerThread> threads) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.isServerRunning = isServerRunning;
        this.file = fileName;
        this.threads = threads;
    }
    public void run() {

        synchronized (this) {
            try {
                InputDevice input = new InputDevice(file);
                OutputDevice output = new OutputDevice(file);
                app = new Application(input, output, this.playerUUID, this.playerName, this.player,this);
                app.run();
                if(isServerRunning== false) {
                    app.setServerstatus(isServerRunning);
                    output.close();
                }

            } catch (FileNotFoundException e) {
                server_mod.LOGGER.error(e.getMessage());
            } catch (IOException e) {
                server_mod.LOGGER.error(e.getMessage());
            }
        }
    }
    public synchronized ServerPlayerEntity getPlayer() {
        return player;
    }
    public synchronized String getPlayerUUID() {
        return playerUUID;
    }
    public synchronized Collection<PlayerThread> getThreads() {
        return this.threads;
    }
    public synchronized Application getApplication() {
        return this.app;
    }
}
