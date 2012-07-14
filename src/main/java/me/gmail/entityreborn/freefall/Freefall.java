package me.gmail.entityreborn.freefall;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.java.JavaPlugin;
import org.matheclipse.parser.client.eval.DoubleEvaluator;
import org.matheclipse.parser.client.eval.DoubleVariable;
import org.matheclipse.parser.client.eval.IDoubleValue;


public class Freefall extends JavaPlugin implements Listener {
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
    
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        String cmdname = command.getName().toLowerCase();
        
        if ((cmdname.equals("freefall")) && (args.length > 0)) {
            String cmd = args[0].toLowerCase();
            
            if (cmd.equals("reload")) {
                if (sender.isOp() || sender.hasPermission("freefall.reload")) {
                    reloadConfig();
                    sender.sendMessage("FreeFall reloaded");
                    return true;
                }
            } else if (cmd.equals("version")) {
                if (sender.isOp() || sender.hasPermission("freefall.reload")) {
                    sender.sendMessage("FreeFall " + getDescription().getVersion());
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            if(event.getCause() == DamageCause.FALL) {
                Player p = (Player)event.getEntity();
                
                boolean bypass = false;
                
                String consolemsg;
                String playermsg;
                String consolemsgdmgd;
                String playermsgdmgd;
                String eval;
                
                if(p.isSneaking()) {
                    if(!p.hasPermission("freefall.sneak")) {
                        return;
                    }
                    
                    if(p.hasPermission("freefall.sneak.bypass")) {
                        bypass = true;
                    }
                    
                    eval = getConfig().getString("sneak-calculation");
                    consolemsg = getConfig().getString("sneak-console-msg");
                    playermsg = getConfig().getString("sneak-player-msg");
                    consolemsgdmgd = getConfig().getString("sneak-console-msg-damaged");
                    playermsgdmgd = getConfig().getString("sneak-player-msg-damaged");
                } else {
                    if(!p.hasPermission("freefall.stand")) {
                        return;
                    }
                    
                    if(p.hasPermission("freefall.stand.bypass")) {
                        bypass = true;
                    } 
                    
                    eval = getConfig().getString("stand-calculation");
                    consolemsg = getConfig().getString("stand-console-msg");
                    playermsg = getConfig().getString("stand-player-msg");
                    consolemsgdmgd = getConfig().getString("stand-console-msg-damaged");
                    playermsgdmgd = getConfig().getString("stand-player-msg-damaged");
                }
                
                double damage = 0;
                int falldistance = (int)p.getFallDistance() + 3;
                
                if (!bypass) {
                    IDoubleValue fallen = new DoubleVariable(falldistance);
                    IDoubleValue dmg = new DoubleVariable((int)event.getDamage());
                    DoubleEvaluator engine = new DoubleEvaluator();
                    engine.defineVariable("distance", fallen);
                    engine.defineVariable("damage", dmg);
                    damage = engine.evaluate(eval);
                }
                
                if (bypass || (int)damage == 0) {
                    if(!consolemsg.isEmpty()){
                        getServer().getConsoleSender()
                            .sendMessage(consolemsg
                                .replace("%NAME%", p.getName())
                                .replace("%DIST%", 
                                    String.valueOf(falldistance)));
                    }
                    
                    if(!playermsg.isEmpty()){
                        p.sendMessage(playermsg
                            .replace("%NAME%", p.getName())
                            .replace("%DIST%", 
                                String.valueOf(falldistance)));
                    }
                
                    event.setCancelled(true);
                } else {
                    if ((damage) > 0) {
                        if(!consolemsgdmgd.isEmpty()){
                            getServer().getConsoleSender()
                                .sendMessage(consolemsgdmgd
                                    .replace("%NAME%", p.getName())
                                    .replace("%DAMG%", String.valueOf((int)damage))
                                    .replace("%DIST%", 
                                        String.valueOf(falldistance)));
                        }
                        
                        if(!playermsgdmgd.isEmpty()){
                            p.sendMessage(playermsgdmgd
                                .replace("%NAME%", p.getName())
                                .replace("%DAMG%", String.valueOf((int)damage))    
                                .replace("%DIST%", 
                                    String.valueOf(falldistance)));
                        }
                        
                        event.setDamage((int) damage);
                    }
                        
                }
            }
        }
    }
}

