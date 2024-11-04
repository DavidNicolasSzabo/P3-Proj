package org.example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class StorageInventory extends Storage {

    private Integer storageCapacity;


    Collection<Slot> slots;
    protected static class Slot{
        Collection<Item> items;
        Stacksize stacksize;
        public Slot(Collection<Item> items) {
            this.items = items;
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

    }
    public StorageInventory(String storageName, Integer storageCapacity, Boolean stackable, String storageType, Boolean getssignal) {

        super(storageName, storageType, stackable, getssignal);
        this.storageCapacity = storageCapacity;
        this.slots = new ArrayList<Slot>() ;
        for (int i = 0; i < storageCapacity; i++) {
            Slot slot = new Slot(new ArrayList<Item>());
            slots.add(slot);
        }
    }
    //Items can have different stack values so when adding a certain amount of items of same type and name we need check if it fills a stack or if it can fill an existent stack.
    public String addItems(Item item, Integer stacksize, Integer quantity) {
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
                for(int i=0;i<quantity && i<stacksize;i++) {
                    slot.items.add(item);
                    quantity--;
                }
                if(quantity>0) {
                    index++;
                }
            }else if (index==slots.size()-1 && slot.items.size()>0 && quantity>0) {
                return "Full Storage";
            }else if(quantity==0)
            {break;
            }
        }
        return "Done";
    }
    public int getStorageCapacity() {
        return storageCapacity;
    }

    public Collection<Slot> getStorageSlots() {
        return slots;
    }


}
