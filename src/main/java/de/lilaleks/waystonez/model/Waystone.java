package de.lilaleks.waystonez.model;

import org.bukkit.Location;


public class Waystone
{

    private int id;
    private String name;
    private Location location;
    private String ownerId;

    public Waystone(String name, Location location, String ownerId)
    {
        this.name = name;
        this.location = location;
        this.ownerId = ownerId;
    }

    public Waystone(int id, String name, Location location, String ownerId)
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

    public String getOwnerId()
    {
        return ownerId;
    }

    public void setOwnerId(String ownerId)
    {
        this.ownerId = ownerId;
    }
}
