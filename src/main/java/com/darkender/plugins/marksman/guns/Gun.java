package com.darkender.plugins.marksman.guns;

import com.darkender.plugins.marksman.Marksman;
import com.darkender.plugins.marksman.sound.SoundCollection;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public abstract class Gun
{
    protected GunSettings gunSettings;
    protected Player player;
    protected ItemStack item;
    
    // Gun data
    private int currentAmmo;
    
    public Gun(GunSettings gunSettings, Player player, ItemStack item)
    {
        this.gunSettings = gunSettings;
        this.player = player;
        this.item = item;
        this.currentAmmo = item.getItemMeta().getPersistentDataContainer().get(Marksman.ammoFlag, PersistentDataType.INTEGER);
    }
    
    public abstract void fire();
    public abstract void reload();
    public abstract void close();
    
    protected void setCurrentAmmo(int currentAmmo)
    {
        this.currentAmmo = currentAmmo;
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + gunSettings.displayName + " [" + currentAmmo + "]");
        meta.getPersistentDataContainer().set(Marksman.ammoFlag, PersistentDataType.INTEGER, currentAmmo);
        item.setItemMeta(meta);
    }
    
    protected int getCurrentAmmo()
    {
        return currentAmmo;
    }
    
    public GunSettings getGunSettings()
    {
        return gunSettings;
    }
}
