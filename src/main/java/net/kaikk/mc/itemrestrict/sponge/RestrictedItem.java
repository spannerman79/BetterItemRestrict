package net.kaikk.mc.itemrestrict.sponge;

import java.util.Optional;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

public class RestrictedItem {
	public static final DataQuery UNSAFE_DAMAGE = DataQuery.of("UnsafeDamage"); // item durability
	public static final DataQuery UNSAFE_DATA = DataQuery.of("UnsafeData"); // item nbt

	CatalogType type;
	Short dv;
	String label, reason;

	public RestrictedItem(CatalogType type, Short dv) {
		this.type = type;
		this.dv = dv;
	}

	public RestrictedItem(CatalogType type, Short dv, String label, String reason) {
		this.type = type;
		this.dv = dv;
		this.label = label;
		this.reason = reason;
	}

	@Override
	public String toString() {
		return "RestrictedItem "+type+(dv!=null ? ":"+dv : "");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dv == null) ? 0 : dv.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		if (type != other.type) {
			return false;
		}
		return true;
	}

	public boolean isRestricted(ItemStack itemStack) {
		return this.type.equals(itemStack.getItem()) && (this.dv==null || this.costlyRestrictedCheck(itemStack));
	}

	private boolean costlyRestrictedCheck(ItemStack itemStack) {
		Optional<Object> unsafeDamage = itemStack.toContainer().get(UNSAFE_DAMAGE);
		return !unsafeDamage.isPresent() || this.dv.shortValue() == ((Number)unsafeDamage.get()).shortValue();
	}

	public boolean isRestricted(BlockState block) {
		return this.type.equals(block.getType());
	}

	public boolean isRestricted(CatalogType type, Short dv) {
		return this.type.equals(type) && (this.dv==null || this.dv == dv);
	}

	public static RestrictedItem deserialize(String serialized) {
		String[] s = serialized.split("[|]");
		Short dv = null;
		String label = s.length>1 ? s[1] : "", reason = s.length>2 ? s[2] : "";

		String[] sp = s[0].split("[,]");
		Optional<ItemType> optItemType = Sponge.getRegistry().getType(ItemType.class, sp[0]);
		if (!optItemType.isPresent()) {
			throw new IllegalArgumentException("Invalid material "+sp[0]);
		}
		if (sp.length>1) {
			dv = Short.valueOf(sp[1]);
		}

		return new RestrictedItem(optItemType.get(), dv, label, reason);
	}

	public String serialize() {
		return type+(dv!=null ? ","+dv : "")+"|"+label+"|"+reason;
	}
}
