package org.example;

import java.util.Collection;

public class NormalChest extends StorageInventory {
    private static Integer ChestIndex = 0;
    private String PlayerUUID;
    private Collection<String> PlayerFriends;
    private Integer CIndex;

    public NormalChest(String name, String Storagetype, String PlayerUUID, Collection<String> PlayerFriends) {
        super(name, 27, true, Storagetype, false, Stacksize.SIXTYFOUR);
        this.PlayerUUID = PlayerUUID;
        this.PlayerFriends = PlayerFriends;
        this.CIndex = ChestIndex++;
    }

    public void RemoveChest(String type, String name) {
        if (this.getType().equals(type) && this.getName().equals(name)) {
            super.RemoveStorage(type, name);
            this.PlayerFriends.clear();
            this.CIndex = null;
            this.PlayerUUID = null;
        }
    }
}
