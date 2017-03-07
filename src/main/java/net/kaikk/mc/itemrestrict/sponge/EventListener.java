package net.kaikk.mc.itemrestrict.sponge;

import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.chunk.LoadChunkEvent;

public class EventListener {
	private BetterItemRestrict instance;
	
	public EventListener(BetterItemRestrict instance) {
		this.instance = instance;
	}
	
	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event) {
		instance.inventoryCheck(event.getTargetEntity());
	}
	
	@Listener(beforeModifications = true, order=Order.FIRST)
	public void onBlockPlace(ChangeBlockEvent.Place event) {
		Optional<Player> optPlayer = event.getCause().first(Player.class);
		if (!optPlayer.isPresent()) {
			return;
		}
		
		Player player = optPlayer.get();
		if (instance.check(player, event.getTransactions().get(0).getFinal().getState())) {
			event.getTransactions().get(0).setValid(false);
			event.setCancelled(true);
			instance.inventoryCheck(player);
		}
	}
	
	@Listener(beforeModifications = true, order=Order.FIRST)
	public void onPlayerInteract(InteractEvent event) {
		Optional<Player> optPlayer = event.getCause().first(Player.class);
		if (!optPlayer.isPresent()) {
			return;
		}
		Player player = optPlayer.get();
		if (instance.checkHands(player)) {
			event.setCancelled(true);
			instance.inventoryCheck(player);
		}
		
		if (event instanceof InteractBlockEvent) {
			InteractBlockEvent blockEvent = (InteractBlockEvent) event;
			if (blockEvent.getTargetBlock() != BlockSnapshot.NONE && instance.check(player, blockEvent.getTargetBlock().getExtendedState())) {
				event.setCancelled(true);
				blockEvent.getTargetBlock().getLocation().get().setBlockType(BlockTypes.AIR, Cause.of(NamedCause.source("BetterItemRestrict")));
			}
		}
	}
	
	@Listener(beforeModifications = true, order=Order.FIRST)
	public void onItemClick(ClickInventoryEvent event) {
		Optional<Player> optPlayer = event.getCause().first(Player.class);
		if (!optPlayer.isPresent()) {
			return;
		}
		
		Player player = optPlayer.get();
		if (instance.ownershipCheck(player, event.getCursorTransaction().getOriginal().createStack()) || instance.ownershipCheck(player, event.getCursorTransaction().getFinal().createStack())) {
			event.getCursorTransaction().setValid(false);
			event.setCancelled(true);
			instance.inventoryCheck(player);
		}
	}
	
	@Listener(order=Order.FIRST)
	public void onChunkLoad(LoadChunkEvent event) {
		
	}
}
