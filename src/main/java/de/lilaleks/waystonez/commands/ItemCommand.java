package de.lilaleks.waystonez.commands;

import de.lilaleks.waystonez.custom.block.WaystoneBlock;
import de.lilaleks.waystonez.custom.item.WaystoneWand;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemCommand implements BasicCommand
{
    @Override
    public void execute(CommandSourceStack commandSourceStack, String @NotNull [] args)
    {
        if (!(commandSourceStack.getSender() instanceof Player player))
        {
            commandSourceStack.getSender().sendMessage("This command can only be used by players.");
            return;
        }

        if (args.length == 0)
        {
            player.sendMessage("Usage: /waystonez [waystone|wand] <player>");
            return;
        }

        Player target = player;
        if (args.length == 2) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage("Player not found");
                return;
            }
        }

        switch (args[0].toLowerCase())
        {
            case "waystone":
                target.give(WaystoneBlock.ITEM_STACK);
                break;

            case "wand":
                target.give(WaystoneWand.ITEM_STACK);
                break;

            default:
                player.sendMessage("Usage: /waystonez [waystone|wand] <player>");
                break;
        }
    }

    @Override
    public @NotNull Collection<String> suggest(CommandSourceStack commandSourceStack, String[] args)
    {
        List<String> list = new ArrayList<>();
        if (args.length == 0)
        {
            list.add("waystone");
            list.add("wand");
        } else if (args.length == 2) {
            Bukkit.getOnlinePlayers().forEach(player -> list.add(player.getName()));
        }
        return list;
    }

    @Override
    public boolean canUse(CommandSender sender)
    {
        return BasicCommand.super.canUse(sender);
    }

    @Override
    public @org.jspecify.annotations.Nullable String permission()
    {
        return "waystonez.command";
    }
}
