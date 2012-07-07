package me.gmail.entityreborn.freefall;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.java.JavaPlugin;

public class Freefall extends JavaPlugin implements Listener {
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            if(event.getCause() == DamageCause.FALL) {
                Player p = (Player)event.getEntity();
                
                if(p.isSneaking() || p.hasMetadata("freefall.protect")) {
                    event.setCancelled(true);
                }
            }
        }
    }
}

