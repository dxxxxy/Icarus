package studio.dreamys.icarus.util;

import lombok.Getter;

public class Bounds {
    @Getter private double width, height, offsetY;

    public Bounds(double width, double height) {
        this(width, height, 0);
    }

    public Bounds(double width, double height, double offsetY) {
        this.width = width;
        this.height = height;
        this.offsetY = offsetY;
    }
}
