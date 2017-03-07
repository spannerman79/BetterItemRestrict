package net.kaikk.mc.itemrestrict.sponge;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Text.Builder;
import org.spongepowered.api.text.format.TextColors;

public class BannedItemsCommand implements CommandExecutor {
	private BetterItemRestrict instance;
	
	public BannedItemsCommand(BetterItemRestrict instance) {
		this.instance = instance;
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		src.sendMessage(Text.of(TextColors.GOLD, "--- BetterItemRestrict Banned Items List ---"));
		
		Builder sb = Text.builder();
		boolean sw = true;
		for (RestrictedItem ri : instance.config().ownership.values()) {
			if (ri.label != null && !ri.label.isEmpty()) {
				sb.append(Text.of(sw ? TextColors.GREEN : TextColors.DARK_GREEN, ri.label, ", "));
				sw = !sw;
			}
		}

		src.sendMessage(Text.of(TextColors.GOLD, "Ownership: ", sb.build()));
		
		sb = Text.builder();
		sw = true;
		for (RestrictedItem ri : instance.config().usage.values()) {
			if (ri.label != null && !ri.label.isEmpty()) {
				sb.append(Text.of(sw ? TextColors.GREEN : TextColors.DARK_GREEN, ri.label, ", "));
				sw = !sw;
			}
		}
	
		src.sendMessage(Text.of(TextColors.GOLD, "Use/Place: ",sb.build()));
		return CommandResult.success();
	}

}
