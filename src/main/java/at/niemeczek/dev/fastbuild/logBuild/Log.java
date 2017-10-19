package at.niemeczek.dev.fastbuild.logBuild;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.*;

//https://bukkit.org/threads/making-a-log-file-for-your-plugins.85430/


public class Log {
    Plugin plugin;
    Player player;
    File file = null;
    File userfolder;
    //Konstruktor

    /**
     * Constructor,
     * - fills plugin and player variables,
     * - creates plugin- and user-folder if they don't exist
     *
     * @param plugin this Plugin
     * @param player Player that logs will be associated with
     */
    public Log(Plugin plugin, Player player){
        System.out.println("log");
        this.plugin = plugin;
        this.player = player;
        File dataFolder =  plugin.getDataFolder();
        if (!dataFolder.exists()){
            dataFolder.mkdir();
        }
        this.userfolder = new File("" + dataFolder.getPath() + "/" + player.getUniqueId());
        if (!this.userfolder.exists()){
            this.userfolder.mkdir();
            System.out.println(this.userfolder.getName());
        }
    }
    private boolean createNextFile(){
        //Test how many files are stored
        int j = 0, i = 0;
        for (; i < 1000; i++) {
            if (new File(userfolder, i + ".txt").exists()){
                j++;
            }
        }
        // if more than hundred are stored -> Capacity reached, returns false
        if (j >= 100){
            this.player.sendMessage(ChatColor.RED + "Your capacity for storing build history files is exceeded! Please contact the administrator!");
            return false;
        } else {
            try {
                file = new File(userfolder, j + ".txt");
                System.out.println(file.createNewFile());

            } catch (IOException io){
                System.out.println("FILECREATION ERROR");
                player.sendMessage(ChatColor.RED + "There was an error when trying to create a new file for logging built blocks!");
                io.printStackTrace();
                return false;
            }
        }
        return true;
    }
    public boolean logBlockToFile(Location location){
        System.out.println(file);
        if (file == null){
            if (createNextFile() == false){
                System.out.println("FileCreation failed!");
                return false;
            }
            System.out.println("logBlockToFile");
        }
        System.out.println("logBlockToFile2");
        try {

            FileWriter fileWriter = new FileWriter(file, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            printWriter.println(
                    location.getX() + " " + location.getY() + " " + location.getZ() + " " +
                    location.getWorld().getName() + " " + location.getBlock().getType()
            );

            printWriter.flush();
            printWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

// WRITES ONLY A SINGLE LINE WITH DATA, WHY???
//        try {
//            BufferedWriter out = null;
//            try {
//                out = new BufferedWriter(new FileWriter(this.file));
//                out.write(location.getX() + " " + location.getY() + " " + location.getZ() + " " +
//                        location.getWorld().getName() + " " + location.getBlock().getType() + "\n");
//            } finally {
//                if (out != null){
//                    out.close();
//                }
//            }
//
//
//        } catch (IOException ex){
//            player.sendMessage("OOPS! Something went wrong! A file could not be created, logging failed!");
//            System.err.println("I/O ERROR: " + ex.getMessage());
//        }

        return true;

    }

    public String[] showStoredFiles() {
        //Find out number of files in userfolder to create correctly sized array
        int n = 0;
        for (int i = 0;i < 1000; i++) {
            if (new File(this.userfolder, i + ".txt").exists()){
                n++;
            }
        }
        String[] strings = new String[n];

        // Read every Files first line and add to array
            try {
                BufferedReader in = null;
                try {
                    for (int j = 0, k = 0; j <= 1000; j++) {
                        if(new File(this.userfolder, j + ".txt").exists()){
                            in = new BufferedReader(new FileReader(new File(userfolder, j + ".txt")));
                            strings[k] = "" + j + ": " + in.readLine();
                            k++;
                            in.close();
                        }
                    }
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
            } catch (IOException ex) {
                player.sendMessage("OOPS! Something went wrong! There was an I/O-Exception!");
                System.err.println("I/O ERROR: " + ex.getMessage());
            }
        return strings;
    }

    /**
     * finds file with highest number
     */
    public int latestFile(){
        int j = 0;
        for (int i = 0;  i < 110; i++) {
            if ((new File(this.userfolder, i + ".txt").exists())){
                j = i;
            }
        }
        return j;

    }
    public void buildUndo(int filenumber){

        // undo every line in file
        try {
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(new File(userfolder, filenumber + ".txt")));
                String line;
                Location location = null;
                for (int i = 0; (line = in.readLine()) != null; i++) {
                    String[] strings = line.split(" ");
                    double x, y, z;
                    try {
                        x = Double.parseDouble(strings[0]);
                        y = Double.parseDouble(strings[1]);
                        z = Double.parseDouble(strings[2]);
                    } catch (NumberFormatException e) {
                        this.player.sendMessage(ChatColor.RED + "Could not read location from file! Undo impossible!");
                        return;
                    }
                    location = new Location(Bukkit.getWorld(strings[3]), x, y, z);

                    location.getBlock().setType(Material.matchMaterial(strings[4]));

                }
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } catch (IOException ex) {
            player.sendMessage("OOPS! Something went wrong! There was an I/O-Exception!");
            System.err.println("I/O ERROR: " + ex.getMessage());
        }
        new File(userfolder, filenumber + ".txt").delete();
    }
    public boolean clearHistory(){
        player.sendMessage("Clearing History ...");
        int i = 0;
        for (int j = 0, k = 0; j <= 1000; j++) {
            if(new File(this.userfolder, j + ".txt").exists()){
                i++;
                new File(this.userfolder, j + ".txt").delete();
            }
        }
        player.sendMessage("... Done, deleted " + i + " files!");
        return true;
    }
}
