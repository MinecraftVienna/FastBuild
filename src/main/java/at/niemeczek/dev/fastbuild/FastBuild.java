package at.niemeczek.dev.fastbuild;

import org.bukkit.plugin.java.JavaPlugin;

public class FastBuild extends JavaPlugin {
//    public JavaPlugin getPlugin(){
//        return this;
//    }

    @Override
    public void onEnable() {



        // Command Executor for "/build"
        this.getCommand("build").setExecutor(new FastBuildCommandExecutor(this));
    }

    @Override
    public void onDisable() {}


}
