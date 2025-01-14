package Proj.Server;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.item.ItemStack;

public class ShulkerBox extends StorageInventory {
    private static Integer ShulkerIndex = 0;
    @JsonProperty
    private Integer ShIndex;
    @JsonProperty
    private String worldName;
    @JsonIgnore
    private World world;
    @JsonIgnore
    private BlockPos Pos;
    @JsonCreator
    ShulkerBox(@JsonProperty("storageName") String name,@JsonProperty("storageType") String Storagetype,@JsonProperty("playerUUID") String PlayerUUID,@JsonProperty("playerFriends") Collection<String> PlayerFriends,BlockPos Pos,World world) {
        super(name, 27, false, Storagetype, false, Stacksize.ONE,new int[]{Pos.getX(), Pos.getY(), Pos.getZ()},PlayerUUID,PlayerFriends);
        this.ShIndex = ShulkerIndex++;
        this.worldName = world.getDimension().toString();
        this.Pos = Pos;
        this.world = world;
        InitializeShulkerContents();
    }

    private void InitializeShulkerContents() {

        BlockEntity blockEntity = world.getBlockEntity(Pos);

        if (blockEntity instanceof ShulkerBoxBlockEntity) {
            ShulkerBoxBlockEntity shulkerBoxEntity = (ShulkerBoxBlockEntity) blockEntity;
            Inventory shulkerInventory = shulkerBoxEntity;
            for (int i = 0; i < shulkerInventory.size(); i++) {
                ItemStack itemStack = shulkerInventory.getStack(i);

                if (!itemStack.isEmpty()) {
                    try {
                        Item customItem = new Item(
                                itemStack.getItem().getName().getString(),
                                itemStack.getItem().getTranslationKey(),
                                itemStack.isStackable(),
                                mapStackSize(itemStack.getMaxCount(), itemStack.getItem().getTranslationKey())
                        );
                        this.addItems(customItem, itemStack.getCount(),i);
                    } catch (CustomExcept e) {
                        server_mod.LOGGER.error(e.getMessage());
                    }

                }
            }
        }
    }

    private Item.Stacksize mapStackSize(int maxCount, String itemtype) throws CustomExcept {
        return switch (maxCount) {
            case 1 -> Item.Stacksize.ONE;
            case 16 -> Item.Stacksize.SIXTEEN;
            case 64 -> Item.Stacksize.SIXTYFOUR;
            default -> throw CustomExcept.forUnsupportedStackSize(maxCount, itemtype);
        };
    }
    public Integer getShIndex(){
        return ShIndex;
    }

}