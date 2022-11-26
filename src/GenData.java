import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.util.*;
import edu.princeton.cs.algs4.*;

public class GenData {
    public static void main( String[] args ) {
        System.out.println("请依次输入：长度 宽度 细胞数量 时间范围 query个数 存储的文件名");
        double width;      //in 1/15 meters
        double height;     //in 1/15 meters
        double x;
        double y;
        int cells_num;
        int query_num;
        int name_of_file;
        Double timeLimit;
        String[] colors;
        colors = new String[]{"r", "g", "b", "y"};
        //TODO：需要给定长度 宽度 细胞数量 时间范围 query个数
        width = StdIn.readDouble();
        height = StdIn.readDouble();
        cells_num = StdIn.readInt();
        timeLimit = StdIn.readDouble();
        query_num = StdIn.readInt();
        name_of_file = StdIn.readInt();
        double[][] cells = new double[cells_num][4];    // 存储所有细胞的数组
        x = StdRandom.uniform(0, width);
        y = StdRandom.uniform(0, height);
        double[] limit1 ={x,y,width-x,height-y};
        Arrays.sort(limit1);
        double r_limit1 = limit1[0];
        double radius1 = StdRandom.uniform(Math.min(0.5*r_limit1,0.1*Math.min(width,height)/(Math.sqrt(cells_num)+1)), Math.min(r_limit1,0.5*Math.min(width,height)/(Math.sqrt(cells_num)+1)));
        double perception_range1 = StdRandom.uniform(0, x);
        String color1 = colors[StdRandom.uniform(0,4)];
        cells[0][0] = x;
        cells[0][1] = y;
        cells[0][2] = radius1;
        cells[0][3] = perception_range1;//获取第一个细胞的x y r range color 并填入数组
        int m=1;

        while (m < cells_num){
            x = StdRandom.uniform(0, width);
            y = StdRandom.uniform(0, height);
            double[] limit ={x,y,width-x,height-y};
            Arrays.sort(limit);
            double r_limit = limit[0];
            if (r_limit<0.45*Math.min(width,height)/(Math.sqrt(cells_num)+1)) continue;
            // 控制细胞半径最值
            double radius = StdRandom.uniform(0.1*Math.min(width,height)/(Math.sqrt(cells_num)+1), 0.5*Math.min(width,height)/(Math.sqrt(cells_num)+1));
            double perception_range = StdRandom.uniform(0.5*Math.min(width,height)/(Math.sqrt(cells_num)+1), 2*Math.min(width,height)/(Math.sqrt(cells_num)+1));
            if(overlap(cells,m,x,y,radius)) {
                cells[m][0] = x;
                cells[m][1] = y;
                cells[m][2] = radius;
                cells[m][3] = perception_range;//与前面细胞相比较 如果有重叠 则不能放入
                ++m;
            }
        }
        double[] time = new double[query_num];
        for (int i = 0; i < query_num; i++) {
            time[i]= StdRandom.uniform(0,timeLimit);
        }
        Arrays.sort(time);
        time[query_num-1] = timeLimit;

//        System.out.println(width+" "+height);
//        System.out.println(cells_num);
//        for (int i = 0; i < cells_num; i++) {
//            for (int j = 0; j < 4; j++) {
//                System.out.print(cells[i][j]+" ");
//            }
//            System.out.print(colors[StdRandom.uniform(0,4)]);
//            System.out.print("\r\n");
//        }
//        System.out.println(query_num);
//        for (int i = 0; i < query_num; i++) {
//                System.out.println(time[i]+" "+StdRandom.uniform(0,cells_num));
//            }
//        
        
        new File("./big_sample(bonus)").mkdir();
        try ( PrintWriter fout = new PrintWriter("./big_sample(bonus)/big_sample"+name_of_file+".txt") ) {

            fout.println(width+" "+height);
            fout.println(cells_num);
            for (int i = 0; i < cells_num; i++) {
                for (int j = 0; j < 4; j++) {
                    fout.print(cells[i][j]+" ");
                }
                fout.print(colors[StdRandom.uniform(0,4)]);
                fout.print("\r\n");
            }
            fout.println(query_num);
            for (int i = 0; i < query_num; i++) {
                fout.println(time[i]+" "+StdRandom.uniform(0,cells_num));
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }





    }

    public static boolean overlap(double cells[][],int m,double x2,double y2,double r2){
        int judge = 0;
        for (int i = 0;i<m;i++){
            double x1 =cells[i][0];
            double y1 =cells[i][1];
            double r1 =cells[i][2];
            if((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)-(r1+r2)*(r1+r2)<0.1){
                judge++;
            }
        }
        return judge==0;
    }
}
