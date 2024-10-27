package org.example;

public class PlayerInventory  {
    private String playerName;
    private final Item[] invSlots=new Item[36];
    public PlayerInventory(String playerName) {
        this.playerName = playerName;
    }
    public String getPlayerName() {
        return playerName;
    }
    public Item[] getInvSlots() {
        return invSlots;
    }

}
