package studio.dreamys.icarus.annotation.type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class nested in an {@link IPage} or {@link ISubPage} as a group box. Its static fields become settings.
 * Groups are placed by the theme; {@link #column()} and {@link #order()} are hints it follows.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface IGroup {
    int AUTO = -1;

    /** Preferred column (0-based), or {@link #AUTO} to let the theme balance columns. */
    int column() default AUTO;

    /** Position within its column; lower comes first. Ties are ordered by class name. */
    int order() default Integer.MAX_VALUE;

    /** Keep the group at the bottom of its column. */
    boolean pinBottom() default false;
}
