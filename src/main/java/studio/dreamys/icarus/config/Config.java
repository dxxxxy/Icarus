package studio.dreamys.icarus.config;

import net.minecraft.client.Minecraft;
import scala.actors.threadpool.Arrays;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.component.Window;
import studio.dreamys.icarus.component.sub.*;
import studio.dreamys.icarus.util.ClassUtils;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("unused")
public class Config {
    public static File file;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Config(String modid) {
        File dir = new File(Minecraft.getMinecraft().mcDataDir, modid);
        file = new File(dir, "config.txt");
        try {
            if (!dir.exists()) dir.mkdir();
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            ArrayList<String> toSave = new ArrayList<>();

            for (Component comp : Window.instance.all) {
                //do nothing for button
                if (comp instanceof Checkbox) {
                    toSave.add("Checkbox:" + comp.getLabel() + ":" + ((Checkbox) comp).isToggled());
                }
                if (comp instanceof Choice) {
                    toSave.add("Choice:" + comp.getLabel() + ":" + ((Choice) comp).getSelected());
                }
                //TODO: color
                if (comp instanceof Combo) {
                    toSave.add("Combo:" + comp.getLabel() + ":" + ((Combo) comp).getActiveOptions());
                }
                if (comp instanceof Field) {
                    toSave.add("Field:" + comp.getLabel() + ":" + ((Field) comp).getText());
                }
                //do nothing for Group
                //TODO: keybind
                if (comp instanceof Slider) {
                    toSave.add("Slider:" + comp.getLabel() + ":" + ((Slider) comp).getValue());
                }
            }

            PrintWriter pw = new PrintWriter(file);

            for (String str : toSave) {
                pw.println(str);
            }

            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        try {
            ArrayList<String> lines = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
            reader.close();

            for (String s : lines) {
                String[] args = s.split(":");

                //for every line, create a component and set the default value
                for (@SuppressWarnings("unchecked") Class<Component> sub : ClassUtils.findAllClassesUsingClassLoader("studio.dreamys.icarus.component.sub")) {
                    if (sub.getSimpleName().equals(args[0])) {
                        Component component = (Component) Class.forName("studio.dreamys.icarus.component.sub." + args[0]).newInstance();
                        if (component instanceof Button) {
                            ((Button) component).setLabel(args[1]);
                        }
                        if (component instanceof Checkbox) {
                            ((Checkbox) component).setLabel(args[1]);
                            ((Checkbox) component).setToggled(Boolean.parseBoolean(args[2]));
                        }
                        if (component instanceof Choice) {
                            ((Choice) component).setLabel(args[1]);
                            ((Choice) component).setSelected(args[2]);
                        }
                        //TODO: color
                        if (component instanceof Combo) {
                            ((Combo) component).setLabel(args[1]);
                            ((Combo) component).setActiveOptions(args[2]);
                        }
                        if (component instanceof Field) {
                            ((Field) component).setLabel(args[1]);
                            ((Field) component).setText(args[2]);
                        }
                        //do nothing for Group
                        //TODO: keybind
                        if (component instanceof Slider) {
                            ((Slider) component).setLabel(args[1]);
                            ((Slider) component).setValue(Integer.parseInt(args[2]));
                        }
                    }
                }
            }

//            for (String s : lines) {
//                String[] args = s.split(":");
//                for (@SuppressWarnings("unchecked") Class<Component> sub : ClassUtils.findAllClassesUsingClassLoader("studio.dreamys.icarus.component.sub")) {
//                    if (sub.getSimpleName().equals(args[0])) {
//                        for (Component comp : Window.instance.all) {
//                            if (comp.getLabel().equals(args[1])) {
//                                //do nothing for button
//                                if (comp instanceof Checkbox) {
//                                    ((Checkbox) comp).setToggled(Boolean.parseBoolean(args[2]));
//                                }
//                                if (comp instanceof Choice) {
//                                    ((Choice) comp).setSelected(args[2]);
//                                }
//                                //TODO: color
//                                if (comp instanceof Combo) {
//                                    ((Combo) comp).setActiveOptions(args[2]);
//                                }
//                                if (comp instanceof Field) {
//                                    ((Field) comp).setText(args[2].equals("null") ? "" : args[2]);
//                                }
//                                //do nothing for Group
//                                //TODO: keybind
//                                if (comp instanceof Slider) {
//                                    ((Slider) comp).setValue(Double.parseDouble(args[2]));
//                                }
//                            }
//                        }
//                    }
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getCheckbox(String label) {
        for (Component comp : Window.instance.all) {
            if (comp.getLabel().equals(label) && comp instanceof Checkbox) {
                return ((Checkbox) comp).isToggled();
            }
        }
        return false;
    }

    public String getChoice(String label) {
        for (Component comp : Window.instance.all) {
            if (comp.getLabel().equals(label) && comp instanceof Choice) {
                return ((Choice) comp).getSelected();
            }
        }
        return null;
    }

    public String getCombo(String label) {
        for (Component comp : Window.instance.all) {
            if (comp.getLabel().equals(label) && comp instanceof Combo) {
                return ((Combo) comp).getActiveOptions();
            }
        }
        return null;
    }

    public String getField(String label) {
        for (Component comp : Window.instance.all) {
            if (comp.getLabel().equals(label) && comp instanceof Field) {
                return ((Field) comp).getText();
            }
        }
        return null;
    }

    public double getSlider(String label) {
        for (Component comp : Window.instance.all) {
            if (comp.getLabel().equals(label) && comp instanceof Slider) {
                return ((Slider) comp).getValue();
            }
        }
        return 0;
    }

    public void setCheckbox(String label, boolean toggled) {
        for (Component comp : Window.instance.all) {
            if (comp.getLabel().equals(label) && comp instanceof Checkbox) {
                ((Checkbox) comp).setToggled(toggled);
            }
        }
    }

    public void setChoice(String label, String selected) {
        for (Component comp : Window.instance.all) {
            if (comp.getLabel().equals(label) && comp instanceof Choice) {
                ((Choice) comp).setSelected(selected);
            }
        }
    }

    public void setCombo(String label, String activeOptions) {
        for (Component comp : Window.instance.all) {
            if (comp.getLabel().equals(label) && comp instanceof Combo) {
                ((Combo) comp).setActiveOptions(activeOptions);
            }
        }
    }

    public void setField(String label, String text) {
        for (Component comp : Window.instance.all) {
            if (comp.getLabel().equals(label) && comp instanceof Field) {
                ((Field) comp).setText(text);
            }
        }
    }

    public void setSlider(String label, double value) {
        for (Component comp : Window.instance.all) {
            if (comp.getLabel().equals(label) && comp instanceof Slider) {
                ((Slider) comp).setValue(value);
            }
        }
    }
}
