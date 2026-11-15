package studio.dreamys.icarus.settings;

import studio.dreamys.icarus.annotation.field.DropdownOptions;
import studio.dreamys.icarus.annotation.field.FieldOptions;
import studio.dreamys.icarus.annotation.field.IAttach;
import studio.dreamys.icarus.annotation.field.IKeybind;
import studio.dreamys.icarus.annotation.field.IDisabled;
import studio.dreamys.icarus.annotation.field.IShowIf;
import studio.dreamys.icarus.annotation.field.IWarning;
import studio.dreamys.icarus.annotation.field.SliderOptions;
import studio.dreamys.icarus.annotation.type.IGroup;
import studio.dreamys.icarus.annotation.type.IPage;
import studio.dreamys.icarus.annotation.type.ISubPage;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

/** Builds a {@link SettingsModel} from {@code @IPage} classes, validating as it goes. */
public final class ModelBuilder {
    private static final Logger LOG = Logger.getLogger("Icarus");

    private final List<SettingType<?>> types;

    public ModelBuilder(List<SettingType<?>> types) {
        this.types = types;
    }

    public SettingsModel build(List<Class<?>> pageClasses) {
        List<PageNode> pages = new ArrayList<>();
        for (Class<?> pageClass : pageClasses) {
            IPage annotation = pageClass.getAnnotation(IPage.class);
            if (annotation == null) throw new IllegalArgumentException(pageClass.getName() + " is not annotated with @IPage");
            PageNode page = new PageNode(pageClass, pageClass.getSimpleName(), annotation.value(), annotation.icon(), annotation.order());

            List<Class<?>> subPageClasses = new ArrayList<>();
            List<Class<?>> groupClasses = new ArrayList<>();
            for (Class<?> nested : pageClass.getDeclaredClasses()) {
                if (nested.isAnnotationPresent(ISubPage.class)) subPageClasses.add(nested);
                else if (nested.isAnnotationPresent(IGroup.class)) groupClasses.add(nested);
                else LOG.warning("[Icarus] " + nested.getName() + " is nested in an @IPage but has no @IGroup/@ISubPage; skipped");
            }
            if (!subPageClasses.isEmpty() && !groupClasses.isEmpty()) {
                throw new IllegalStateException("@IPage " + pageClass.getName() + " mixes @ISubPage and @IGroup classes; put its groups inside sub-pages");
            }

            if (subPageClasses.isEmpty()) {
                SubPageNode implicit = new SubPageNode(page, null, null, 0);
                addGroups(implicit, groupClasses);
                page.subPages.add(implicit);
            } else {
                sortByOrder(subPageClasses, c -> c.getAnnotation(ISubPage.class).order());
                for (Class<?> subClass : subPageClasses) {
                    SubPageNode sub = new SubPageNode(page, subClass, subClass.getSimpleName(), subClass.getAnnotation(ISubPage.class).order());
                    List<Class<?>> subGroups = new ArrayList<>();
                    for (Class<?> nested : subClass.getDeclaredClasses()) {
                        if (nested.isAnnotationPresent(IGroup.class)) subGroups.add(nested);
                        else LOG.warning("[Icarus] " + nested.getName() + " is nested in an @ISubPage but has no @IGroup; skipped");
                    }
                    addGroups(sub, subGroups);
                    page.subPages.add(sub);
                }
            }
            pages.add(page);
        }
        pages.sort(Comparator.comparingInt(PageNode::getOrder).thenComparing(PageNode::getName));

        SettingsModel model = new SettingsModel(pages);
        for (Setting<?> setting : model.getSettings()) setting.captureDefault();
        return model;
    }

    private void addGroups(SubPageNode sub, List<Class<?>> groupClasses) {
        sortByOrder(groupClasses, c -> c.getAnnotation(IGroup.class).order());
        for (Class<?> groupClass : groupClasses) {
            IGroup annotation = groupClass.getAnnotation(IGroup.class);
            GroupNode group = new GroupNode(sub, groupClass, groupClass.getSimpleName(), annotation.column(), annotation.order(), annotation.pinBottom());
            group.height = annotation.height();
            for (Field field : groupClass.getDeclaredFields()) {
                if (field.isSynthetic()) continue;
                Setting<?> setting = createSetting(group, field);
                if (setting != null) group.settings.add(setting);
            }
            resolveShowIf(group);
            sub.groups.add(group);
        }
    }

    /** Links {@code @IShowIf} settings to the checkbox they follow. */
    private static void resolveShowIf(GroupNode group) {
        for (Setting<?> setting : group.settings) {
            Field field = setting.getField();
            IShowIf showIf = field == null ? null : field.getAnnotation(IShowIf.class);
            if (showIf == null) continue;
            Setting<?> condition = null;
            for (Setting<?> other : group.settings) if (other.getName().equals(showIf.value())) condition = other;
            if (condition == null || condition.getType() != SettingTypes.CHECKBOX) {
                LOG.warning("[Icarus] @IShowIf(\"" + showIf.value() + "\") on " + field + " ignored: no checkbox of that name in the group");
                continue;
            }
            setting.showIf = condition;
            setting.showIfNot = showIf.not();
        }
    }

    private Setting<?> createSetting(GroupNode group, Field field) {
        SettingType<?> type = null;
        for (SettingType<?> candidate : types) {
            if (candidate.matches(field)) {
                type = candidate;
                break;
            }
        }
        if (type == null) {
            LOG.warning("[Icarus] " + field + " has no supported setting type (" + field.getType().getSimpleName() + "); skipped");
            return null;
        }
        if (!Modifier.isStatic(field.getModifiers())) {
            throw new IllegalStateException("Setting " + field + " must be static");
        }
        field.setAccessible(true);
        return create(group, field, type);
    }

    private <T> Setting<T> create(GroupNode group, Field field, SettingType<T> type) {
        DropdownOptions dropdown = field.getAnnotation(DropdownOptions.class);
        SliderOptions sliderOptions = field.getAnnotation(SliderOptions.class);
        FieldOptions fieldOptions = field.getAnnotation(FieldOptions.class);
        IKeybind keybind = field.getAnnotation(IKeybind.class);

        Setting.SliderSpec slider = null;
        if (type == SettingTypes.SLIDER) {
            try {
                slider = new Setting.SliderSpec(sliderOptions.min(), sliderOptions.max(), sliderOptions.onlyInt(), sliderOptions.units()).step(sliderOptions.step()).endTexts(sliderOptions.minText(), sliderOptions.maxText());
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Invalid @SliderOptions on " + field + ": " + e.getMessage(), e);
            }
        }

        //any setting can show a key; buttons run, checkboxes follow the mode, others just store it for the mod to read
        boolean bindable = keybind != null;

        if (type == SettingTypes.CHOICE || type == SettingTypes.COMBO) {
            if (dropdown.value().length == 0) throw new IllegalStateException("@DropdownOptions on " + field + " has no options");
        }

        Setting.Holder<T> holder = new FieldHolder<>(field, type);
        Setting<T> setting = new Setting<>(group, field.getName(), type, holder, field, true,
                dropdown == null ? null : Arrays.asList(dropdown.value()), slider,
                fieldOptions == null ? 64 : fieldOptions.maxLength(), bindable ? keybind.value() : -1);
        if (bindable) setting.defaultMode = keybind.mode();
        setting.warning = field.isAnnotationPresent(IWarning.class);
        setting.disabled = field.isAnnotationPresent(IDisabled.class);
        if (field.isAnnotationPresent(IAttach.class)) {
            if (type == SettingTypes.COLOR) setting.attached = true;
            else LOG.warning("[Icarus] @IAttach on " + field + " ignored: only colours can share a row");
        }
        if (type == SettingTypes.LIST) {
            try {
                Object model = field.get(null);
                if (model == null) throw new IllegalStateException("ListModel field " + field + " must be initialised");
                setting.setExtra(model);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
        return setting;
    }

    private interface OrderOf {
        int order(Class<?> c);
    }

    private static void sortByOrder(List<Class<?>> classes, OrderOf order) {
        classes.sort(Comparator.<Class<?>>comparingInt(order::order).thenComparing(Class::getName));
    }

    private static final class FieldHolder<T> implements Setting.Holder<T> {
        private final Field field;
        private final SettingType<T> type;

        FieldHolder(Field field, SettingType<T> type) {
            this.field = field;
            this.type = type;
        }

        @Override
        public T get() {
            try {
                return type.load(field);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public void set(T value) {
            try {
                type.store(field, value);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
