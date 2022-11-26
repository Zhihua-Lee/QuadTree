package terminal;

import java.awt.*;

//四种颜色（后续会更多），包含index（便于比较/equal），以及具体颜色
public enum Colors {
    R("r", Color.RED, new int[]{0, 1}),
    G("g", Color.GREEN, new int[]{0, -1}),
    B("b", Color.BLUE, new int[]{-1, 0}),
    Y("y", Color.YELLOW, new int[]{1, 0});


    private final String name;
    private final Color color;
    private final int[] direction;

    //constructor
    Colors(String name, Color color, int[] direction) {
        this.name = name;
        this.color = color;
        this.direction = direction;//分别为move时的分量开关      direction[0]:x     direction[1]:y
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public int[] getDirection() {
        return direction;
    }


}
