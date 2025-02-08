package net.da23335;

import net.da23335.PlayerThread;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.WorldEvents;
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
    public static final String MOD_ID = "storage-logging-server";
    private static final Map<String, PlayerThread> activeThreads = new HashMap<>();
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final Collection<PlayerThread> threads = new ArrayList<>();
    private static final Lock fileLock = new ReentrantLock();
    private static volatile boolean isServerRunning = false;
    private static File customFolder;
    public static volatile Map<String,String > players = new HashMap<>();
    private static Collection<ServerPlayerEntity> playersList = new ArrayList<>();
    public Collection<PlayerThread> getThreads() {
        return threads;
    }
    @Override
    public void onInitializeServer() {
        LOGGER.info("Initializing Server Mod");
        PayloadTypeRegistry.playS2C().register(InventoryDataPayload.ID, InventoryDataPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ExtraModInfoPayload.ID, ExtraModInfoPayload.CODEC);
        ServerLifecycleEvents.SERVER_STARTING.register(server-> {
            Path worldsDir = server.getSavePath(WorldSavePath.ROOT);
            File worldFolder = worldsDir.toFile();
            customFolder = new File(worldFolder, "PlayersInvLogs");
            synchronized (fileLock) {
                if (!customFolder.exists() && !customFolder.mkdirs()) {
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
                if (customFolder.exists() && customFolder != null) {
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


        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {

        });
    }



    public static void onPlayerJoin(ServerPlayerEntity player) {
        LOGGER.info("Player {} joined", player.getName());
        String uuid = player.getGameProfile().getId().toString();
        String name = player.getName().getString();
        players.put(uuid, name);
        playersList.add(player);
        synchronized (threads) {
            if (!activeThreads.containsKey(uuid)) {
                File newFile = new File(customFolder, uuid + ".json");
                try (RandomAccessFile randomAccessFile = new RandomAccessFile(newFile, "rw")) {
                    PlayerThread newThread = new PlayerThread(uuid, name, randomAccessFile, isServerRunning, threads,player);
                    activeThreads.put(uuid, newThread);
                    threads.add(newThread);
                    newThread.run();
                } catch (IOException e) {
                    LOGGER.error("An error occurred while creating the file for player " + name + ": " + e.getMessage());
                }
            }
        }
    }
    public static void setupPlayerThreads(MinecraftServer server) {

        Path worldsDir = server.getSavePath(WorldSavePath.ROOT);
        File worldFolder = worldsDir.toFile();
        customFolder = new File(worldFolder, "PlayersInvLogs");


        synchronized (fileLock) {
            if (!customFolder.exists()) {
                LOGGER.error("Failed to find custom folder: " + customFolder.getAbsolutePath());
            } else {
                for (File file : customFolder.listFiles()) {
                    if (file.isFile() && file.getName().endsWith(".json")) {
                        String baseName = file.getName().substring(0, file.getName().lastIndexOf(".json"));
                        LOGGER.info("Basename of file is {}", baseName);
                        try {
                            for (ServerPlayerEntity player : playersList) {
                                if(player.getUuid()==UUID.fromString(baseName)){
                                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                                PlayerThread thread = new PlayerThread(baseName, file.getName(), randomAccessFile, isServerRunning, threads,player);
                                synchronized (threads) {
                                    activeThreads.put(baseName, thread);
                                    threads.add(thread);
                                    thread.run();
                                }
                                }
                            }

                            LOGGER.info("Thread for UUID " + baseName + " loaded during server startup.");
                        } catch (IllegalArgumentException e) {
                            LOGGER.warn("Invalid UUID in file name: " + baseName);
                            continue;
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
      these are just notes of a course ignore them
     */
}

