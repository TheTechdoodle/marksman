package com.darkender.plugins.marksman.guns;

import com.darkender.plugins.marksman.Marksman;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class HuntingRifle extends Gun
{
    private BukkitTask reloadTask;
    private long lastShot = 0;
    
    public HuntingRifle(GunSettings gunSettings, Player player, ItemStack item)
    {
        super(gunSettings, player, item);
    }
    
    @Override
    public void fire()
    {
        if(getCurrentAmmo() == 0)
        {
            reload();
            return;
        }
        
        long nanoTime = System.nanoTime();
        if((nanoTime - lastShot) > (gunSettings.getShootDelay() * 50000000))
        {
            return;
        }
        lastShot = nanoTime;
        
        // Cancel reloading if reloading while the gun is firing
        if(reloadTask != null)
        {
            reloadTask.cancel();
            reloadTask = null;
        }
        
        gunSettings.getFireSound().play(player);
        setCurrentAmmo(getCurrentAmmo() - 1);
        
        if(getCurrentAmmo() == 0)
        {
            reload();
        }
    }
    
    @Override
    public void reload()
    {
        if(getCurrentAmmo() == gunSettings.getReloadAmount())
        {
            return;
        }
        
        if(reloadTask == null)
        {
            scheduleReloadSingle();
        }
    }
    
    @Override
    public void close()
    {
        if(reloadTask != null)
        {
            reloadTask.cancel();
            reloadTask = null;
        }
    }
    
    private void scheduleReloadSingle()
    {
        BukkitRunnable runnable = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                reloadSingle();
            }
        };
        reloadTask = runnable.runTaskLater(Marksman.instance, gunSettings.getReloadDelay());
    }
    
    private void reloadSingle()
    {
        setCurrentAmmo(getCurrentAmmo() + 1);
        gunSettings.getReloadSound().play(player);
        
        if(getCurrentAmmo() < gunSettings.getReloadAmount())
        {
            scheduleReloadSingle();
        }
        else
        {
            reloadTask = null;
        }
    }
}