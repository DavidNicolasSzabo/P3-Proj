package org.example;

public class PlayerInventory  {
    private String playerName;
    private final String[] invSlots=new String[36];
    public PlayerInventory(String playerName) {
        this.playerName = playerName;
    }
    public String getPlayerName() {
        return playerName;
    }
    public String[] getInvSlots() {
        return invSlots;
    }

}
