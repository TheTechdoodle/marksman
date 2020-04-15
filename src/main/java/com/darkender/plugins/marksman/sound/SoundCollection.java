package com.darkender.plugins.marksman.sound;

import jdk.internal.jline.internal.Nullable;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SoundCollection
{
    private List<SoundData> sounds = new ArrayList<>();
    
    /**
     * Plays all the sounds using the player's location
     * @param player The player to base the contexts on
     */
    public void play(@Nullable Player player)
    {
        for(SoundData soundData : sounds)
        {
            soundData.play(player);
        }
    }
    
    public List<SoundData> getSounds()
    {
        return sounds;
    }
}
