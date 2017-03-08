package net.kaikk.mc.itemrestrict.sponge;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.google.inject.Inject;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

@Plugin(id=PluginInfo.id, name = PluginInfo.name, version = PluginInfo.version, description = PluginInfo.description, dependencies = @Dependency(id="kaiscommons"))
public class BetterItemRestrict {
	private static BetterItemRestrict instance;
	private Config config;
	
	@Inject
	@DefaultConfig(sharedRoot = true)
	private ConfigurationLoader<CommentedConfigurationNode> configManager;
	
	@Inject
	private Logger logger;
	
	public void load() throws Exception {
		this.config = new Config(this);
		
		Sponge.getScheduler().createTaskBuilder().execute(() -> {
			for (Player p : Sponge.getServer().getOnlinePlayers()) {
				this.inventoryCheck(p);
			}
		}).delayTicks(100).intervalTicks(10).submit(this);
	}

	@Listener
	public void onServerStart(GameStartedServerEvent event) throws Exception {
		instance = this;
		
		this.load();
		
		// Register listener
		Sponge.getEventManager().registerListeners(this, new EventListener(this));
		
		// Register command
		Sponge.getCommandManager().register(this, CommandSpec.builder().description(Text.of("Banned Items Commands")).permission("betteritemrestrict.list").executor(new BannedItemsCommand(this)).build(), "banneditems");
	}

	
	@Listener
	public void onPluginReload(GameReloadEvent event) throws Exception {
		this.load();
	}
	

	public RestrictedItem restricted(BlockState block) {
		for (RestrictedItem ri : config.ownership.get(block.getType())) {
			if (ri.isRestricted(block)) {
				return ri;
			}
		}
		for (RestrictedItem ri : config.usage.get(block.getType())) {
			if (ri.isRestricted(block)) {
				return ri;
			}
		}
		
		return null;
	}
	
	public RestrictedItem restricted(ItemStack itemStack) {
		for (RestrictedItem ri : config.ownership.get(itemStack.getItem())) {
			if (ri.isRestricted(itemStack)) {
				return ri;
			}
		}
		for (RestrictedItem ri : config.usage.get(itemStack.getItem())) {
			if (ri.isRestricted(itemStack)) {
				return ri;
			}
		}
		
		return null;
	}
	
	public RestrictedItem usageRestricted(BlockState block) {
		for (RestrictedItem ri : config.usage.get(block.getType())) {
			if (ri.isRestricted(block)) {
				return ri;
			}
		}
		
		return null;
	}
	
	public RestrictedItem usageRestricted(ItemStack itemStack) {
		for (RestrictedItem ri : config.usage.get(itemStack.getItem())) {
			if (ri.isRestricted(itemStack)) {
				return ri;
			}
		}
	
		return null;
	}
	
	public RestrictedItem ownershipRestricted(ItemStack itemStack) {
		for (RestrictedItem ri : config.ownership.get(itemStack.getItem())) {
			if (ri.isRestricted(itemStack)) {
				return ri;
			}
		}
	
		return null;
	}

	public boolean check(Player player, ItemStack itemStack) {
		if (itemStack == null || itemStack.getItem() == ItemTypes.NONE) {
			return false;
		}
		
		if (player.hasPermission("betteritemrestrict.bypass") || player.hasPermission("betteritemrestrict.bypass."+itemStack.getItem().getId().replace(":", "-"))) {
			return false;
		}
		
		RestrictedItem ri = this.restricted(itemStack);
		if (ri==null) {
			return false;
		}
		this.inventoryCheck(player);
		this.notify(player, ri);
		return true;
	}
	
	public boolean check(Player player, BlockState block) {
		if (block == null || block.getType() == BlockTypes.AIR) {
			return false;
		}
		
		if (player.hasPermission("betteritemrestrict.bypass") || player.hasPermission("betteritemrestrict.bypass."+block.getType().getId().replace(":", "-"))) {
			return false;
		}
		
		RestrictedItem ri = this.restricted(block);
		if (ri==null) {
			return false;
		}
		this.inventoryCheck(player);
		this.notify(player, ri);
		return true;
	}
	
	public boolean check(Player player, BlockState block, ItemStack itemStack) {
		if (player.hasPermission("betteritemrestrict.bypass")) {
			return false;
		}
		
		RestrictedItem ri;
		if (block!=null && block.getType() != BlockTypes.AIR && !player.hasPermission("betteritemrestrict.bypass."+block.getType().getId().replace(":", "-"))) {
			ri = this.restricted(block);
			if (ri!=null) {
				this.inventoryCheck(player);
				this.notify(player, ri);
				return true;
			}
		}
		
		if (itemStack!=null && itemStack.getItem() !=ItemTypes.NONE && !player.hasPermission("betteritemrestrict.bypass."+itemStack.getItem().getId().replace(":", "-"))) {
			ri = this.restricted(itemStack);
			if (ri!=null) {
				this.inventoryCheck(player);
				this.notify(player, ri);
				return true;
			}
		}
		
		return false;
	}
	
	public boolean check(Player player, BlockState block, ItemStack... itemStacks) {
		if (player.hasPermission("betteritemrestrict.bypass")) {
			return false;
		}
		
		RestrictedItem ri;
		if (block!=null && block.getType() != BlockTypes.AIR && !player.hasPermission("betteritemrestrict.bypass."+block.getType().getId().replace(":", "-"))) {
			ri = this.restricted(block);
			if (ri!=null) {
				this.inventoryCheck(player);
				this.notify(player, ri);
				return true;
			}
		}
		for(ItemStack itemStack : itemStacks) {
			if (itemStack!=null && itemStack.getItem() !=ItemTypes.NONE && !player.hasPermission("betteritemrestrict.bypass."+itemStack.getItem().getId().replace(":", "-"))) {
				ri = this.restricted(itemStack);
				if (ri!=null) {
					this.inventoryCheck(player);
					this.notify(player, ri);
					return true;
				}
			}
		}

		return false;
	}
	
	public boolean checkHands(Player player) {
		if (player.hasPermission("betteritemrestrict.bypass")) {
			return false;
		}
		
		ItemStack itemStack = player.getItemInHand(HandTypes.MAIN_HAND).orElse(null);
		if (itemStack != null && itemStack.getItem() != ItemTypes.NONE && !player.hasPermission("betteritemrestrict.bypass."+itemStack.getItem().getId().replace(":", "-"))) {
			RestrictedItem ri = this.restricted(itemStack);
			if (ri!=null) {
				this.inventoryCheck(player);
				this.notify(player, ri);
				return true;
			}
		}
		
		itemStack = player.getItemInHand(HandTypes.OFF_HAND).orElse(null);
		if (itemStack != null && itemStack.getItem() != ItemTypes.NONE && !player.hasPermission("betteritemrestrict.bypass."+itemStack.getItem().getId().replace(":", "-"))) {
			RestrictedItem ri = this.restricted(itemStack);
			if (ri!=null) {
				this.inventoryCheck(player);
				this.notify(player, ri);
				return true;
			}
		}
		
		return false;
	}
	
	public boolean ownershipCheck(Player player, ItemStack itemStack) {
		if (itemStack == null || itemStack.getItem() == ItemTypes.NONE) {
			return false;
		}
		
		if (player.hasPermission("betteritemrestrict.bypass") || player.hasPermission("betteritemrestrict.bypass."+itemStack.getItem().getId().replace(":", "-"))) {
			return false;
		}
		
		RestrictedItem ri = this.ownershipRestricted(itemStack);
		if (ri==null) {
			return false;
		}
		this.inventoryCheck(player);
		this.notify(player, ri);
		return true;
	}
	
	public void inventoryCheck(Player player) {
		if (player.hasPermission("betteritemrestrict.bypass")) {
			return;
		}
		
		for (Inventory s : player.getInventory().slots()) {
			if (s.peek().isPresent()) {
				ItemStack itemStack = s.peek().get();
				RestrictedItem ri = this.ownershipRestricted(itemStack);
				if (ri!=null && ((!player.hasPermission("betteritemrestrict.bypass."+itemStack.getItem().getId().replace(":", "-"))))) {
					s.clear();
					this.notify(player, ri);
				}
			}
		}
	}
	
	public void notify(Player player, RestrictedItem restrictedItem) {
		this.logger().info(player.getName()+" @ "+player.getLocation()+" tried to own/use "+restrictedItem);
		if (player instanceof Player) {
			player.sendMessage(Text.of(TextColors.RED, restrictedItem.label, " is restricted. Reason: ", restrictedItem.reason));
		}
	}
	public static BetterItemRestrict instance() {
		return instance;
	}
	
	public Config config() {
		return config;
	}

	public Logger logger() {
		return logger;
	}
	
	public static void log(String message) {
		if (instance.logger()!=null) {
			instance.logger().info(message);
		} else {
			System.out.println(message);
		}
	}
	
	public ConfigurationLoader<CommentedConfigurationNode> getConfigManager() {
		return configManager;
	}

	PaginationService getPaginationService() {
		return Sponge.getServiceManager().provide(PaginationService.class).get();
	}
}
