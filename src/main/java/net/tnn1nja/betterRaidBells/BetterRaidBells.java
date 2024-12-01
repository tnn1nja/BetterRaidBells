package net.tnn1nja.betterRaidBells;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Raider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BellResonateEvent;
import org.bukkit.event.block.BellRingEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

public final class BetterRaidBells extends JavaPlugin implements Listener {

    //Constants
    Logger log = Bukkit.getLogger();
    int vanillaDetectionRadius = 32;
    int customDetectionRadius = 128;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        log.info("[BetterRaidBells] BetterRaidBells loaded.");
    }

    @Override
    public void onDisable() {
        log.info("[BetterRaidBells] BetterRaidBells loaded.");
    }

    @EventHandler
    public void onBellRing(BellRingEvent e){
        //Determine if Vanilla Resonate Event Fires
        Location l = e.getBlock().getLocation();
        Collection<Entity> entities =
                l.getWorld().getNearbyEntities(l,
                        vanillaDetectionRadius, vanillaDetectionRadius, vanillaDetectionRadius,
                        entity -> entity instanceof Raider && isWithinVanillaRange(entity.getLocation(), l));

        if(!entities.isEmpty()){
            Collection<LivingEntity> raidersInRange = new ArrayList<LivingEntity>();
            for(LivingEntity le: getRaidersWithinCustomRange(l)){
                raidersInRange.add(le);
            }
        }
    }

    @EventHandler
    public void onBellResonate(BellResonateEvent e){
        //Add All Pillagers in Custom Range to Default Resonate Event
        Location l = e.getBlock().getLocation();
        e.getResonatedEntities().addAll(getRaidersWithinCustomRange(l));
    }



    //Find if Entity Coordinate is Within Radius of Bell
    public boolean isWithinVanillaRange(Location entityLocation, Location bellLocation){
        return Math.pow((bellLocation.getX() - entityLocation.getX()), 2) +
                Math.pow((bellLocation.getY() - entityLocation.getY()), 2) +
                Math.pow((bellLocation.getZ() - entityLocation.getZ()), 2)
                <= Math.pow(vanillaDetectionRadius, 2);
    }

    //Returns all Raiders within a 128 Box of Location
    public Collection<LivingEntity> getRaidersWithinCustomRange(Location l){
        return l.getWorld().getNearbyEntities(l,
                        customDetectionRadius, customDetectionRadius, customDetectionRadius,
                        entity -> entity instanceof Raider)
                .stream().map(entity -> (LivingEntity) entity).toList();
    }

}
