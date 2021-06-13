# DefinedHUD<sub><sup> · A Minecraft Server Plugin</sup></sub>

Display player's coordinates and world time on the player's action bar. All the functionality of <a href="https://vanillatweaks.net/picker/datapacks/.">Coordinates HUD</a> and more!

Compiled using **Spigot 1.17 (Using new API instead of legacy NMS)**.

## Credit

This project is based on [InfoHUD](https://github.com/RoverIsADog/InfoHUD) and inspired by [CoordinatesHUD](https://vanillatweaks.net/picker/datapacks/).

## Download & Installation

[Go the download page](https://github.com/xixi-wonderland/defined-hud/releases/tag/v0.1)

Drag the `jar` file into the `plugins` folder (at the root of your server). And done.

This plugin should work for all versions 1.17+.

## Features
Display your current coordinates.
<p align="center"><img src="/img/banner.png"></p>
Display the current time in different formats.
<p align="center"><img src="/img/villagerTime.png"></p>
Automatically switch to dark mode in brighter biomes such as deserts and snow biomes.
<p align="center"><img src="/img/darkMode.png"></p>
Nearly every settings can be adjusted (See config.yml).

## Commands
**Per player (Permission: hud.use):**\
`/hud <enable|disable>` : Enable/Disable DefinedHUD for yourself.\
`/hud coordinates <disabled|enabled>` : Enable/Disable showing your coordinates.\
`/hud time <disabled|currentTick|clock12|clock24|villagerSchedule>` : Time display format. \
`/hud darkMode <disabled|enabled|auto>` : Dark mode settings.

**Global (Permission: hud.admin):**\
`/hud messageUpdateDelay`: Change how quickly (ticks) the text is being updated.\
`/hud reload`: Reload settings (Reloads config.yml).\
`/hud benchmark`: Display how long DefinedHUD took to process the last update.\
`/hud brightBiomes <add|remove> <here|BIOME_NAME>`: Add/Remove biomes where dark mode turns on automatically.

## Permissions
`hud.use` Allows player to enable/disable DefinedHUD and change their own settings.\
`hud.admin` Allows player to change global settings.

## config.yml
```yaml
version: '0.X'
# Ticks between each update. Performance cost is extremely small so you are unlikely to run into any
# performance issues even if it is set to 1. Values above 20 can lead to the message fading.
messageUpdateDelay: <number> {Default:5}
# Lower to reduce the delay between entering a bright biome and DefinedHUD changing colors. 
# Very heavy performance impact since MC 1.13. Recommend above 20.
biomeUpdateDelay: <number> {Default:40}
# Colors used by the bright and dark modes respectively (UPPERCASE). https://minecraft.gamepedia.com/Formatting_codes
colors:
  bright1: GOLD
  bright2: WHITE
  dark1: DARK_BLUE
  dark2: DARK_AQUA
# Biomes where dark mode will turn on automatically.
# Find at https://minecraft.gamepedia.com/Biome#Biome_IDs, the F3 menu or use /hud biome add
# Must be in UPPERCASE. Eg. DEEP_FROZEN_OCEAN
# Only biomes in the list recognized by your current MC version will be loaded. Biomes from older/newer versions
# will not be loaded, but remain in the file.
brightBiomes:
- DESERT
- BIOME_NAME
- ...
# Settings on a per-player basis. https://namemc.com/ to get your UUID.
playerConfig:
  7445052d-632b-4aa1-8da8-44be2053bd5b:
    coordinatesMode: <enabled | disabled>
    timeMode: <disabled | currentTick | clock12 | clock24 | villagerSchedule>
    darkMode: <disabled | enabled | auto>
  AnotherUUID:
    coordinatesMode: enabled
    timeMode: clock12
    darkMode: auto
  ...
```

## See also
This plugin is inspired by the excellent CoordinatesHUD datapack. Find it at https://vanillatweaks.net/picker/datapacks/.

## Future Plan
May add the support of version 1.13 in the future. But InfoHUD can also support 1.13 - 1.16 perfectly so this is just a plan.
