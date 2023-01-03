package studio.dreamys.icarus.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import org.reflections.Reflections;
import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.annotation.field.Options;
import studio.dreamys.icarus.annotation.field.SOptions;
import studio.dreamys.icarus.annotation.type.IGroup;
import studio.dreamys.icarus.annotation.type.IPage;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.component.Group;
import studio.dreamys.icarus.component.Page;
import studio.dreamys.icarus.component.sub.*;
import studio.dreamys.icarus.reflection.ReflectionCache;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

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

                    //get additional annotations
                    Options options = iSetting.getAnnotation(Options.class);
                    SOptions sOptions = iSetting.getAnnotation(SOptions.class);

                    try {
                        if (iSetting.getType() == Runnable.class) { //if button
                            component = new Button(iSetting.getName(), (Runnable) iSetting.get(null));
                        }

                        else if (iSetting.getType() == boolean.class) { //if checkbox
                            component = new Checkbox(iSetting.getName());
                        }

                        else if (iSetting.getType() == String.class && options != null) { //if choice
                            component = new Choice(iSetting.getName(), Arrays.asList(options.value()));
                        }

                        else if (iSetting.getType() == String[].class && options != null) { //if combo
                            component = new Combo(iSetting.getName(), Arrays.asList(options.value()));
                        }

                        else if (iSetting.getType() == String.class) { //if field
                            component = new studio.dreamys.icarus.component.sub.Field(iSetting.getName());
                        }

                        else if (iSetting.getType() == double.class && sOptions != null) { //if slider
                            component = new Slider(iSetting.getName(), sOptions.min(), sOptions.max(), sOptions.onlyInt(), sOptions.units());
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                    if (component == null) continue; //if component is null, skip it
                    component.configField = iSetting;
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
                    //get additional annotations
                    Options options = iSetting.getAnnotation(Options.class);
                    SOptions sOptions = iSetting.getAnnotation(SOptions.class);

                    try {
                        if (iSetting.getType() == boolean.class) { //if checkbox
                            groupObject.addProperty(iSetting.getName(), (boolean) iSetting.get(null));
                        }

                        else if (iSetting.getType() == String.class && options != null) { //if choice
                            groupObject.addProperty(iSetting.getName(), (String) iSetting.get(null));
                        }

                        else if (iSetting.getType() == String[].class && options != null) { //if combo
                            groupObject.add(iSetting.getName(), gson.toJsonTree(iSetting.get(null)));
                        }

                        else if (iSetting.getType() == String.class) { //if field
                            groupObject.addProperty(iSetting.getName(), (String) iSetting.get(null));
                        }

                        else if (iSetting.getType() == double.class && sOptions != null) { //if slider
                            groupObject.addProperty(iSetting.getName(), (double) iSetting.get(null));
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
        JsonObject json;

        try {
            json = new JsonParser().parse(new FileReader(file)).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load settings");
            return;
        }

        for (ReflectionCache reflectionCache : reflectionCaches) { //for every reflection
            JsonObject pageObject = json.getAsJsonObject(reflectionCache.getIPage().getSimpleName());

            for (Map.Entry<Class<?>, Field[]> groupSettings : reflectionCache.getIGroupMap().entrySet()) {
                if (pageObject == null) continue;
                JsonObject groupObject = pageObject.getAsJsonObject(groupSettings.getKey().getSimpleName());


                if (groupObject == null) continue;
                Field[] iSettings = groupSettings.getValue();

                for (Field iSetting : iSettings) { //for every setting
                    if (groupObject.get(iSetting.getName()) == null) continue;

                    //get additional annotations
                    Options options = iSetting.getAnnotation(Options.class);
                    SOptions sOptions = iSetting.getAnnotation(SOptions.class);

                    try {
                        if (iSetting.getType() == boolean.class) { //if checkbox
                            iSetting.set(null, groupObject.get(iSetting.getName()).getAsBoolean());
                        }

                        else if (iSetting.getType() == String.class && options != null) { //if choice
                            iSetting.set(null, groupObject.get(iSetting.getName()).getAsString());
                        }

                        else if (iSetting.getType() == String[].class && options != null) { //if combo
                            iSetting.set(null, gson.fromJson(groupObject.get(iSetting.getName()), String[].class));
                        }

                        else if (iSetting.getType() == String.class) { //if field
                            iSetting.set(null, groupObject.get(iSetting.getName()).getAsString());
                        }

                        else if (iSetting.getType() == double.class && sOptions != null) { //if slider
                            iSetting.set(null, groupObject.get(iSetting.getName()).getAsDouble());
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
