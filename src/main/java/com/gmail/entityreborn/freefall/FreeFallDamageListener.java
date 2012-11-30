/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.entityreborn.freefall;

import java.util.logging.Level;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 *
 * @author import
 */
public class FreeFallDamageListener implements Listener {
    FreeFall plugin;
    
    public FreeFallDamageListener(FreeFall plug) {
        plugin = plug;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                Player p = (Player)event.getEntity();

                if (event.isCancelled()) {
                    plugin.debugMessage("Damage event was cancelled for player "
                            + p.getName() + "!");
                    return;
                } else if (event.getDamage() == 0) {
                    plugin.debugMessage("Damage was 0 for player "
                            + p.getName() + "!");
                    return;
                }
                
                plugin.debugMessage("Original damage: " + event.getDamage());

                int damage = 0;
                int falldistance = (int) p.getFallDistance() + 1;
                
                String sneakorstand = "stand";
                
                if (p.isSneaking()) {
                    sneakorstand = "sneak";
                }
                
                if (!p.hasPermission("freefall." + sneakorstand)) {
                    return;
                }

                plugin.debugMessage(p.getName() + " fell " + falldistance + 
                        " while " + sneakorstand + "ing.");

                if (p.hasPermission("freefall." + sneakorstand + ".bypass")) {
                    plugin.debugMessage(p.getName() + " bypassed damage.");
                    event.setDamage(0);
                    event.setCancelled(true);
                    return;
                }

                String eval = plugin.getConfig().getString(sneakorstand + "-calculation");
                String consolemsg = plugin.getConfig().getString(sneakorstand + "-console-msg");
                String playermsg = plugin.getConfig().getString(sneakorstand + "-player-msg");
                String consolemsgdmgd = plugin.getConfig().getString(sneakorstand + "-console-msg-damaged");
                String playermsgdmgd = plugin.getConfig().getString(sneakorstand + "-player-msg-damaged");

                ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
                Bindings bindings = engine.createBindings();

                bindings.put("fallen", falldistance);
                bindings.put("damage", event.getDamage());

                try {
                    damage = ((Double)engine.eval(eval, bindings)).intValue();
                } catch (ScriptException ex) {
                    plugin.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                }
                
                plugin.debugMessage("New calculated damage: " + damage);

                if (damage == 0) {
                    if (!consolemsg.isEmpty()) {
                        plugin.getLogger().info(consolemsg
                                .replace("%NAME%", p.getName())
                                .replace("%DIST%",
                                String.valueOf(falldistance)));
                    }

                    if (!playermsg.isEmpty()) {
                        p.sendMessage(playermsg
                                .replace("%NAME%", p.getName())
                                .replace("%DIST%",
                                String.valueOf(falldistance)));
                    }

                    plugin.debugMessage(p.getName() + " didn't get hurt.");
                    event.setCancelled(true);
                } else {
                    if (damage > 0) {
                        if (!consolemsgdmgd.isEmpty()) {
                            plugin.getLogger().info(consolemsgdmgd
                                    .replace("%NAME%", p.getName())
                                    .replace("%DAMG%", String.valueOf(damage))
                                    .replace("%DIST%",
                                    String.valueOf(falldistance)));
                        }

                        if (!playermsgdmgd.isEmpty()) {
                            p.sendMessage(playermsgdmgd
                                    .replace("%NAME%", p.getName())
                                    .replace("%DAMG%", String.valueOf(damage))
                                    .replace("%DIST%",
                                    String.valueOf(falldistance)));
                        }

                        plugin.debugMessage(p.getName() + " suffered " + 
                                damage + " damage.");
                        
                        event.setDamage(damage);
                    }
                }
            }
        }
    }
}
