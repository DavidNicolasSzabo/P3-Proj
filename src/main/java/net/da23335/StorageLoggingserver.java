package net.da23335;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;


import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StorageLoggingserver implements ModInitializer {
	public static final String MOD_ID = "storage-logging-server";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Detected singleplayer world. Registering server logic.");
		server_mod serverMod = new server_mod();
		serverMod.onInitializeServer();
		LOGGER.info("StorageLoggingMod initialized.");
	}


}