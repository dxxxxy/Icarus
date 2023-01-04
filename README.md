# Icarus
> rebranded from MineSenseUI

A simple and extremely [lightweight](#lightweightness) Gui and config library for Minecraft Forge 1.8.9 modding heavily inspired by the GameSense/Skeet CS:GO cheat (not an exact copy). A fully working example mod using this library can be found at [test](src/main/java/studio/dreamys/test).

## Setup
### Gradle
```
repositories {
    maven { url "https://repo.dreamys.studio/" }
}
```
```
dependencies {
    implementation "studio.dreamys:Icarus:$VERSION"
}
```

Replace `$VERSION` with the latest from the [repo](https://github.com/DxxxxY/repo/tree/master/studio/dreamys/Icarus).

## What's v2
- **.json**
- **Same look**
- **Customizability**
- **~90% code rewrite**
- **New and redesigned config system**
- **Complete separation of UI and Config**
- **Enhanced and simplified developer experience**

## [Wiki](https://github.com/DxxxxY/Icarus/wiki/)

## UI Components
- Button
- Checkbox
- Choice
- ~~Color~~ (coming soon)
- Combo
- Field
- Slider
- Keybind
- Group
- Page
- Window

## Extra
- [Notification](https://www.youtube.com/watch?v=624J5kAZqNw&ab_channel=DxxxxY)
- Watermark ![Watermark](.github/watermark.png)

## Lightweightness
- Everything is created and stored on client init.
- Everything is rendered using libraries present on runtime.
- Small size, no extra-dependencies, no bloatware.

## Menu (as of 09/02/2022) is fully functional:
![icarus.gif](.github/icarus.gif)