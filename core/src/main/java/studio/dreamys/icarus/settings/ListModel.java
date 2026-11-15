package studio.dreamys.icarus.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Backing value of a list box: a list of items plus the selected one. Declare it as a static field in a group;
 * the selection is saved in configs, the items are supplied by the mod (and may change at runtime).
 */
public final class ListModel {
    private final List<String> items = new ArrayList<>();
    private final List<String> view = Collections.unmodifiableList(items);
    private String selected;
    private int version;

    public ListModel(String... items) {
        this.items.addAll(Arrays.asList(items));
    }

    private boolean searchable;
    private double height;

    /** Height of the list box in design units (0 lets the theme choose). */
    public ListModel height(double height) {
        this.height = height;
        return this;
    }

    public double getHeight() {
        return height;
    }

    /** Shows a search box above the list that filters it as the user types. */
    public ListModel searchable(boolean searchable) {
        this.searchable = searchable;
        return this;
    }

    public boolean isSearchable() {
        return searchable;
    }

    public List<String> getItems() {
        return view;
    }

    public void setItems(Collection<String> newItems) {
        items.clear();
        items.addAll(newItems);
        if (selected != null && !items.contains(selected)) selected = null;
        version++;
    }

    public void add(String item) {
        items.add(item);
        version++;
    }

    public void remove(String item) {
        items.remove(item);
        if (item.equals(selected)) selected = null;
        version++;
    }

    /** The selected item, or null. */
    public String getSelected() {
        return selected;
    }

    /** Selects an item (null clears). A value that isn't in the list is kept, so it can be restored once it appears. */
    public void select(String item) {
        selected = item;
        version++;
    }

    /** Incremented on every change, so views can cache derived data. */
    public int getVersion() {
        return version;
    }
}
