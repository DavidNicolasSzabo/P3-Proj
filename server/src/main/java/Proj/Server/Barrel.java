package Proj.Server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;

public class Barrel extends StorageInventory {
    private static Integer BarrelIndex = 0;
    @JsonProperty
    private Integer BIndex;
    @JsonProperty
    private String worldName;
    @JsonIgnore
    private World world;
    @JsonIgnore
    private BlockPos Pos;
    public Barrel(String name, String Storagetype, String PlayerUUID, Collection<String> PlayerFriends,BlockPos Pos, World world) {
        super(name, 27, true, Storagetype, false, Stacksize.SIXTYFOUR,new int[]{Pos.getX(), Pos.getY(), Pos.getZ()},PlayerUUID,PlayerFriends);
        this.BIndex = BarrelIndex++;
        this.Pos = Pos;
        this.world = world;
    }
    public Integer getBIndex() {
        return BIndex;
    }
}
