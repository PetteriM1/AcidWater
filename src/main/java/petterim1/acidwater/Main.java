package petterim1.acidwater;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockWater;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.Config;

public class Main extends PluginBase implements Listener {

    private Effect effect;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Config config = getConfig();

        effect = Effect.getEffect(Effect.POISON).setVisible(config.getBoolean("effect-visible")).setDuration(config.getInt("acid-duration-ticks")).setAmplifier(config.getInt("effect-amplifier"));

        final boolean rain = config.getBoolean("acid-rain");
        final boolean water = config.getBoolean("acid-water");
        final boolean mobs = config.getBoolean("any-mob");

        if (config.getBoolean("acid-water-bottle")) {
            getServer().getPluginManager().registerEvents(this, this);
        }

        getServer().getScheduler().scheduleDelayedRepeatingTask(this, () -> {
            if (mobs) {
                for (Level level : getServer().getLevels().values()) {
                    for (Entity entity : level.getEntities()) {
                        if (!(entity instanceof EntityLiving)) {
                            continue;
                        }

                        if (rain && entity.getLevel().isRaining()) {
                            if (!entity.hasEffect(Effect.POISON) && entity.canSeeSky()) {
                                entity.addEffect(effect.clone());
                            }
                        }

                        if (water && isInWater(entity)) {
                            if (!entity.hasEffect(Effect.POISON)) {
                                entity.addEffect(effect.clone());
                            }
                        }
                    }
                }
            } else {
                for (Player player : getServer().getOnlinePlayers().values()) {
                    if (rain && player.getLevel().isRaining()) {
                        if (!player.hasEffect(Effect.POISON) && player.canSeeSky()) {
                            player.addEffect(effect.clone());
                        }
                    }

                    if (water && isInWater(player)) {
                        if (!player.hasEffect(Effect.POISON)) {
                            player.addEffect(effect.clone());
                        }
                    }
                }
            }
        }, config.getInt("acid-check-ticks", 20), config.getInt("acid-check-ticks", 20));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void PlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
        if (event.getItem().getId() == ItemID.POTION && event.getItem().getDamage() == 0) {
            if (!event.getPlayer().hasEffect(Effect.POISON)) {
                event.getPlayer().addEffect(effect.clone());
            }
        }
    }

    private static boolean isInWater(Entity entity) {
        for (Block block : entity.getCollisionBlocks()) {
            if (block instanceof BlockWater) {
                return true;
            }
        }

        return false;
    }
}
