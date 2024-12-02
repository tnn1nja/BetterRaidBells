package net.tnn1nja.betterRaidBells;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BellResonateEvent;
import org.bukkit.event.block.BellRingEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.Logger;

public final class BetterRaidBells extends JavaPlugin implements Listener {

    Logger log = Bukkit.getLogger();
    int vanillaDetectionRadius = 32;
    int customDetectionRadius = 128;
    HashSet<Location> bellOnCooldownLocations = new HashSet<Location>();

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
        Location l = e.getBlock().getLocation();
        if(!isRaidersWithinVanillaRange(l)){
            Collection<Raider> raiders = getRaidersWithinCustomRange(l);
            if(!raiders.isEmpty() && !bellOnCooldownLocations.contains(l)) {
                Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                    @Override
                    public void run() {
                        for (LivingEntity le: raiders) {
                            le.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 60, 0));
                        }
                    }
                }, 60L);
                Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                    @Override
                    public void run() {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            p.playSound(l, Sound.BLOCK_BELL_RESONATE, 1, getRandomPitch());
                        }
                    }
                }, 10L);
                startBellCooldown(l);
                log.info("[BetterRaidBells] Bell resonated with range of " + customDetectionRadius + " blocks.");
            }
        }
    }

    @EventHandler
    public void onBellResonate(BellResonateEvent e){
        Location l = e.getBlock().getLocation();
        e.getResonatedEntities().addAll(getRaidersWithinCustomRange(l));
        startBellCooldown(l);
        log.info("[BetterRaidBells] Bell resonation range extended to " + customDetectionRadius + " blocks.");
    }

    public boolean isRaidersWithinVanillaRange(Location bellLocation){
        Collection<Entity> entities = bellLocation.getWorld().getNearbyEntities(bellLocation,
                        vanillaDetectionRadius, vanillaDetectionRadius, vanillaDetectionRadius);
        for(Entity e: entities){
            if(e instanceof Raider && isWithinSphere(e.getLocation(), bellLocation, vanillaDetectionRadius)){
                return true;
            }
        }
        return false;
    }

    public void startBellCooldown(Location l){
        bellOnCooldownLocations.add(l);
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                bellOnCooldownLocations.remove(l);
            }
        }, 60L);
    }

    public float getRandomPitch(){
        float[] pitches = new float[]{0.85F, 0.9F, 1.0F};
        return pitches[new Random().nextInt(pitches.length)];
    }

    public boolean isWithinSphere(Location entityLocation, Location bellLocation, int radius){
        return Math.pow((bellLocation.getX() - entityLocation.getX()), 2) +
                Math.pow((bellLocation.getY() - entityLocation.getY()), 2) +
                Math.pow((bellLocation.getZ() - entityLocation.getZ()), 2)
                <= Math.pow(radius, 2);
    }

    public Collection<Raider> getRaidersWithinCustomRange(Location bellLocation){
        return bellLocation.getWorld().getNearbyEntities(bellLocation,
                customDetectionRadius, customDetectionRadius, customDetectionRadius,
                entity -> entity instanceof Raider &&
                        isWithinSphere(entity.getLocation(), bellLocation, customDetectionRadius)).
                stream().map(entity -> (Raider) entity).toList();
    }

}
