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
        System.out.println("Please chose a function:\nInventory items total:[IT]\nAdd Inventory:[AdP]\nAdd Storage:[AdS]\nExit:[Q]\n");
        String funcChos=inputDevice.getString();
        switch (funcChos){
            case "IT":
                String message="" + inventory.InventoryTotal();
                System.out.println(message);
                break;
            case "AdP":
                System.out.println("Please input player name:");
                String playerName=inputDevice.getString();
                inventory=new Inventory(playerName);
                System.out.println();
                break;
            case "AdS":
                try{
                    System.out.println("Please input storage name:");
                    String storageName=inputDevice.getString();
                    System.out.println("Please input storage type:");
                    String storageType=inputDevice.getString();
                    System.out.println("Please input storage size:");
                    Integer storageSize=inputDevice.getInteger();
                    System.out.println("Please input storage stack-ability status:");
                    Boolean stackable=inputDevice.getBoolean();
                    System.out.println("Please input storage signal accepting status:");
                    Boolean getssignal=inputDevice.getBoolean();
                    StorageInventory storageInv = new StorageInventory(storageName,storageSize,stackable,storageType,getssignal);
                    inventory.addStorage(storageInv);
                    System.out.println();
                } catch (CustomExcept e) {
                    if (e.getMessage().equals("Inventory already exists!")) {
                        System.out.println("Please choose another name:");
                    }
                    else
                        System.out.println(e.getMessage());
                }
            case "Q":
                break;
            default:
                System.out.println("Invalid function");
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
