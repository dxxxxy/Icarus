package studio.dreamys.icarus.annotation.type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class nested in an {@link IPage} as a sub-tab. Its nested {@link IGroup} classes are its content.
 * A page either has sub-tabs or groups directly, not both.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ISubPage {
    /** Sub-tab position; lower comes first. Ties are ordered by class name. */
    int order() default Integer.MAX_VALUE;

    /**
     * Sub-tab icon, for themes that show sub-tabs as icons (the weapon rows of both built-in themes). Either a PNG in
     * your mod's resources, as an absolute path such as {@code "/assets/mymod/icons/deagle.png"}, or the name of a
     * built-in icon of the theme:
     * <ul>
     *     <li>skeet: "pistol", "smg", "rifle", "shotgun", "machine_gun", "sniper";</li>
     *     <li>fatality: "auto", "scout", "awp", "heavy_pistol", "pistol", "rifle".</li>
     * </ul>
     * The image is a white mask on transparency, tinted by the theme. Skeet centres it in a 90x44 cell; fatality uses
     * it as the whole slot (its width is the slot's, so include the padding; the built-in ones are 27 pixels high).
     * Without an icon, skeet shows the label and fatality an empty slot.
     */
    String icon() default "";
}
