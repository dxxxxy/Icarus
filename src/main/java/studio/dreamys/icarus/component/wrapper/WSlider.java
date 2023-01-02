package studio.dreamys.icarus.component.wrapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WSlider {
    private double value;
    private double min;
    private double max;
    private boolean onlyInt;
    private String units;

    public WSlider(double value, double min, double max) {
        this.value = value;
        this.min = min;
        this.max = max;
    }

    public WSlider(double value, double min, double max, boolean onlyInt) {
        this.value = value;
        this.min = min;
        this.max = max;
        this.onlyInt = onlyInt;
    }

    public WSlider(double value, double min, double max, String units) {
        this.value = value;
        this.min = min;
        this.max = max;
        this.units = units;
    }

    public WSlider(double value, double min, double max, boolean onlyInt, String units) {
        this.value = value;
        this.min = min;
        this.max = max;
        this.onlyInt = onlyInt;
        this.units = units;
    }
}
