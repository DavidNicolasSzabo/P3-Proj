package org.example;

import java.util.*;
public class PlayerInventory  {
    private String playerName;
    Collection<Slot> invSlots;
    private final Integer slotsNumber=36;
    public PlayerInventory(String playerName) {
        this.playerName = playerName;
        this.invSlots = new ArrayList<Slot>();
        for (int i = 0; i < slotsNumber; i++) {
            PlayerInventory.Slot slot = new PlayerInventory.Slot(new ArrayList<Item>(), i, Item.Stacksize.SIXTYFOUR);
            invSlots.add(slot);
        }
    }
    public String getPlayerName() {
        return playerName;
    }
    public Collection<Slot> getInvSlots() {
        return invSlots;
    }
    public Integer getSlotsNumber() {
        return slotsNumber;
    }

    public void addItems(Item item, Integer quantity) {
        Integer index=0;
        for (PlayerInventory.Slot slot : invSlots) {
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
            }else if (index==invSlots.size()-1 && slot.items.size()>0 && quantity>0) {
                return;
            }else if(quantity==0) {
                return;
            }
        }
    }

    protected static class Slot{
        List<Item> items;
        Integer index;
        Item.Stacksize stacksize;

        public Slot(List<Item> items, Integer index, Item.Stacksize stacksize) {
            this.items = items;
            this.index = index;
        }
        //this uses item compare to  because a slot can hold only 1 type of item so you can compare slots by the name and type of item and if both are the same you can compare by size
        public int compareTo(PlayerInventory.Slot o) {
            try{Iterator<Item> it1 = items.iterator();
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
            return 0;} catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public void removeItemFromSlot(Item item, Integer quantity) {
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
