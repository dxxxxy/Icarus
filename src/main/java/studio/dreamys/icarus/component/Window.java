package studio.dreamys.icarus.component;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.component.sub.Group;
import studio.dreamys.icarus.util.RenderUtils;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class Window extends GuiScreen {
    public static Window instance;

    public double x;
    public double y;
    public double width;
    public double height;
    public Color color;

    public static Page active;
    public static int pageIndex;
    public ArrayList<Page> pages = new ArrayList<>();
    public ArrayList<Component> components = new ArrayList<>();

    //dragging stuff
    public double dragX;
    public double dragY;
    public boolean isDragging;

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
        setActivePage(pages.get(pageIndex));
        Icarus.config.load();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        //draw our very professional skeet background
        mc.getTextureManager().bindTexture(new ResourceLocation("icarus", "bg.png"));
        RenderUtils.drawTexture(x, y, width, height);

        //draw them sexy bottom strings
        RenderUtils.drawString("uid: 001", x + 3, y + height - RenderUtils.getFontHeight() - 3, Color.WHITE);
        RenderUtils.drawCenteredString("Icarus", x + width / 2, y + height - RenderUtils.getFontHeight() - 3, Color.WHITE);
        RenderUtils.drawString("dxxxxy#0776", x + width - 3 - RenderUtils.getStringWidth("dxxxxy#0776"), y + height - RenderUtils.getFontHeight() - 3, Color.WHITE);

        update(mouseX, mouseY);

        //draw and update children
        for (Component component : components) {
            component.render(mouseX, mouseY);
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
        for (Component component : components.stream().filter(component -> !(component instanceof Page)).collect(Collectors.toList())) {
            component.mouseClicked(mouseX, mouseY, mouseButton);
            if (component.open()) break; //avoid clicking underlying elements when something is open
        }

        Icarus.config.save();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        //call for children
        for (Component component : components) {
            component.keyTyped(typedChar, keyCode);
        }

        Icarus.config.save();
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        isDragging = false;

        //call for children
        for (Component component : components) {
            component.mouseReleased(mouseX, mouseY, state);
        }

        Icarus.config.save();
    }

    private boolean hovered(double x, double y) {
        return x > this.x && x < this.x + width && y > this.y && y < this.y + height / 25; //only 1/25 from the top is draggable
    }

    private void update(int mouseX, int mouseY) {
        if (isDragging) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }
    }

    public Page addPage(Page page) {
        //add page to list
        pages.add(page);
        //pass window to page
        page.setWindow(this);
        return page;
    }

    public void setActivePage(Page page) {
        //set page to active
        active = page;
        //keep page index for later
        pageIndex = pages.indexOf(active);
        //clear components to draw
        components.clear();
        //add pages
        components.addAll(pages);
        //refill components to draw with those inside the page
        for (Group group : active.getGroups()) {
            components.add(group);
            components.addAll(group.getChildren());
        }
        //reverse list
        Collections.reverse((components));
    }
}
