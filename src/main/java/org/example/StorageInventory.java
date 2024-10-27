package org.example;

import java.util.ArrayList;

public class StorageInventory extends Storage {

    private Integer storageCapacity;
    private ArrayList<ArrayList<Item>> storageSlots;

    public StorageInventory(String storageName, Integer storageCapacity, Boolean stackable, String storageType, Boolean getssignal) {

        super(storageName, storageType, stackable, getssignal);
        this.storageCapacity = storageCapacity;
        this.storageSlots = new ArrayList<>();
    }

    public int getStorageCapacity() {
        return storageCapacity;
    }

    public ArrayList<ArrayList<Item>> getStorageSlots() {
        return storageSlots;
    }
}
