package org.example;

import java.util.Collection;

public class ShulkerBox extends StorageInventory {
    private static Integer ShulkerIndex = 0;
    private String PlayerUUID;
    private Collection<String> PlayerFriends;
    private Integer ShIndex;

    ShulkerBox(String name, String Storagetype, String PlayerUUID, Collection<String> PlayerFriends) {
        super(name, 27, false, Storagetype, false);
        this.ShIndex = ShulkerIndex++;
        this.PlayerUUID = PlayerUUID;
        this.PlayerFriends = PlayerFriends;
    }

    public void RemoveShulker(String type, String name) {
        if (this.getType().equals(type) && this.getName().equals(name)) {
            super.RemoveStorage(type, name);
        }
    }
}
