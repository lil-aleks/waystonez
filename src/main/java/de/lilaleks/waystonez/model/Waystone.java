package de.lilaleks.waystonez.model;

import org.bukkit.Location;

import java.util.UUID;

public class Waystone
{

    private int id;
    private String name;
    private Location location;
    private UUID ownerId;

    public Waystone(String name, Location location, UUID ownerId)
    {
        this.name = name;
        this.location = location;
        this.ownerId = ownerId;
    }

    public Waystone(int id, String name, Location location, UUID ownerId)
    {
        this.id = id;
        this.name = name;
        this.location = location;
        this.ownerId = ownerId;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Location getLocation()
    {
        return location;
    }

    public String getLocationString()
    {
        return String.format("%s, %d, %d, %d",
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ());
    }

    public UUID getOwnerId()
    {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId)
    {
        this.ownerId = ownerId;
    }
}
