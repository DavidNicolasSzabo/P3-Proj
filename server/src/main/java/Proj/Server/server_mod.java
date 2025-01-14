package Proj.Server;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class server_mod implements DedicatedServerModInitializer {
    public static final String MOD_ID = "Storage Logging";
    private final Map<String, PlayerThread> activeThreads = new HashMap<>();
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private final Collection<PlayerThread> threads = new ArrayList<>();
    private final Lock fileLock = new ReentrantLock();
    private volatile boolean isServerRunning = false;
    private File customFolder;
    public static volatile Map<String,String > players = new HashMap<>();
    public Collection<PlayerThread> getThreads() {
        return threads;
    }
    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTING.register(server-> {
            Path gameDir = FabricLoader.getInstance().getGameDir();
            Path worldsDir = gameDir.resolve("saves");
            File worldFolder = worldsDir.toFile();
            customFolder = new File(worldFolder, "PlayersInvLogs");
            synchronized (fileLock) {
                if (!customFolder.exists()) {
                    try {
                        boolean created = customFolder.mkdirs();
                        if (!created) {
                            LOGGER.error("Failed to create custom folder.");
                        }
                    } catch (Exception e) {
                        LOGGER.error("Failed to create custom folder", e);
                    }
                }
            }
            File Players = new File(customFolder, "Players.csv");

            if (Players.exists()) {
                players= CSVUtils.loadMapFromCSV(Players.getPath());
            } else {
                try{
                    if(Players.createNewFile()){}
                    else{
                        LOGGER.error("Failed to create Players.csv");
                    }
                }catch(IOException e){
                    LOGGER.error("Failed to create Players.csv", e);
                }
            }

            setupPlayerThreads(server);
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            isServerRunning = true;
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            if (isServerRunning) {
                if (customFolder.exists() && customFolder == null) {
                    onPlayerJoin(player);
                }
            }
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            isServerRunning = false;
            Path gameDir = FabricLoader.getInstance().getGameDir();
            Path worldsDir = gameDir.resolve("saves");
            File worldFolder = worldsDir.toFile();
            customFolder = new File(worldFolder, "PlayersInvLogs");
            File Players = new File(customFolder, "Players.csv");
            CSVUtils.saveMapToCSV(players, Players.getPath());

            try {
                wait(120000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {

        });
    }


    private void onPlayerJoin(ServerPlayerEntity player) {
        String uuid = player.getGameProfile().getId().toString();
        String name = player.getName().getString();
        players.put(uuid, name);
        synchronized (threads) {
            if (!activeThreads.containsKey(uuid)) {
                File newFile = new File(customFolder, uuid + ".json");
                try (RandomAccessFile randomAccessFile = new RandomAccessFile(newFile, "rw")) {
                    PlayerThread newThread = new PlayerThread(uuid, name, randomAccessFile, isServerRunning, this.threads);
                    activeThreads.put(uuid, newThread);
                    threads.add(newThread);
                    newThread.run();
                } catch (IOException e) {
                    LOGGER.error("An error occurred while creating the file for player " + name + ": " + e.getMessage());
                }
            }
        }
    }
    private void setupPlayerThreads(MinecraftServer server) {
        Path gameDir = FabricLoader.getInstance().getGameDir();
        Path worldsDir = gameDir.resolve("saves");
        File worldFolder = worldsDir.toFile();
        customFolder = new File(worldFolder, "PlayersInvLogs");

        synchronized (fileLock) {
            if (!customFolder.exists()) {
                LOGGER.error("Failed to find custom folder: " + customFolder.getAbsolutePath());
            } else {
                for (File file : customFolder.listFiles()) {
                    if (file.isFile() && file.getName().matches("^[^/\\\\]+\\.json$")) {
                        String baseName = file.getName().substring(0, file.getName().lastIndexOf(".json"));
                        try {
                            UUID.fromString(baseName);
                            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                            PlayerThread thread = new PlayerThread(baseName, file.getName(), randomAccessFile, isServerRunning, this.threads);
                            synchronized (threads) {
                                activeThreads.put(baseName, thread);
                                threads.add(thread);
                                thread.run();
                            }
                            LOGGER.info("Thread for UUID " + baseName + " loaded during server startup.");
                        } catch (IllegalArgumentException e) {
                            LOGGER.warn("Invalid UUID in file name: " + baseName);
                        } catch (IOException e) {
                            LOGGER.error("Failed to load thread for UUID: " + baseName, e);
                        }
                    }
                }
            }
        }
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

