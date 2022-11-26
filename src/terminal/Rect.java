package terminal;

public class Rect {
    // 分别表示左下顶点和右上顶点
    public final double minX;
    public final double minY;
    public final double maxX;
    public final double maxY;
// Rect类是 immutable datatype

    public Rect(double x0, double y0, double x1, double y1) {
        minX = x0;
        minY = y0;
        maxX = x1;
        maxY = y1;
    }
// 判断该点是否位于该矩形之内
    public boolean contains(Position point) {
        return (point.getX() >= minX) && (point.getX() <= maxX)
                && (point.getY() >= minY) && (point.getY() <= maxY);
    }

// 计算矩形到某一点的最近距离(以平方和的形式)
    public double distanceSquareToPoint(Position point) {
        double dx = 0.0;
        double dy = 0.0;
        if (point.x < minX) dx = minX - point.x;
        else if (point.x > maxX) dx = point.x - maxX;
        if (point.y < minY) dy = minY - point.y;
        else if (point.y > maxY) dy = point.y - maxY;
        return dx * dx + dy * dy;

    }
}
