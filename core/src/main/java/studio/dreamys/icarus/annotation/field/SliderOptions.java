package studio.dreamys.icarus.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SliderOptions {
    double min();
    double max();
    boolean onlyInt() default false;
    String units() default "";

    /** Step of themes with -/+ buttons (and of keyboard/scroll nudging); 0 means 1 for integer sliders, 1% of the range otherwise. */
    double step() default 0;

    /** Shown instead of the value at the minimum (e.g. "Off"); empty shows the value. */
    String minText() default "";

    /** Shown instead of the value at the maximum (e.g. "∞"); empty shows the value. */
    String maxText() default "";
}
