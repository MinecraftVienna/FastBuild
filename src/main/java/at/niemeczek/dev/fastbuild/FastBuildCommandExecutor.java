package at.niemeczek.dev.fastbuild;

import at.niemeczek.dev.fastbuild.build.BuildRoof;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

import static java.lang.Integer.parseInt;

public class FastBuildCommandExecutor implements CommandExecutor {

    public final FastBuild plugin;

    public FastBuildCommandExecutor(FastBuild plugin){
            this.plugin = plugin;
        }


        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
            if (cmd.getName().equalsIgnoreCase("build") && (sender instanceof Player)){
                switch (args[0]){
                    case "roof":
                        Material material;
                        try {
                            material = Material.matchMaterial(args[1]);
                        } catch (NullPointerException nPE) {
                            sender.sendMessage(ChatColor.RED + args[1] + " is not a valid material!");
                            return true;
                        }
                        int height;
                        try {
                            height = parseInt(args[2]);
                        }catch (NumberFormatException nFE) {
                            sender.sendMessage(ChatColor.RED + "The height could not be read as an Integer. \n The correct command is /build roof <Material> [height]");
                            return true;
                        }
                        sender.sendMessage("Creating Roof ...");
                        BuildRoof buildRoof = new BuildRoof(((Player) sender).getPlayer());
                        buildRoof.construct(((Player) sender).getPlayer(), ((Player) sender).getTargetBlock((Set<Material>) null, 100).getLocation(), material, height);
                        return true;

                }

            }
            return false;
    }
}
