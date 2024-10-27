package org.example;

public class Item implements Stackable {
    protected String item_name;
    protected String item_type;
    protected Boolean stackable;
    public Item(String item_name, String item_type, Boolean stackable) {
        this.item_name = item_name;
        this.item_type = item_type;
        this.stackable = stackable;
    }
    public String getItem_name() {
        return item_name;
    }
    public String getItem_type() {
        return item_type;
    }
    @Override
    public Boolean isStackable() {
        return stackable;
    }
}
