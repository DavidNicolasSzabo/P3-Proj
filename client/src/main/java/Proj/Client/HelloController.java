package Proj.Client;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import java.util.List;
import java.util.Map;

public class HelloController {
    @FXML private VBox contentVBox;
    @FXML private Button refreshButton;
    @FXML private TextField cassetteField;
    @FXML private Button addFriendButton;
    @FXML private Button clearWarningsButton;

    public void initialize() {
        Map<String, Object> objectMapper = Client.getObjectMapper();
        refreshButton.setOnAction(event -> {
            String playerName = getPlayername();
            ExtraModInfoPayload message=new ExtraModInfoPayload("Refresh");
            ClientPlayNetworking.send(message);
            displayData(playerName, objectMapper);
        });
        addFriendButton.setOnAction(event -> {
            String friendName = cassetteField.getText();
            if (friendName != null && !friendName.isEmpty()) {
                ExtraModInfoPayload message=new ExtraModInfoPayload("Add Friend "+friendName);
                ClientPlayNetworking.send(message);
                cassetteField.clear();
            }
        });
        clearWarningsButton.setOnAction(event -> {
            clearReportsAndDestroyedStorages();
        });
    }

    private void clearReportsAndDestroyedStorages() {
        ExtraModInfoPayload message=new ExtraModInfoPayload("Clear Reports");
        ClientPlayNetworking.send(message);
    }

    private void displayData(String playerName, Map<String, Object> inventoryData) {
        contentVBox.getChildren().clear();
        Label playerNameLabel = new Label("Player Name: " + playerName);
        contentVBox.getChildren().add(playerNameLabel);
        Label playerInventoryLabel = new Label("Player Inventory:");
        contentVBox.getChildren().add(playerInventoryLabel);
        Map<String, Integer> playerInventory = (Map<String, Integer>) inventoryData.get("PlayerInventory");
        addItemCountsToVBox(playerInventory);
        Label storagesLabel = new Label("Storages:");
        contentVBox.getChildren().add(storagesLabel);
        List<Map<String, Object>> storages = (List<Map<String, Object>>) inventoryData.get("Storages");
        for (Map<String, Object> storage : storages) {
            String storageType = (String) storage.get("StorageType");
            String storageName = (String) storage.get("StorageName");
            Object storageIndex = storage.get("StorageIndex");
            Label storageLabel = new Label(String.format("%s %s %s", storageType, storageName, storageIndex));
            contentVBox.getChildren().add(storageLabel);
            Map<String, Integer> storageItemCounts = (Map<String, Integer>) storage.get("ItemCounts");
            addItemCountsToVBox(storageItemCounts);
        }
        Label reportsLabel = new Label("Reports:");
        contentVBox.getChildren().add(reportsLabel);
        addItemCountsToVBox(inventoryData.get("Reports"));
        Label destroyedStoragesLabel = new Label("Destroyed Storages:");
        contentVBox.getChildren().add(destroyedStoragesLabel);
        addItemCountsToVBox(inventoryData.get("DestroyedStorages"));
    }
    private void addItemCountsToVBox(Object data) {
        if (data instanceof Map) {
            Map<String, Integer> itemCounts = (Map<String, Integer>) data;
            int count = 0;
            StringBuilder row = new StringBuilder();
            for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
                row.append(String.format("[%s: %d] ", entry.getKey(), entry.getValue()));
                count++;
                if (count == 6) {
                    contentVBox.getChildren().add(new Label(row.toString()));
                    row.setLength(0);
                    count = 0;
                }
            }
            if (row.length() > 0) {
                contentVBox.getChildren().add(new Label(row.toString()));
            }
        }
    }

    private String getPlayername() {
        return MinecraftClient.getInstance().player.getGameProfile().getName();
    }
}
