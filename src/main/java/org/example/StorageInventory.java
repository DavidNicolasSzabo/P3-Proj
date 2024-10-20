package org.example;

public class StorageInventory  {
    private String storageName;
    private Integer storageCapacity;
    private String storageType;
    private String[] storageSlots;
    public StorageInventory(String storageName, Integer storageCapacity, String storageType) {
        this.storageName = storageName;
        this.storageCapacity = storageCapacity;
        this.storageType = storageType;
        this.storageSlots = new String[storageCapacity];
    }
    public String getStorageName() {
        return storageName;
    }
    public int getStorageCapacity() {
        return storageCapacity;
    }
    public String getStorageType() {
        return storageType;
    }
    public String[] getStorageSlots() {
        return storageSlots;
    }
}
