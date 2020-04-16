package com.darkender.plugins.marksman;

import com.darkender.plugins.marksman.commands.MarksmanCommand;
import com.darkender.plugins.marksman.guns.Gun;
import com.darkender.plugins.marksman.guns.GunSettings;
import com.darkender.plugins.marksman.guns.HuntingRifle;
import com.darkender.plugins.marksman.sound.SoundCollection;
import com.darkender.plugins.marksman.sound.SoundContext;
import com.darkender.plugins.marksman.sound.SoundData;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Predicate;

public class Marksman extends JavaPlugin implements Listener
{
    public static NamespacedKey gunFlag;
    public static NamespacedKey ammoFlag;
    public static Marksman instance;
    
    private Set<GunSettings> guns;
    private Map<UUID, Gun> activeGuns;
    private Set<UUID> ignoreInteract;
    
    @Override
    public void onEnable()
    {
        instance = this;
        gunFlag = new NamespacedKey(this, "gun");
        ammoFlag = new NamespacedKey(this, "ammo");
        ignoreInteract = new HashSet<>();
        
        guns = new HashSet<>();
        activeGuns = new HashMap<>();
        
        GunSettings huntingRifleSettings = new GunSettings("hunting-rifle");
        huntingRifleSettings.setDisplayName("Hunting Rifle");
        huntingRifleSettings.setGunMaterial(Material.IRON_HORSE_ARMOR);
        huntingRifleSettings.setReloadAmount(5);
        huntingRifleSettings.setReloadIndividually(true);
        huntingRifleSettings.setReloadDelay(12);
        huntingRifleSettings.setShootDelay(22);
        
        huntingRifleSettings.setHeadshotsEnabled(true);
        huntingRifleSettings.setHeadshotFirework(true);
        huntingRifleSettings.setHeadshotDamage(5.0);
        
        huntingRifleSettings.setTerrainParticles(true);
        huntingRifleSettings.setEntityParticles(true);
        huntingRifleSettings.setShootParticles(Particle.FIREWORKS_SPARK);
        
        huntingRifleSettings.setKnockback(1.4);
        huntingRifleSettings.setDamage(8.0);
        
        huntingRifleSettings.setFireSound(new SoundCollection(Arrays.asList(
                new SoundData(Sound.ENTITY_BLAZE_HURT, 1.0F, 1.0F, SoundContext.LOCATION, 0),
                new SoundData(Sound.ENTITY_GENERIC_EXPLODE, 2.0F, 1.0F, SoundContext.LOCATION, 0),
                new SoundData(Sound.BLOCK_NOTE_BLOCK_HAT, 0.0F, 1.0F, SoundContext.LOCATION, 5),
                new SoundData(Sound.BLOCK_PISTON_CONTRACT, 2.0F, 1.0F, SoundContext.LOCATION, 7),
                new SoundData(Sound.BLOCK_PISTON_EXTEND, 2.0F, 1.0F, SoundContext.LOCATION, 18)
        )));
        huntingRifleSettings.setReloadSound(new SoundCollection(Arrays.asList(
                new SoundData(Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 1.0F, SoundContext.LOCATION, 0)
        )));
        huntingRifleSettings.setHeadshotSound(new SoundCollection(Arrays.asList(
                new SoundData(Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 1.0F, SoundContext.PLAYER, 0)
        )));
        
        guns.add(huntingRifleSettings);
        
        
        MarksmanCommand marksmanCommand = new MarksmanCommand(this);
        getCommand("marksman").setExecutor(marksmanCommand);
        getCommand("marksman").setTabCompleter(marksmanCommand);
        
        getServer().getPluginManager().registerEvents(this, this);
        
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
        {
            @Override
            public void run()
            {
                for(Player p : Bukkit.getOnlinePlayers())
                {
                    gunTick(p, p.getInventory().getItemInMainHand(), false);
                    gunTick(p, p.getInventory().getItemInOffHand(), true);
                }
            }
        }, 1L, 4L);
        
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
        {
            @Override
            public void run()
            {
                for(Player p : Bukkit.getOnlinePlayers())
                {
                    String gunName = getGun(p.getInventory().getItemInMainHand());
                    if(activeGuns.containsKey(p.getUniqueId()))
                    {
                        // If it's a different gun or if it's null
                        if(gunName == null || !gunName.equals(activeGuns.get(p.getUniqueId()).getGunSettings().getName()))
                        {
                            activeGuns.get(p.getUniqueId()).close();
                            activeGuns.remove(p.getUniqueId());
                        }
                    }
                    
                    if(gunName != null && !activeGuns.containsKey(p.getUniqueId()))
                    {
                        if(gunName.equals("hunting-rifle"))
                        {
                            activeGuns.put(p.getUniqueId(), new HuntingRifle(getGunSettings(gunName), p.getUniqueId()));
                        }
                    }
                }
            }
        }, 1L, 1L);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        if(activeGuns.containsKey(event.getPlayer().getUniqueId()))
        {
            activeGuns.get(event.getPlayer().getUniqueId()).close();
            activeGuns.remove(event.getPlayer().getUniqueId());
        }
    }
    
    public static Location getHandScreenLocation(Location loc, boolean offhand)
    {
        Location spawnFrom = loc.clone();
        Vector normal2D = spawnFrom.getDirection().clone().setY(0).normalize()
                .rotateAroundY((offhand ? 1 : -1) * (Math.PI / 2))
                .multiply(0.40).setY(-0.35);
        spawnFrom.add(normal2D);
        spawnFrom.add(loc.getDirection().clone().multiply(-0.3));
        return spawnFrom;
    }
    
    private GunSettings getGunSettings(String name)
    {
        for(GunSettings settings : guns)
        {
            if(settings.getName().equals(name))
            {
                return settings;
            }
        }
        return null;
    }
    
    private String getGun(ItemStack item)
    {
        if(item == null)
        {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        if(meta == null)
        {
            return null;
        }
        PersistentDataContainer data = meta.getPersistentDataContainer();
        if(data.has(gunFlag, PersistentDataType.STRING))
        {
            return data.get(gunFlag, PersistentDataType.STRING);
        }
        return null;
    }
    
    private void gunTick(Player player, ItemStack item, boolean offhand)
    {
        String gunName = getGun(item);
        if(gunName == null)
        {
            return;
        }
            
        if(gunName.equals("debug"))
        {
            // Fire debug particles
            
            // Raytrace to get nearest collision
            //Location debugStart = getHandScreenLocation(player.getEyeLocation(), offhand);
            Location debugStart = player.getEyeLocation();
            RayTraceResult rayTraceResult = debugStart.getWorld().rayTrace(debugStart, player.getEyeLocation().getDirection(),
                    50.0, FluidCollisionMode.NEVER, true, 0.0, new Predicate<Entity>()
                    {
                        @Override
                        public boolean test(Entity entity)
                        {
                            // Ensure the raytrace doesn't collide with the player
                            return (entity.getEntityId() != player.getEntityId());
                        }
                    });
            
            Location modelLoc = getHandScreenLocation(player.getEyeLocation(), offhand);
            Vector modelDirection;
            double distance;
            if(rayTraceResult == null)
            {
                distance = 50.0;
                Vector to = player.getEyeLocation().toVector().add(player.getEyeLocation().getDirection().multiply(distance));
                Vector from = modelLoc.toVector();
                modelDirection = to.subtract(from).normalize();
            }
            else
            {
                Location hitLoc = new Location(debugStart.getWorld(), rayTraceResult.getHitPosition().getX(),
                        rayTraceResult.getHitPosition().getY(), rayTraceResult.getHitPosition().getZ());
                distance = modelLoc.distance(hitLoc);
                hitLoc.getWorld().spawnParticle(Particle.FLAME, hitLoc, 0);

                Vector to = hitLoc.toVector();
                Vector from = modelLoc.toVector();
                modelDirection = to.subtract(from).normalize();
            }
            
            // Draw particles extending until the raytrace collides or expires
            double distanceProgress = 0.0;
            double stepDistance = 0.15;
            Vector step = modelDirection.clone().multiply(stepDistance);
            Location current = modelLoc.clone();
            while(distanceProgress <= distance)
            {
                if((distance - distanceProgress) < 5)
                {
                    if(distance != 50)
                    {
                        if(stepDistance > 0.15)
                        {
                            stepDistance -= 0.3;
                            step = modelDirection.clone().multiply(stepDistance);
                        }
                        else if(stepDistance < 0.15)
                        {
                            stepDistance = 0.15;
                            step = modelDirection.clone().multiply(stepDistance);
                        }
                    }
                }
                else if(stepDistance < 2.0)
                {
                    stepDistance += 0.15;
                    step = modelDirection.clone().multiply(stepDistance);
                }
                
                current.getWorld().spawnParticle(Particle.REDSTONE, current, 0, new Particle.DustOptions(
                        (distance - distanceProgress) < 3 ? Color.RED : Color.AQUA, 0.5F));
                current = current.add(step);
                distanceProgress += stepDistance;
            }
        }
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
        if(ignoreInteract.contains(event.getPlayer().getUniqueId()))
        {
            ignoreInteract.remove(event.getPlayer().getUniqueId());
            return;
        }
        String gunName = getGun(event.getItem());
        if(gunName != null)
        {
            if(gunName.equals("debug"))
            {
                event.getPlayer().sendMessage(ChatColor.GOLD + "Interacted with " + ChatColor.DARK_AQUA + gunName +
                        ChatColor.BLUE + " (" + event.getAction().name().toLowerCase() + ")");
            }
            event.setCancelled(true);
            
            if(activeGuns.containsKey(event.getPlayer().getUniqueId()))
            {
                if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
                {
                    activeGuns.get(event.getPlayer().getUniqueId()).fire();
                }
                else if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                {
                    activeGuns.get(event.getPlayer().getUniqueId()).reload();
                }
            }
        }
    }
    
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event)
    {
        if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR &&
                activeGuns.containsKey(event.getPlayer().getUniqueId()))
        {
            event.setCancelled(true);
            ignoreInteract.add(event.getPlayer().getUniqueId());
            activeGuns.get(event.getPlayer().getUniqueId()).reload();
        }
    }
    
    public Set<GunSettings> getGuns()
    {
        return guns;
    }
}
