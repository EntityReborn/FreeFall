package com.gmail.entityreborn.freefall;

import java.io.IOException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.mcstats.Metrics;

public class FreeFall extends JavaPlugin implements Listener {

    private boolean debug = false;

    public void debugMessage(String message) {
        if (debug) {
            getLogger().info(message);
        }
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new FreeFallDamageListener(this), this);
        
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

        if (cmdname.equals("freefall") && args.length > 0) {
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
}
