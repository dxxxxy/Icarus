package studio.dreamys.icarus.settings;

import studio.dreamys.icarus.annotation.ILabel;

import java.lang.reflect.AnnotatedElement;

/** Display labels: {@code @ILabel} if present, otherwise the Java name with underscores as spaces. */
final class Labels {
    private Labels() {
    }

    static String of(AnnotatedElement element, String name) {
        ILabel label = element == null ? null : element.getAnnotation(ILabel.class);
        if (label != null) return label.value();
        return name == null ? null : name.replace('_', ' ');
    }
}
