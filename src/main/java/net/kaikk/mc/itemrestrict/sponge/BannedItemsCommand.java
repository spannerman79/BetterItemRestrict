package net.kaikk.mc.itemrestrict.sponge;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Text.Builder;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;

public class BannedItemsCommand implements CommandExecutor {
	private BetterItemRestrict instance;
	
	public BannedItemsCommand(BetterItemRestrict instance) {
		this.instance = instance;
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		List<Text> texts = new ArrayList<>();
		texts.add(Text.of(TextColors.GOLD, "Ownership: "));
		boolean sw = true;
		for (RestrictedItem ri : instance.config().ownership.values()) {
			if (ri.label != null && !ri.label.isEmpty()) {
				texts.add(Text.of(sw ? TextColors.GREEN : TextColors.DARK_GREEN, ri.label, TextColors.WHITE, " - ", TextColors.RED, ri.reason));
				sw = !sw;
			}
		}

		texts.add(Text.of(TextColors.GOLD, "Use/Place: "));
		sw = true;
		for (RestrictedItem ri : instance.config().usage.values()) {
			if (ri.label != null && !ri.label.isEmpty()) {
				texts.add(Text.of(sw ? TextColors.GREEN : TextColors.DARK_GREEN, ri.label, TextColors.WHITE, " - ", TextColors.RED, ri.reason));
				sw = !sw;
			}
		}

		instance.getPaginationService().builder()
				.contents(texts)
				.title(Text.of(TextColors.RED, "Banned Items List"))
				.sendTo(src);


		return CommandResult.success();
	}

}
