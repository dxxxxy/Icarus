package studio.dreamys.icarus.component.wrapper;

import lombok.Getter;

import java.util.List;

@Getter
public class WChoice {
    private String selected;
    private List<String> options;

    public WChoice(String selected, List<String> options) {
        this.selected = selected;
        this.options = options;
    }
}
