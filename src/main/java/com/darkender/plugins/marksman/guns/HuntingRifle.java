package com.darkender.plugins.marksman.guns;

import com.darkender.plugins.marksman.Marksman;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.UUID;
import java.util.function.Predicate;

public class HuntingRifle extends Gun
{
    private BukkitTask reloadTask;
    private long lastShot = 0;
    private final long initialized;
    
    public HuntingRifle(GunSettings gunSettings, UUID uuid)
    {
        super(gunSettings, uuid);
        initialized = System.nanoTime();
    }
    
    @Override
    public String getItemName()
    {
        return ChatColor.YELLOW + gunSettings.getDisplayName() + " ▪ «" + getCurrentAmmo() + "»" +
                ((reloadTask == null) ? "" : "ᴿ");
    }
    
    private void refreshItemName()
    {
        ItemMeta meta = getItem().getItemMeta();
        meta.setDisplayName(getItemName());
        getItem().setItemMeta(meta);
    }
    
    @Override
    public void fire()
    {
        Player player = getPlayer();
        if(getCurrentAmmo() == 0)
        {
            if(reloadTask == null)
            {
                scheduleReloadSingle(gunSettings.getShootDelay());
                refreshItemName();
            }
            return;
        }
    
        // Cancel reloading if reloading while the gun is firing
        if(reloadTask != null)
        {
            reloadTask.cancel();
            reloadTask = null;
            refreshItemName();
        }
        
        long nanoTime = System.nanoTime();
        if((nanoTime - lastShot) < Gun.ticksToNanoseconds(gunSettings.getShootDelay()) ||
                (nanoTime - initialized)  < Gun.ticksToNanoseconds(gunSettings.getShootDelay()))
        {
            return;
        }
        lastShot = nanoTime;
        
        gunSettings.getFireSound().play(player);
        setCurrentAmmo(getCurrentAmmo() - 1);
    
        Location rayStart = player.getEyeLocation();
        RayTraceResult rayTraceResult = rayStart.getWorld().rayTrace(rayStart, player.getEyeLocation().getDirection(),
                100.0, FluidCollisionMode.NEVER, true, 0.0, new Predicate<Entity>()
                {
                    @Override
                    public boolean test(Entity entity)
                    {
                        // Ensure the raytrace doesn't collide with the player
                        return (entity.getEntityId() != player.getEntityId()) && (entity instanceof LivingEntity);
                    }
                });
        
        if(rayTraceResult != null)
        {
            if(rayTraceResult.getHitBlock() != null)
            {
                if(gunSettings.isTerrainParticles())
                {
                    rayTraceResult.getHitBlock().getWorld().playEffect(rayTraceResult.getHitBlock().getLocation(),
                            Effect.STEP_SOUND, rayTraceResult.getHitBlock().getType());
                }
            }
            else if(rayTraceResult.getHitEntity() != null)
            {
                LivingEntity e = (LivingEntity) rayTraceResult.getHitEntity();
                // Don't try to damage the player if they're in creative
                if(!(e instanceof Player) || ((Player) e).getGameMode() != GameMode.CREATIVE)
                {
                    double damage = gunSettings.getDamage();
                    boolean headshot = false;
                    if(gunSettings.isHeadshotsEnabled())
                    {
                        Vector relativeHit = rayTraceResult.getHitPosition().subtract(e.getLocation().toVector());
                        if((e.getHeight() - relativeHit.getY()) < 0.60)
                        {
                            damage += gunSettings.getHeadshotDamage();
                            headshot = true;
                        }
                    }
    
                    EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(player, e,
                            EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage);
                    Bukkit.getPluginManager().callEvent(event);
                    if(!event.isCancelled())
                    {
                        if(headshot)
                        {
                            gunSettings.getHeadshotSound().play(player);
                            if(gunSettings.isHeadshotFirework())
                            {
                                Firework fw = e.getWorld().spawn(e.getLocation(), Firework.class);
                                FireworkMeta meta = fw.getFireworkMeta();
                                meta.addEffect(FireworkEffect.builder().withColor(Color.RED).withFlicker().withTrail().build());
                                fw.setFireworkMeta(meta);
                            }
                        }
                        e.setVelocity(e.getVelocity().add(player.getEyeLocation().getDirection().multiply(gunSettings.getKnockback())));
                        e.damage(damage, player);
                    }
                }
            }
        }
        
        if(getCurrentAmmo() == 0)
        {
            if(reloadTask == null)
            {
                scheduleReloadSingle(gunSettings.getShootDelay());
            }
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
            scheduleReloadSingle(Math.max(gunSettings.getShootDelay() - Gun.nanosecondsToTicks(System.nanoTime() - lastShot), 0));
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
        scheduleReloadSingle(0);
    }
    
    private void scheduleReloadSingle(int extra)
    {
        BukkitRunnable runnable = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                reloadSingle();
            }
        };
        reloadTask = runnable.runTaskLater(Marksman.instance, gunSettings.getReloadDelay() + extra);
    }
    
    private void reloadSingle()
    {
        setCurrentAmmo(getCurrentAmmo() + 1);
        gunSettings.getReloadSound().play(getPlayer());
        //lastShot += Gun.ticksToNanoseconds(gunSettings.getReloadDelay());
        
        if(getCurrentAmmo() < gunSettings.getReloadAmount())
        {
            scheduleReloadSingle();
        }
        else
        {
            reloadTask = null;
            refreshItemName();
        }
    }
}
