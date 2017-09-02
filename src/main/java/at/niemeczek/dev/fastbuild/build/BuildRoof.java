package at.niemeczek.dev.fastbuild.build;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * This class creates a roof
 */

public class BuildRoof extends Throwable {
    private final Player player;
    /**
     *
     * @param player Player that will start Roof creation. Needs to be not null
     */
    public BuildRoof(Player player){
        this.player = player;

    }
    /**
     * Main Method for roof-building
     *
     * First: Tries to find Location that is height - 1 Blocks from edge away
     * Second: Builds base level
     * Third: Builds all the other levels on-by-one
     *
     * @param location Location to start the roof-building from. Needs to be within the plain the roof should be built on.
     * @param material Material the roof will consist of.
     * @param height Number of Layers the roof will be high. Needs to be > 0
     */
    public boolean construct(Location location, Material material, int height){
        //Tests for zero or negative roof height.
        if (height < 1){
            player.sendMessage("It is impossible to build a roof that has zero or negative height");
            return false;
        }

        int size = 2000;
        //location is within the roof-plain, move one block up
        location.setY( location.getY()+1 );
        location = findLocation(location, height);
        if (location == null){
            return false;
        }

        // All requirements for successful roof creation seem to be fulfilled
        player.sendMessage("Creating Roof ...");
        //create base level (as big as house)
        baseLevelFloodFill4Neighbour(location, size, material);
        //build another height - 1 levels on top of
        for (int j = 1; j < height; j++) {
            // Move Location Block up
            location.add(0, 1, 0);
            // Build Level
            anyLevelFloodFill4Neighbour(location, size, material);
        }
        player.sendMessage("... Done!");
        return true;

    }
    /**
     * Tries to find Location [height] Blocks away from each edge of the plain
     * (Moves the location [height] Blocks away from each edge, then tests if far enough away from each edge)
     *
     * @param location Location at the plain the roof will built at. Needs to be not null
     * @param height height of the roof that will be built. Needs to be > 1
     * @return location if location is found, null if not
     */
    private Location  findLocation (Location location, int height){
        //find a spot that is at least <height> blocks away from border to ensure correct roof creation
        while (isEmpty(location, (height -1), -1, 0) && !isEmpty(location, -1, -1, 0)){
            location.add(-1, 0, 0);
        }
        while (isEmpty(location, 0, -1, (height -1)) && !isEmpty(location, 0, -1, -1)){
            location.add(0, 0, -1);
        }
        while (isEmpty(location, -(height -1), -1, 0) && !isEmpty(location, 1, -1, 0)){
            location.add(1, 0, 0);
        }
        while (isEmpty(location, 0, -1, -(height -1)) && !isEmpty(location, 0, -1, 1)){
            location.add(0, 0, 1);
        }

        // Test if spot found is [height] blocks away from each edge
        //if no spot is found -> Warning
        if (    isEmpty(location, 0 , -1,0) ||
                isEmpty(location, (height -1) , -1,0) ||
                isEmpty(location, -(height -1), -1,0) ||
                isEmpty(location, 0, -1, (height -1)) ||
                isEmpty(location, 0, -1, -(height -1))
            ){
            this.player.sendMessage(ChatColor.RED + "Not able to locate a point that is at least " + height + " (height of the roof) blocks away from the edge of the house at each side. please select a starting point that is more suitable!");
            return null;
        }
        return location;
    }
    /**
     * Builds the base level of the roof. Roof covers whole plain completely.
     * Uses recursive 4-connected Floodfill-Algorithm
     * https://de.wikipedia.org/wiki/Floodfill
     *
     * @param location Location to start building level from. Needs to be isSuitable() - true
     * @param i Size of permitted Stack and block-placements to prevent StackOverflow and running to infinity when set in "Superflat" at the ground
     * @param material Material to build roof out of
     * @return Blocks left to place (intern, see i)
     */
    private int baseLevelFloodFill4Neighbour(Location location, int i, Material material) {

        // return if Block is not empty or Block underneath is NOT set (or i > 1 to prevent StackOverflow and running to infinity when set in "Superflat" at the ground)
        if (!isEmpty(location, 0, 0, 0) || isEmpty(location, 0, -1, 0) || i < 1){
            return i + 1;
        }
        location.getBlock().setType(material);
        i = baseLevelFloodFill4Neighbour(location.clone().add(1, 0, 0), i - 1, material);
        i = baseLevelFloodFill4Neighbour(location.clone().add(0, 0, 1), i - 1, material);
        i = baseLevelFloodFill4Neighbour(location.clone().add(-1, 0, 0), i - 1, material);
        i = baseLevelFloodFill4Neighbour(location.clone().add(0, 0, -1), i - 1, material);



        return i - 1;

    }
    /**
     * Builds the upper levels of the roof. Roof covers whole plain except blocks that share an edge with the edge of the plain.
     * Uses recursive 4-connected Floodfill-Algorithm
     * https://de.wikipedia.org/wiki/Floodfill
     *
     * @param location Location to start building level from. Needs to be isSuitable() - true
     * @param i Size of permitted Stack and block-placements to prevent StackOverflow and running to infinity when set in "Superflat" at the ground
     * @param material Material to build roof out of
     * @return Blocks left to place (intern, see i)
     */
    private int anyLevelFloodFill4Neighbour(Location location, int i, Material material) {
        // return if Block is not empty or Block is NOT isSuitable-true (or i > 1 to prevent StackOverflow and running to infinity when set in "Superflat" at the ground)
        if (!isEmpty(location, 0, 0, 0) || !isSuitable(location) || i < 1) {
            return i + 1;
        }
        // Places Block
        location.getBlock().setType(material);

        //RECURSION
        i = anyLevelFloodFill4Neighbour(location.clone().add(1, 0, 0), i - 1, material);
        i = anyLevelFloodFill4Neighbour(location.clone().add(0, 0, 1), i - 1, material);
        i = anyLevelFloodFill4Neighbour(location.clone().add(-1, 0, 0), i - 1, material);
        i = anyLevelFloodFill4Neighbour(location.clone().add(0, 0, -1), i - 1, material);




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
    /**
     * Tests if all the blocks in the layer below that share one edge with location-block are not empty.
     * Used to create a 1-block free border at the edges of layers.
     * @param location location to be tested
     * @return true if all the blocks are set
     */
    private boolean isSuitable(Location location){
        return (!isEmpty(location, 1, -1, 0) &&
                !isEmpty(location, -1, -1, 0) &&
                !isEmpty(location, 0, -1, 1) &&
                !isEmpty(location, 0, -1, -1)
        );
    }
}

