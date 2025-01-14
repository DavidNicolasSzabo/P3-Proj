package Proj.Server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;

public class NormalChest extends StorageInventory {
    private static Integer ChestIndex = 0;
    @JsonProperty
    private Integer CIndex;

    @JsonProperty
    private String worldName;
    @JsonIgnore
    private World world;
    @JsonIgnore
    private BlockPos Pos;
    public NormalChest(String name, String Storagetype, String PlayerUUID, Collection<String> PlayerFriends,BlockPos Pos, World world) {
        super(name, 27, true, Storagetype, false, Stacksize.SIXTYFOUR,new int[]{Pos.getX(), Pos.getY(), Pos.getZ()},PlayerUUID,PlayerFriends);
        this.CIndex = ChestIndex++;
        this.worldName = world.getDimension().toString();
        this.Pos = Pos;
        this.world = world;
    }
    public Integer getCIndex() {
        return CIndex;
    }
}
