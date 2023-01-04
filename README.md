# Icarus
> rebranded from MineSenseUI

A simple and extremely [lightweight](#lightweightness) Gui and config library for Minecraft Forge 1.8.9 modding heavily inspired by the GameSense/Skeet CS:GO cheat (not an exact copy). A fully working example mod using this library can be found at [test](src/main/java/studio/dreamys/test).

## Importing
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

Replace $VERSION with any version from the [repo](https://github.com/DxxxxY/repo/tree/master/studio/dreamys/Icarus).

## Components
> Some components have secondary constructors with default values.
- Button
- Checkbox
- Choice
- ~~Color~~ (coming soon)
- Combo
- Field
- Group
- Keybind
- Slider
- Page
- Window

## Extra
- [Notification](https://www.youtube.com/watch?v=624J5kAZqNw&ab_channel=DxxxxY)
- Watermark ![Watermark](.github/watermark.png)

## Demo
### Initializing Icarus

We will be doing this in the `preInit` event of the test mod.


```java
@Mod(modid = "test", name = "Test", version = "1.0")
public class TestMod {
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        //...
    }
}
```

Create a window
```java
Window window = new Window(Minecraft.getMinecraft().displayWidth / 2.0, Minecraft.getMinecraft().displayHeight / 4.0, 731 / 2.0, 617 / 2.0, new Color(29, 122, 215));
```

Initialize Icarus.
```java
Icarus.init(e.getModMetadata().modid, window);
```

On the

## Lightweightness
- Everything is created and stored on client init.
- Everything is rendered using libraries present on runtime.
- Small size, no extra-dependencies, no bloatware.

## Menu (as of 09/02/2022) is fully functional:
![icarus.gif](.github/icarus.gif)