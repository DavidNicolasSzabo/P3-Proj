package org.example;

public class EnderChest extends StorageInventory {
    private String PlayerUUID;
    private Integer ECIndex = 1;

    EnderChest(String PlayerUUID) {
        super("Enderchest", 27, true, "Enderchest", false, Stacksize.SIXTYFOUR);
        this.PlayerUUID = PlayerUUID;
    }

    public void RemoveEnderChest(String type, String name) {
        if (this.getType().equals(type) && this.getName().equals(name)) {
            super.RemoveStorage(type, name);
        }
    }

}
