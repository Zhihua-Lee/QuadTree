package terminal;

//一个矩形，属性有长、宽、中心点位置
public class Rectangle {

    private double xmid;        //中心x位置
    private double ymid;        //中心y位置
    private double xlength;     //x方向长度
    private double ylength;     //y方向长度

    public Rectangle(double xmid, double ymid, double xlength, double ylength) {
        this.xmid = xmid;
        this.ymid = ymid;
        this.xlength = xlength;
        this.ylength = ylength;
    }

    // background
    public Rectangle(double xlength, double ylength) {
        this.xlength = xlength;
        this.ylength = ylength;
        this.xmid = xlength / 2;
        this.ymid = ylength / 2;
    }

    //        public double length() {
//            return length;
//        }

    public double minX(){
        return this.xmid-0.5*xlength;
    }

    public double maxX(){
        return this.xmid+0.5*xlength;
    }

    public double minY(){
        return this.ymid-0.5*ylength;
    }

    public double maxY(){
        return this.ymid+0.5*ylength;
    }

    // 计算矩形到某一点的最近距离(以平方和的形式)
    public double distanceSquareToPoint(Position point) {
        double dx = 0.0;
        double dy = 0.0;
        double minX = this.xmid-0.5*xlength, maxX = this.xmid+0.5*xlength;
        double minY = this.ymid-0.5*ylength, maxY =this.ylength+0.5*ylength;
        if (point.x < minX) dx = minX - point.x;
        else if (point.x > maxX) dx = point.x - maxX;
        if (point.y < minY) dy = minY - point.y;
        else if (point.y > maxY) dy = point.y - maxY;
        return dx * dx + dy * dy;

    }

    //判断该开矩形是否能放下cell
    //如果该矩形可以放下cell的最上、最下、最左、最右四个边界点，则可以放下cell。反之也成立。
    public boolean open_can_contain_cell(Position position, double radius) {
        double x = position.getX(), y = position.getY();
        return (this.open_contain_point(x - radius, y - radius) &&
                this.open_contain_point(x - radius, y + radius) &&
                this.open_contain_point(x + radius, y - radius) &&
                this.open_contain_point(x + radius, y + radius));
    }

    //判断该闭矩形是否能放下cell
    //如果该矩形可以放下cell的最上、最下、最左、最右四个边界点，则可以放下cell。反之也成立。
    public boolean close_can_contain_cell(Position position, double radius) {
        double x = position.getX(), y = position.getY();
        return (this.close_contain_point(x - radius, y - radius) &&
                this.close_contain_point(x - radius, y + radius) &&
                this.close_contain_point(x + radius, y - radius) &&
                this.close_contain_point(x + radius, y + radius));
    }

    // 判断该开矩形是否能够放下给定闭矩形
    // 只需判断包含对象矩形的左上、右下角
    public boolean open_can_contain_rectangle(Rectangle rectangle) {
        return (this.open_contain_point(rectangle.xmid - rectangle.xlength / 2, rectangle.ymid - rectangle.ylength / 2) &&
                this.open_contain_point(rectangle.xmid + rectangle.xlength / 2, rectangle.ymid + rectangle.ylength / 2));
    }

    // 判断该闭矩形是否能够放下给定闭矩形
    // 只需判断包含对象矩形的左上、右下角
    public boolean close_can_contain_rectangle(Rectangle rectangle) {
        return (this.close_contain_point(rectangle.xmid - rectangle.xlength / 2, rectangle.ymid - rectangle.ylength / 2) &&
                this.close_contain_point(rectangle.xmid + rectangle.xlength / 2, rectangle.ymid + rectangle.ylength / 2));
    }

    // 判断该开矩形是否包含(x,y)点
    public boolean open_contain_point(double x, double y) {
        double xHalfLen = this.xlength / 2.0;
        double yHalfLen = this.xlength / 2.0;
        return (x < this.xmid + xHalfLen &&
                x > this.xmid - xHalfLen &&
                y < this.ymid + yHalfLen &&
                y > this.ymid - yHalfLen);
    }

    // 判断该闭矩形是否包含(x,y)点
    public boolean close_contain_point(double x, double y) {
        double xHalfLen = this.xlength / 2.0;
        double yHalfLen = this.xlength / 2.0;
        return (x <= this.xmid + xHalfLen &&
                x >= this.xmid - xHalfLen &&
                y <= this.ymid + yHalfLen &&
                y >= this.ymid - yHalfLen);
    }

    // 将矩形限制在背景之中
    public Rectangle adjust_rectangle(double width, double height) {
        double x_left = Math.max(0, this.minX()), x_right = Math.min(width, this.maxX());
        double y_down = Math.max(0, this.minY()), y_up = Math.min(height, this.maxY());
        Position position = new Position((x_left + x_right) / 2, (y_down + y_up) / 2);
        double xlen = x_right - x_left;
        double ylen = y_up - y_down;
        return new Rectangle(position.getX(), position.getY(), xlen, ylen);
    }

    public Rectangle NW() {
        double x = this.xmid - this.xlength / 4.0;
        double y = this.ymid + this.ylength / 4.0;
        double xLen = this.xlength / 2.0;
        double yLen = this.ylength / 2.0;
        Rectangle NW = new Rectangle(x, y, xLen, yLen);
        return NW;
    }

    public Rectangle NE() {
        double x = this.xmid + this.xlength / 4.0;
        double y = this.ymid + this.ylength / 4.0;
        double xLen = this.xlength / 2.0;
        double yLen = this.ylength / 2.0;
        Rectangle NE = new Rectangle(x, y, xLen, yLen);
        return NE;
    }

    public Rectangle SW() {
        double x = this.xmid - this.xlength / 4.0;
        double y = this.ymid - this.ylength / 4.0;
        double xLen = this.xlength / 2.0;
        double yLen = this.ylength / 2.0;
        Rectangle SW = new Rectangle(x, y, xLen, yLen);
        return SW;
    }

    public Rectangle SE() {
        double x = this.xmid + this.xlength / 4.0;
        double y = this.ymid - this.ylength / 4.0;
        double xLen = this.xlength / 2.0;
        double yLen = this.ylength / 2.0;
        Rectangle SE = new Rectangle(x, y, xLen, yLen);
        return SE;
    }

    public double getXlength() {
        return xlength;
    }

    public double getYlength() {
        return ylength;
    }

    public double getXmid() {
        return xmid;
    }

    public double getYmid() {
        return ymid;
    }

    @Override
    public String toString() {
        return String.format("Rectangle:\txmid=%f\tymid=%f\txlen=%f\tylen=%f", xmid / 15, ymid / 15, xlength / 15, ylength / 15);
    }
}
