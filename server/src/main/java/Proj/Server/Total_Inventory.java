package Proj.Server;

import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


@JsonSerialize
@JsonDeserialize
public class Total_Inventory {
    private final Player_Inventory playerInv;
    private Collection<String> playerFriends;
    private Collection<StorageInventory> storageInv;
    private Collection<String> StoragesDestroyed = new ArrayList();
    private Collection<String> Reports=new ArrayList();
    public Total_Inventory(String playerName, String playerUUID) {
        playerInv = new Player_Inventory(playerName, playerUUID);
        playerFriends = new ArrayList<>();
        storageInv = new ArrayList<>();
    }
    public synchronized void addStorage(StorageInventory storage){
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

    public synchronized void addFriend(String friendUUID) {
        playerFriends.add(friendUUID);
        for(StorageInventory s : storageInv){
            s.addFriend(friendUUID);
        }
    }
    public synchronized Collection<StorageInventory> getStorageInv() {
        return storageInv;
    }
    public synchronized Player_Inventory getPlayerInv() {
        return playerInv;
    }
    public void sortSlotsInStorages()
    {
        for (StorageInventory s : storageInv){

            s.slots=s.slots.stream().sorted(StorageInventory.Slot::compareTo).toList();
        }
    }
    public void sortSlotsInPlayerInventory(){
        playerInv.invSlots=playerInv.invSlots.stream().sorted(Player_Inventory.Slot::compareTo).toList();
    }

    public synchronized Collection<String> getPlayerFriends() {
        return playerFriends;
    }
    public synchronized void addStoragesDestroyed(String message) {
        this.StoragesDestroyed.add(message);
    }
    public synchronized void addReport(String message) {
        this.Reports.add(message);
    }
    public synchronized void removeStoragesDestroyed() {
        this.StoragesDestroyed.clear();
        this.StoragesDestroyed= new ArrayList();
    }
    public synchronized void removeReport() {
        this.Reports.clear();
        this.Reports= new ArrayList();
    }
    public synchronized Collection<String> getReports() {
        return Reports;
    }
    public synchronized Collection<String> getStoragesDestroyed() {
        return StoragesDestroyed;
    }
}
