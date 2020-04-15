package com.darkender.plugins.marksman.sound;

import com.darkender.plugins.marksman.Marksman;
import jdk.internal.jline.internal.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SoundData
{
    private Sound sound = null;
    private float pitch = 0.0F;
    private float volume = 0.0F;
    private SoundContext context = null;
    private int delay = 0;
    
    public SoundData()
    {
    
    }
    
    public SoundData(Sound sound, float pitch, float volume, SoundContext context, int delay)
    {
        this.sound = sound;
        this.pitch = pitch;
        this.volume = volume;
        this.context = context;
        this.delay = delay;
    }
    
    /**
     * Plays the sound in the context using the player's location
     * @param player The player to base the context on
     */
    public void play(@Nullable Player player)
    {
        if(delay != 0)
        {
            BukkitRunnable soundTask = new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    playWithoutDelay(player);
                }
            };
            soundTask.runTaskLater(Marksman.instance, delay);
        }
        else
        {
            playWithoutDelay(player);
        }
    }
    
    /**
     * Plays the sound in the context without any delay
     * @param player The player to base the context on
     */
    public void playWithoutDelay(@Nullable Player player)
    {
        if(context == SoundContext.PLAYER)
        {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
        else
        {
            playWithoutDelay(player.getLocation());
        }
    }
    
    /**
     * Plays the sound in the context without any delay
     * @param location The location to base the context on
     */
    public void playWithoutDelay(Location location)
    {
        if(context == SoundContext.LOCATION)
        {
            location.getWorld().playSound(location, sound, volume, pitch);
        }
        else if(context == SoundContext.WORLD)
        {
            for(Player p : location.getWorld().getPlayers())
            {
                p.playSound(p.getLocation(), sound, volume, pitch);
            }
        }
        else if(context == SoundContext.SERVER)
        {
            for(Player p : Bukkit.getOnlinePlayers())
            {
                p.playSound(p.getLocation(), sound, volume, pitch);
            }
        }
    }
    
    public Sound getSound()
    {
        return sound;
    }
    
    public void setSound(Sound sound)
    {
        this.sound = sound;
    }
    
    public float getPitch()
    {
        return pitch;
    }
    
    public void setPitch(float pitch)
    {
        this.pitch = pitch;
    }
    
    public float getVolume()
    {
        return volume;
    }
    
    public void setVolume(float volume)
    {
        this.volume = volume;
    }
    
    public SoundContext getContext()
    {
        return context;
    }
    
    public void setContext(SoundContext context)
    {
        this.context = context;
    }
    
    public int getDelay()
    {
        return delay;
    }
    
    public void setDelay(int delay)
    {
        this.delay = delay;
    }
}
