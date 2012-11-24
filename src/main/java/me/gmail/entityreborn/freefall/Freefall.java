package me.gmail.entityreborn.freefall;

import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.java.JavaPlugin;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.bukkit.ChatColor;
import org.bukkit.event.EventPriority;
import org.mcstats.Metrics;

public class Freefall extends JavaPlugin implements Listener {

    private boolean debug = false;

    public void debugMessage(String message) {
        if (debug) {
            getLogger().info(message);
        }
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        getConfig().options().copyDefaults(true);
        saveConfig();

        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            this.getLogger().info("Could not initialize metrics.");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        String cmdname = command.getName().toLowerCase();

        if ((cmdname.equals("freefall")) && (args.length > 0)) {
            String cmd = args[0].toLowerCase();

            if (cmd.equals("reload")) {
                if (sender.isOp() || sender.hasPermission("freefall.reload")) {
                    reloadConfig();
                    
                    sender.sendMessage("FreeFall reloaded");
                    getLogger().info(sender.getName() + " reloaded FreeFall.");
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission.");
                }

                return true;
            } else if (cmd.equals("debug")) {
                if (sender.isOp() || sender.hasPermission("freefall.debug")) {
                    debug = !debug;

                    if (debug) {
                        sender.sendMessage("Freefall debug mode enabled.");
                        getLogger().info(sender.getName() + " enabled FreeFall debug mode.");
                    } else {
                        sender.sendMessage("Freefall debug mode disabled.");
                        getLogger().info(sender.getName() + " disabled FreeFall debug mode.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission.");
                }

                return true;
            } else if (cmd.equals("info")) {
                if (sender.isOp() || sender.hasPermission("freefall.info")) {
                    sender.sendMessage("Stand calculation: " + getConfig().getString("stand-calculation"));
                    sender.sendMessage("Sneak calculation: " + getConfig().getString("sneak-calculation"));
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission.");
                }

                return true;
            } else if (cmd.equals("version")) {
                sender.sendMessage("FreeFall " + getDescription().getVersion());
                return true;
            }
        }

        return false;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            if (event.getCause() == DamageCause.FALL) {
                Player p = (Player) event.getEntity();

                if (event.isCancelled()) {
                    this.debugMessage("Damage event was cancelled for player "
                            + p.getName() + "!");
                    return;
                }

                if (event.getDamage() == 0) {
                    this.debugMessage("Damage was 0 for player "
                            + p.getName() + "!");
                    return;
                }

                boolean bypass = false;

                String consolemsg;
                String playermsg;
                String consolemsgdmgd;
                String playermsgdmgd;
                String eval;

                int damage = 0;
                int falldistance = (int) p.getFallDistance() + 1;

                if (p.isSneaking()) {
                    if (!p.hasPermission("freefall.sneak")) {
                        return;
                    }

                    this.debugMessage(p.getName() + " fell " + falldistance + " while sneaking.");

                    if (p.hasPermission("freefall.sneak.bypass")) {
                        bypass = true;
                    }

                    eval = getConfig().getString("sneak-calculation");
                    consolemsg = getConfig().getString("sneak-console-msg");
                    playermsg = getConfig().getString("sneak-player-msg");
                    consolemsgdmgd = getConfig().getString("sneak-console-msg-damaged");
                    playermsgdmgd = getConfig().getString("sneak-player-msg-damaged");
                } else {
                    if (!p.hasPermission("freefall.stand")) {
                        return;
                    }

                    this.debugMessage(p.getName() + " fell " + falldistance + " while standing.");

                    if (p.hasPermission("freefall.stand.bypass")) {
                        bypass = true;
                    }

                    eval = getConfig().getString("stand-calculation");
                    consolemsg = getConfig().getString("stand-console-msg");
                    playermsg = getConfig().getString("stand-player-msg");
                    consolemsgdmgd = getConfig().getString("stand-console-msg-damaged");
                    playermsgdmgd = getConfig().getString("stand-player-msg-damaged");
                }

                if (!bypass) {
                    ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
                    Bindings bindings = engine.createBindings();

                    bindings.put("fallen", falldistance);
                    bindings.put("damage", event.getDamage());

                    try {
                        damage = ((Double) engine.eval(eval, bindings)).intValue();
                    } catch (ScriptException ex) {
                        getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }

                if (bypass || damage == 0) {
                    if (!consolemsg.isEmpty()) {
                        getLogger().info(consolemsg
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

                    this.debugMessage(p.getName() + " didn't get hurt.");
                    event.setCancelled(true);
                } else {
                    if (damage > 0) {
                        if (!consolemsgdmgd.isEmpty()) {
                            getLogger().info(consolemsgdmgd
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

                        this.debugMessage(p.getName() + " suffered " + damage + " damage.");
                        event.setDamage(damage);
                    }
                }
            }
        }
    }
}
