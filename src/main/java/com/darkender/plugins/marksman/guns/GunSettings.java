package com.darkender.plugins.marksman.guns;

import com.darkender.plugins.marksman.Marksman;
import com.darkender.plugins.marksman.sound.SoundCollection;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class GunSettings
{
    String name;
    String displayName;
    private Material gunMaterial;
    private SoundCollection fireSound;
    private SoundCollection reloadSound;
    private int reloadAmount;
    private boolean reloadIndividually;
    private int reloadDelay;
    private int shootDelay;
    
    private boolean headshotsEnabled;
    private boolean headshotFirework;
    private double headshotDamage;
    private SoundCollection headshotSound;
    
    private boolean terrainParticles;
    private boolean entityParticles;
    private boolean shootParticles;
    
    private double knockback;
    private double damage;
    
    public GunSettings(String name)
    {
        this.name = name;
    }
    
    public ItemStack generate()
    {
        ItemStack item = new ItemStack(gunMaterial);
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(Marksman.gunFlag, PersistentDataType.STRING, name);
        meta.getPersistentDataContainer().set(Marksman.ammoFlag, PersistentDataType.INTEGER, reloadAmount);
        meta.setDisplayName(ChatColor.YELLOW + displayName + " ▪ «" + reloadAmount + "»");
        item.setItemMeta(meta);
        return item;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getDisplayName()
    {
        return displayName;
    }
    
    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }
    
    public int getShootDelay()
    {
        return shootDelay;
    }
    
    public void setShootDelay(int shootDelay)
    {
        this.shootDelay = shootDelay;
    }
    
    public double getDamage()
    {
        return damage;
    }
    
    public void setDamage(double damage)
    {
        this.damage = damage;
    }
    
    public int getReloadDelay()
    {
        return reloadDelay;
    }
    
    public void setReloadDelay(int reloadDelay)
    {
        this.reloadDelay = reloadDelay;
    }
    
    public Material getGunMaterial()
    {
        return gunMaterial;
    }
    
    public void setGunMaterial(Material gunMaterial)
    {
        this.gunMaterial = gunMaterial;
    }
    
    public SoundCollection getFireSound()
    {
        return fireSound;
    }
    
    public void setFireSound(SoundCollection fireSound)
    {
        this.fireSound = fireSound;
    }
    
    public SoundCollection getReloadSound()
    {
        return reloadSound;
    }
    
    public void setReloadSound(SoundCollection reloadSound)
    {
        this.reloadSound = reloadSound;
    }
    
    public int getReloadAmount()
    {
        return reloadAmount;
    }
    
    public void setReloadAmount(int reloadAmount)
    {
        this.reloadAmount = reloadAmount;
    }
    
    public boolean isReloadIndividually()
    {
        return reloadIndividually;
    }
    
    public void setReloadIndividually(boolean reloadIndividually)
    {
        this.reloadIndividually = reloadIndividually;
    }
    
    public boolean isHeadshotsEnabled()
    {
        return headshotsEnabled;
    }
    
    public void setHeadshotsEnabled(boolean headshotsEnabled)
    {
        this.headshotsEnabled = headshotsEnabled;
    }
    
    public boolean isHeadshotFirework()
    {
        return headshotFirework;
    }
    
    public void setHeadshotFirework(boolean headshotFirework)
    {
        this.headshotFirework = headshotFirework;
    }
    
    public double getHeadshotDamage()
    {
        return headshotDamage;
    }
    
    public void setHeadshotDamage(double headshotDamage)
    {
        this.headshotDamage = headshotDamage;
    }
    
    public SoundCollection getHeadshotSound()
    {
        return headshotSound;
    }
    
    public void setHeadshotSound(SoundCollection headshotSound)
    {
        this.headshotSound = headshotSound;
    }
    
    public boolean isTerrainParticles()
    {
        return terrainParticles;
    }
    
    public void setTerrainParticles(boolean terrainParticles)
    {
        this.terrainParticles = terrainParticles;
    }
    
    public boolean isEntityParticles()
    {
        return entityParticles;
    }
    
    public void setEntityParticles(boolean entityParticles)
    {
        this.entityParticles = entityParticles;
    }
    
    public boolean isShootParticles()
    {
        return shootParticles;
    }
    
    public void setShootParticles(boolean shootParticles)
    {
        this.shootParticles = shootParticles;
    }
    
    public double getKnockback()
    {
        return knockback;
    }
    
    public void setKnockback(double knockback)
    {
        this.knockback = knockback;
    }
}
