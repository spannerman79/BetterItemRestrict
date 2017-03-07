package net.kaikk.mc.itemrestrict.sponge;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

public class Config {
	public Multimap<CatalogType,RestrictedItem> usage = HashMultimap.create(), ownership = HashMultimap.create(), world = HashMultimap.create();
	
	public Config(BetterItemRestrict instance) throws Exception {
		//load defaults
		Asset asset = Sponge.getAssetManager().getAsset(instance, "config.yml").get();
		YAMLConfigurationLoader defaultsLoader = YAMLConfigurationLoader.builder().setURL(asset.getUrl()).build();
		ConfigurationNode defaults = defaultsLoader.load();

		//load config & merge defaults
		ConfigurationNode rootNode = instance.getConfigManager().load();
		rootNode.mergeValuesFrom(defaults);
		instance.getConfigManager().save(rootNode);
		
		for (String s : rootNode.getNode("Ownership").getList(TypeToken.of(String.class))) {
			try {
				RestrictedItem ri = RestrictedItem.deserialize(s);
				ownership.put(ri.type, ri);
			} catch (IllegalArgumentException e) {
				instance.logger().warn(e.getMessage());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		for (String s : rootNode.getNode("Usage").getList(TypeToken.of(String.class))) {
			try {
				RestrictedItem ri = RestrictedItem.deserialize(s);
				if (!ownership.get(ri.type).contains(ri)) {
					usage.put(ri.type, ri);
				}
			} catch (IllegalArgumentException e) {
				instance.logger().warn(e.getMessage());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		for (String s : rootNode.getNode("World").getList(TypeToken.of(String.class))) {
			try {
				RestrictedItem ri = RestrictedItem.deserialize(s);
				world.put(ri.type, ri);
			} catch (IllegalArgumentException e) {
				instance.logger().warn(e.getMessage());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		instance.logger().info("Loaded "+usage.size()+" usage, "+ownership.size()+" ownership, and "+world.size()+" world restrictions.");
	}
}

