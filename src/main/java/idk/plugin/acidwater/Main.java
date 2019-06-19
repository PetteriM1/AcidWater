package idk.plugin.acidwater;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockWater;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.potion.Effect;
import cn.nukkit.scheduler.NukkitRunnable;
import cn.nukkit.utils.Config;

public class Main extends PluginBase {

    public void onEnable() {
        saveDefaultConfig();
        Config c = getConfig();
        Effect e = Effect.getEffect(Effect.POISON).setVisible(c.getBoolean("effect-visible")).setDuration(c.getInt("acid-duration-ticks")).setAmplifier(c.getInt("effect-amplifier"));
        getServer().getScheduler().scheduleDelayedRepeatingTask(this, new NukkitRunnable() {

            @Override
            public void run() {
                for (Player p : getServer().getOnlinePlayers().values()) {
                    if (c.getBoolean("acid-rain") && p.getLevel().isRaining()) {
                        if (!p.hasEffect(Effect.POISON) && canSeeSky(p)) {
                            p.addEffect(e.clone());
                        }
                    }
                    
                    if (c.getBoolean("acid-water") && isInWater(p)) {
                        if (!p.hasEffect(Effect.POISON)) {
                            p.addEffect(e.clone());
                        }
                    }
                }
            }
        }, c.getInt("acid-check-ticks", 20), c.getInt("acid-check-ticks", 20));
    }

    boolean canSeeSky(Player p) {
        for (int i = (int) p.y + 1; i <= 255; i++) {
            if (!(p.getLevel().getBlock((int) p.x, i, (int) p.z, false) instanceof BlockAir)) {
                return false;
            }
        }

        return true;
    }

    boolean isInWater(Player p) {
        for (Block b : p.getCollisionBlocks()) {
            if (b instanceof BlockWater) {
                return true;
            }
        }

        return false;
    }
}
