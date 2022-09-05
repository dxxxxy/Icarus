package studio.dreamys.icarus.config;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.component.Component;
import studio.dreamys.icarus.component.sub.*;
import studio.dreamys.icarus.event.ComponentStateChangeEvent;

import java.io.*;
import java.util.ArrayList;

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
            load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            ArrayList<String> toSave = new ArrayList<>();

            for (Component comp : Icarus.getWindow().all) {
                //do nothing for button
                if (comp instanceof Checkbox) {
                    toSave.add("Checkbox:" + comp.getLabel() + ":" + comp.getGroup().getLabel() + ":" + ((Checkbox) comp).isToggled());
                }
                if (comp instanceof Choice) {
                    toSave.add("Choice:" + comp.getLabel() + ":" + comp.getGroup().getLabel() + ":" + ((Choice) comp).getSelected());
                }
                //TODO: color
                if (comp instanceof Combo) {
                    toSave.add("Combo:" + comp.getLabel() + ":" + comp.getGroup().getLabel() + ":" + ((Combo) comp).getActiveOptions());
                }
                if (comp instanceof Field) {
                    toSave.add("Field:" + comp.getLabel() + ":" + comp.getGroup().getLabel() + ":" + ((Field) comp).getText());
                }
                //do nothing for Group
                if (comp instanceof Keybind) {
                    toSave.add("Keybind:" + ((Keybind) comp).getChild().getLabel() + ":" + ((Keybind) comp).getChild().getGroup().getLabel() + ":" + ((Keybind) comp).getKey());
                }
                if (comp instanceof Slider) {
                    toSave.add("Slider:" + comp.getLabel() + ":" + comp.getGroup().getLabel() + ":" + ((Slider) comp).getValue());
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
                for (Component comp : Icarus.getWindow().all) {
                    if (comp.getClass().getSimpleName().equals(args[0])) {
                        if (comp.getLabel().equals(args[1])) {
                            if (comp.getGroup().getLabel().equals(args[2])) {
                                //do nothing for button
                                if (comp instanceof Checkbox) {
                                    ((Checkbox) comp).setToggled(Boolean.parseBoolean(args[3]));
                                }
                                if (comp instanceof Choice) {
                                    ((Choice) comp).setSelected(args[3]);
                                }
                                //TODO: color
                                if (comp instanceof Combo) {
                                    ((Combo) comp).setActiveOptions(args[3]);
                                }
                                if (comp instanceof Field) {
                                    ((Field) comp).setText(args[3].equals("null") ? "" : args[3]);
                                }
                                //do nothing for Group
                                if (comp instanceof Keybind) {
                                    ((Keybind) comp).setKey(Integer.parseInt(args[3]));
                                }
                                if (comp instanceof Slider) {
                                    ((Slider) comp).setValue(Double.parseDouble(args[3]));
                                }
                                MinecraftForge.EVENT_BUS.post(new ComponentStateChangeEvent(comp));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getCheckbox(String group, String label) {
        for (Component comp : Icarus.getWindow().all) {
            if (comp.getLabel().equals(label) && comp.getGroup().getLabel().equals(group) && comp instanceof Checkbox) {
                return ((Checkbox) comp).isToggled();
            }
        }
        return false;
    }

    public String getChoice(String group, String label) {
        for (Component comp : Icarus.getWindow().all) {
            if (comp.getLabel().equals(label) && comp.getGroup().getLabel().equals(group) && comp instanceof Choice) {
                return ((Choice) comp).getSelected();
            }
        }
        return null;
    }

    public String getCombo(String group, String label) {
        for (Component comp : Icarus.getWindow().all) {
            if (comp.getLabel().equals(label) && comp.getGroup().getLabel().equals(group) && comp instanceof Combo) {
                return ((Combo) comp).getActiveOptions();
            }
        }
        return null;
    }

    public String getField(String group, String label) {
        for (Component comp : Icarus.getWindow().all) {
            if (comp.getLabel().equals(label) && comp.getGroup().getLabel().equals(group) && comp instanceof Field) {
                return ((Field) comp).getText();
            }
        }
        return null;
    }

    public double getSlider(String group, String label) {
        for (Component comp : Icarus.getWindow().all) {
            if (comp.getLabel().equals(label) && comp.getGroup().getLabel().equals(group) && comp instanceof Slider) {
                return ((Slider) comp).getValue();
            }
        }
        return 0;
    }

    public void setCheckbox(String group, String label, boolean toggled) {
        for (Component comp : Icarus.getWindow().all) {
            if (comp.getLabel().equals(label) && comp.getGroup().getLabel().equals(group) && comp instanceof Checkbox) {
                ((Checkbox) comp).setToggled(toggled);
            }
        }
    }

    public void setChoice(String group, String label, String selected) {
        for (Component comp : Icarus.getWindow().all) {
            if (comp.getLabel().equals(label) && comp.getGroup().getLabel().equals(group) && comp instanceof Choice) {
                ((Choice) comp).setSelected(selected);
            }
        }
    }

    public void setCombo(String group, String label, String activeOptions) {
        for (Component comp : Icarus.getWindow().all) {
            if (comp.getLabel().equals(label) && comp.getGroup().getLabel().equals(group) && comp instanceof Combo) {
                ((Combo) comp).setActiveOptions(activeOptions);
            }
        }
    }

    public void setField(String group, String label, String text) {
        for (Component comp : Icarus.getWindow().all) {
            if (comp.getLabel().equals(label) && comp.getGroup().getLabel().equals(group) && comp instanceof Field) {
                ((Field) comp).setText(text);
            }
        }
    }

    public void setSlider(String group, String label, double value) {
        for (Component comp : Icarus.getWindow().all) {
            if (comp.getLabel().equals(label) && comp.getGroup().getLabel().equals(group) && comp instanceof Slider) {
                ((Slider) comp).setValue(value);
            }
        }
    }
}
