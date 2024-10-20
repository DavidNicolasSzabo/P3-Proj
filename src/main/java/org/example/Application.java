package org.example;



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
                outputDevice.writeMessage("Please input storage name:");
                String storageName=inputDevice.getString();
                outputDevice.writeMessage("Please input storage type:");
                String storageType=inputDevice.getString();
                outputDevice.writeMessage("Please input storage size:");
                Integer storageSize=inputDevice.getInteger();
                StorageInventory storageInv = new StorageInventory(storageName,storageSize,storageType);
                inventory.addStorage(storageInv);
                outputDevice.writeMessage("\n");
                break;
            case "Q":
                break;
            default:
                outputDevice.writeMessage("Invalid function");
        }
    }

}
