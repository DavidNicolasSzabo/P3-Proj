package Proj.Server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;

public class Hopper extends StorageInventory {
    private static Integer HopperIndex = 0;
    @JsonProperty
    private Integer HIndex;
    @JsonProperty
    private String worldName;
    @JsonIgnore
    private World world;
    @JsonIgnore
    private BlockPos Pos;
    public Hopper(String name, String Storagetype, String PlayerUUID, Collection<String> PlayerFriends,BlockPos Pos, World world) {
        super(name, 5, true, Storagetype, false, Stacksize.SIXTYFOUR,new int[]{Pos.getX(), Pos.getY(), Pos.getZ()},PlayerUUID,PlayerFriends);
        this.HIndex = HopperIndex++;
        this.worldName = world.getDimension().toString();
        this.Pos = Pos;
        this.world = world;
    }
    public Integer getHIndex() {
        return HIndex;
    }
}
