package studio.dreamys.icarus.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The text shown for a page, sub-page, group or setting, when its Java name can't spell it (e.g.
 * "Auto-accept matchmaking"). Without it, the name is shown with underscores as spaces. Config keys always use
 * the Java name, so changing a label never loses saved values.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface ILabel {
    String value();
}
