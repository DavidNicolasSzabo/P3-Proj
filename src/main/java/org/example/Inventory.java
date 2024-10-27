package org.example;
import java.util.ArrayList;
public class Inventory {
    private final PlayerInventory playerInv;
    private ArrayList<StorageInventory> storageInv;
    public Inventory(String playerName) {
        playerInv = new PlayerInventory(playerName);
        storageInv = new ArrayList<>();
    }
    public void addStorage(StorageInventory storage) {
        storageInv.add(storage);
    }
    public Integer InventoryTotal(){
        Integer total = 0;
        for(StorageInventory s : storageInv){
            for(ArrayList<Item> i :s.getStorageSlots()) {
                total++;
            }
        }
        for(Item p : playerInv.getInvSlots())
            total++;
        return total;
    }
}
