# Icarus

A lightweight GUI and config library for Minecraft mods:

- You write your settings as annotated static fields.
- Icarus builds the menu, saves configs and handles keybinds.
- The menus recreate two popular CS:GO menus, skeet (gamesense) and fatality.
- The same mod code runs on Forge, Fabric and NeoForge, from 1.7.10 to 26.3.

| `Themes.SKEET` | `Themes.FATALITY` |
|---|---|
| ![skeet](.github/skeet.gif) | ![fatality](.github/fatality.gif) |

## Supported versions

| Minecraft | Fabric | Forge | NeoForge |
|---|---|---|---|
| 1.7.10, 1.8.9, 1.12.2 | | ✅ | |
| 1.16.5, 1.20.1 | ✅ | ✅ | |
| 1.21.1 | ✅ | | ✅ |
| 26.3 | ✅ | | ⚠️ builds, untested |

## Setup

```groovy
repositories {
    maven { url "https://repo.dreamys.studio/" }
}

dependencies {
    // studio.dreamys:icarus-<loader>-<minecraft version>:<icarus version>
    implementation "studio.dreamys:icarus-fabric-1.21.1:3.0.1"
}
```

How the jar ships with your mod:

- **Forge 1.7.10 to 1.12.2:** shade the jar into your mod.
- **Everything else:** ship the jar inside your mod with Jar-in-Jar (mod id `icarus`). Fabric API isn't needed.

The jars are also attached to each [release](https://github.com/DxxxxY/Icarus/releases).

**Fonts must be installed beforehand:**

- skeet uses Verdana;
- fatality uses Segoe UI Semibold, Segoe UI and Bahnschrift.

Windows 10 and 11 ship them. Elsewhere Icarus falls back to other fonts, and the menus won't match exactly.

## Usage

```java
@IPage(icon = "rage")
public class Combat {
    @IGroup(column = 0)
    public static class KillAura {
        @IKeybind(Keys.KEY_R)
        public static boolean Enabled = false;

        @SliderOptions(min = 1, max = 6, units = "m")
        public static double Range = 3.5;

        @DropdownOptions({"Head", "Chest", "Legs"})
        public static String[] Hitboxes = {"Head"};
    }
}
```

```java
IcarusMod mod = Icarus.create("yourmodid", Themes.SKEET); // once, after the game window exists
mod.onChange(Combat.KillAura.class, "Enabled", (Boolean on) -> mod.toasts().send("KillAura", on ? "On" : "Off"));
```

Right Shift opens the menu. Settings are saved to `config/yourmodid/`.

The **[wiki](https://github.com/DxxxxY/Icarus/wiki)** covers:

- [where to call `create`](https://github.com/DxxxxY/Icarus/wiki/Getting-Started) on each loader;
- [every setting type and annotation](https://github.com/DxxxxY/Icarus/wiki/Pages-and-Settings);
- [keybinds](https://github.com/DxxxxY/Icarus/wiki/Keybinds) and [configs](https://github.com/DxxxxY/Icarus/wiki/Configs);
- [toasts and the HUD](https://github.com/DxxxxY/Icarus/wiki/Callbacks-and-HUD);
- [themes](https://github.com/DxxxxY/Icarus/wiki/Themes);
- [the example mod](https://github.com/DxxxxY/Icarus/wiki/Example-Mod) and [building](https://github.com/DxxxxY/Icarus/wiki/Building);
- [upgrading from v2](https://github.com/DxxxxY/Icarus/wiki/Upgrading-from-v2).

## Credits

- The menu designs belong to [gamesense / skeet](https://gamesense.pub) and [fatality](https://fatality.win). Icarus isn't affiliated with either.
- Measurements, colours and icons were taken from the YouTube reviews "My Brutally Honest Skeet.cc Review" and "My Brutally Honest Fatality.win Review".
- `undefeated.ttf` is the "undefeated" CS:GO icon font from the CS:GO community.

## Disclaimer
This is for educational purposes only. I am not responsible for any damage caused by this tool.

## License
GPLv3 © dxxxxy
