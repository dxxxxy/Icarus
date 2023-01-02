package studio.dreamys.icarus.config;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import net.minecraft.client.Minecraft;
import org.reflections.Reflections;
import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.annotation.IGroup;
import studio.dreamys.icarus.annotation.IPage;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.component.Page;
import studio.dreamys.icarus.component.sub.*;
import studio.dreamys.icarus.component.wrapper.WChoice;
import studio.dreamys.icarus.component.wrapper.WSlider;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"UnstableApiUsage", "unchecked"})
public class Config {
    public static File file;
    public static Reflections reflections;
    public static Gson gson;
    public static List<ReflectionCache> reflectionCaches = new ArrayList<>();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void init(String modid) {
        //init file
        file = new File(new File(Minecraft.getMinecraft().mcDataDir, modid), "config.json");
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //init gson
        gson = new GsonBuilder().setPrettyPrinting().create();

        //init reflection
        reflections = new Reflections();
        for (Class<?> iPage : reflections.getTypesAnnotatedWith(IPage.class)) {
            Map<Class<?>, Field[]> iGroupMap = new HashMap<>();

            Class<?>[] iGroups = iPage.getDeclaredClasses();
            for (Class<?> iGroup : iGroups) {
                iGroupMap.put(iGroup, iGroup.getDeclaredFields());
            }

            reflectionCaches.add(new ReflectionCache(iPage, iGroupMap));
        }
    }

    public static void generateWindow() {
        for (ReflectionCache reflectionCache : reflectionCaches) {
            Class<?> iPage = reflectionCache.getIPage();

            IPage annotatedPage = iPage.getAnnotation(IPage.class); //get annotation
            Page page = new Page(iPage.getSimpleName(), annotatedPage.icon()); //create page object
            Icarus.getWindow().addPage(page); //add page to window

            for (Map.Entry<Class<?>, Field[]> entry : reflectionCache.getIGroupMap().entrySet()) {
                Class<?> iGroup = entry.getKey();
                Field[] iSettings = entry.getValue();

                IGroup annotatedGroup = iGroup.getAnnotation(IGroup.class); //get annotation
                Group group = new Group(iGroup.getSimpleName(), annotatedGroup.x(), annotatedGroup.y()); //create group object
                page.addGroup(group); //add group to page

                for (Field iSetting : iSettings) { //for every setting
                    Component component = null; //create component object

                    try {
                        if (iSetting.getType() == Runnable.class) { //if button
                            component = new Button(iSetting.getName(), (Runnable) iSetting.get(null));
                        }

                        if (iSetting.getType() == boolean.class) { //if checkbox
                            component = new Checkbox(iSetting.getName(), (boolean) iSetting.get(null));
                        }

                        if (iSetting.getType() == WChoice.class) { //if choice
                            component = new Choice(iSetting.getName(), (WChoice) iSetting.get(null));
                        }

                        if (iSetting.getType() == HashMap.class) { //if combo
                            component = new Combo(iSetting.getName(), (HashMap<String, Boolean>) iSetting.get(null));
                        }

                        if (iSetting.getType() == String.class) { //if field
                            component = new studio.dreamys.icarus.component.sub.Field(iSetting.getName(), (String) iSetting.get(null));
                        }

                        if (iSetting.getType() == WSlider.class) {
                            WSlider WSlider = (WSlider) iSetting.get(null); //get slider wrapper
                            component = new Slider(iSetting.getName(), WSlider.getValue(), WSlider.getMin(), WSlider.getMax(), WSlider.isOnlyInt(), WSlider.getUnits());
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

    public static void save() {
        JsonObject json = new JsonObject();

        for (ReflectionCache reflectionCache : reflectionCaches) { //for every reflection
            JsonObject pageObject = new JsonObject();

            for (Map.Entry<Class<?>, Field[]> groupSettings : reflectionCache.getIGroupMap().entrySet()) {
                JsonObject groupObject = new JsonObject();

                Class<?> iGroup = groupSettings.getKey();
                Field[] iSettings = groupSettings.getValue();

                for (Field iSetting : iSettings) { //for every setting
                    try {
                        if (iSetting.getType() == boolean.class) { //if checkbox
                            groupObject.addProperty(iSetting.getName(), (boolean) iSetting.get(null));
                        }

                        if (iSetting.getType() == WChoice.class) { //if choice
                            groupObject.addProperty(iSetting.getName(), ((WChoice) iSetting.get(null)).getSelected());
                        }

                        if (iSetting.getType() == HashMap.class) { //if combo
                            HashMap<String, Boolean> options = (HashMap<String, Boolean>) iSetting.get(null); //get all combo options
                            List<String> active = options.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toList()); //get active (true ones)
                            groupObject.add(iSetting.getName(), gson.toJsonTree(active, new TypeToken<List<String>>(){}.getType())); //save as an array of strings
                        }

                        if (iSetting.getType() == String.class) { //if field
                            groupObject.addProperty(iSetting.getName(), (String) iSetting.get(null));
                        }

                        if (iSetting.getType() == WSlider.class) { //if slider
                            groupObject.addProperty(iSetting.getName(), ((WSlider) iSetting.get(null)).getValue());
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                pageObject.add(iGroup.getSimpleName(), groupObject);
            }

            json.add(reflectionCache.getIPage().getSimpleName(), pageObject);
        }

        try { //write to file
            FileWriter writer = new FileWriter(file);
            writer.write(gson.toJson(json));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        JsonObject json = new JsonObject();

        try {
            json = new JsonParser().parse(new FileReader(file)).getAsJsonObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
