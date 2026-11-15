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

    /** Icon character for themes that show sub-tabs as icons (the classic theme's weapon row uses the weapon icon font). */
    char icon() default ' ';
}
