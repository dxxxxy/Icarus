package studio.dreamys.icarus.component;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.component.sub.attachment.Attachment;
import studio.dreamys.icarus.util.RenderUtils;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Window extends GuiScreen {
    public static Window instance;

    public double x;
    public double y;
    public double width;
    public double height;
    public Color color;

    public int activePageIndex;
    public ArrayList<Page> pages = new ArrayList<>();
    public ArrayList<Component> visible = new ArrayList<>();
    public List<Attachment> attachments = new ArrayList<>();

    //dragging stuff
    public double dragX;
    public double dragY;
    public boolean isDragging;
    
    public int key = Keyboard.KEY_RSHIFT; //default key

    public Window(double x, double y, double width, double height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;

        instance = this;
    }

    @Override
    public void initGui() {
        //set the active page
        setActivePage(pages.get(activePageIndex));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        //draw our very professional skeet background
        mc.getTextureManager().bindTexture(new ResourceLocation("icarus", "bg.png"));
        RenderUtils.drawTexture(x, y, width, height);

        //draw them sexy bottom strings
        RenderUtils.drawString("uid: 001", x + 3, y + height - RenderUtils.getFontHeight() - 3, Color.WHITE);
        RenderUtils.drawXCenterString(Icarus.getModid(), x + width / 2, y + height - RenderUtils.getFontHeight() - 3, Color.WHITE);
        RenderUtils.drawString("dxxxxy#0776", x + width - 3 - RenderUtils.getStringWidth("dxxxxy#0776"), y + height - RenderUtils.getFontHeight() - 3, Color.WHITE);

        update(mouseX, mouseY);

        //draw and update page
        for (Page page : pages) {
            page.render(mouseX, mouseY);
        }

        //draw and update children
        for (Component component : visible) {
            component.render(mouseX, mouseY);

            //render bounds
//            RenderUtils.drawOutline(component.x, component.y - component.getBounds().getOffsetY(), component.getBounds().getWidth(), component.getBounds().getHeight() + component.getBounds().getOffsetY(), Color.RED);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (hovered(mouseX, mouseY) && mouseButton == 0) {
            isDragging = true;
            dragX = mouseX - x;
            dragY = mouseY - y;
        }

        //call for pages first
        for (Page page : pages) {
            page.mouseClicked(mouseX, mouseY, mouseButton);
        }

        //call for children then
        for (Component component : Lists.reverse(visible)) {
            component.mouseClicked(mouseX, mouseY, mouseButton);
            if (component.open) break; //avoid clicking underlying elements when something is open
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        //call for children
        for (Component component : visible) {
            component.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        isDragging = false;

        //call for children
        for (Component component : visible) {
            component.mouseReleased(mouseX, mouseY, state);
        }
    }

    public boolean hovered(double x, double y) {
        return x > this.x && x < this.x + width && y > this.y && y < this.y + height / 25; //only 1/25 from the top is draggable
    }

    public void update(int mouseX, int mouseY) {
        if (isDragging) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }
    }

    public void addPage(Page page) {
        pages.add(page);
        page.setWindow(this);
    }

    public void setActivePage(Page page) {
        //set the index of the active page
        activePageIndex = pages.indexOf(page);

        //clear components to draw
        visible.clear();

        //add components + group
        for (Group group : pages.get(activePageIndex).getGroups()) {
            visible.add(group);
            visible.addAll(group.getChildren());
        }

        //reverse list to render from bottom to top
        Collections.reverse(visible);
    }
}
