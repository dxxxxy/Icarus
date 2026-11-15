package studio.dreamys.icarus.annotation.field;

import studio.dreamys.icarus.settings.KeybindMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Makes a button, checkbox or label bindable to a key ({@link studio.dreamys.icarus.input.Keys} code, or
 * {@code Keys.KEY_NONE}). Buttons run on press; checkboxes follow {@link #mode()} (users can change both).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IKeybind {
    int value();

    /** How a bound checkbox reacts to its key. */
    KeybindMode mode() default KeybindMode.TOGGLE;
}
