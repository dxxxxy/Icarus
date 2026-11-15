package studio.dreamys.icarus.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Shows the setting only while a checkbox in the same group is ticked (or unticked with {@code not = true}), e.g.
 * {@code @IShowIf("Weapon") public static boolean Weapon_icons;}. Hidden rows take no space.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IShowIf {
    /** Field name of a boolean setting in the same group. */
    String value();

    /** Show while that checkbox is off instead. */
    boolean not() default false;
}
