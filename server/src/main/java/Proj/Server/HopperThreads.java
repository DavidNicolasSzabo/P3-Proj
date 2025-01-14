package Proj.Server;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.Arrays;
import java.util.List;

public class HopperThreads extends Thread{

    private HopperBlockEntity Hopper;
    private ServerWorld world;
    private Total_Inventory inventory;
    private boolean running;
    HopperThreads(HopperBlockEntity Hopper, ServerWorld world, Total_Inventory inventory,boolean running) {
        this.Hopper = Hopper;
        this.world = world;
        this.inventory = inventory;
        this.running = running;
    }
    public void run() {
        synchronized (this){
            while(running==true){
                monitorHopperBehavior(Hopper, world);
                try {
                    Thread.sleep(50); // 1 in game tick;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

    }
    private synchronized void monitorHopperBehavior(HopperBlockEntity hopper, ServerWorld world) {
        BlockPos pos = hopper.getPos();


        boolean hasRedstoneSignal = world.isReceivingRedstonePower(pos);

        // Check for item pickup above the hopper
        BlockPos abovePos = pos.up();
        BlockEntity Blockabove= world.getBlockEntity(abovePos);
        Inventory aboveInventory = HopperBlockEntity.getInventoryAt(world, abovePos);
        if (aboveInventory != null && hasRedstoneSignal == false && !(aboveInventory instanceof HopperBlockEntity) ) {

                for (int i = 0; i < aboveInventory.size(); i++) {
                    ItemStack stack = aboveInventory.getStack(i);
                    if (!stack.isEmpty()) {
                        String itemName = stack.getName().getString();
                        String itemType = Registries.ITEM.getId(stack.getItem()).toString();
                        boolean isStackable = stack.isStackable();
                        int maxStackSize = stack.getMaxCount();
                        Item.Stacksize stacksize = getStackSizeFromValue(maxStackSize);
                        Item item = new Item(itemName, itemType, isStackable, stacksize);
                        if (aboveInventory instanceof Inventory) {
                            if (Blockabove.getType() == BlockEntityType.CHEST || Blockabove.getType() == BlockEntityType.TRAPPED_CHEST || Blockabove.getType() == BlockEntityType.SHULKER_BOX || Blockabove.getType() == BlockEntityType.BARREL) {
                                synchronized (inventory) {
                                    for (StorageInventory FrontStorage : inventory.getStorageInv()) {
                                        int[] customPosition = FrontStorage.getPosition();
                                        if (Arrays.equals(FrontStorage.getPosition(), new int[]{abovePos.getX(), abovePos.getY(), abovePos.getZ()})) {
                                            FrontStorage.removeItemFromInv(item, 1, i);
                                        }
                                        BlockPos Position = new BlockPos(customPosition[0], customPosition[1], customPosition[2]);
                                        if (pos.equals(Position)) {

                                                for (StorageInventory.Slot slot : FrontStorage.getStorageSlots()) {
                                                    if (slot.getItems() == null) {
                                                        FrontStorage.addItems(item, 1, slot.index);
                                                        return;
                                                    } else if (slot.getItems().equals(item) && slot.items.size() < slot.stacksize.getstackValue() && slot.index < FrontStorage.getStorageCapacity() - 1) {
                                                        FrontStorage.addItems(item, 1, slot.index);
                                                        return;
                                                    } else if (slot.getItems().equals(item) && slot.items.size() == slot.stacksize.getstackValue() && slot.index < FrontStorage.getStorageCapacity() - 1) {
                                                    } else if (slot.getItems().equals(item) && slot.items.size() == slot.stacksize.getstackValue() && slot.index == FrontStorage.getStorageCapacity() - 1) {
                                                        return;
                                                    }
                                                }

                                        }


                                    }
                                }
                            }
                        }
                    }
                }

        }
        // This is to check if there are items above(not a storage but this block has that functionality)
        if (!hasRedstoneSignal) {
            monitorHopperPickups(hopper, world);
        }

        // Check if the hopper is transferring items to the facing block (bellowPos because initially I was checking only bellow, but realised that it's needed to check the facing block anyway and did not want to change the naming
        BlockPos belowPos = hopper.getPos().offset(hopper.getCachedState().get(Properties.FACING).getOpposite());
        Inventory belowInventory = HopperBlockEntity.getInventoryAt(world, belowPos);
        BlockEntity Block = world.getBlockEntity(belowPos);
        if (belowInventory != null && hasRedstoneSignal == false) {
            TransferItems(belowPos,hopper,Block,hasRedstoneSignal,pos);
        }

    }
    public synchronized void TransferItems(BlockPos pos, HopperBlockEntity hopper,BlockEntity Block, boolean hasRedstoneSignal,BlockPos Hopperpos) {
        if (hasRedstoneSignal) {
            return;
        }
        for (int i = 0; i < hopper.size(); i++) {
            ItemStack stack = hopper.getStack(i);
            while (!stack.isEmpty()) {
                String itemName = stack.getName().getString();
                String itemType = Registries.ITEM.getId(stack.getItem()).toString();
                boolean isStackable = stack.isStackable();
                int maxStackSize = stack.getMaxCount();
                Item.Stacksize stacksize = getStackSizeFromValue(maxStackSize);
                Item item = new Item(itemName, itemType, isStackable, stacksize);
                if (Block instanceof Inventory) {
                    if (Block.getType() == BlockEntityType.CHEST || Block.getType() == BlockEntityType.TRAPPED_CHEST || Block.getType() == BlockEntityType.SHULKER_BOX || Block.getType() == BlockEntityType.BARREL || Block.getType() == BlockEntityType.HOPPER) {
                        synchronized (inventory) {
                            for (StorageInventory FrontStorage : inventory.getStorageInv()) {
                                int[] customPosition = FrontStorage.getPosition();
                                if (Arrays.equals(FrontStorage.getPosition(), new int[]{Hopperpos.getX(), Hopperpos.getY(), Hopperpos.getZ()})) {
                                    FrontStorage.removeItemFromInv(item, 1, i);
                                }
                                BlockPos Position = new BlockPos(customPosition[0], customPosition[1], customPosition[2]);
                                if (pos.equals(Position)) {

                                    for (StorageInventory.Slot slot : FrontStorage.getStorageSlots()) {
                                        if (slot.getItems() == null) {
                                            FrontStorage.addItems(item, 1, slot.index);
                                            return;
                                        } else if (slot.getItems().equals(item) && slot.items.size() < slot.stacksize.getstackValue() && slot.index < FrontStorage.getStorageCapacity() - 1) {
                                            FrontStorage.addItems(item, 1, slot.index);
                                            return;
                                        } else if (slot.getItems().equals(item) && slot.items.size() == slot.stacksize.getstackValue() && slot.index < FrontStorage.getStorageCapacity() - 1) {
                                        } else if (slot.getItems().equals(item) && slot.items.size() == slot.stacksize.getstackValue() && slot.index == FrontStorage.getStorageCapacity() - 1) {
                                            return;
                                        }
                                    }
                                }


                            }
                        }
                    }
                }

            }
        }
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
    public synchronized void setRunning(boolean running) {
        this.running = running;
    }
    private synchronized void monitorHopperPickups(HopperBlockEntity hopper, ServerWorld world) {
        BlockPos hopperPos = hopper.getPos();
        Box hopperBox = new Box(hopperPos.getX() - 0.5, hopperPos.getY() - 0.5, hopperPos.getZ() - 0.5, hopperPos.getX() + 0.5, hopperPos.getY() + 0.5, hopperPos.getZ() + 0.5);
        synchronized (inventory) {
            for (StorageInventory Hopper : inventory.getStorageInv()) {
                if (Arrays.equals(Hopper.getPosition(), new int[]{hopperPos.getX(), hopperPos.getY(), hopperPos.getZ()})) {
                    List<ItemEntity> itemEntities = world.getEntitiesByClass(ItemEntity.class, hopperBox, entity -> true);
                    for (ItemEntity itemEntity : itemEntities) {
                        ItemStack stack = itemEntity.getStack();
                        while (!stack.isEmpty()) {
                            Integer quantity = stack.getCount();
                            String itemName = stack.getName().getString();
                            String itemType = Registries.ITEM.getId(stack.getItem()).toString();
                            boolean isStackable = stack.isStackable();
                            int maxStackSize = stack.getMaxCount();
                            Item.Stacksize stacksize = getStackSizeFromValue(maxStackSize);
                            Item item = new Item(itemName, itemType, isStackable, stacksize);
                            for (StorageInventory.Slot slot : Hopper.getStorageSlots()) {
                                if (slot.getItems() == null) {
                                    Hopper.addItems(item, quantity, slot.index);
                                    stack.decrement(quantity);
                                    return;
                                } else if (slot.getItems().equals(item) && slot.items.size() < slot.stacksize.getstackValue() && slot.index < Hopper.getStorageCapacity() - 1) {
                                    Hopper.addItems(item, quantity - slot.items.size(), slot.index);
                                    stack.decrement(quantity - slot.items.size());
                                    return;
                                } else if (slot.getItems().equals(item) && slot.items.size() == slot.stacksize.getstackValue() && slot.index < Hopper.getStorageCapacity() - 1) {
                                } else if (slot.getItems().equals(item) && slot.items.size() == slot.stacksize.getstackValue() && slot.index == Hopper.getStorageCapacity() - 1) {
                                    return;
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}
