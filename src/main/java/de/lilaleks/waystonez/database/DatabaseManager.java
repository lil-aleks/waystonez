package de.lilaleks.waystonez.database;

import de.lilaleks.waystonez.Waystonez;
import de.lilaleks.waystonez.model.Waystone;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DatabaseManager
{

    private final Waystonez plugin;
    private Connection connection;

    public DatabaseManager(Waystonez plugin)
    {
        this.plugin = plugin;
    }

    public void initialize()
    {
        try
        {
            // Create data folder if it doesn't exist
            if (!plugin.getDataFolder().exists())
            {
                plugin.getDataFolder().mkdirs();
            }

            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Connect to database
            connection = DriverManager.getConnection("jdbc:sqlite:" + new File(plugin.getDataFolder(), "waystones.db"));

            // Create tables if they don't exist
            createTables();

            plugin.getLogger().info("Database connection established.");
        } catch (ClassNotFoundException | SQLException e)
        {
            plugin.getLogger().severe("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException
    {
        try (Statement statement = connection.createStatement())
        {
            // Create waystones table
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS waystones (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "name TEXT NOT NULL, " +
                            "world TEXT NOT NULL, " +
                            "x DOUBLE NOT NULL, " +
                            "y DOUBLE NOT NULL, " +
                            "z DOUBLE NOT NULL, " +
                            "owner_uuid TEXT NOT NULL, " +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ")"
            );

            // Create player_waystones table for tracking discovered waystones
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS player_waystones (" +
                            "player_uuid TEXT NOT NULL, " +
                            "waystone_id INTEGER NOT NULL, " +
                            "discovered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                            "PRIMARY KEY (player_uuid, waystone_id), " +
                            "FOREIGN KEY (waystone_id) REFERENCES waystones(id) ON DELETE CASCADE" +
                            ")"
            );
        }
    }

    public void close()
    {
        try
        {
            if (connection != null && !connection.isClosed())
            {
                connection.close();
                plugin.getLogger().info("Database connection closed.");
            }
        } catch (SQLException e)
        {
            plugin.getLogger().severe("Error closing database connection: " + e.getMessage());
        }
    }

    public void saveWaystone(Waystone waystone)
    {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO waystones (name, world, x, y, z, owner_uuid) VALUES (?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS))
        {

            statement.setString(1, waystone.getName());
            statement.setString(2, waystone.getLocation().getWorld().getName());
            statement.setDouble(3, waystone.getLocation().getX());
            statement.setDouble(4, waystone.getLocation().getY());
            statement.setDouble(5, waystone.getLocation().getZ());
            statement.setString(6, waystone.getOwnerId());

            statement.executeUpdate();

            // Get the generated ID
            try (ResultSet generatedKeys = statement.getGeneratedKeys())
            {
                if (generatedKeys.next())
                {
                    waystone.setId(generatedKeys.getInt(1));
                }
            }

            // Add the owner to the discovered list
            addDiscoveredWaystone(waystone.getOwnerId(), waystone.getId());

        } catch (SQLException e)
        {
            plugin.getLogger().severe("Error saving waystone: " + e.getMessage());
        }
    }

    public void deleteWaystone(int id)
    {
        // delete waystone by id
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM waystones WHERE id = ?"))
        {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e)
        {
            plugin.getLogger().severe("Error deleting waystone: " + e.getMessage());
        }
    }

    public void addDiscoveredWaystone(String playerUUID, int waystoneId)
    {
        // set waystone as discovered for a player
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT OR IGNORE INTO player_waystones (player_uuid, waystone_id) VALUES (?, ?)"))
        {

            statement.setString(1, playerUUID);
            statement.setInt(2, waystoneId);

            statement.executeUpdate();
        } catch (SQLException e)
        {
            plugin.getLogger().severe("Error adding discovered waystone: " + e.getMessage());
        }
    }

    public List<Waystone> getPlayerWaystones(String playerUUID)
    {
        List<Waystone> waystones = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT w.* FROM waystones w " +
                        "JOIN player_waystones pw ON w.id = pw.waystone_id " +
                        "WHERE pw.player_uuid = ?"))
        {

            statement.setString(1, playerUUID);

            try (ResultSet resultSet = statement.executeQuery())
            {
                while (resultSet.next())
                {
                    waystones.add(resultSetToWaystone(resultSet));
                }
            }
        } catch (SQLException e)
        {
            plugin.getLogger().severe("Error getting player waystones: " + e.getMessage());
        }

        return waystones;
    }

    public Optional<Waystone> getWaystone(int id)
    {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM waystones WHERE id = ?"))
        {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery())
            {
                if (resultSet.next())
                {
                    return Optional.of(resultSetToWaystone(resultSet));
                }
            }
        } catch (SQLException e)
        {
            plugin.getLogger().severe("Error getting waystone: " + e.getMessage());
        }

        return Optional.empty();
    }

    public Optional<Waystone> getWaystoneAtLocation(Location location)
    {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM waystones WHERE world = ? AND x = ? AND y = ? AND z = ?"))
        {

            statement.setString(1, location.getWorld().getName());
            statement.setDouble(2, location.getX());
            statement.setDouble(3, location.getY());
            statement.setDouble(4, location.getZ());

            try (ResultSet resultSet = statement.executeQuery())
            {
                if (resultSet.next())
                {
                    return Optional.of(resultSetToWaystone(resultSet));
                }
            }
        } catch (SQLException e)
        {
            plugin.getLogger().severe("Error getting waystone at location: " + e.getMessage());
        }

        return Optional.empty();
    }

    private Waystone resultSetToWaystone(ResultSet resultSet) throws SQLException
    {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String worldName = resultSet.getString("world");
        double x = resultSet.getDouble("x");
        double y = resultSet.getDouble("y");
        double z = resultSet.getDouble("z");
        UUID ownerUUID = UUID.fromString(resultSet.getString("owner_uuid"));

        World world = Bukkit.getWorld(worldName);
        if (world == null)
        {
            throw new SQLException("World not found: " + worldName);
        }

        Location location = new Location(world, x, y, z);

        return new Waystone(id, name, location, ownerUUID.toString());
    }

    public boolean playerHasWaystone(String uniqueId, int id)
    {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM player_waystones WHERE player_uuid = ? AND waystone_id = ?"))
        {

            statement.setString(1, uniqueId.toString());
            statement.setDouble(2, id);
            try (ResultSet resultSet = statement.executeQuery())
            {
                if (resultSet.next())
                {
                    return true;
                }
            }
        } catch (SQLException e)
        {
            plugin.getLogger().severe("Error while checking if player has waystone: " + e.getMessage());
        }
        return false;
    }
}
