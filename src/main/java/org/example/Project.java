package org.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Project implements ModInitializer {
    public static final String MOD_ID = "Project";

    // This logger is used to write text to the console and the log file.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Override
    public void onInitialize() {
        System.out.println("Mod initialized!");
        NameMC nameMC = new NameMC();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            Path gameDir = FabricLoader.getInstance().getGameDir();
            Path worldsDir = gameDir.resolve("saves");
            File worldFolder = worldsDir.toFile();
            File customFolder = new File(worldFolder, "PlayersInvLogs");
            File playerdataFolder = new File(worldFolder, "playerdata");
            if (!customFolder.exists()) {
                boolean created = customFolder.mkdirs();
                if (created) {
                    if (!playerdataFolder.exists()) {
                        System.err.println("PlayersInvLogs folder doesn't exist!");
                    } else {
                        for (File file : playerdataFolder.listFiles()) {
                            if (file.isFile() && file.getName().matches("^[^/\\\\]+\\.dat$")) {
                                String baseName = file.getName().substring(0, file.getName().lastIndexOf(".dat"));
                                File newFile = new File(customFolder, baseName + ".dat");
                                try (RandomAccessFile randomAccessFile = new RandomAccessFile(newFile, "rw")) {
                                    String playername = nameMC.getUsernameFromUUID(baseName);
                                    PlayerThread newThread = new PlayerThread(baseName, playername, randomAccessFile);
                                    newThread.run();
                                } catch (IOException e) {
                                    System.err.println("An error occurred while creating the file: " + e.getMessage());
                                }
                            }
                        }
                    }
                } else {
                    System.err.println("Failed to create custom folder: " + customFolder.getAbsolutePath());
                }
            } else {
                System.out.println("Custom folder already exists: " + customFolder.getAbsolutePath());
            }
        });
    }
    /*
                          TCP SERVER
                          SOCKET()
                          BIND()
                          LISTEN()
                          ACCEPT()
      TCP CLIENT
      SOCKET()
      CONNECT()
      WRITE()             READ()
                          WRITE()
      READ()
      CLOSE               READ()
                          CLOSE()
     */
}
