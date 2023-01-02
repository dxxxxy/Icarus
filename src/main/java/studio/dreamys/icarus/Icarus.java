package studio.dreamys.icarus;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import org.reflections.Reflections;
import studio.dreamys.icarus.annotation.IGroup;
import studio.dreamys.icarus.annotation.IPage;
import studio.dreamys.icarus.config.SliderWrapper;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.component.Page;
import studio.dreamys.icarus.component.Window;
import studio.dreamys.icarus.component.sub.*;
import studio.dreamys.icarus.config.Config;
import studio.dreamys.icarus.util.RenderUtils;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.List;
import java.util.Set;

public class Icarus {
    @Getter private static studio.dreamys.icarus.config.Config config;
    @Getter private static Window window;
    @Getter private static String modid;

    public static void init(String modid, Window window) {
        RenderUtils.loadFonts();
        Icarus.window = window; //create window object and store it forever
        Icarus.modid = modid;
        config = new Config(modid); //create config which will load settings into window
//        config.load(); //load settings from config
        MinecraftForge.EVENT_BUS.register(new Icarus());

        generateWindow();
    }

    public static void generateWindow() {
        Reflections reflections = new Reflections();
        Set<Class<?>> iPages = reflections.getTypesAnnotatedWith(IPage.class);

        for (Class<?> iPage : iPages) { //for every declared page
            IPage annotatedPage = iPage.getAnnotation(IPage.class); //get annotation
            Page page = new Page(annotatedPage.icon()); //create page object
            window.addPage(page); //add page to window

            Class<?>[] iGroups = iPage.getDeclaredClasses(); //get groups
            for (Class<?> iGroup : iGroups) { //for every declared group
                IGroup annotatedGroup = iGroup.getAnnotation(IGroup.class); //get annotation
                Group group = new Group(iGroup.getSimpleName(), annotatedGroup.x(), annotatedGroup.y()); //create group object
                page.addGroup(group); //add group to page

                Field[] iSettings = iGroup.getDeclaredFields(); //get settings
                for (Field iSetting : iSettings) { //for every declared setting
//                    ISetting annotatedSetting = iSetting.getAnnotation(ISetting.class); //get annotation
                    Component component = null; //create component object

                    try {
                        if (iSetting.getType() == Runnable.class) { //if button
                            component = new Button(iSetting.getName(), (Runnable) iSetting.get(null)); //create button object
                        }

                        if (iSetting.getType() == boolean.class) { //if checkbox
                            component = new Checkbox(iSetting.getName(), (boolean) iSetting.get(null)); //create checkbox object
                        }

                        if (iSetting.getType() == AbstractMap.SimpleEntry.class) { //if choice or combo
                            AbstractMap.SimpleEntry<?, List<String>> entry = (AbstractMap.SimpleEntry<?, List<String>>) iSetting.get(null); //get entry

                            if (entry.getKey() instanceof String) { //if choice
                                component = new Choice(iSetting.getName(), (String) entry.getKey(), entry.getValue()); //create choice object
                            }

                            if (entry.getKey() instanceof List) { //if combo
                                component = new Combo(iSetting.getName(), (List<String>) entry.getKey(), entry.getValue()); //create combo object
                            }
                        }

                        if (iSetting.getType() == String.class) { //if field
                            component = new studio.dreamys.icarus.component.sub.Field(iSetting.getName(), (String) iSetting.get(null)); //create field object
                        }

                        if (iSetting.getType() == SliderWrapper.class) {
                            SliderWrapper sliderWrapper = (SliderWrapper) iSetting.get(null); //get slider wrapper
                            component = new Slider(iSetting.getName(), sliderWrapper.getValue(), sliderWrapper.getMin(), sliderWrapper.getMax(), sliderWrapper.isOnlyInt(), sliderWrapper.getUnits()); //create slider object
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                    if (component == null) continue; //if component is null, skip it
                    group.addChild(component); //add component to group
                }
            }
        }
    }

    @SubscribeEvent
    public void key(InputEvent.KeyInputEvent e) {
        if (Minecraft.getMinecraft().theWorld == null || Minecraft.getMinecraft().thePlayer == null) return;
        if (Keyboard.getEventKeyState()) { //if the key is down
            int keyCode = Keyboard.getEventKey(); //get keycode
            if (keyCode <= 0) return; //ignore invalid keycode
            if (keyCode == window.key) {
                Minecraft.getMinecraft().displayGuiScreen(window);
                return;
            }
            for (Component attachment : window.all) { //for every attachment
                if (attachment instanceof Keybind) { //if it's a keybind
                    Keybind keybind = (Keybind) attachment; //cast to keybind
                    if (keybind.getKey() == keyCode) { //if the keys match
                        if (keybind.getChild() instanceof Checkbox) { //if the child is a checkbox
                            ((Checkbox) keybind.getChild()).toggle(); //toggle the checkbox
                            config.save(); //save the config
                            keybind.getChild().fireChange(); //fire change event
                        }
                        if (keybind.getChild() instanceof Button) { //if the child is a button
                            ((Button) keybind.getChild()).getRunnable().run(); //click the button
                        }
                    }
                }
            }
        }
    }
}
