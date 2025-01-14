package Proj.Client;
import javafx.application.Platform;
import javafx.stage.Stage;
import net.minecraft.client.option.KeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

public class Client_Keybinding {

    public static KeyBinding Client_Keybind;
    private static Stage stage = null;
    private static boolean ispressed = false;
    public static void registerKeybinding() {
        // Creating the keybinding
        Client_Keybind = new KeyBinding(
                "key.Storage Logging(Client).Keybind",
                GLFW.GLFW_KEY_P,
                "key.categories.misc"
        );

        KeyBindingRegistry.INSTANCE.register((FabricKeyBinding) Client_Keybind);
    }


    public static void onTick() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (Client_Keybind.isPressed() &&ispressed==false) {
                ispressed = true;
                openHelloApplication();
            } else if (Client_Keybind.isPressed() && ispressed==true) {
                ispressed = false;
                closeHelloApplication();
            }
        });
    }
    private static void openHelloApplication() {
        if (stage == null || !stage.isShowing()) {
            Platform.runLater(() -> {
                try {
                    HelloApplication app = new HelloApplication();
                    app.start(stage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
    private static void closeHelloApplication() {
        if (stage != null && stage.isShowing()) {
            Platform.runLater(() -> {
                stage.close();
            });
        }
    }
}
