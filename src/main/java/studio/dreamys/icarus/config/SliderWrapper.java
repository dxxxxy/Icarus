package studio.dreamys.icarus.config;

import lombok.Getter;

@Getter
public class SliderWrapper {
    private double value;
    private double min;
    private double max;
    private boolean onlyInt;
    private String units;

    public SliderWrapper(double value, double min, double max) {
        this.value = value;
        this.min = min;
        this.max = max;
    }

    public SliderWrapper(double value, double min, double max, boolean onlyInt) {
        this.value = value;
        this.min = min;
        this.max = max;
        this.onlyInt = onlyInt;
    }

    public SliderWrapper(double value, double min, double max, boolean onlyInt, String units) {
        this.value = value;
        this.min = min;
        this.max = max;
        this.onlyInt = onlyInt;
        this.units = units;
    }
}
