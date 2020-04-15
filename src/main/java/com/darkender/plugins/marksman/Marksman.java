package com.darkender.plugins.marksman;

import com.darkender.plugins.marksman.commands.MarksmanCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class Marksman extends JavaPlugin implements Listener
{
    private NamespacedKey gunFlag = new NamespacedKey(this, "gun");
    
    @Override
    public void onEnable()
    {
        MarksmanCommand marksmanCommand = new MarksmanCommand(this);
        getCommand("marksman").setExecutor(marksmanCommand);
        getCommand("marksman").setTabCompleter(marksmanCommand);
        
        getServer().getPluginManager().registerEvents(this, this);
    }
    
    public ItemStack getDebugGun()
    {
        ItemStack debugGun = new ItemStack(Material.DIAMOND_HOE);
        debugGun.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
        ItemMeta meta = debugGun.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName(ChatColor.AQUA + "Debug Gun");
        meta.getPersistentDataContainer().set(gunFlag, PersistentDataType.STRING, "debug");
        debugGun.setItemMeta(meta);
        return debugGun;
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        if(event.getItem() == null)
        {
            return;
        }
        
        ItemMeta meta = event.getItem().getItemMeta();
        if(meta == null)
        {
            return;
        }
        PersistentDataContainer data = meta.getPersistentDataContainer();
        if(data.has(gunFlag, PersistentDataType.STRING))
        {
            String gunName = data.get(gunFlag, PersistentDataType.STRING);
            event.getPlayer().sendMessage(ChatColor.GOLD + "Interacted with " + ChatColor.DARK_AQUA + gunName +
                    ChatColor.BLUE + " (" + event.getAction().name().toLowerCase() + ")");
            event.setCancelled(true);
        }
    }
}
