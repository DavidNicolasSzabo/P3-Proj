package Proj.Server;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.*;
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ShulkerBox.class, name = "ShulkerBox"),
        @JsonSubTypes.Type(value = TrappedChest.class,name = "TrappedChest"),
        @JsonSubTypes.Type(value = NormalChest.class, name = "NormalChest"),
        @JsonSubTypes.Type(value = Hopper.class,name = "Hopper"),
        @JsonSubTypes.Type(value = Barrel.class,name = "Barrel")
})
public abstract class StorageInventory extends Storage {
    @JsonProperty
    private Integer storageCapacity;
    @JsonProperty
    Collection<Slot> slots;
    @JsonProperty
    private int[] Position;
    @JsonProperty
    private Collection<String> Stolen;
    @JsonProperty
    private Collection<String> Borrowed;
    @JsonProperty
    private String PlayerUUID;
    @JsonProperty
    private Collection<String> PlayerFriends;
    public StorageInventory(String storageName, Integer storageCapacity, Boolean stackable, String storageType, Boolean getssignal, Stacksize stacksize,int[] Position,String PlayerUUID,Collection<String> PlayerFriends) {
        super(storageName, storageType, stackable, getssignal, stacksize);
        this.storageCapacity = storageCapacity;
        this.slots = new LinkedList<Slot>();
        this.Position = Position;
        this.PlayerUUID = PlayerUUID;
        this.PlayerFriends = PlayerFriends;
        for (int i = 0; i < storageCapacity; i++) {
            Slot slot = new Slot(new ArrayList<Item>(), i, Stacksize.SIXTYFOUR);
            slots.add(slot);
        }

    }
    public int[] getPosition() {
        return Position;
    }
    //Items can have different stack values so when adding a certain amount of items of same type and name we need check if it fills a stack or if it can fill an existent stack.
    public synchronized void addItems(Item item, Integer quantity,Integer slotindex) {
        for (Slot slot : slots) {
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
            }else if ((slotindex==slots.size()-1 && slot.items.size() > 0 && quantity-slot.items.size()>0 && quantity>0 )|| (!slot.items.contains(item) && slot.items.size()>0 && slot.index==slotindex)) {
                return;
            }else if(quantity==0) {
                return;
            }
        }
    }
    public synchronized void removeItemFromInv(Item item, Integer quantity,Integer slotindex) {
        for (Slot slot : slots) {
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
    public int getStorageCapacity() {
        return storageCapacity;
    }

    public Collection<Slot> getStorageSlots() {
        return slots;
    }
    @JsonSerialize
    @JsonDeserialize
    protected static class Slot{
        @JsonProperty
        Integer index;
        @JsonProperty
        List<Item> items;
        @JsonProperty
        Stacksize stacksize;

        public Slot(List<Item> items, Integer index, Stacksize stacksize) {
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
        public List<Item> getItems() {
            return items;
        }

        public void updateStacksize(Stacksize stacksize) {
            this.stacksize = stacksize;
        }

    }
    public void addFriend(String UUID){
        PlayerFriends.add(UUID);
    }
    public void removeFriend(String UUID){
        PlayerFriends.remove(UUID);
    }
    public void AddStolen(String Message){
        Stolen.add(Message);
    }
    public void RemoveStolen(String Message){
        Stolen.remove(Message);
    }
    public String getPlayerUUID(){
        return PlayerUUID;
    }
}
