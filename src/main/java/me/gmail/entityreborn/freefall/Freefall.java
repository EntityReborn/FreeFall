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
        
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            if(event.getCause() == DamageCause.FALL) {
                Player p = (Player)event.getEntity();
                
                if(p.hasPermission("freefall.protect")) {
                    int distance;
                    
                    String consolemsg;
                    String playermsg;
                    
                    if(p.isSneaking()) {
                        distance = getConfig().getInt("sneak-max-distance");
                        consolemsg = getConfig().getString("sneak-console-msg");
                        playermsg = getConfig().getString("sneak-player-msg");
                    } else {
                        distance = getConfig().getInt("stand-max-distance");
                        consolemsg = getConfig().getString("stand-console-msg");
                        playermsg = getConfig().getString("stand-player-msg");
                    }
                    
                    if (distance == 0 || p.getFallDistance() <= distance) {
                        if(!consolemsg.isEmpty()){
                            getServer().getConsoleSender()
                                .sendMessage(consolemsg
                                    .replace("%NAME%", p.getName())
                                    .replace("%DIST%", 
                                        String.valueOf((int)p.getFallDistance())));
                        }
                        
                        if(!playermsg.isEmpty()){
                            p.sendMessage(playermsg
                                .replace("%NAME%", p.getName())
                                .replace("%DIST%", 
                                    String.valueOf((int)p.getFallDistance())));
                        }
                    
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}

