package studio.dreamys.icarus.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Shows the setting on the previous setting's row instead of a row of its own, e.g. a colour swatch next to the
 * checkbox it belongs to. Themes decide where in the row it goes (usually right-aligned). Only colours are
 * attachable; other types ignore it.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IAttach {
}
