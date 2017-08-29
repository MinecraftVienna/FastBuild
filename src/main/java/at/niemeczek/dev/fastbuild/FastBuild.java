package at.niemeczek.dev.fastbuild;

import org.bukkit.plugin.java.JavaPlugin;

public class FastBuild extends JavaPlugin {
    @Override
    public void onEnable() {
        // getLogger().info("!");
        // new LoginListener(this);
        // new MoveListener(this);
        this.getCommand("build").setExecutor(new FastBuildCommandExecutor(this));
    }

    @Override
    public void onDisable() {
     //   getLogger().info("onDisable has been invoked!");
    }


}
