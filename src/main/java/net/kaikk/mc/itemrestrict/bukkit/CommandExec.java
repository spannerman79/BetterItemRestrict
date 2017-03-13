package net.kaikk.mc.itemrestrict.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandExec implements CommandExecutor {
	private BetterItemRestrict instance;
	
	CommandExec(BetterItemRestrict instance) {
		this.instance = instance;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equals("betteritemrestrict")) {
			if (!sender.hasPermission("betteritemrestrict.manage")) {
				sender.sendMessage(ChatColor.RED+"Permission denied");
				return false;
			}
			
			if (args.length==0) {
				sender.sendMessage("Usage: /"+label+" [<reload>]");
				return false;
			}
			
			switch(args[0].toLowerCase()) {
			case "reload": {
				Bukkit.getPluginManager().disablePlugin(instance);
				Bukkit.getPluginManager().enablePlugin(instance);
				sender.sendMessage("Plugin reloaded.");
				break;
			}
			default:
				sender.sendMessage("Wrong parameter "+args[0]);
				break;
			}
			return true;
		} else if (cmd.getName().equals("banneditems")) {
			if (!sender.hasPermission("betteritemrestrict.list")) {
				sender.sendMessage(ChatColor.RED+"Permission denied");
				return false;
			}
			
			sender.sendMessage(ChatColor.GOLD+"--- BetterItemRestrict Banned Items List ---");
			
			StringBuilder sb = new StringBuilder();
			boolean sw = true;
			for (RestrictedItem ri : instance.config().ownership.values()) {
				if (ri.label != null && !ri.label.isEmpty()) {
					sb.append(sw ? ChatColor.GREEN : ChatColor.DARK_GREEN);
					sw = !sw;
					sb.append(ri.label);
					sb.append(ChatColor.WHITE);
					sb.append(" - ");
					sb.append(ChatColor.RED);
					sb.append(ri.reason);
				}
			}
			if (sb.length()>1) {
				sb.setLength(sb.length()-2);
			}
			sender.sendMessage(ChatColor.GOLD+"Ownership: "+sb.toString());
			
			sb.setLength(0);
			sw = true;
			for (RestrictedItem ri : instance.config().usage.values()) {
				if (ri.label != null && !ri.label.isEmpty()) {
					sb.append(sw ? ChatColor.GREEN : ChatColor.DARK_GREEN);
					sw = !sw;
					sb.append(ri.label);
					//sb.append(", ");
					sb.append(ChatColor.WHITE);
					sb.append(" - ");
					sb.append(ChatColor.RED);
					sb.append(ri.reason);
				}
			}
			if (sb.length()>1) {
				sb.setLength(sb.length()-2);
			}
			sender.sendMessage(ChatColor.GOLD+"Use/Place: "+sb.toString());
			return true;
		}
		return false;
	}
}
