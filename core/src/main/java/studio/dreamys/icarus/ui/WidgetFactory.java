package studio.dreamys.icarus.ui;

import studio.dreamys.icarus.settings.Label;
import studio.dreamys.icarus.settings.ListModel;
import studio.dreamys.icarus.settings.Setting;
import studio.dreamys.icarus.settings.SettingType;
import studio.dreamys.icarus.settings.SettingTypes;
import studio.dreamys.icarus.ui.widget.ButtonWidget;
import studio.dreamys.icarus.ui.widget.CheckboxWidget;
import studio.dreamys.icarus.ui.widget.ChoiceWidget;
import studio.dreamys.icarus.ui.widget.ColorWidget;
import studio.dreamys.icarus.ui.widget.ComboWidget;
import studio.dreamys.icarus.ui.widget.FieldWidget;
import studio.dreamys.icarus.ui.widget.LabelWidget;
import studio.dreamys.icarus.ui.widget.ListWidget;
import studio.dreamys.icarus.ui.widget.SliderWidget;
import studio.dreamys.icarus.ui.widget.Widget;

import java.awt.Color;
import java.util.IdentityHashMap;
import java.util.Map;

/** Creates the widget for a setting. Replace one per setting type with {@code IcarusMod#replaceWidget}. */
public interface WidgetFactory<T> {
    Widget create(Menu menu, Setting<T> setting);

    /** The built-in factory for each built-in type. */
    @SuppressWarnings("unchecked")
    static Map<SettingType<?>, WidgetFactory<?>> defaults() {
        Map<SettingType<?>, WidgetFactory<?>> map = new IdentityHashMap<>();
        map.put(SettingTypes.BUTTON, (WidgetFactory<Runnable>) ButtonWidget::new);
        map.put(SettingTypes.CHECKBOX, (WidgetFactory<Boolean>) CheckboxWidget::new);
        map.put(SettingTypes.CHOICE, (WidgetFactory<String>) ChoiceWidget::new);
        map.put(SettingTypes.COMBO, (WidgetFactory<String[]>) ComboWidget::new);
        map.put(SettingTypes.FIELD, (WidgetFactory<String>) FieldWidget::new);
        map.put(SettingTypes.SLIDER, (WidgetFactory<Double>) SliderWidget::new);
        map.put(SettingTypes.COLOR, (WidgetFactory<Color>) ColorWidget::new);
        map.put(SettingTypes.LABEL, (WidgetFactory<Label>) LabelWidget::new);
        map.put(SettingTypes.LIST, (WidgetFactory<String>) (menu, setting) -> new ListWidget(menu, setting, (ListModel) setting.getExtra()));
        return map;
    }
}
