package studio.dreamys.test;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.event.ComponentEvent;
import studio.dreamys.icarus.extra.notification.Notification;
import studio.dreamys.icarus.extra.notification.NotificationManager;
import studio.dreamys.icarus.extra.Watermark;
import studio.dreamys.icarus.util.Placement;

import java.awt.*;

@Mod(modid = "TestMod")
public class TestMod {
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(this);
        Icarus.init(e.getModMetadata().modId, new TestWindow());
        System.out.println(Icarus.getConfig().getCheckbox("Visuals", "haha"));
        System.out.println(Icarus.getConfig().getCheckbox("Visuals", "Another Checkbox"));
        System.out.println(Icarus.getConfig().getCheckbox("Visuals", "Checkbox"));
        new Watermark("dxxxxyware | uid 001 (dxxxxy) | mc.hypixel.net | 23ms", Placement.TOP_RIGHT, Color.WHITE).enable();

        //try all except top_right
        new Watermark("testing123testing", Placement.TOP_LEFT, Color.WHITE).enable();
        new Watermark("testing123testing", Placement.TOP_CENTER, Color.WHITE).enable();
        new Watermark("testing123testing", Placement.CENTER_LEFT, Color.WHITE).enable();
        new Watermark("testing123testing", Placement.CENTER, Color.WHITE).enable();
        new Watermark("testing123testing", Placement.CENTER_RIGHT, Color.WHITE).enable();
        new Watermark("testing123testing", Placement.BOTTOM_LEFT, Color.WHITE).enable();
        new Watermark("testing123testing", Placement.BOTTOM_CENTER, Color.WHITE).enable();
        new Watermark("testing123testing", Placement.BOTTOM_RIGHT, Color.WHITE).enable();
    }

    @SubscribeEvent
    public void onComponentStateChange(ComponentEvent e) {
        System.out.println(e.component);
    }

    @SubscribeEvent
    public void onComponentStateChange(ComponentEvent.CheckboxEvent e) {
        System.out.println(e.component);
        System.out.println(e.toggled);
    }

    @SubscribeEvent
    public void onComponentStateChange(ComponentEvent.ChoiceEvent e) {
        System.out.println(e.component);
        System.out.println(e.selected);
    }

    @SubscribeEvent
    public void onComponentStateChange(ComponentEvent.ComboEvent e) {
        System.out.println(e.component);
        System.out.println(e.activeOptions);
    }

    @SubscribeEvent
    public void onComponentStateChange(ComponentEvent.FieldEvent e) {
        System.out.println(e.component);
        System.out.println(e.text);
    }

    @SubscribeEvent
    public void onComponentStateChange(ComponentEvent.KeybindEvent e) {
        System.out.println(e.component);
        System.out.println(e.key);
    }

    @SubscribeEvent
    public void onComponentStateChange(ComponentEvent.SliderEvent e) {
        System.out.println(e.component);
        System.out.println(e.value);
    }

    @SubscribeEvent
    public void key(InputEvent.KeyInputEvent e) {
        if (Minecraft.getMinecraft().theWorld == null || Minecraft.getMinecraft().thePlayer == null) return;
        if (Keyboard.getEventKeyState()) { //if the key is down
            int keyCode = Keyboard.getEventKey(); //get keycode
            if (keyCode <= 0) return; //ignore invalid keycode
            if (keyCode == Keyboard.KEY_K) {
                NotificationManager.send(new Notification("Test", "Test"));
            }
            if (keyCode == Keyboard.KEY_J) {
                NotificationManager.send(new Notification("Test", "This is a very cool informational notification which is much larger in size.", 40));
            }
            if (keyCode == Keyboard.KEY_L) {
                NotificationManager.send(new Notification("Hello", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec congue neque libero, a posuere erat pharetra sed. Phasellus convallis nisi vel tortor malesuada commodo eu in nunc. Mauris vulputate pretium tellus eu mattis. Proin risus libero, ornare ut dolor id, dignissim imperdiet mauris. Nam a interdum metus. Ut faucibus nisl sem, vel dictum nisi luctus dapibus. Donec lobortis vitae ante non convallis. Nam et tincidunt lectus. Aliquam erat volutpat. In hac habitasse platea dictumst. Praesent tempor pulvinar ante. Donec vitae risus consequat, tristique sapien ac, consequat eros. Sed hendrerit porttitor ligula, eu euismod mi. Etiam nec quam risus. Curabitur sit amet dictum diam.", 40, Color.BLACK, Color.CYAN, Color.WHITE, Color.YELLOW));
            }
        }
    }
}
