import terminal.*;
import edu.princeton.cs.algs4.*;
import terminal.Rectangle;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;

// 程序从此处开始运行
public class Main_KdTree {
    static boolean isGUI = true;                            // default set as GUI mode
    static int cycle_count = 0;                             // here 1 represents a unit time step, i.e. 1/15s; initialized with cycle_count=0
    static int timelimit;                                   // time bound *15
    static int query_pointer = 0;                           // query查询的指针
    static ArrayList<String[]> output = new ArrayList<>();  // 输出暂时存储
    static JLabel status;

    // terminal/GUI 暴力循环函数：
    public static void brute_force_cycles(Cell[] cells, int timelimit, int[][] query, double width, double height) {
        while (isGUI || (cycle_count <= timelimit + 1)) {
            Stopwatch stopwatch = new Stopwatch();
            // 输出查询
            // 如果当前时间==第query_pointer次 query查询的时间，则加入信息进output
            while (query_pointer < query.length && cycle_count == query[query_pointer][0]) {
                Position position = cells[query[query_pointer][1]].getPosition();
                String[] piece_output = {position.getX() / 15 + "", position.getY() / 15 + "", cells[query[query_pointer][1]].getColors().getName()};
                output.add(piece_output);
//                // 测试语句：显示输出到运行栏
//                System.out.printf("query: %d \tabsolute time: %f \tcycle: %d \tid: %d \t\t result: ", query_pointer + 1, (double) cycle_count / 15.0, cycle_count, query[query_pointer][1]);
//                System.out.println(Arrays.toString(piece_output));
                query_pointer++;
            }

            // 一轮移动
            for (int i = 0; i < cells.length; i++)//move cell[i] to new position.
            {
                // 移动
                cells[i].move(Arrays.asList(cells), width, height);
            }

            // 一轮感知，保存变色结果
            Colors[] changed_color_list = new Colors[cells.length];//保存要变色的颜色

            for (int i = 0; i < cells.length; i++) {
                // 保存感知导致的变色结果
                changed_color_list[i] = (cells[i].color_changed_given_list(Arrays.asList(cells)));
            }

            // 一轮变色
            for (int i = 0; i < cells.length; i++)//change cell[i]'s color with the rules if necessary.
            {
                cells[i].setColors(changed_color_list[i]);
            }

            // 如果是GUI， wait for 1/15 seconds.
            if (isGUI) {
                StdDraw_new.clear();
                for (int k = 0; k < cells.length; k++) {
                    cells[k].draw();
                }
                StdDraw_new.show();
                double elapsedTime = stopwatch.elapsedTime();
                double suspend = 1000 / 15 - elapsedTime * 1000;//根据运行时间调整暂停时间
                if (suspend >= 0)
                    StdDraw_new.pause((int) Math.abs(suspend));
//                else {
//                    System.out.println("此循环与时间不完全同步");
//                }
                double elapsedTime1 = stopwatch.elapsedTime();
//                System.out.println(elapsedTime1-elapsedTime);
                //输出一个循环的总时间/每秒帧率
                status.setText(String.format("Iterations per second:  %.6f", (1 / elapsedTime1)));
                status.setOpaque(true);
                status.setBackground(Color.green);
            }

            cycle_count++;
        }
    }


    // terminal/GUI 多次循环函数：
    public static void cycles(Cell[] cells, int timelimit, int[][] query, double width, double height, double rmax) {

        while (isGUI || (cycle_count <= timelimit + 1)) {
            Stopwatch stopwatch = new Stopwatch();
            // 输出查询
            // 如果当前时间==第query_pointer次 query查询的时间，则加入信息进output
            while (query_pointer < query.length && cycle_count == query[query_pointer][0]) {
                Position position = cells[query[query_pointer][1]].getPosition();
                String[] piece_output = {position.getX() / 15 + "", position.getY() / 15 + "", cells[query[query_pointer][1]].getColors().getName()};
                output.add(piece_output);
//                // 测试语句：显示输出到运行栏
//                System.out.printf("query: %d \tabsolute time: %f \tcycle: %d \tid: %d \t\t result: ", query_pointer + 1, (double) cycle_count / 15.0, cycle_count, query[query_pointer][1]);
//                System.out.println(Arrays.toString(piece_output));
                query_pointer++;
            }

            // 建立Kd树
            KdTree kdTree = new KdTree(width, height);
            for (int i = 0; i < cells.length; i++) {
                kdTree.insert(cells[i].getPosition(), i);
            }

            // 一轮移动
            for (int i = 0; i < cells.length; i++)//move cell[i] to new position.
            {
                // 细胞移动的冲撞矩形半径
                double radius_checking_rectangle = 2 + cells[i].getRadius() + rmax;
                // 冲突矩形
                Rect checking_rectangle = new Rect(Math.max(0, cells[i].getPosition().getX() - radius_checking_rectangle),
                        Math.max(0, cells[i].getPosition().getY() - radius_checking_rectangle),
                        Math.min(width, cells[i].getPosition().getX() + radius_checking_rectangle),
                        Math.min(height, cells[i].getPosition().getY() + radius_checking_rectangle));

                // 找出冲突集合（包含cells[i]自身）
                LinkedList<Integer> contradiction_id_list = (LinkedList<Integer>) kdTree.range(checking_rectangle);
                ArrayList<Cell> contradiction_list = new ArrayList<>();
                for (int j = 0; j < contradiction_id_list.size(); j++) {
                    contradiction_list.add(cells[contradiction_id_list.get(j)]);
                }
                // 移动
                cells[i].move(contradiction_list, width, height);
            }

            // 一轮感知，保存变色结果
            Colors[] changed_color_list = new Colors[cells.length];//保存要变色的颜色

            for (int i = 0; i < cells.length; i++) {

                // 细胞移动的感知矩形半径
                double radius_checking_rectangle = 0.1+ cells[i].getPerception_range() + rmax;
                // 感知矩形
                Rect checking_rectangle = new Rect(Math.max(0, cells[i].getPosition().getX() - radius_checking_rectangle),
                        Math.max(0, cells[i].getPosition().getY() - radius_checking_rectangle),
                        Math.min(width, cells[i].getPosition().getX() + radius_checking_rectangle),
                        Math.min(height, cells[i].getPosition().getY() + radius_checking_rectangle));
                // 找出可能感知集合（包含cells[i]自身）
                LinkedList<Integer> perception_id_list = (LinkedList<Integer>) kdTree.range(checking_rectangle);
                ArrayList<Cell> perception_list = new ArrayList<>();
                for (int j = 0; j < perception_id_list.size(); j++) {
                    perception_list.add(cells[perception_id_list.get(j)]);
                }
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
                StdDraw_new.clear();
                for (int k = 0; k < cells.length; k++) {
                    cells[k].draw();
                }
                StdDraw_new.show();
                double elapsedTime = stopwatch.elapsedTime();
                double suspend = 1000 / 15 - elapsedTime * 1000;//根据运行时间调整暂停时间
                if (suspend >= 0) {
                    StdDraw_new.pause((int) Math.abs(suspend));
                }
//                else {
//                    System.out.println("此循环与时间不完全同步");
//                }
                double elapsedTime1 = stopwatch.elapsedTime();
//                System.out.println(elapsedTime1-elapsedTime);
                //输出一个循环的总时间/每秒帧率
                status.setText(String.format("Iterations per second:  %.6f", (1 / elapsedTime1)));
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
        double rmax = 0;
        Cell[] cells;    // 存储所有细胞
//        long startMili = System.currentTimeMillis();// 当前时间对应的毫秒数

        // 判断GUI/terminal模式
        if (args.length != 0 && args[0].equals("--terminal")) {
            isGUI = false;
//            System.out.println("/**开始 " + startMili);
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
            rmax = Math.max(rmax, radius);

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

        // GUI画布
        if (isGUI) {
            double ratio = width / height;
            int width_pixels = (int) (ratio * 670);             //换算为合适比例
            JPanel panel = StdDraw_new.setCanvasSize(width_pixels, 700);
            panel.setBounds(0, 670, width_pixels, 30);
            panel.setOpaque(true);
            status = (JLabel) panel.getComponent(0);
            panel.setVisible(true);
            // enable double buffering
            StdDraw_new.enableDoubleBuffering();
            StdDraw_new.setXscale(0, width);
            StdDraw_new.setYscale(-0.045 * height, height);
            StdDraw_new.clear();
            for (int i = 0; i < cells.length; i++) {
                cells[i].draw();
            }
            StdDraw_new.show();
        }

        // 运行程序: cycles或暴力法二选一
        cycles(cells, timelimit, query, width, height, rmax);
//        brute_force_cycles(cells, timelimit, query, width, height);

        // 输出txt
        if (!isGUI) {
            new File("./results/").mkdir();
            try (PrintWriter fout = new PrintWriter("./results/Results_sample3.txt")) {
                for (int j = 0; j < output.size(); ++j)
                    fout.println(Arrays.toString(output.get(j)).replace("[", "").replace("]", "").replace(",", ""));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        long endMili = System.currentTimeMillis();//结束时间
//        System.out.println("/**结束 s" + endMili);
//        System.out.println("/**总耗时为：" + (endMili - startMili) + "毫秒");
    }
}
