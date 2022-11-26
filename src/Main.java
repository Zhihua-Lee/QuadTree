import terminal.*;
import edu.princeton.cs.algs4.*;
import terminal.Rectangle;
import terminal.StdDraw_new;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;

// 程序从此处开始运行
public class Main {
    static boolean isGUI = true;                            // default set as GUI mode
    static int cycle_count = 0;                             // here 1 represents a unit time step, i.e. 1/15s; initialized with cycle_count=0
    static int timelimit;                                   // time bound *15
    static int query_pointer = 0;                           // query查询的指针
    static ArrayList<String[]> output = new ArrayList<>();  // 输出暂时存储
    static JLabel status;


    // terminal/GUI 多次循环函数：
    public static void cycles(Cell[] cells, QuadTree quadTree, int timelimit, int[][] query) {

        while (isGUI || (cycle_count <= timelimit + 1)) {
            Stopwatch stopwatch = new Stopwatch();
            // 输出查询
            // 如果当前时间==第query_pointer次 query查询的时间，则加入信息进output
            while (query_pointer < query.length && cycle_count == query[query_pointer][0]) {
                Position position = cells[query[query_pointer][1]].getPosition();
                String[] piece_output = {position.getX() / 15 + "", position.getY() / 15 + "", cells[query[query_pointer][1]].getColors().getName()};
                output.add(piece_output);
                // 测试语句：显示输出到运行栏
//                System.out.printf("query: %d \tabsolute time: %f \tcycle: %d \tid: %d \t\t result: ", query_pointer + 1, (double) cycle_count / 15.0, cycle_count, query[query_pointer][1]);
//                System.out.println(Arrays.toString(piece_output));

                query_pointer++;
            }

            // 一轮移动
            for (int i = 0; i < cells.length; i++)//move cell[i] to new position.
            {
                // 绘出细胞移动的轨迹：初始位置和结束位置
                double Xlength_of_node = quadTree.getRectangle().getXlength(), Ylength_of_node = quadTree.getRectangle().getYlength();
                // 包络node，同时包含位于初始位置和结束位置的两个细胞cells[i]
                Rectangle trace = cells[i].possible_trace(Xlength_of_node,Ylength_of_node);
                QuadTree contradiction_node = /*quadTree.search_move_node_given_rectangle(trace);*/
              quadTree.search_node_given_cells(cells[i].getPosition(), cells[i].possible_position(Xlength_of_node, Ylength_of_node), cells[i].getRadius());
                // 通过node找出冲突集合（包含cells[i]自身）
                List<Cell> contradiction_list = quadTree.get_Cells();
                System.out.println(contradiction_list.size());
                // 先删除四叉树中的cells[i]，因为移动后细胞位置改变，而要通过其位置索引在树中位置
                quadTree.delete(cells[i]);
                // 移动
                cells[i].move(contradiction_list, quadTree.getRectangle().getXlength(), quadTree.getRectangle().getYlength());
                // 重新insert 进入 treenode
                quadTree.insert(cells[i]);

            }

            // 一轮感知，保存变色结果
            Colors[] changed_color_list = new Colors[cells.length];//保存要变色的颜色
            for (int i = 0; i < cells.length; i++) {
                // scans cell[i]'s perception range, calculate the number of rgb cells.
                // 定义其感知Rectangle
                Rectangle perception_rectangle = cells[i].adjust_perception_rectangle(quadTree.getRectangle().getXlength(), quadTree.getRectangle().getYlength());
                // 找到感知Rectangle的包络节点
                QuadTree perception_node = quadTree.search_node_given_rectangle(perception_rectangle);
                // 感知集合
                List<Cell> perception_list = perception_node.get_Cells();
                // 保存感知导致的变色结果
                changed_color_list[i] = (cells[i].color_changed_given_list(perception_list));
            }

            // 一轮变色
            for (int i = 0; i < cells.length; i++)//change cell[i]'s color with the rules if necessary.
            {
                cells[i].setColors(changed_color_list[i]);
            }

            // 如果是GUI， wait for 1/15 seconds.
            if (isGUI) {
                double abc = 1234;
                StdDraw_new.clear();
                for (int k = 0; k < cells.length; k++) {
                    cells[k].draw();
                }
                StdDraw_new.show();
                double elapsedTime = stopwatch.elapsedTime();

                double suspend = 1000 / 15 - elapsedTime * 1000;//根据运行时间调整暂停时间

                if (suspend >= 0) {
                    StdDraw_new.pause((int) suspend);
                }

                double elapsedTime1 = stopwatch.elapsedTime();

                //输出一个循环的总时间/每秒帧率
                status.setText(String.format("Iterations per second:  %.6f",(1 / elapsedTime1)));
                status.setOpaque(true);
                status.setBackground(Color.green);
            }

            cycle_count++;
        }
    }


    public static void main(String[] args) {
        // 初始变量设置
        QuadTree quadTree;
        Rectangle background;
        double width;      //in 1/15 meters
        double height;     //in 1/15 meters
        Cell[] cells;    // 存储所有细胞

        // 判断GUI/terminal模式
        if (args.length != 0 && args[0].equals("--terminal")) {
            isGUI = false;
        }
        width = StdIn.readDouble() * 15;
        height = StdIn.readDouble() * 15;
        int cells_num = StdIn.readInt();
        cells = new Cell[cells_num];

        // 传入每个细胞的性质 cells[i]
        for (int i = 0; i < cells_num; i++) {
            double x = StdIn.readDouble() * 15;
            double y = StdIn.readDouble() * 15;
            Position position = new Position(x, y);
            double radius = StdIn.readDouble() * 15;
            double perception_range = StdIn.readDouble() * 15;
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

        // 传入query以及计算timelimit（查询时间已经*15）
        timelimit = 0;
        int query_num = StdIn.readInt();            //query个数
        int[][] query = new int[query_num][2];
        for (int i = 0; i < query_num; i++) {
            for (int j = 0; j < 2; j++) {
                if (j == 0) {
                    double t = StdIn.readDouble() * 15.0;
                    query[i][j] = (int) t;
                } else {
                    query[i][j] = StdIn.readInt();
                }
            }
            if (query[i][0] >= timelimit) {
                timelimit = query[i][0];
            }
        }

        // 初始化树
        background = new Rectangle(width, height);
        quadTree = new QuadTree(0, background, null);
        for (int i = 0; i < cells_num; i++) {
            quadTree.insert(cells[i]);
        }


        // GUI画布
        if (isGUI) {
            double abc = 123456;
            double ratio = width * 700 / height;
            int widthInt = (int) ratio;             //换算为合适比例
            status = (JLabel) StdDraw_new.setCanvasSize(widthInt, 700).getComponent(0);
            status.setBounds(0, 0, 1000, 0);
            // enable double buffering
            StdDraw_new.enableDoubleBuffering();
            StdDraw_new.setXscale(0, width);
            StdDraw_new.setYscale(0, height);
            StdDraw_new.clear();
            for (int i = 0; i < cells.length; i++) {
                cells[i].draw();
            }

            StdDraw_new.show();
        }

        // 运行程序
        cycles(cells, quadTree, timelimit, query);

        // 输出txt
        if (!isGUI) {
            new File("./results/").mkdir();
            try (PrintWriter fout = new PrintWriter("./results/"+"Results_" + "sample1_out.txt")) {
                for (int j = 0; j < output.size(); ++j)
                    fout.println( Arrays.toString(output.get(j)).replace("[","").replace("]","").replace(",",""));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
