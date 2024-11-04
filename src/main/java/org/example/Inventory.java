package org.example;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

public class Inventory {
    private final PlayerInventory playerInv;
    private Collection<StorageInventory> storageInv;
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
            total=total+ s.getStorageCapacity();
        }
            total=total+playerInv.getSlotsNumber();
        return total;
    }
    public Collection<StorageInventory> getStorageInv() {
        return storageInv;
    }
    public PlayerInventory getPlayerInv() {
        return playerInv;
    }
    public void sortSlotsInStorages()
    {
        for (StorageInventory s : storageInv){

            s.slots=s.slots.stream().sorted(StorageInventory.Slot::compareTo).toList();
        }
    }
    public void sortSlotsInPlayerInventory(){
        playerInv.invSlots=playerInv.invSlots.stream().sorted(PlayerInventory.Slot::compareTo).toList();
    }
    public void SortInventory(){
        sortSlotsInStorages();
        sortSlotsInPlayerInventory();
    }
}
