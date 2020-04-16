package com.darkender.plugins.marksman.guns;

import com.darkender.plugins.marksman.Marksman;
import com.darkender.plugins.marksman.sound.SoundCollection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public abstract class Gun
{
    protected GunSettings gunSettings;
    protected UUID uuid;
    
    // Gun data
    private int currentAmmo;
    
    public Gun(GunSettings gunSettings, UUID uuid)
    {
        this.gunSettings = gunSettings;
        this.uuid = uuid;
        this.currentAmmo = getItem().getItemMeta().getPersistentDataContainer().get(Marksman.ammoFlag, PersistentDataType.INTEGER);
    }
    
    public abstract void fire();
    public abstract void reload();
    public abstract void close();
    protected abstract String getItemName();
    
    protected void setCurrentAmmo(int currentAmmo)
    {
        this.currentAmmo = currentAmmo;
        ItemMeta meta = getItem().getItemMeta();
        meta.setDisplayName(getItemName());
        meta.getPersistentDataContainer().set(Marksman.ammoFlag, PersistentDataType.INTEGER, currentAmmo);
        getItem().setItemMeta(meta);
    }
    
    protected Player getPlayer()
    {
        return Bukkit.getPlayer(uuid);
    }
    
    protected ItemStack getItem()
    {
        return getPlayer().getInventory().getItemInMainHand();
    }
    
    protected int getCurrentAmmo()
    {
        return currentAmmo;
    }
    
    public GunSettings getGunSettings()
    {
        return gunSettings;
    }
    
    protected static long ticksToNanoseconds(int ticks)
    {
        return ticks * 50000000;
    }
    
    protected static int nanosecondsToTicks(long nanoseconds)
    {
        return (int) (nanoseconds / 50000000);
    }
}
