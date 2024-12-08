package org.example;

import java.util.Collection;

public class TrappedChest extends StorageInventory {
    private static Integer TrappedChestIndex = 0;
    private String PlayerUUID;
    private Collection<String> PlayerFriends;
    private Integer TCIndex;

    public TrappedChest(String name, String Storagetype, String PlayerUUID, Collection<String> PlayerFriends) {
        super(name, 27, true, Storagetype, false, Stacksize.SIXTYFOUR);
        this.PlayerUUID = PlayerUUID;
        this.PlayerFriends = PlayerFriends;
        this.TCIndex = TrappedChestIndex++;
    }

    public void RemoveTrappedChest(String type, String name) {
        if (this.getType().equals(type) && this.getName().equals(name)) {
            super.RemoveStorage(type, name);
        }
    }
}
