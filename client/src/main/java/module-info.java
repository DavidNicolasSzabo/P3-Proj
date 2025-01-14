module org.example.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires net.fabricmc.loader;
    requires fabric.networking.api.v1;
    requires org.slf4j;
    requires org.lwjgl.glfw;
    requires fabric.lifecycle.events.v1;
    requires fabric.keybindings.v0;
    requires com.fasterxml.jackson.databind;
    requires minecraft.merged.a5fe06f283;

    exports Proj.Client;

    opens net.fabricmc.client to minecraft.merged.a5fe06f283;
    opens Proj.Client to javafx.fxml;
}
