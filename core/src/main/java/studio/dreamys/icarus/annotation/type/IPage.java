package studio.dreamys.icarus.annotation.type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a menu page (a tab). Its nested {@link IGroup} classes (or {@link ISubPage} classes holding
 * groups) become the page's content.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface IPage {
    /**
     * Tab icon, for themes with icon tabs (skeet). Either a PNG in your mod's resources, as an absolute path such as
     * {@code "/assets/mymod/icons/radar.png"}, or the name of a built-in icon of the theme (skeet: "rage", "antiaim",
     * "legit", "visuals", "misc", "skins", "players", "config"). The image is a white mask on transparency, tinted by
     * the theme, and drawn at its pixel size at the theme's reference scale (skeet's tab icons are about 40x40).
     */
    String icon() default "";

    /** Tab position; lower comes first. Ties are ordered by class name. */
    int order() default Integer.MAX_VALUE;
}
