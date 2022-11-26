package terminal;

import java.awt.*;
import java.util.List;

// Cell逻辑结构
public class Cell {

    private static final double INFINITY = Double.POSITIVE_INFINITY;
    private final int ID;
    private Position position;
    private final double radius;
    private final double perception_range;
    private Colors color;

    // constructor
    public Cell(int ID, Position position, double radius, double perception_range, Colors color) {
        this.ID = ID;
        this.position = position;
        this.radius = radius;
        this.perception_range = perception_range;
        this.color = color;
    }

    // 给定冲突集合、边界、移动一次
    public void move(List<Cell> possible_contraction_Cells, double boundary_width, double boundary_height) {
        this.setPosition(position_after_move(possible_contraction_Cells, boundary_width, boundary_height));
    }

    // 给定 可能冲突的细胞集合、边界
    // 返回this细胞移动一步后的合法位置
    public Position position_after_move(List<Cell> possible_contradiction_Cells, double boundary_width, double boundary_height) {
        double moving_distance = 1; //or 1/15
        int n = possible_contradiction_Cells.size();
        double dvx = this.getColors().getDirection()[0];  // 单位步长除以单位时间=1*direction[0]？15*
        double dvy = this.getColors().getDirection()[1];  // 1*direction[1]？15*
        // 取最小的非负移动距离
        for (int i = 0; i < n; i++) {
            double next_moving_distance = this.distanceToHit(possible_contradiction_Cells.get(i));
            if (next_moving_distance < moving_distance && next_moving_distance >= 0) {
                moving_distance = next_moving_distance;
            }
        }
        double moved_position_x = this.position.getX() + dvx * moving_distance;
        double moved_position_y = this.position.getY() + dvy * moving_distance;
        Position moved_position = new Position(moved_position_x, moved_position_y);
        // 判断边界冲突，修正位置
        moved_position = this.position_after_contradict_boundary(moved_position, boundary_width, boundary_height);
        return moved_position;
    }

    // 与边界冲突之后的修正位置
    public Position position_after_contradict_boundary(Position quasi_position, double boundary_width, double boundary_height) {
        if (quasi_position.getX() < this.radius) {
            quasi_position.setX(this.radius);
        }
        if (quasi_position.getX() > boundary_width - this.radius) {
            quasi_position.setX(boundary_width - this.radius);
        }
        if (quasi_position.getY() < this.radius) {
            quasi_position.setY(this.radius);
        }
        if (quasi_position.getY() > boundary_height - this.radius) {
            quasi_position.setY(boundary_height - this.radius);
        }
        return quasi_position;
    }

    // 给定两个细胞（this移动，that静止），返回直到发生碰撞时，this走过的距离
    // 若不会碰撞，返回无穷大
    public double distanceToHit(Cell that) {
        if (this.equals(that)) return INFINITY;
        double dx = that.position.getX() - this.position.getX();
        double dy = that.position.getY() - this.position.getY();
        double r_sum = that.getRadius() + this.getRadius();
        // 移动方向 与 位移方向相反， 则不可能相撞
        if (dx * this.getColors().getDirection()[0] > 0.0 || dy * this.getColors().getDirection()[1] > 0.0) {
            // 非移动方向上 圆心距离相差 > this.radius+that.radius，则不可能相撞
            if (this.getColors().getDirection()[0] != 0 && Math.abs(dy) <= r_sum) {
                // 算出的移动距离可能因数值误差小于0，若小于0，取0
                return Math.max(0, Math.abs(dx) - Math.sqrt(r_sum * r_sum - dy * dy));
            } else if (this.getColors().getDirection()[1] != 0 && Math.abs(dx) <= r_sum) {
                return Math.max(0, Math.abs(dy) - Math.sqrt(r_sum * r_sum - dx * dx));
            } else return INFINITY;
        } else return INFINITY;
    }


    // 给定细胞，单步最大的移动轨迹
    // 返回移动轨迹的包络矩形
    public Rectangle possible_trace(double width, double height) {
        // 移动方向
        int[] direction = this.color.getDirection();
        // 细胞初始位置
        Position start = this.position;
        // 细胞拟结束位置，未判断边界
        Position qausi_end = new Position(start.getX() + (double) direction[0], start.getY() + (double) direction[1]); //单位步长为1？15？
        // 细胞真实结束位置
        Position end = position_after_contradict_boundary(qausi_end, width, height);
        // 追加一个半径长度
        end.setX(qausi_end.getX() + (double) direction[0] * this.radius);
        end.setY(qausi_end.getY() + (double) direction[1] * this.radius);

        // 创建rectangle
        double xmid = (start.getX() + end.getX()) / 2;
        double ymid = (start.getY() + end.getY()) / 2;
        double xlength, ylength;
        if (direction[0] == 0) {
            xlength = 2.0 * this.radius;
            ylength = 1.0 * Math.abs(end.getY() - start.getY());
        } else {
            xlength = 1.0 * Math.abs(end.getX() - start.getX());
            ylength = 2.0 * this.radius;
        }
        Rectangle rectangle = new Rectangle(xmid, ymid, xlength, ylength);
        rectangle = rectangle.adjust_rectangle(width, height);
        return rectangle;
    }

    // 给定细胞，返还边界修正后 可能合法的移动结束位置
    public Position possible_position(double width, double height) {
        // 移动方向
        int[] direction = this.color.getDirection();
        // 细胞初始位置
        Position start = this.position;
        // 细胞拟结束位置，未判断边界
        Position qausi_end = new Position(start.getX() + (double) direction[0] * 1.1, start.getY() + (double) direction[1] * 1.1); //单位步长为1？15？
        // 细胞真实结束位置
        Position end = position_after_contradict_boundary(qausi_end, width, height);
//        System.out.println(start.toString()+end.toString());
        return end;
    }

    // 将感知矩形限制在背景之中
    public Rectangle adjust_perception_rectangle(double width, double height) {
        double x = this.getPosition().getX(), y = this.getPosition().getY();
        double x_left = Math.max(0, x - this.getPerception_range()), x_right = Math.min(width, x + this.getPerception_range());
        double y_down = Math.max(0, y - this.getPerception_range()), y_up = Math.min(height, y + this.getPerception_range());
        Position position = new Position((x_left + x_right) / 2, (y_down + y_up) / 2);
        double xlen = x_right - x_left;
        double ylen = y_up - y_down;
        return new Rectangle(position.getX(), position.getY(), xlen, ylen);
    }


    // 判断感知 （给定两个细胞）
    public boolean can_percept(Cell that) {
        // ...
        // 参考判断平面上一个矩形和一个圆形是否有重叠（只需要2圆心和感知范围）
        double delta_x = Math.abs(that.getPosition().getX() - this.position.getX()), delta_y = Math.abs(that.getPosition().getY() - this.position.getY());
        if (delta_x <= this.perception_range && delta_y <= this.perception_range + that.getRadius()) {
            return true;
        }
        if (delta_y <= this.perception_range && delta_x <= this.perception_range + that.getRadius()) {
            return true;
        } else if (delta_x > this.perception_range || delta_y > this.perception_range) {
            double vh_x = delta_x - this.perception_range;
            double vh_y = delta_y - this.perception_range;
            if (vh_x < 0) {
                vh_x = 0.0;
            }
            if (vh_y < 0) {
                vh_y = 0.0;
            }
            return Math.pow(vh_x, 2) + Math.pow(vh_y, 2) <= Math.pow(that.getRadius(), 2);//取等待考量
        }
        return false;
    }

    // 根据感知列表变色
    // 感知列表包含自己，需要减一
    public Colors color_changed_given_list(List<Cell> cells) {
        //switch...//只有颜色和数量是有效信息；//改变颜色同时方向

        int n = cells.size();
//        System.out.println(n);
        // r g b y num
        int[] perception_numbers = new int[4];
        // r g b y ratio
//        double[] perception_ration = new double[4];

        // 统计4种num 包含自己
        for (int i = 0; i < n; i++) {
            if (this.can_percept(cells.get(i))) {
                String percepted_cell_color = cells.get(i).getColors().getName();
                switch (percepted_cell_color) {
                    case "r":
                        perception_numbers[0] += 1;
                        break;
                    case "g":
                        perception_numbers[1] += 1;
                        break;
                    case "b":
                        perception_numbers[2] += 1;
                        break;
                    case "y":
                        perception_numbers[3] += 1;
                        break;
                }
            }
        }

        double a = perception_numbers[0], b = perception_numbers[1], c = perception_numbers[2], d = perception_numbers[3];
        String this_color = this.getColors().getName();

        switch (this_color) {
            case "r":
                if (a > 3.9 && (a - 1.0) / (a + b + c + d - 1.0) > 0.70) {
                    return Colors.G; //条件精度待考量
                } else if (d > 0.9 && d / (a + b + c + d - 1.0) < 0.10) {
                    return Colors.Y;
                } else {
                    return this.getColors();
                }
            case "g":
                if (b > 3.9 && (b - 1.0) / (a + b + c + d - 1.0) > 0.70) {
                    return Colors.B; //条件精度待考量
                } else if (a > 0.9 && a / (a + b + c + d - 1.0) < 0.10) {
                    return Colors.R;
                } else {
                    return this.getColors();
                }
            case "b":
                if (c > 3.9 && (c - 1.0) / (a + b + c + d - 1.0) > 0.70) {
                    return Colors.Y; //条件精度待考量
                } else if (b > 0.9 && b / (a + b + c + d - 1.0) < 0.10) {
                    return Colors.G;
                } else {
                    return this.getColors();
                }
            case "y":
                if (d > 3.9 && (d - 1.0) / (a + b + c + d - 1.0) > 0.70) {
                    return Colors.R; //条件精度待考量
                } else if (c > 0.9 && c / (a + b + c + d - 1.0) < 0.10) {
                    return Colors.B;
                } else {
                    return this.getColors();
                }
        }
        return null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return ID == cell.ID;
    }


    public Colors getColors() {
        return color;
    }

    public double getPerception_range() {
        return perception_range;
    }

    public void setColors(Colors color) {
        this.color = color;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public double getRadius() {
        return radius;
    }

    public void draw() {
        if (color.compareTo(Colors.R) == 0) {
            StdDraw_new.setPenColor(Color.RED);
        }
        if (color.compareTo(Colors.G) == 0) {
            StdDraw_new.setPenColor(Color.GREEN);
        }
        if (color.compareTo(Colors.B) == 0) {
            StdDraw_new.setPenColor(Color.BLUE);
        }
        if (color.compareTo(Colors.Y) == 0) {
            StdDraw_new.setPenColor(Color.YELLOW);
        }
        StdDraw_new.filledCircle(position.getX(), position.getY(), radius);
    }

    @Override
    public String toString() {
        return " Cell{" +
                "ID=" + ID +
                ", position=" + position +
                "} ";
    }

    public static void main(String[] args) {
        Position position = new Position(0, 0);
        Colors color = Colors.Y;
        Cell cell = new Cell(1, position, 1, 1, color);
    }
}
