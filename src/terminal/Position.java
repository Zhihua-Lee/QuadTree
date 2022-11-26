package terminal;

public class Position {


    public double x;
    public double y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    //add shift to update the given position
    public void add(int[] direction, double step) {
        this.x = x + direction[0] * step;
        this.y = y + direction[1] * step;
    }

    public double distanceSquareTo(Position that) {

        double dx = that.x - this.x;

        double dy = that.y - this.y;

        return dx * dx + dy * dy;

    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + x / 15 + ", " + y / 15 + ")";
    }

    public static void main(String[] args) {
        Position position = new Position(1.0, 1.0);
    }
}
