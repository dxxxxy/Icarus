package studio.dreamys.icarus.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import org.reflections.Reflections;
import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.annotation.field.DropdownOptions;
import studio.dreamys.icarus.annotation.field.IKeybind;
import studio.dreamys.icarus.annotation.field.SliderOptions;
import studio.dreamys.icarus.annotation.type.IGroup;
import studio.dreamys.icarus.annotation.type.IPage;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.component.Group;
import studio.dreamys.icarus.component.Page;
import studio.dreamys.icarus.component.sub.*;
import studio.dreamys.icarus.component.sub.attachment.Attachment;
import studio.dreamys.icarus.component.sub.attachment.sub.Keybind;
import studio.dreamys.icarus.reflection.ReflectionCache;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class Config {
    public static File configFile, keybindFile;
    public static Reflections reflections;
    public static Gson gson;
    public static List<ReflectionCache> reflectionCaches = new ArrayList<>();
    public static Map<Class<? extends Component>, Class<? extends Component>> componentReplacements = new HashMap<>();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void init(String modid) {
        //init files
        configFile = new File(new File(Minecraft.getMinecraft().mcDataDir, modid), "config.json");
        keybindFile = new File(new File(Minecraft.getMinecraft().mcDataDir, modid), "keybind.json");
        try {
            configFile.getParentFile().mkdirs();
            configFile.createNewFile();
            keybindFile.createNewFile();
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

        load();
        save();

        generateWindow();

        loadAttachments();
        saveAttachments();
    }

    public static void generateWindow() {
        for (ReflectionCache reflectionCache : reflectionCaches) {
            Class<?> iPage = reflectionCache.getIPage();

            IPage annotatedPage = iPage.getAnnotation(IPage.class); //get annotation
            Component page = new Page(iPage.getSimpleName(), annotatedPage.value()); //create page object

            if (componentReplacements.containsKey(Page.class)) {
                try {
                    page = componentReplacements.get(Page.class).getConstructor(String.class, char.class).newInstance(iPage.getSimpleName(), annotatedPage.value());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Icarus.getWindow().addPage((Page) page); //add page to window

            for (Map.Entry<Class<?>, Field[]> entry : reflectionCache.getIGroupMap().entrySet()) {
                Class<?> iGroup = entry.getKey();
                Field[] iSettings = entry.getValue();

                IGroup annotatedGroup = iGroup.getAnnotation(IGroup.class); //get annotation
                Component group = new Group(iGroup.getSimpleName(), annotatedGroup.x(), annotatedGroup.y()); //create group object

                if (componentReplacements.containsKey(Group.class)) {
                    try {
                        group = componentReplacements.get(Group.class).getConstructor(String.class, double.class, double.class).newInstance(iGroup.getSimpleName(), annotatedGroup.x(), annotatedGroup.y());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                ((Page) page).addGroup((Group) group); //add group to page

                for (Field iSetting : iSettings) { //for every setting
                    Component component = null; //create component object

                    iSetting.setAccessible(true); //set accessible

                    //get additional annotations
                    DropdownOptions dropdownOptions = iSetting.getAnnotation(DropdownOptions.class);
                    SliderOptions sliderOptions = iSetting.getAnnotation(SliderOptions.class);
                    IKeybind iKeybind = iSetting.getAnnotation(IKeybind.class);

                    if (iSetting.getType() == Runnable.class) { //if button
                        component = new Button(iSetting.getName());
                        if (componentReplacements.containsKey(Button.class)) {
                            try {
                                component = componentReplacements.get(Button.class).getConstructor(String.class).newInstance(iSetting.getName());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    else if (iSetting.getType() == boolean.class) { //if checkbox
                        component = new Checkbox(iSetting.getName());
                        if (componentReplacements.containsKey(Checkbox.class)) {
                            try {
                                System.out.println("using custom checkbox");
                                component = componentReplacements.get(Checkbox.class).getConstructor(String.class).newInstance(iSetting.getName());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    else if (iSetting.getType() == String.class && dropdownOptions != null) { //if choice
                        component = new Choice(iSetting.getName(), Arrays.asList(dropdownOptions.value()));
                        if (componentReplacements.containsKey(Choice.class)) {
                            try {
                                component = componentReplacements.get(Choice.class).getConstructor(String.class, List.class).newInstance(iSetting.getName(), Arrays.asList(dropdownOptions.value()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    else if (iSetting.getType() == String[].class && dropdownOptions != null) { //if combo
                        component = new Combo(iSetting.getName(), Arrays.asList(dropdownOptions.value()));
                        if (componentReplacements.containsKey(Combo.class)) {
                            try {
                                component = componentReplacements.get(Combo.class).getConstructor(String.class, List.class).newInstance(iSetting.getName(), Arrays.asList(dropdownOptions.value()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    else if (iSetting.getType() == String.class) { //if field
                        component = new studio.dreamys.icarus.component.sub.Field(iSetting.getName());
                        if (componentReplacements.containsKey(studio.dreamys.icarus.component.sub.Field.class)) {
                            try {
                                component = componentReplacements.get(studio.dreamys.icarus.component.sub.Field.class).getConstructor(String.class).newInstance(iSetting.getName());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    else if (iSetting.getType() == double.class && sliderOptions != null) { //if slider
                        component = new Slider(iSetting.getName(), sliderOptions.min(), sliderOptions.max(), sliderOptions.onlyInt(), sliderOptions.units());
                        if (componentReplacements.containsKey(Slider.class)) {
                            try {
                                component = componentReplacements.get(Slider.class).getConstructor(String.class, double.class, double.class, boolean.class, String.class).newInstance(iSetting.getName(), sliderOptions.min(), sliderOptions.max(), sliderOptions.onlyInt(), sliderOptions.units());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (component == null) continue; //if component is null, skip it
                    component.configField = iSetting;
                    ((Group) group).addChild(component); //add component to group

                    //only bind keybind to checkbox or button
                    if ((iSetting.getType() == Runnable.class || iSetting.getType() == boolean.class) && iKeybind != null) new Keybind(iKeybind.value()).attachTo(component);
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
                    DropdownOptions dropdownOptions = iSetting.getAnnotation(DropdownOptions.class);
                    SliderOptions sliderOptions = iSetting.getAnnotation(SliderOptions.class);

                    iSetting.setAccessible(true); //set accessible

                    try {
                        if (iSetting.getType() == boolean.class) { //if checkbox
                            groupObject.addProperty(iSetting.getName(), (boolean) iSetting.get(null));
                        }

                        else if (iSetting.getType() == String.class && dropdownOptions != null) { //if choice
                            groupObject.addProperty(iSetting.getName(), (String) iSetting.get(null));
                        }

                        else if (iSetting.getType() == String[].class && dropdownOptions != null) { //if combo
                            groupObject.add(iSetting.getName(), gson.toJsonTree(iSetting.get(null)));
                        }

                        else if (iSetting.getType() == String.class) { //if field
                            groupObject.addProperty(iSetting.getName(), (String) iSetting.get(null));
                        }

                        else if (iSetting.getType() == double.class && sliderOptions != null) { //if slider
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
            FileWriter writer = new FileWriter(configFile);
            writer.write(gson.toJson(json));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void load() {
        JsonObject json;

        try {
            json = new JsonParser().parse(new FileReader(configFile)).getAsJsonObject();
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
                    DropdownOptions dropdownOptions = iSetting.getAnnotation(DropdownOptions.class);
                    SliderOptions sliderOptions = iSetting.getAnnotation(SliderOptions.class);

                    iSetting.setAccessible(true); //set accessible

                    try {
                        if (iSetting.getType() == boolean.class) { //if checkbox
                            iSetting.set(null, groupObject.get(iSetting.getName()).getAsBoolean());
                        }

                        else if (iSetting.getType() == String.class && dropdownOptions != null) { //if choice
                            iSetting.set(null, groupObject.get(iSetting.getName()).getAsString());
                        }

                        else if (iSetting.getType() == String[].class && dropdownOptions != null) { //if combo
                            iSetting.set(null, gson.fromJson(groupObject.get(iSetting.getName()), String[].class));
                        }

                        else if (iSetting.getType() == String.class) { //if field
                            iSetting.set(null, groupObject.get(iSetting.getName()).getAsString());
                        }

                        else if (iSetting.getType() == double.class && sliderOptions != null) { //if slider
                            iSetting.set(null, groupObject.get(iSetting.getName()).getAsDouble());
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void saveAttachments() {
        JsonObject json = new JsonObject();

        for (Attachment attachment : Icarus.getWindow().attachments) {
            if (attachment instanceof Keybind) {
                if (json.has(attachment.getChild().group.page.getLabel())) { //page already exists
                    //lets get it
                    JsonObject pageObject = json.getAsJsonObject(attachment.getChild().group.page.getLabel());

                    if (pageObject.has(attachment.getChild().group.getLabel())) { //group already exists
                        //lets get it
                        JsonObject groupObject = pageObject.getAsJsonObject(attachment.getChild().group.getLabel());

                        //add keybind to group
                        groupObject.addProperty(attachment.getChild().configField.getName(), ((Keybind) attachment).getKey());
                    } else { //group doesn't exist
                        //lets create one
                        JsonObject groupObject = new JsonObject();

                        //add keybind to group
                        groupObject.addProperty(attachment.getChild().configField.getName(), ((Keybind) attachment).getKey());

                        //add group to page
                        pageObject.add(attachment.getChild().group.getLabel(), groupObject);
                    }
                } else { //page doesn't exist
                    //lets create a page
                    JsonObject pageObject = new JsonObject();

                    //lets create a group
                    JsonObject groupObject = new JsonObject();

                    //add keybind to group
                    groupObject.addProperty(attachment.getChild().configField.getName(), ((Keybind) attachment).getKey());

                    //add group to page
                    pageObject.add(attachment.getChild().group.getLabel(), groupObject);

                    //add page to json
                    json.add(attachment.getChild().group.page.getLabel(), pageObject);
                }
            }
        }

        try { //write to file
            FileWriter writer = new FileWriter(keybindFile);
            writer.write(gson.toJson(json));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadAttachments() {
        JsonObject json;

        try {
            json = new JsonParser().parse(new FileReader(keybindFile)).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load settings");
            return;
        }

        for (Attachment attachment : Icarus.getWindow().attachments) {
            JsonObject pageObject = json.getAsJsonObject(attachment.getChild().group.page.getLabel().replaceAll(" ", "_")); //get page object
            if (pageObject == null) continue;
            JsonObject groupObject = pageObject.getAsJsonObject(attachment.getChild().group.getLabel().replaceAll(" ", "_")); //get group object
            if (groupObject == null) continue;

            if (attachment instanceof Keybind) {
                ((Keybind) attachment).setKey(groupObject.get(attachment.getChild().getLabel().replaceAll(" ", "_")).getAsInt());
            }
        }
    }
}
