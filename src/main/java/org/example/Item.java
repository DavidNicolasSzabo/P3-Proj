package org.example;

public class Item implements Stackable, Comparable<Item> {
    protected String item_name;
    protected String item_type;
    protected Boolean stackable;
    public enum Stacksize {
        ONE(1),SIXTEEN(16),SIXTYFOUR(64);
        private final int value;

        Stacksize(int value) {
            this.value = value;
        }

        public int getstackValue() {
            return value;
        }
    }
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


    @Override
    public int compareTo(Item o) {
        if(item_name.compareTo(o.item_name) < 0) {
            return 1;
        }
        else if(item_name.compareTo(o.item_name) > 0) {
            return -1;
        }
        else if(item_type.compareTo(o.item_type) < 0) {
            return 1;
        }
        else if(item_type.compareTo(o.item_type) > 0) {
            return -1;
        }
        return 0;
    }
    public String getName(){
        return item_name;
    }
    public String getType(){
        return item_type;
    }
}
