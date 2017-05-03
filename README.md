## BetterItemRestrict
An Item Restrict plugin for Bukkit and Sponge. You can restrict players from owning or using a certain item. This plugin is made for modded servers (Cauldron-like, or SpongeForge).  
Item restrictions can be bypassed with the "betteritemrestrict.bypass" permission (for all items) and "betteritemrestrict.bypass.MATERIAL_NAME" for bypassing a specific item. On Sponge, replace any ":" on the item name with "-". If your permission plugin supports world permissions, you can allow items in certain worlds and restrict it in other worlds.   
Also the /banneditems command will show a list of banned items.

### Dependency
This plugin requires [Kai's Commons](http://github.com/KaiKikuchi/KaisCommons)

### Install
Download and copy the jar file in your plugins folder (under mods/plugins/ for Sponge servers).  
After loading the plugin for the first time, a default config will be generated.  
Edit your config and add the 

#### Example:
[Check this example file](https://github.com/KaiKikuchi/BetterItemRestrict/blob/master/config.example.yml)

#### Permissions
- betteritemrestrict.list = for the "/banneditems" command
- betteritemrestrict.manage = for the "/bires" command
- betteritemrestrict.notify = for being notified in-game when a banned item is used
