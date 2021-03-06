package de.minetick.modcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.minetick.antixray.AntiXRay;
import de.minetick.packetbuilder.PacketBuilderThreadPool;

public class ThreadPoolsCommand extends Command {

    public ThreadPoolsCommand(String name) {
        super(name);
        this.usageMessage = "/threadpools <poolName> <threadCount>";
        this.description = "Adjusts the sizes of different thread pools";
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if(sender instanceof ConsoleCommandSender || sender instanceof Player) {
            if(sender instanceof Player) {
                Player p = (Player) sender;
                if(!p.isOp()) {
                    p.sendMessage("You are not allowed to use this command!");
                    return true;
                }
            }
            if(args.length >= 1) {
                if(args.length > 2) {
                    this.sendHelp(sender);
                    return true;
                }
                String pool = args[0];
                int newSize = -1;
                if(args.length == 2) {
                    try {
                        newSize = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage("Invalid argument: " + args[1]);
                        this.sendHelp(sender);
                        return true;
                    }
                }
                if(newSize > 0 && newSize <= 64) {
                    if(pool.equalsIgnoreCase("packetbuilder")) {
                        PacketBuilderThreadPool.adjustPoolSize(newSize);
                        sender.sendMessage("Packetbuilder pool size adjusted to " + newSize + " threads.");
                    }
                } else {
                    sender.sendMessage("Set a value between 1 and 64");
                }
            } else {
                this.sendHelp(sender);
            }
        
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("Usage: /threadpools <poolName> <threadCount>");
        sender.sendMessage("Example: /theadpools packetbuilder 4  -  Sets 4 threads for chunk packet creation");
        sender.sendMessage("What you set here is not written to the minetickmod.yml settings file. You need to do that manually.");
        sender.sendMessage("Possible thread pool names:");
        sender.sendMessage("  packetbuilder  -  This pool creates, orebfuscates and compresses the chunk network packets");
    }
}
