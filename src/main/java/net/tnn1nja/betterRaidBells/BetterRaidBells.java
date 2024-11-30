package net.tnn1nja.betterRaidBells;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BellResonateEvent;
import org.bukkit.event.block.BellRingEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.logging.Logger;

public final class BetterRaidBells extends JavaPlugin implements Listener {

    Logger log = Bukkit.getLogger();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        log.info("[BetterRaidBells] BetterRaidBells loaded.");
    }

    @EventHandler
    public void onBellRing(BellRingEvent e){
        Location l = e.getBlock().getLocation();
        Collection<Entity> entities = l.getWorld().getNearbyEntities(l, 128, 128, 128);
    }

    @EventHandler
    public void onBellResonate(BellResonateEvent e){
        e.getResonatedEntities().clear();
    }

    @Override
    public void onDisable() {
        log.info("[BetterRaidBells] BetterRaidBells loaded.");
    }
}
