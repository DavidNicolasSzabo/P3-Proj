package Proj.Server;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import java.util.*;
import net.minecraft.util.ActionResult;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Application {
    protected InputDevice inputDevice;
    protected OutputDevice outputDevice;
    protected Total_Inventory inventory;
    protected ServerPlayerEntity player;
    protected boolean serverstatus =true;
    protected Collection<HopperThreads> HopperThreads= new ArrayList<>();
    protected PlayerThread OurPlayerThread;
    public Application(InputDevice in, OutputDevice out, String UUID, String Playername,ServerPlayerEntity player,PlayerThread OurPlayer) {
        this.inputDevice = in;
        this.outputDevice = out;
        this.player = player;
        this.inventory = new Total_Inventory(UUID, Playername);
        this.OurPlayerThread = OurPlayer;
    }
    public void setServerstatus(boolean status) {
        this.serverstatus = status;
    }
    private synchronized void registerEventListeners() {
        PayloadTypeRegistry.playC2S().register(ExtraModInfoPayload.ID,ExtraModInfoPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ExtraModInfoPayload.ID,(payload,context) -> {
            if(payload!=null && payload.extraData()!=null) {
                String Message = payload.extraData().toString();
                Pattern pattern = Pattern.compile("^Add Friend \\[([a-zA-Z0-9_]+)\\]$");
                Matcher matcher = pattern.matcher(Message);
                if(Message.equals("Refresh")){
                    onPlayerConnect(player);
                } else if (Message.equals("Clear Reports")) {
                    this.inventory.removeReport();
                    this.inventory.removeStoragesDestroyed();
                }else if(matcher.matches()) {
                    String name = matcher.group(1);
                    if(server_mod.players.containsValue(name) ) {
                        String uuid="";
                        for (Map.Entry<String, String> entry : server_mod.players.entrySet()) {
                            if (entry.getValue().equals(name)) {
                                uuid = entry.getKey().toString();
                                break;
                            }
                        }
                        this.inventory.addFriend(uuid);
                    }
                }
            }
        });

        // Monitor block breaking (e.g., chests, barrels, shulkers)
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if(player.getUuid().toString().equals(this.inventory.getPlayerInv().getPlayerUUID())) {
                if (blockEntity instanceof Inventory) {
                    for(StorageInventory DeletedStorage : this.inventory.getStorageInv()){
                        if(Arrays.equals(DeletedStorage.getPosition(),new int[]{pos.getX(),pos.getY(),pos.getZ()})) {
                            this.inventory.getStorageInv().remove(DeletedStorage);
                            if(blockEntity instanceof HopperBlockEntity hopper){
                                ServerWorld world2= (ServerWorld) world;
                                HopperThreads Thread= new HopperThreads(hopper,world2,this.inventory,this.serverstatus);
                                for(HopperThreads Thread2 : this.HopperThreads){
                                    if(Thread2.equals(Thread)){
                                        this.HopperThreads.remove(Thread);
                                    }
                                }
                            }
                        }
                    }
                }
                return true;
            }
            else{
                this.inventory.addStoragesDestroyed(blockEntity.getType().toString()+" has been destroyed by "+player.getName().getString()+" at position "+pos);
                for(StorageInventory DeletedStorage : this.inventory.getStorageInv()){
                    if(Arrays.equals(DeletedStorage.getPosition(),new int[]{pos.getX(),pos.getY(),pos.getZ()})) {
                        this.inventory.getStorageInv().remove(DeletedStorage);
                        if(blockEntity instanceof HopperBlockEntity hopper){
                            ServerWorld world2= (ServerWorld) world;
                            HopperThreads Thread= new HopperThreads(hopper,world2,this.inventory,this.serverstatus);
                            for(HopperThreads Thread2 : this.HopperThreads){
                                if(Thread2.equals(Thread)){
                                    this.HopperThreads.remove(Thread);
                                }
                            }
                        }
                    }
                }
                return true;
            }

        });
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!world.isClient) {
                ItemStack heldItem = player.getStackInHand(hand);
                if(player.getUuid().toString().equals(this.inventory.getPlayerInv().getPlayerUUID())) {
                    if (heldItem.getItem() instanceof BlockItem) {
                        BlockPos placedPos = hitResult.getBlockPos().offset(hitResult.getSide());
                        //Server tick delay to allow proper block entity creation
                        ServerTickEvents.END_SERVER_TICK.register(server -> {
                            BlockEntity blockEntity = world.getBlockEntity(placedPos);
                            if (blockEntity instanceof ChestBlockEntity) {
                                String blockname= ((ChestBlockEntity) blockEntity).getName().getString();
                                String blockType= blockEntity.getType().toString();
                                this.inventory.addStorage(new NormalChest(blockname,blockType,this.inventory.getPlayerInv().getPlayerUUID(),this.inventory.getPlayerFriends(),placedPos,world));
                            } else if (blockEntity instanceof BarrelBlockEntity) {
                                String blockname= ((BarrelBlockEntity) blockEntity).getName().getString();
                                String blockType= blockEntity.getType().toString();
                                this.inventory.addStorage(new Barrel(blockname,blockType,this.inventory.getPlayerInv().getPlayerUUID(),this.inventory.getPlayerFriends(),placedPos,world));
                            } else if (blockEntity instanceof ShulkerBoxBlockEntity) {
                                String blockname= ((ShulkerBoxBlockEntity) blockEntity).getName().getString();
                                String blockType= blockEntity.getType().toString();
                                this.inventory.addStorage(new ShulkerBox(blockname,blockType,this.inventory.getPlayerInv().getPlayerUUID(),this.inventory.getPlayerFriends(),placedPos,world));
                            } else if (blockEntity instanceof HopperBlockEntity) {
                                String blockname= ((HopperBlockEntity) blockEntity).getName().getString();
                                String blockType= blockEntity.getType().toString();
                                this.inventory.addStorage(new Hopper(blockname,blockType,this.inventory.getPlayerInv().getPlayerUUID(),this.inventory.getPlayerFriends(),placedPos,world));
                            } else if (blockEntity instanceof TrappedChestBlockEntity) {
                                String blockname= ((TrappedChestBlockEntity) blockEntity).getName().getString();
                                String blockType= blockEntity.getType().toString();
                                this.inventory.addStorage(new TrappedChest(blockname,blockType,this.inventory.getPlayerInv().getPlayerUUID(),this.inventory.getPlayerFriends(),placedPos,world));
                            }
                        });
                    }
                }
            }
            return ActionResult.PASS;
        });
        // This is for monitoring player interactions with storages (his or others) from storage perspective
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            BlockEntity blockEntity = world.getBlockEntity(hitResult.getBlockPos());
            if (blockEntity instanceof Inventory storageInventory) {
                String storageOwnerUUID = this.inventory.getPlayerInv().getPlayerUUID();
                String playerUUID = player.getUuidAsString();
                boolean isOwner = storageOwnerUUID.equals(this.inventory.getPlayerInv().getPlayerUUID());
                boolean isFriend = !isOwner && this.inventory.getPlayerFriends().contains(playerUUID);
                ItemStack[] storagePreviousState = new ItemStack[storageInventory.size()];
                for (int i = 0; i < storageInventory.size(); i++) {
                    storagePreviousState[i] = storageInventory.getStack(i).copy();
                }
                ServerTickEvents.END_SERVER_TICK.register(server -> {
                    for (int i = 0; i < storageInventory.size(); i++) {
                        storagePreviousState[i] = storageInventory.getStack(i).copy();
                    }
                    for (int i = 0; i < storageInventory.size(); i++) {
                        ItemStack currentStack = storageInventory.getStack(i);

                        if (!ItemStack.areEqual(storagePreviousState[i], currentStack)) {
                            int[] StoragePosition = new int[]{hitResult.getBlockPos().getX(), hitResult.getBlockPos().getY(), hitResult.getBlockPos().getZ()};
                            handleStorageInteraction(storageInventory, (ServerPlayerEntity) player,i,storagePreviousState[i],currentStack,isOwner,isFriend,StoragePosition);
                            storagePreviousState[i] = currentStack.copy();
                        }
                    }

                });
            }
            return ActionResult.PASS;
        });
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            UUID targetPlayerUUID = UUID.fromString(this.inventory.getPlayerInv().getPlayerUUID());
            ItemStack[] previousInventory = null;

            for (ServerWorld world : server.getWorlds()) {
                PlayerEntity targetPlayer = world.getPlayerByUuid(targetPlayerUUID);

                if (targetPlayer != null) { // If the player is online in this world
                    Inventory playerInventory = targetPlayer.getInventory();
                    ItemStack[] currentInventory = new ItemStack[playerInventory.size()];
                    for (int i = 0; i < playerInventory.size(); i++) {
                        currentInventory[i] = playerInventory.getStack(i).copy();
                    }
                    if (previousInventory != null) {
                        // Detect drops
                        for (int i = 0; i < previousInventory.length; i++) {
                            ItemStack previousStack = previousInventory[i];
                            ItemStack currentStack = currentInventory[i];
                            String itemName =previousStack.getName().getString();
                            String itemType = Registries.ITEM.getId(previousStack.getItem()).toString();
                            boolean isStackable = previousStack.isStackable();
                            int maxStackSize =previousStack.getMaxCount();
                            Item.Stacksize stacksize = getStackSizeFromValue(maxStackSize);
                            Item item = new Item(itemName, itemType, isStackable, stacksize);
                            if (!previousStack.isEmpty() && (currentStack.isEmpty() || !ItemStack.areEqual(previousStack, currentStack))) {
                                int droppedAmount = previousStack.getCount() - (currentStack.isEmpty() ? 0 : currentStack.getCount());
                                if (droppedAmount > 0) {
                                    this.inventory.getPlayerInv().removeItemFromInv(item,droppedAmount,i);
                                }
                            }
                        }

                        // Detect pickups
                        for (int i = 0; i < previousInventory.length; i++) {
                            ItemStack previousStack = previousInventory[i];
                            ItemStack currentStack = currentInventory[i];
                            String itemName =currentStack.getName().getString();
                            String itemType = Registries.ITEM.getId(currentStack.getItem()).toString();
                            boolean isStackable = currentStack.isStackable();
                            int maxStackSize =currentStack.getMaxCount();
                            Item.Stacksize stacksize = getStackSizeFromValue(maxStackSize);
                            Item item = new Item(itemName, itemType, isStackable, stacksize);
                            if (currentStack.getCount() > (previousStack.isEmpty() ? 0 : previousStack.getCount())) {
                                int pickedUpAmount = currentStack.getCount() - (previousStack.isEmpty() ? 0 : previousStack.getCount());
                                if (pickedUpAmount > 0) {
                                    this.inventory.getPlayerInv().addItems(item,pickedUpAmount,i);
                                }
                            }
                        }
                    }
                    previousInventory = currentInventory;
                }
            }
        });
        monitorHopperAndDropperActions();
    }
    private synchronized void monitorHopperAndDropperActions() {

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerWorld world : server.getWorlds()) {
                Collection<BlockPos>hoppers = new ArrayList<>();
                for (StorageInventory Block : this.inventory.getStorageInv())
                {
                    if(Block instanceof Hopper)
                    {
                        BlockPos e= new BlockPos(Block.getPosition()[0],Block.getPosition()[1],Block.getPosition()[2]);
                        hoppers.add(e);

                    }
                }
                Collection<BlockPos> Positions = new ArrayList<>(hoppers);
                for (BlockPos pos : Positions ) {
                    BlockEntity blockEntity = world.getBlockEntity(pos);
                    if (blockEntity != null) {
                        // Checking  if the blockEntity is a hopper, so we can monitor hoppers
                        if (blockEntity instanceof HopperBlockEntity hopper) {
                            HopperThreads NewThread= new HopperThreads(hopper,world,this.inventory,this.serverstatus);
                            addHopperThread(NewThread);
                        }

                    }
                }
            }
        });
    }
    public synchronized void run(){
        if (inputDevice.isFileEmpty()){}
        else{
            try {
                inventory =inputDevice.deserializeTotalInventory();
            } catch (IOException e) {
                server_mod.LOGGER.error(e.getMessage());
            }
        }
        if(serverstatus==true)
        {
            registerEventListeners();
            if(this.HopperThreads!=null) {
                for (HopperThreads hopperTh : HopperThreads) {
                    hopperTh.setRunning(serverstatus);
                    hopperTh.run();
                }
            }
        }
        while(serverstatus == true) {
            registerEventListeners();
            ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
                ServerPlayerEntity player = handler.getPlayer();
                if (serverstatus==true) {
                    onPlayerConnect(player);
                }
            });
        }
        synchronized(HopperThreads){HopperThreads.removeAll(HopperThreads);}
        outputDevice.writeSerializedObject(inventory);

    }
    public synchronized Map<String, Object> PrintItemsPerCategory() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Integer> playerItemCounts = new HashMap<>();
        for (Player_Inventory.Slot slot : inventory.getPlayerInv().getInvSlots()) {
            for (Item item : slot.items) {
                String itemType = item.getType();
                playerItemCounts.put(itemType, playerItemCounts.getOrDefault(itemType, 0) + 1);
            }
        }
        result.put("PlayerInventory", playerItemCounts);
        List<Map<String, Object>> storageDetails = new ArrayList<>();
        for (StorageInventory storage : inventory.getStorageInv()) {
            Map<String, Object> storageInfo = new HashMap<>();
            storageInfo.put("StorageName", storage.getName());
            storageInfo.put("StorageType", storage.getType());
            if(storage instanceof ShulkerBox)
            {
                ShulkerBox shulkerBox = (ShulkerBox) storage;
                storageInfo.put("StorageIndex",shulkerBox.getShIndex());
            }else if(storage instanceof TrappedChest){
                TrappedChest trappedChest = (TrappedChest) storage;
                storageInfo.put("StorageIndex",trappedChest.getTCIndex());
            } else if (storage instanceof NormalChest) {
                NormalChest normalChest = (NormalChest) storage;
                storageInfo.put("StorageIndex",normalChest.getCIndex());
            } else if (storage instanceof Barrel) {
                Barrel barrel = (Barrel) storage;
                storageInfo.put("StorageIndex",barrel.getBIndex());
            } else if (storage instanceof Hopper) {
                Hopper hopper = (Hopper) storage;
                storageInfo.put("StorageIndex",hopper.getHIndex());
            }else {
                storageInfo.put("StorageIndex", "Unknown");
            }

            Map<String, Integer> storageItemCounts = new HashMap<>();
            for (StorageInventory.Slot slot : storage.getStorageSlots()) {
                if (slot.items != null) {
                    for (Item item : slot.items) {
                        String itemType = item.getType();
                        storageItemCounts.put(itemType, storageItemCounts.getOrDefault(itemType, 0) + 1);
                    }
                }
            }
            storageInfo.put("ItemCounts", storageItemCounts);

            storageDetails.add(storageInfo);
        }
        result.put("Storages", storageDetails);
        result.put("Reports",this.inventory.getReports());
        result.put("Destroyed storages",this.inventory.getStoragesDestroyed());
        return result;
    }



    // In this section Added means added to storage and removed from inventory and vice versa;

    private synchronized void handleStorageInteraction(Inventory storageInventory, ServerPlayerEntity player, int slotIndex, ItemStack previousStack, ItemStack currentStack, boolean isOwner, boolean isFriend,int[] Position) {
        String playerUUID = player.getUuidAsString();
        String action;
        String context = isOwner ? "personal" : (isFriend ? "Friend" : "stolen");
        String itemName = previousStack.getName().getString();
        String itemType = Registries.ITEM.getId(previousStack.getItem()).toString();
        boolean isStackable = previousStack.isStackable();
        int maxStackSize = previousStack.getMaxCount();
        Item.Stacksize stacksize = getStackSizeFromValue(maxStackSize);
        Item item = new Item(itemName, itemType, isStackable, stacksize);
        String itemName2 = currentStack.getName().getString();
        String itemType2 = Registries.ITEM.getId(currentStack.getItem()).toString();
        boolean isStackable2 = currentStack.isStackable();
        int maxStackSize2 = currentStack.getMaxCount();
        Item.Stacksize stacksize2 = getStackSizeFromValue(maxStackSize2);
        Item item2 = new Item(itemName2, itemType2, isStackable2, stacksize2);
        if (currentStack.isEmpty() && !previousStack.isEmpty()) {
            action = "removed";
            int change= previousStack.getCount()-currentStack.getCount();
            for(StorageInventory storage : inventory.getStorageInv() ) {
                if(Arrays.equals(storage.getPosition(),Position)) {
                    storage.removeItemFromInv(item,change,slotIndex);
                    break;
                }
            }
            updatePlayerThread(playerUUID, previousStack, action, context,slotIndex,change);
        } else if (!currentStack.isEmpty() && previousStack.isEmpty()) {
            int change= previousStack.getCount()-currentStack.getCount();
            action = "added";
            for(StorageInventory storage : inventory.getStorageInv() ) {
                if(Arrays.equals(storage.getPosition(),Position)) {
                    storage.addItems(item,change,slotIndex);
                    break;
                }
            }
            updatePlayerThread( playerUUID, currentStack, action, context,slotIndex,change);
        } else if (currentStack.getCount()< previousStack.getCount())
        {
            action = "removed";
            int change= previousStack.getCount()-currentStack.getCount();
            for(StorageInventory storage : inventory.getStorageInv() ) {
                if(Arrays.equals(storage.getPosition(),Position)) {
                    storage.removeItemFromInv(item,change,slotIndex);
                    break;
                }
            }
            updatePlayerThread(playerUUID, previousStack, action, context,slotIndex,change);
        } else if (currentStack.getCount() > previousStack.getCount()) {
            action = "added";
            int change= previousStack.getCount()-currentStack.getCount();
            for(StorageInventory storage : inventory.getStorageInv() ) {
                if(Arrays.equals(storage.getPosition(),Position)) {
                    storage.addItems(item,change,slotIndex);
                    break;
                }
            }
            updatePlayerThread(playerUUID, previousStack, action, context,slotIndex,change);
        }
        else if (!ItemStack.areItemsEqual(previousStack, currentStack)) {
            for(StorageInventory storage : inventory.getStorageInv() ) {
                if(Arrays.equals(storage.getPosition(),Position)) {
                    storage.removeItemFromInv(item, previousStack.getCount(), slotIndex);
                    storage.addItems(item2,currentStack.getCount(),slotIndex);
                    break;
                }
            }
            action = "added";
            updatePlayerThread(playerUUID, currentStack, action, context, slotIndex,currentStack.getCount());
            action = "removed";
            updatePlayerThread(playerUUID, previousStack, action, context, slotIndex,previousStack.getCount());


        }
    }


    private synchronized void updatePlayerThread(String playerUUID, ItemStack stack, String action,String context,int slotIndex,int change) {
        String itemName = stack.getName().getString();
        String itemType = Registries.ITEM.getId(stack.getItem()).toString();
        boolean isStackable = stack.isStackable();
        int maxStackSize = stack.getMaxCount();
        Item.Stacksize stacksize = getStackSizeFromValue(maxStackSize);
        Item item = new Item(itemName, itemType, isStackable, stacksize);
        if(context == "personal")
        {
            if(action.equals("added"))
            {
                this.inventory.getPlayerInv().removeItemFromInv(item,change,slotIndex);
            } else if (action.equals("removed")) {
                this.inventory.getPlayerInv().addItems(item,change,slotIndex);
            }
        }else if(context == "Friend")
        {
            PlayerThread targetThread = fetchPlayerThread(playerUUID);
            if (targetThread != null) {
                synchronized (targetThread) {
                    Total_Inventory targetInventory = targetThread.getApplication().getInventory();
                    if(action.equals("added"))
                    {
                        targetInventory.getPlayerInv().removeItemFromInv(item,change,slotIndex);
                        this.inventory.addReport("Friend "+targetThread.getPlayer().getName().getString()+" added "+change +" "+ itemName+" "+itemType+"!");
                    } else if (action.equals("removed")) {
                        targetInventory.getPlayerInv().addItems(item,change,slotIndex);
                        this.inventory.addReport("Friend "+targetThread.getPlayer().getName().getString()+" borrowed "+change +" "+ itemName+" "+itemType+"!");
                    }
                }
            }

        }else if(context == "stolen")
        {
            PlayerThread targetThread = fetchPlayerThread(playerUUID);

            if (targetThread != null) {
                synchronized (targetThread) {
                    Total_Inventory targetInventory = targetThread.getApplication().getInventory();
                    if(action.equals("added"))
                    {
                        targetInventory.getPlayerInv().removeItemFromInv(item,change,slotIndex);
                        this.inventory.addReport("Player "+targetThread.getPlayer().getName().getString()+" added "+change +" "+ itemName+" "+itemType+"!");
                    } else if (action.equals("removed")) {
                        targetInventory.getPlayerInv().addItems(item,change,slotIndex);
                        this.inventory.addReport("Player "+targetThread.getPlayer().getName().getString()+" stole "+change +" "+ itemName+" "+itemType+"!");
                    }
                }
            }
        }

    }
    public synchronized PlayerThread fetchPlayerThread(String playerUUID) {
        for(PlayerThread player :this.OurPlayerThread.getThreads())
        {
            if(player.getPlayerUUID().equals(playerUUID)){
                return player;
            }
        }
        return null;
    }
    public synchronized Total_Inventory getInventory()
    {
        return this.inventory;
    }
    public synchronized Item.Stacksize getStackSizeFromValue(int maxStackSize) {
        switch (maxStackSize) {
            case 1:
                return Item.Stacksize.ONE;
            case 16:
                return Item.Stacksize.SIXTEEN;
            case 64:
                return Item.Stacksize.SIXTYFOUR;
            default:
                return Item.Stacksize.SIXTYFOUR;
        }
    }
    private synchronized void addHopperThread(HopperThreads hopperThread) {
        HopperThreads.add(hopperThread);
    }
    public void onPlayerConnect(ServerPlayerEntity player) {

        if (player.equals(this.player)) {
            Map<String, Object> inventoryData = PrintItemsPerCategory();
            sendInventoryDataToClient(player, inventoryData);
        }
    }
    private void sendInventoryDataToClient(ServerPlayerEntity player, Map<String, Object> inventoryData) {
        InventoryDataPayload payload = new InventoryDataPayload(inventoryData);
        ServerPlayNetworking.send(player,payload);
    }

}
