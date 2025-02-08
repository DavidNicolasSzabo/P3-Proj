package net.da23335;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageLoggingclient implements ModInitializer {
	public static final String MOD_ID = "storage-logging-client";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		Client_Keybinding.registerKeybinding();
	}
}