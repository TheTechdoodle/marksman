package com.darkender.plugins.marksman.commands;

import com.darkender.plugins.marksman.Marksman;
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
        
        if(args[0].equalsIgnoreCase("debug"))
        {
            if(!(sender instanceof Player))
            {
                sender.sendMessage(ChatColor.RED + "Must be a player to run this command!");
                return true;
            }
            
            Player p = (Player) sender;
            p.getInventory().addItem(marksman.getDebugGun());
        }
        else
        {
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
        }
        return suggestions;
    }
}
