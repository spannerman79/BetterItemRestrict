package net.kaikk.mc.itemrestrict.bukkit;

import java.util.UUID;

import org.bukkit.Chunk;

public class ChunkIdentifier {
	final private UUID world;
	final private int x, z;
	
	public ChunkIdentifier(UUID world, int x, int z) {
		this.world = world;
		this.x = x;
		this.z = z;
	}
	
	public ChunkIdentifier(Chunk chunk) {
		this.world = chunk.getWorld().getUID();
		this.x = chunk.getX();
		this.z = chunk.getZ();
	}

	public UUID getWorld() {
		return world;
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((world == null) ? 0 : world.hashCode());
		result = prime * result + x;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ChunkIdentifier)) {
			return false;
		}
		ChunkIdentifier other = (ChunkIdentifier) obj;
		if (x != other.x) {
			return false;
		}
		if (z != other.z) {
			return false;
		}
		if (world == null) {
			if (other.world != null) {
				return false;
			}
		} else if (!world.equals(other.world)) {
			return false;
		}
		return true;
	}
	
	
}
