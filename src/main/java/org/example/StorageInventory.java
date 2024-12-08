package org.example;

import java.util.*;

public class StorageInventory extends Storage {

    private Integer storageCapacity;


    Collection<Slot> slots;
    public StorageInventory(String storageName, Integer storageCapacity, Boolean stackable, String storageType, Boolean getssignal, Item.Stacksize stacksize) {
        super(storageName, storageType, stackable, getssignal, stacksize);
        this.storageCapacity = storageCapacity;
        this.slots = new ArrayList<Slot>() ;
        for (int i = 0; i < storageCapacity; i++) {
            Slot slot = new Slot(new ArrayList<Item>(), i, Item.Stacksize.SIXTYFOUR);
            slots.add(slot);
        }
    }

    //Items can have different stack values so when adding a certain amount of items of same type and name we need check if it fills a stack or if it can fill an existent stack.
    public void addItems(Item item, Integer quantity) {
        Integer index=0;
        for (Slot slot : slots) {
            if (slot.items.contains(item) && slot.items.size()<slot.stacksize.getstackValue() && quantity>0) {
                Integer q=quantity-(slot.stacksize.getstackValue()-slot.items.size());
                for(int i=0;i<q;i++) {
                    slot.items.add(item);
                }
                quantity-=q;
                if(quantity>0) {
                    index++;
                }
            } else if (slot.items.isEmpty() && quantity>0) {
                slot.updateStacksize(item.getStack());
                for (int i = 0; i < quantity && i < item.stack.getstackValue(); i++) {
                    slot.items.add(item);
                    quantity--;
                }
                if(quantity>0) {
                    index++;
                }
            }else if (index==slots.size()-1 && slot.items.size()>0 && quantity>0) {
                return;
            }else if(quantity==0) {
                return;
            }
        }
    }

    public void RemoveStorage(String type, String name) {
        if (this.getType().equals(type) && this.getName().equals(name)) {
            for (Slot slot : slots) {
                slot.clearSlot();
                slot.stacksize = null;
                slot.index = null;
                slot.items.clear();

            }
            slots.clear();

        }
    }
    public int getStorageCapacity() {
        return storageCapacity;
    }

    public Collection<Slot> getStorageSlots() {
        return slots;
    }

    protected static class Slot{
        Integer index;
        List<Item> items;
        Item.Stacksize stacksize;

        public Slot(List<Item> items, Integer index, Item.Stacksize stacksize) {
            this.items = items;
            this.index = index;
            this.stacksize = stacksize;
        }
        //this uses item compare to  because a slot can hold only 1 type of item so you can compare slots by the name and type of item and if both are the same you can compare by size
        public int compareTo(Slot o) {
            Iterator<Item> it1 = items.iterator();
            Iterator<Item> it2 = o.items.iterator();
            if(it1.hasNext() && it2.hasNext()) {
                if(it1.next().compareTo(it2.next()) > 0) {
                    return 1;
                }else if(it1.next().compareTo(it2.next()) < 0) {
                    return -1;
                } else if (it1.next().compareTo(it2.next()) == 0) {
                    if(items.size()>o.items.size()) {
                        return 1;
                    }else if(items.size()<o.items.size()) {
                        return -1;
                    }
                    return 0;
                }

            }else if(it1.hasNext() && !it2.hasNext()) {
                return 1;
            }
            return 0;
        }

        public void removeItemFromInv(Item item, Integer quantity) {
            List<Item> itemList = (List<Item>) items;
            ListIterator<Item> it = itemList.listIterator(itemList.size()); // Start at the end
            while (it.hasPrevious() && quantity > 0) {
                Item currentItem = it.previous();
                if (currentItem.equals(item)) {
                    currentItem.RemoveItem();
                    it.remove();
                    quantity--;
                }
            }
        }

        public void clearSlot() {
            for (Item item : items) {
                item.RemoveItem();
            }
        }

        public void updateStacksize(Item.Stacksize stacksize) {
            this.stacksize = stacksize;
        }

    }

}
