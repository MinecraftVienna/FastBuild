package at.niemeczek.dev.fastbuild;

import at.niemeczek.dev.fastbuild.build.BuildRoof;
import at.niemeczek.dev.fastbuild.build.RoofHollow;
import at.niemeczek.dev.fastbuild.logBuild.Log;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

import static java.lang.Integer.parseInt;
import static org.bukkit.Bukkit.getServer;

public class FastBuildCommandExecutor implements CommandExecutor {

    public final FastBuild plugin;

    public FastBuildCommandExecutor(FastBuild plugin) {
        this.plugin = plugin;
    }

    /**
     * processes all the entered commands
     * Available subcommands:
     *  - roof
     *  - roof_make_hollow / rmh
     *  - listbuilds [player]
     *  - undo [player]
     *
     * @param sender Wo has sent the command
     * @param cmd    command that has been entered, currently only "build"
     * @param label  Alias of the used command. Currently not processed.
     * @param args   Array with all the arguments. args[0] is always a subcommand of /build
     * @return returns true if command succeeded and also when the command had problems, false is only returned when usage: from command.yml should be shown.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //test if command is "build" and if it has been entered by Player
        if (cmd.getName().equalsIgnoreCase("build") && (sender instanceof Player)) {
            if (args.length < 1) {
                sender.sendMessage(ChatColor.DARK_RED + " Invalid command!");
                return false;
            }
            switch (args[0]) {
                case "roof":
                    return onCommand_roof(sender, args);
                case "roof_make_hollow":
                    return onCommand_roof_make_hollow(sender);
                case "rmh":
                    return onCommand_roof_make_hollow(sender);
                case "listbuilds":
                    return onCommand_listbuilds(sender, args);
                case "undo":
                    return onCommand_undo(sender, args);
                case "clearhistory":
                    return new Log(this.plugin, ((Player) sender)).clearHistory();
            }

        }
        return false;
    }

    /**
     * Processes the command /build roof ...
     * Roof is built at the place the player looks at.
     *
     * @param sender Sender of the command. Needs to be player.
     * @param args Arguments of the command. Need to be material (args[1]) and height (args[2], Number of Layers of the roof)
     * @return returns always true
     */
    private boolean onCommand_roof(CommandSender sender, String[] args) {
        //Tests if arraylength is enough to contain all the arguments needed for that command
        if (args.length < 3) {
            sender.sendMessage(ChatColor.DARK_RED + "Command usage: /build roof <Material> [height]");
            return true;
        }
        //Find out Material to build roof out of
        Material material = Material.matchMaterial(args[1]);
        if (material == null) {
            sender.sendMessage(ChatColor.RED + args[1] + " is not a valid material!");
            return true;
        } else if (!material.isSolid()){
            sender.sendMessage(ChatColor.RED + args[1] + " is not a solid material! Please choose a solid material!");
            return true;
        }
        //Find out desired height of the roof
        int height;
        try {
            height = parseInt(args[2]);
        } catch (NumberFormatException nFE) {
            sender.sendMessage(ChatColor.RED + "The height could not be read as an Integer. \n The correct command is /build roof <Material> [height]");
            return true;
        }
        // Find out location to start roof building process from.
        Location location = ((Player) sender).getTargetBlock((Set<Material>) null, 30).getLocation();
        if (location.getBlock().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Please look at the spot you want the roof to be built at! \nMaybe you are too far away?");
            return true;
        }


        //Build Roof
        BuildRoof buildRoof = new BuildRoof(plugin, ((Player) sender).getPlayer());
        buildRoof.construct(location, material, height);

        return true;
    }

    /**
     * Processes the command /build roof_make_hollow
     *
     * @param sender Sender of the command
     * @return always true
     */
    private boolean onCommand_roof_make_hollow(CommandSender sender){
        // Find out location to start roof hollowing process from.
        Location location = ((Player) sender).getTargetBlock((Set<Material>) null, 30).getLocation();
        if (location.getBlock().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Please look at the upper Layer of the roof you want to make hollow! \nMaybe you are too far away?");
            return true;
        }

        //Make roof hollow
        (new RoofHollow(plugin, ((Player) sender).getPlayer())).makeHollow(location);
        return true;
    }

    /**
     * Processes the command /build listbuilds [player]
     *
     * @return returns always true
     */
    private boolean onCommand_listbuilds(CommandSender sender, String[] args){
        // find out which players actions should be listed
        Player commandAffected;
        if (args.length == 1){
            commandAffected = (Player) sender;
        } else if (args.length == 2){
            commandAffected = getServer().getPlayer(args[2]);
            if (commandAffected == null){
                sender.sendMessage(ChatColor.RED + args[2] + " is not online!");
                return true;
            }
        } else {
            sender.sendMessage("The command is " + ChatColor.RED + "/build listbuilds [player]");
            return true;
        }
        // list actions
        sender.sendMessage((new Log(plugin, commandAffected)).showStoredFiles());
        return true;
    }

    /**
     * Processes the command /build undo [number] [player]
     * if number is not set, last one is chosen
     * if player is not set, CommandSender is chosen
     *
     * @param sender Sender of the command, must not be Console or null
     * @param args arguments can be:
     *              - no Arguments
     *              - number
     *              - number and player
     *
     * @return returns always true
     */
    private boolean onCommand_undo(CommandSender sender, String[] args){
        // find out which players actions should be undone
        Player affectedPlayer;
        if (args == null || args.length < 3){
            affectedPlayer = (Player) sender;
        } else if (args.length == 3){
            affectedPlayer = getServer().getPlayer(args[2]);
            if (affectedPlayer == null){
                sender.sendMessage(ChatColor.RED + args[2] + " is not online!");
                return true;
            }
        } else {
            sender.sendMessage("The command is " + ChatColor.RED + "/build undo [number] [player]");
            return true;
        }

        // find out which action should be undone
        int undo_fileNr;
        Log log = new Log(this.plugin, affectedPlayer);
        if(args == null || args.length < 2){ //find out latest action, if no action is specified
            undo_fileNr = log.latestFile();
        } else{ //if action is specified, test if it is valid
            try {
                undo_fileNr = parseInt(args[1]);
            } catch (NumberFormatException nFE) {
                sender.sendMessage(
                        ChatColor.RED + "It could not be identified which action should be undone since " + args[1] +
                        " could not be read as an Integer. \n The correct command is /build undo [number] [player]"
                );
                return true;
            }
        }
        //undo
        log.buildUndo(undo_fileNr);
        return true;
    }

}
