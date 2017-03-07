package net.kaikk.mc.itemrestrict.bukkit;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class RestrictedItem {
	Material material;
	Short dv;
	String label, reason;
	
	public RestrictedItem(Material material, Short dv) {
		this.material = material;
		this.dv = dv;
	}
	
	public RestrictedItem(Material material, Short dv, String label, String reason) {
		this.material = material;
		this.dv = dv;
		this.label = label;
		this.reason = reason;
	}

	@Override
	public String toString() {
		return "RestrictedItem "+material+(dv!=null ? ":"+dv : "");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dv == null) ? 0 : dv.hashCode());
		result = prime * result + ((material == null) ? 0 : material.hashCode());
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
		if (!(obj instanceof RestrictedItem)) {
			return false;
		}
		RestrictedItem other = (RestrictedItem) obj;
		if (dv == null) {
			if (other.dv != null) {
				return false;
			}
		} else if (!dv.equals(other.dv)) {
			return false;
		}
		if (material != other.material) {
			return false;
		}
		return true;
	}
	
	public boolean isRestricted(ItemStack itemStack) {
		return itemStack.getType()==this.material && (this.dv==null || this.dv == itemStack.getDurability());
	}
	
	@SuppressWarnings("deprecation")
	public boolean isRestricted(Block block) {
		return block.getType()==this.material && (this.dv==null || this.dv == block.getData());
	}
	
	public boolean isRestricted(Material material, Short dv) {
		return material==this.material && (this.dv==null || this.dv == dv);
	}
	
	@SuppressWarnings("deprecation")
	public static RestrictedItem fromItemRestrict(String serialized) {
		String[] split = serialized.split("[-]");
		Material m = Material.getMaterial(Integer.valueOf(split[0]));
		if (m==null) {
			throw new IllegalArgumentException("Invalid id "+split[0]);
		}
		Short sh = null;
		
		if (split.length>1) {
			sh = Short.valueOf(split[1]);
		}
		
		return new RestrictedItem(m, sh);
	}
	
	public static RestrictedItem deserialize(String serialized) {
		String[] s = serialized.split("[|]");
		Material material;
		Short dv = null;
		String label = s.length>1 ? s[1] : "", reason = s.length>2 ? s[2] : "";
		
		String[] sp = s[0].split("[,]");
		material = Material.matchMaterial(sp[0]);
		if (material==null) {
			throw new IllegalArgumentException("Invalid material "+sp[0]);
		}
		if (sp.length>1) {
			dv = Short.valueOf(sp[1]);
		}
		
		return new RestrictedItem(material, dv, label, reason);
	}
	
	public String serialize() {
		return material+(dv!=null ? ","+dv : "")+"|"+label+"|"+reason;
	}
}
