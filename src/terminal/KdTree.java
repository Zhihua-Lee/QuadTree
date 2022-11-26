package terminal;


import java.util.LinkedList;

public class KdTree {
    private double width, height;

    // 其中 rect 成员表示该节点所分割的平面，即它的左右孩子所表示的空间之和，该成员用于判断最邻近点
    private class Node {
        Position point;
        int id;
        Rect rectangle;
        Node left;
        Node right;

        Node(Position p, Rect r, int id) {
            point = p;
            rectangle = r;
            this.id = id;
            left = null;
            right = null;
        }
    }

    // 根节点
    private Node root;

    // 构造函数
    public KdTree(double width, double height) {
        root = null;
        this.width = width;
        this.height = height;
    }

    // 默认根节点是纵向分割
    public void insert(Position point, int id) {
        root = insert(point, root, false, 0.0, 0.0, width, height, id);
    }

    private Node insert(Position point, Node node, boolean is_perpendicular, double x_min, double y_min, double x_max, double y_max, int id) {
        if (node == null) {
            return new Node(point, new Rect(x_min, y_min, x_max, y_max), id);
        }
// 改变分割方向
        is_perpendicular = !is_perpendicular;
// 判断要插入的点在当前点的左/下还是右/上
        double value0 = is_perpendicular ? point.getX() : point.getY();
        double value1 = is_perpendicular ? node.point.getX() : node.point.getY();
        if (value0 < value1) {
            node.left = insert(point, node.left, is_perpendicular, x_min, y_min,
                    is_perpendicular ? node.point.getX() : x_max, is_perpendicular ? y_max : node.point.getY(), id);
        } else {
            node.right = insert(point, node.right, is_perpendicular,
                    is_perpendicular ? node.point.getX() : x_min, is_perpendicular ? y_min : node.point.getY(), x_max, y_max, id);
        }
        return node;
    }


    // 判断是否包含该点
    public boolean contains(Position point) {
        return contains(point, root, false);
    }

    private boolean contains(Position point, Node node, boolean is_perpendicular) {
        if (node == null) return false;
        if (node.point.equals(point)) return true;
// 改变分割方向
        is_perpendicular = !is_perpendicular;
// 判断要查询的点在当前点的左/下还是右/上
        double value1 = is_perpendicular ? point.getX() : point.getY();
        double value2 = is_perpendicular ? node.point.getX() : node.point.getY();
        if (value1 < value2) {
            return contains(point, node.left, is_perpendicular);
        } else {
            return contains(point, node.right, is_perpendicular);
        }
    }

    // 返回矩形范围内的所有点
    public Iterable range(Rect rect) {
        LinkedList result = new LinkedList();
        range(rect, root, false, result);
        return result;
    }

    private void range(Rect rectangle, Node node, boolean is_perpendicular, LinkedList IDs_of_rectangle) {
        if (node == null) return;
// 改变分割方向
        is_perpendicular = !is_perpendicular;
        Position point = node.point;
        int id = node.id;
        if (rectangle.contains(point)) IDs_of_rectangle.add(id);
// 判断当前点所分割的两个空间是否与矩形相交
        double value = is_perpendicular ? point.getX() : point.getY();
        double min = is_perpendicular ? rectangle.minX : rectangle.minY;
        double max = is_perpendicular ? rectangle.maxX : rectangle.maxY;
        if (min < value) {
            range(rectangle, node.left, is_perpendicular, IDs_of_rectangle);
        }
        if (max >= value) {
            range(rectangle, node.right, is_perpendicular, IDs_of_rectangle);
        }
    }
}
