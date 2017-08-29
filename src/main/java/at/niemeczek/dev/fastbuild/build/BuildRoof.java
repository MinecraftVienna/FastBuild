package at.niemeczek.dev.fastbuild.build;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class BuildRoof {
    private final Player player;

    public BuildRoof(Player player){
        this.player = player;
    }
    public BuildRoof(){
        this.player = null;
    }


    public void construct(Player player, Location location, Material material, int height){
        int i = 2000;
        //location ist innerhalb des Hausdaches, bewege nach oben
        location.setY( location.getY()+1 );
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
        //if no spot is found -> Warning
        if (

                        isEmpty(location, (height -1) , -1,0) ||
                        isEmpty(location, -(height -1), -1,0) ||
                        isEmpty(location, 0, -1, (height -1)) ||
                        isEmpty(location, 0, -1, -(height -1))

           ){
                    player.sendMessage(ChatColor.RED + "Not able to locate a point that is at least " + height + " (height of the roof) blocks away from the edge of the house at each side. please select a starting point that is more suitable!");
                    return;
        }



        baseLevelAlgorithm(location, i, material);
        RoofAlgorithm(location.add(0,1,0), material, height - 2, i);
    }

    private void RoofAlgorithm(Location location, Material material, int height, int size){
        levelAlgorithm(location, size, material);
        if (height >= 1){
            RoofAlgorithm(location.add(0, 1, 0), material, --height, size);
        }
    }
    private int baseLevelAlgorithm(Location location, int i, Material material) {



        if (player != null) {
            System.out.println("Coordinates: " + location.getX() + " " + location.getY() + " " + location.getZ() + "\n Int " + i);
        } else System.out.println("ERROR no Player");




        location.getBlock().setType(material);


        if (isEmpty(location, 1, 0, 0) && !isEmpty(location, 1, -1, 0) && i > 1) { // nach "oben"
            System.out.println("+x");
            i = baseLevelAlgorithm(location.clone().add(1, 0, 0), --i, material);
        }
        if (isEmpty(location, 0, 0, 1) && !isEmpty(location, 0, -1, 1) && i > 1) { // nach "links"
            System.out.println("+z");
            i = baseLevelAlgorithm(location.clone().add(0, 0, 1), --i, material);
        }
        if (isEmpty(location, -1, 0, 0) && !isEmpty(location, -1, -1, 0) && i > 1) { // nach "unten"
            System.out.println("-x");
            i = baseLevelAlgorithm(location.clone().add(-1, 0, 0), --i, material);

        }
        if (isEmpty(location, 0, 0, -1) && !isEmpty(location, 0, -1, -1) && i > 1) { // nach "rechts"
            System.out.println("-y");
            i = baseLevelAlgorithm(location.clone().add(0, 0, -1), --i, material);
        }


        return i;

    }
    private int levelAlgorithm(Location location, int i, Material material) {



        if (player != null) {
            System.out.println("Coordinates: " + location.getX() + " " + location.getY() + " " + location.getZ() + "\n Int " + i);
        } else System.out.println("ERROR no Player");



        if (isSuitable(location)) {
            System.out.println("set");
            location.getBlock().setType(material);
        }


        if (isEmpty(location, 1, 0, 0) && isSuitable(location) && i > 1) { // nach "oben"
            System.out.println("+x");
            i = levelAlgorithm(location.clone().add(1, 0, 0), --i, material);
        }
        if (isEmpty(location, 0, 0, 1) && isSuitable(location) && i > 1) { // nach "links"
            System.out.println("+z");
            i = levelAlgorithm(location.clone().add(0, 0, 1), --i, material);
        }
        if (isEmpty(location, -1, 0, 0) && isSuitable(location) && i > 1) { // nach "unten"
            System.out.println("-x");
            i = levelAlgorithm(location.clone().add(-1, 0, 0), --i, material);

        }
        if (isEmpty(location, 0, 0, -1) && isSuitable(location) && i > 1) { // nach "rechts"
            System.out.println("-y");
            i = levelAlgorithm(location.clone().add(0, 0, -1), --i, material);
        }


        return i;

    }
    private boolean isEmpty(Location location, int x, int y, int z){
        return location.clone().add(x, y, z).getBlock().isEmpty();
    }
    private boolean isSuitable(Location location){
        return (!isEmpty(location, 1, -1, 0) &&
                !isEmpty(location, -1, -1, 0) &&
                !isEmpty(location, 0, -1, 1) &&
                !isEmpty(location, 0, -1, -1)
        );
    }
}

