import edu.princeton.cs.algs4.StdIn;
import terminal.*;

public class Test_Big_Sample_Position_Legal {
    public static void main(String[] args) {
        // 初始变量设置
        double width;      //in 1/15 meters
        double height;     //in 1/15 meters
        double rmax = 0;
        Cell[] cells;    // 存储所有细胞
        width = StdIn.readDouble() ;
        height = StdIn.readDouble() ;
        int cells_num = StdIn.readInt();
        cells = new Cell[cells_num];

        // 传入每个细胞的性质 cells[i]
        for (int i = 0; i < cells_num; i++) {
            double x = StdIn.readDouble() ;
            double y = StdIn.readDouble() ;
            Position position = new Position(x, y);
            double radius = StdIn.readDouble() ;
            double perception_range = StdIn.readDouble() ;
            String color1 = StdIn.readString();
            Colors color = null;
            switch (color1) {
                case "r":
                    color = Colors.R;
                    break;
                case "g":
                    color = Colors.G;
                    break;
                case "b":
                    color = Colors.B;
                    break;
                case "y":
                    color = Colors.Y;
                    break;
            }
            cells[i] = new Cell(i, position, radius, perception_range, color);
        }
        for (int i = 0; i <cells.length; i++) {
         for (int j = 0; j <cells.length; j++) {
            double a = cells[i].getPosition().getX()-cells[j].getPosition().getX(),b = cells[i].getPosition().getY()-cells[j].getPosition().getY(),
                    c=cells[i].getRadius(), d=cells[j].getRadius();
            if (i!=j){
            if (a*a+b*b-(c+d)*(c+d)<0){
                System.out.println("Error");
            }
            }
        }
    }
    }
}
