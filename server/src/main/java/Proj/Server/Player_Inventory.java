package Proj.Server;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.*;
public class Player_Inventory {
    @JsonProperty
    private String playerName;
    @JsonProperty
    private final String playerUUID;
    @JsonProperty
    Collection<Slot> invSlots;
    private final Integer slotsNumber=36;

    public Player_Inventory(String playerName, String playerUUID) {
        this.playerName = playerName;
        this.playerUUID = playerUUID;
        this.invSlots = new LinkedList<Slot>();
        for (int i = 0; i < slotsNumber; i++) {
            Slot slot = new Slot(new ArrayList<Item>(), i, Item.Stacksize.SIXTYFOUR);
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

    public synchronized void addItems(Item item, Integer quantity,Integer slotindex) {
        for (Player_Inventory.Slot slot : invSlots) {
            if (slot.items.contains(item) && slot.items.size()<slot.stacksize.getstackValue() && quantity>0 && slot.index==slotindex) {
                Integer q=quantity-(slot.stacksize.getstackValue()-slot.items.size());
                for(int i=0;i<q;i++) {
                    slot.items.add(item);
                }
                quantity-=q;
            } else if (slot.items.isEmpty() && quantity>0 && slot.index == slotindex) {
                slot.updateStacksize(item.getStack());
                for (int i = 0; i < quantity && i < item.stack.getstackValue(); i++) {
                    slot.items.add(item);
                    quantity--;
                }
            }else if ((slotindex==invSlots.size()-1 && slot.items.size() > 0 && quantity-slot.items.size()>0 && quantity>0 )|| (!slot.items.contains(item) && slot.items.size()>0 && slot.index==slotindex)) {
                return;
            }else if(quantity==0) {
                return;
            }
        }
    }
    public synchronized void removeItemFromInv(Item item, Integer quantity,Integer slotindex) {
        for (Player_Inventory.Slot slot : invSlots) {
            if (slot.items.contains(item) && slotindex==slot.index) {
                List<Item> itemList = (List<Item>) slot.getItems();
                ListIterator<Item> it = itemList.listIterator(itemList.size()); // Start at the end
                while (it.hasPrevious() && quantity > 0) {

                    if (it.equals(item)) {
                        it.remove();
                        quantity--;
                    }
                }
            }
        }

    }

    public synchronized String getPlayerUUID() {
        return playerUUID;
    }
    @JsonSerialize
    @JsonDeserialize
    protected static class Slot{
        @JsonProperty
        List<Item> items;
        @JsonProperty
        Integer index;
        @JsonProperty
        Item.Stacksize stacksize;

        public Slot(List<Item> items, Integer index, Item.Stacksize stacksize) {
            this.items = items;
            this.index = index;
        }
        //this uses item compare to  because a slot can hold only 1 type of item so you can compare slots by the name and type of item and if both are the same you can compare by size
        public int compareTo(Slot o) {
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
        public List<Item> getItems() {
            return items;
        }

        public void updateStacksize(Item.Stacksize stacksize) {
            this.stacksize = stacksize;
        }

    }

}
