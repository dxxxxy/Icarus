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
    /** Tab icon: a character of the icon font (used by themes with icon tabs). */
    char value() default ' ';

    /**
     * Named tab icon provided by the theme (e.g. the skeet theme's "rage", "antiaim", "legit", "visuals", "misc",
     * "skins", "players", "config"); takes precedence over {@link #value()} when the theme has it.
     */
    String icon() default "";

    /** Tab position; lower comes first. Ties are ordered by class name. */
    int order() default Integer.MAX_VALUE;
}
