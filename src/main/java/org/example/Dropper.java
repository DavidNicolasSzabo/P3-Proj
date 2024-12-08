package org.example;

import java.util.Collection;

public class Dropper extends StorageInventory {
    private static Integer DropperIndex = 0;
    private String PlayerUUID;
    private Collection<String> PlayerFriends;
    private Integer DrIndex;

    Dropper(String name, String Storagetype, String PlayerUUID, Collection<String> PlayerFriends) {
        super(name, 9, true, Storagetype, false, Stacksize.ONE);
        this.DrIndex = DropperIndex++;
        this.PlayerUUID = PlayerUUID;
        this.PlayerFriends = PlayerFriends;
    }

    public void RemoveDropper(String type, String name) {
        if (this.getType().equals(type) && this.getName().equals(name)) {
            super.RemoveStorage(type, name);
        }
    }
}
