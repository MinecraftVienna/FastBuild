package at.niemeczek.dev.fastbuild.build;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Set;

public class RoofHollow {
    private static Player player;
    public RoofHollow(Player player){
        this.player = player;
    }

    /**
     * Makes roof hollow
     * @param location Must be at the top of the roof!
     * @return always true
     */
    public boolean makeHollow(Location location){
        //get Material Player is looking at

        player.sendMessage("Hollowing out roof ...");
        Material material = location.getBlock().getType();
        //go one block deeper
        location.add(0, -1, 0);

        //move to lowest roof layer
        while (
                !isEmpty(location, 0, -1, 0) &&
                location.clone().add(0, -1, 0).getBlock().getType() == material
              ){
                location.add(0, -1, 0);
        }


        //remove layers one by one from bottom up
        while (!isEmpty(location, 0, 1, 0)){
            levelFloodFill(location.clone(), 2000, material);
            location.add(0, 1, 0);
        }

        player.sendMessage("... Done!");
        return true;
    }

    /**
     * Hollows out a single layer
     * @param location location to start from
     * @param i counter to prevent StackOverflow
     * @param material Material roof consists of
     * @return
     */
    private int levelFloodFill(Location location, int i, Material material){
        if (
                isEmpty(location, 0, 0, 0) ||       // Block is empty
                isEmpty(location, 0, 1, 0) ||       // Block above empty
                location.getBlock().getType() != material ||// Block different than roof
                i < 1                                       // StackOverflow prevention
           ){
            return i + 1;
        }
        // Removes Block
        location.getBlock().setType(Material.AIR);

        //RECURSION
        i = levelFloodFill(location.clone().add(1, 0, 0), i - 1, material);
        i = levelFloodFill(location.clone().add(0, 0, 1), i - 1, material);
        i = levelFloodFill(location.clone().add(-1, 0, 0), i - 1, material);
        i = levelFloodFill(location.clone().add(0, 0, -1), i - 1, material);

        return i;
    }
    /**
     * Test if block at (location + x, y, z) is set
     * @param location Location to start from
     * @param x Blocks to be added to location at X-Axis
     * @param y Blocks to be added to location at Y-Axis
     * @param z Blocks to be added to location at Z-Axis
     * @return true if empty, false if not empty
     */
    private boolean isEmpty(Location location, int x, int y, int z){
        return location.clone().add(x, y, z).getBlock().isEmpty();
    }
}
