package org.example;

import java.util.Collection;

public class Hopper extends StorageInventory {
    private static Integer HopperIndex = 0;
    private String PlayerUUID;
    private Collection<String> PlayerFriends;
    private Integer HIndex;

    public Hopper(String name, String Storagetype, String PlayerUUID, Collection<String> PlayerFriends) {
        super(name, 5, true, Storagetype, false, Stacksize.SIXTYFOUR);
        this.HIndex = HopperIndex++;
        this.PlayerUUID = PlayerUUID;
        this.PlayerFriends = PlayerFriends;
    }

    public void RemoveHopper(String type, String name) {
        if (this.getType().equals(type) && this.getName().equals(name)) {
            super.RemoveStorage(type, name);
        }
    }
}
