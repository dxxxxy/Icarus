package studio.dreamys.icarus.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Options for a text field ({@code String} setting without {@link DropdownOptions}). */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FieldOptions {
    int maxLength() default 64;
}
