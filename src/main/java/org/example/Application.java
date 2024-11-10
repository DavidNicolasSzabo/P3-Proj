package org.example;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.example.CustomExcept;
public class Application {
    InputDevice inputDevice;
    OutputDevice outputDevice;
    Inventory inventory;
    public Application(InputDevice in, OutputDevice out) {
        this.inputDevice = in;
        this.outputDevice = out;

    }

    public void run(){
        outputDevice.writeMessage("Please chose a function:\nInventory items total:[IT]\nAdd Inventory:[AdP]\nAdd Storage:[AdS]\nExit:[Q]\n");
        String funcChos=inputDevice.getString();
        switch (funcChos){
            case "IT":
                String message="" + inventory.InventoryTotal();
                outputDevice.writeMessage(message);
                break;
            case "AdP":
                outputDevice.writeMessage("Please input player name:");
                String playerName=inputDevice.getString();
                inventory=new Inventory(playerName);
                outputDevice.writeMessage("\n");
                break;
            case "AdS":
                try{
                outputDevice.writeMessage("Please input storage name:");
                String storageName=inputDevice.getString();
                outputDevice.writeMessage("Please input storage type:");
                String storageType=inputDevice.getString();
                outputDevice.writeMessage("Please input storage size:");
                Integer storageSize=inputDevice.getInteger();
                outputDevice.writeMessage("Please input storage stack-ability status:");
                Boolean stackable=inputDevice.getBoolean();
                outputDevice.writeMessage("Please input storage signal accepting status:");
                Boolean getssignal=inputDevice.getBoolean();
                StorageInventory storageInv = new StorageInventory(storageName,storageSize,stackable,storageType,getssignal);
                inventory.addStorage(storageInv);
                outputDevice.writeMessage("\n");
                break;} catch (Exception e) {
                    if (e.getMessage().equals("Inventory already exists!")) {
                        outputDevice.writeMessage("Please choose another name:");
                    }
                    else
                        outputDevice.writeMessage(e.getMessage());
                }
            case "Q":
                break;
            default:
                outputDevice.writeMessage("Invalid function");
        }
    }
    public void PrintItemsPerCategory() {
        Map<String, Integer> itemCounts = new HashMap<>();
        for (StorageInventory storage : inventory.getStorageInv()) {
            for (StorageInventory.Slot slot : storage.getStorageSlots()) {
                for (Item item : slot.items) {
                    String itemName = item.getName();
                    itemCounts.put(itemName, itemCounts.getOrDefault(itemName, 0) + 1);
                }
            }
        }


        String mess1="Item counts per category:";
        outputDevice.writeMessage(mess1);
        String mess2 = "";
        for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
            String name = entry.getKey();
            Integer count = entry.getValue();
            mess2 += "  - " + name + ": " + count + "\n";
        }
        outputDevice.writeMessage(mess2);
    }

}
