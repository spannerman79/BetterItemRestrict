package net.kaikk.mc.itemrestrict.bukkit;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.kaikk.mc.kaiscommons.bukkit.CommonBukkitUtils;

public class Config {
	private JavaPlugin instance;
	public Multimap<Material,RestrictedItem> usage = HashMultimap.create(), ownership = HashMultimap.create(), world = HashMultimap.create();
	
	Config(JavaPlugin instance) {
		this.instance = instance;
		
		File iresFolder = new File(instance.getDataFolder().getParentFile(), "ItemRestrict");
		File iresConfigFile = new File(iresFolder, "config.yml");
		File configFile = new File(instance.getDataFolder(), "config.yml");
		FileConfiguration config = instance.getConfig();
		
		if (!configFile.exists() && iresConfigFile.exists()) {
			// import existing ires config
			try {
				instance.getLogger().info("Importing ItemRestrict config file...");
				FileConfiguration iresConfig = YamlConfiguration.loadConfiguration(iresConfigFile);
				
				// import
				this.iresImportMaterials("Bans.Usage", usage, iresConfig.getStringList("Bans.Usage"));
				this.iresImportMaterials("Bans.Ownership", ownership, iresConfig.getStringList("Bans.Ownership"));
				this.iresImportMaterials("Bans.World", world, iresConfig.getStringList("Bans.World"));
				this.iresProcessMessages(iresConfig);
				
				// generate config.yml
				List<String> serializedList = new ArrayList<String>();
				for (RestrictedItem ri : usage.values()) {
					serializedList.add(ri.serialize());
				}
				config.set("Usage", serializedList);
				
				serializedList = new ArrayList<String>();
				for (RestrictedItem ri : ownership.values()) {
					serializedList.add(ri.serialize());
				}
				config.set("Ownership", serializedList);
				
				serializedList = new ArrayList<String>();
				for (RestrictedItem ri : world.values()) {
					serializedList.add(ri.serialize());
				}
				config.set("World", serializedList);
				
				config.options().header("Format: \"MaterialName,DamageValue|ItemLabel|Reason\" - Note: \",DamageValue\" can be omit");
				config.save(configFile);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} else {
			// load config
			CommonBukkitUtils.copyAsset(instance, "config.yml");
			instance.reloadConfig();
			
			for(String s : config.getStringList("Ownership")) {
				try {
					RestrictedItem ri = RestrictedItem.deserialize(s);
					ownership.put(ri.material, ri);
				} catch (IllegalArgumentException e) {
					instance.getLogger().warning(e.getMessage());
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			
			for(String s : config.getStringList("Usage")) {
				try {
					RestrictedItem ri = RestrictedItem.deserialize(s);
					if (!ownership.get(ri.material).contains(ri)) {
						usage.put(ri.material, ri);
					}
				} catch (IllegalArgumentException e) {
					instance.getLogger().warning(e.getMessage());
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			
			
			for(String s : config.getStringList("World")) {
				try {
					RestrictedItem ri = RestrictedItem.deserialize(s);
					world.put(ri.material, ri);
				} catch (IllegalArgumentException e) {
					instance.getLogger().warning(e.getMessage());
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			
			
		}
		
		instance.getLogger().info("Loaded "+usage.size()+" usage, "+ownership.size()+" ownership, and "+world.size()+" world restrictions.");
	}
	

	private void iresImportMaterials(String importName, Multimap<Material,RestrictedItem> map, List<String> list) {
		for(String s : list) {
			try {
				RestrictedItem ri = RestrictedItem.fromItemRestrict(s);
				map.put(ri.material, ri);
			} catch (Throwable e) {
				instance.getLogger().warning("Error during ItemRestrict config file import "+importName+": "+s+" - Error: "+e.getMessage());
			}
		}
	}
	
	private void iresProcessMessages(FileConfiguration iresConfig) {
		for (String key : iresConfig.getConfigurationSection("Messages.labels").getKeys(false)) {
			try {
				String label = iresConfig.getString("Messages.labels."+key);
				RestrictedItem tempri = RestrictedItem.fromItemRestrict(key);
				Collection<RestrictedItem> ric = usage.get(tempri.material);
				if (ric!=null) {
					for(RestrictedItem ri : ric) {
						if (ri.equals(tempri)) {
							ri.label = label;
						}
					}
				}
				
				ric = ownership.get(tempri.material);
				if (ric!=null) {
					for(RestrictedItem ri : ric) {
						if (ri.equals(tempri)) {
							ri.label = label;
						}
					}
				}
				
				ric = world.get(tempri.material);
				if (ric!=null) {
					for(RestrictedItem ri : ric) {
						if (ri.equals(tempri)) {
							ri.label = label;
						}
					}
				}
			} catch (Throwable e) {
				instance.getLogger().warning("Error during ItemRestrict config file import Messages.labels: "+key+" - Error: "+e.getMessage());
			}
		}
		
		for (String key : iresConfig.getConfigurationSection("Messages.reasons").getKeys(false)) {
			try {
				String reason = iresConfig.getString("Messages.reasons."+key);
				RestrictedItem tempri = RestrictedItem.fromItemRestrict(key);
				Collection<RestrictedItem> ric = usage.get(tempri.material);
				if (ric!=null) {
					for(RestrictedItem ri : ric) {
						if (ri.equals(tempri)) {
							ri.reason = reason;
						}
					}
				}
				
				ric = ownership.get(tempri.material);
				if (ric!=null) {
					for(RestrictedItem ri : ric) {
						if (ri.equals(tempri)) {
							ri.reason = reason;
						}
					}
				}
				
				ric = world.get(tempri.material);
				if (ric!=null) {
					for(RestrictedItem ri : ric) {
						if (ri.equals(tempri)) {
							ri.reason = reason;
						}
					}
				}
			} catch (Throwable e) {
				instance.getLogger().warning("Error during ItemRestrict config file import Messages.reasons: "+key+" - Error: "+e.getMessage());
			}
		}
	}
	
}
