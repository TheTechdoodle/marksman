package com.darkender.plugins.marksman.commands;

import com.darkender.plugins.marksman.Marksman;
import com.darkender.plugins.marksman.guns.GunSettings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MarksmanCommand implements CommandExecutor, TabCompleter
{
    private Marksman marksman;
    
    public MarksmanCommand(Marksman marksman)
    {
        this.marksman = marksman;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(args.length == 0)
        {
            sender.sendMessage(ChatColor.RED + "Requires argument");
            return true;
        }
    
        if(!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED + "Must be a player to run this command!");
            return true;
        }
    
        Player p = (Player) sender;
        
        if(args[0].equalsIgnoreCase("debug"))
        {
            p.getInventory().addItem(marksman.getDebugGun());
        }
        else
        {
            for(GunSettings gunSettings : marksman.getGuns())
            {
                if(args[0].equals(gunSettings.getName()))
                {
                    p.getInventory().addItem(gunSettings.generate());
                    return true;
                }
            }
            sender.sendMessage(ChatColor.RED + "Unknown command!");
        }
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
    {
        List<String> suggestions = new ArrayList<>();
        if(args.length == 1)
        {
            suggestions.add("debug");
            for(GunSettings gunSettings : marksman.getGuns())
            {
                suggestions.add(gunSettings.getName());
            }
        }
        return suggestions;
    }
}
