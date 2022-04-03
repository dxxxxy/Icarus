package studio.dreamys;

import gg.essential.elementa.ElementaVersion;
import gg.essential.elementa.UIComponent;
import gg.essential.elementa.WindowScreen;
import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.components.UIContainer;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.constraints.*;
import gg.essential.elementa.constraints.animation.AnimatingConstraints;
import gg.essential.elementa.constraints.animation.Animations;
import gg.essential.elementa.effects.OutlineEffect;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class ClickGUI extends WindowScreen {
    public static String activeCategory;
    public Color color = Color.getHSBColor((System.currentTimeMillis() % 1000) / 1000F, 0.8F, 1F);

    UIComponent box = new UIBlock()
            .setX(new CenterConstraint())
            .setY(new CenterConstraint())
            .setWidth(new RelativeWindowConstraint(0.03f))
            .setHeight(new RelativeWindowConstraint(0.03f))
            .setChildOf(getWindow())
            .setColor(new Color(20, 20, 20))
            .enableEffect(new OutlineEffect(color, 5f));

    UIComponent top = new UIContainer()
            .setX(new CenterConstraint())
            .setY(new RelativeConstraint(0.03f))
            .setColor(Color.WHITE)
            .setHeight(new RelativeConstraint(0.07f))
            .enableEffect(new OutlineEffect(color, 2f, true, true, new HashSet<>(Collections.singletonList(OutlineEffect.Side.Bottom))))
            .setChildOf(box);

    UIComponent client = new UIText()
            .setText("etsy")
            .setX(new RelativeConstraint(0.003f))
            .setY(new RelativeConstraint(1f - 0.0275f))
            .setColor(color)
            .setChildOf(box);

    UIComponent author = new UIText()
            .setText("dxxxxy#0776")
            .setY(new RelativeConstraint(1f - 0.0275f))
            .setColor(color)
            .setChildOf(box);


    public ClickGUI() {
        super(ElementaVersion.V1);
        author.setX(new SubtractiveConstraint(new RelativeConstraint(1f), new PixelConstraint(((UIText) author).getTextWidth()))).setTextScale(new RelativeWindowConstraint(0.0015f));
        client.setTextScale(new RelativeWindowConstraint(0.0015f));

        activeCategory = Arrays.stream(Category.values()).findFirst().get().get();

        for (Category category : Category.values()) {
            UIText child = new UIText().setText(category.get());
            top.addChild(child);

            child.setTextScale(new RelativeWindowConstraint(0.003f));
            child.setColor(activeCategory.equals(child.getText()) ? color : Color.GRAY.darker());
            child.setX(new SiblingConstraint(10f));
            child.setY(new RelativeConstraint(0.03f));

            child.onMouseClickConsumer(event -> {
                activeCategory = child.getText();

                AnimatingConstraints childAnim = child.makeAnimation();
                childAnim.setColorAnimation(Animations.OUT_EXP, 1.0f, new ConstantColorConstraint(color));
                child.animateTo(childAnim);

                for (UIComponent uc : top.getChildren()) {
                    if (uc instanceof UIText) {
                        if (uc != child) {
                            AnimatingConstraints ucAnim = uc.makeAnimation();
                            ucAnim.setColorAnimation(Animations.OUT_EXP, 1.0f, new ConstantColorConstraint(Color.GRAY.darker()));
                            uc.animateTo(ucAnim);
                        }
                    }
                }
            });
        }

//        top.setWidth(new ChildBasedSizeConstraint());

        AnimatingConstraints topAnim = top.makeAnimation();
        topAnim.setWidthAnimation(Animations.OUT_EXP, 1.0f, new ChildBasedSizeConstraint(), 0.05f);
        topAnim.setHeightAnimation(Animations.OUT_EXP, 1.0f, new RelativeConstraint(0.07f), 1f);
        top.animateTo(topAnim);

        AnimatingConstraints boxAnim = box.makeAnimation();
        boxAnim.setWidthAnimation(Animations.OUT_EXP, 1.0f, new RelativeWindowConstraint(0.55f));
        boxAnim.setHeightAnimation(Animations.OUT_EXP, 1.0f, new RelativeWindowConstraint(0.65f));
        box.animateTo(boxAnim);
    }
}
