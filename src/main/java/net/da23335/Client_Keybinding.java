package net.da23335;

import javafx.application.Platform;
import javafx.stage.Stage;
import net.da23335.HelloApplication;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

public class Client_Keybinding {

    public static KeyBinding Client_Keybind;
    private static Stage stage = null;
    private static boolean ispressed = false;
    public static void registerKeybinding() {
        // Creating the keybinding
        Client_Keybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.storage_logging_client.keybind",
                GLFW.GLFW_KEY_P,
                "key.categories.misc"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (Client_Keybind.isPressed() && !ispressed) {
                ispressed = true;
                openHelloApplication();
            } else if (!Client_Keybind.isPressed() && ispressed) {
                ispressed = false;
                closeHelloApplication();
            }
        });
    }



    private static void openHelloApplication() {
        if (stage == null || !stage.isShowing()) {
            if (!Platform.isFxApplicationThread()) {
                new Thread(() -> {
                    try {
                        Platform.startup(() -> {});
                        Platform.runLater(() -> {
                            try {
                                HelloApplication app = new HelloApplication();
                                app.start(new Stage());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (IllegalStateException ignored) {
                    }
                }).start();
            } else {
                Platform.runLater(() -> {
                    try {
                        HelloApplication app = new HelloApplication();
                        app.start(new Stage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
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
