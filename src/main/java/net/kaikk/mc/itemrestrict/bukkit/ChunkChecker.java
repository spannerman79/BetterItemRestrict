package net.kaikk.mc.itemrestrict.bukkit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import net.kaikk.mc.kaiscommons.bukkit.CommonBukkitUtils;

public class ChunkChecker extends Thread {
	final private Chunk chunk;
	final private BetterItemRestrict instance = BetterItemRestrict.instance();
	final private int yMax;
	
	public ChunkChecker(Chunk chunk) {
		super("BetterItemRestrict-ChunkChecker");
		this.chunk = chunk;
		this.yMax = chunk.getWorld().getMaxHeight();
	}

	@Override
	public void run() {
		final List<Block> toBeRemoved = new ArrayList<>();
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < yMax; y++) {
					final Block block = chunk.getBlock(x, y, z);
					for (RestrictedItem ri : instance.config().world.get(block.getType())) {
						if (ri.isRestricted(block)) {
							toBeRemoved.add(block);
							break;
						}
					}
					
				}
			}
		}
		
		if (!toBeRemoved.isEmpty()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					for (Block block : toBeRemoved) {
						instance.getLogger().info("Removing "+block.getType()+" at "+CommonBukkitUtils.locationToString(block.getLocation()));
						block.setType(Material.AIR);
					}
				}
			}.runTask(instance);
		}
	}
}
