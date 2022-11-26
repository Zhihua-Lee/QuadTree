package terminal;

import java.util.ArrayList;
import java.util.List;
/*
四叉树组织cell的排布，更易搜索
每一个quadtree node对应一片区域
 */

/*
  待选常量：
  - MAX_OBJECTS: 每个节点（象限）所能包含物体的最大数量
  - MAX_LEVELS: 四叉树的最大深度
*/
public class QuadTree {


    private ArrayList<Cell> cells_yes = new ArrayList<>();          // 用于存储该节点下可以继续放入更细矩形的cells（不包含子节点所含cell），只有该list里的cells可以参与split
    private ArrayList<Cell> cells_no = new ArrayList<>();           // 用于存储该节点下不可继续放入更细矩形的cells（不包含子节点所含cell）
    private int level;                                              // 该节点的深度，根节点的默认深度为0
    private Rectangle rectangle;                                    // 该节点对应的象限在屏幕上的范围，bounds是一个矩形
    private QuadTree NWsubnode;                                     // tree representing northwest quadrant
    private QuadTree NEsubnode;                                     // tree representing northeast quadrant
    private QuadTree SWsubnode;                                     // tree representing southwest quadrant
    private QuadTree SEsubnode;                                     // tree representing southeast quadrant
    private QuadTree parent;                                        // parent tree

    // constructor
    public QuadTree(int level, Rectangle rectangle, QuadTree parent) {
        this.level = level;
        this.rectangle = rectangle;
        this.parent = parent;
    }

    // 将新的cell添加到这棵树
    // 依据其位置(通过分裂)细化到叶节点；每次insert后仍保持四叉树的结构，每个小象限/rectangle至只包含一个可细分cell
    // 三种情况：
    // 1.cell不可细分；
    // 2.cell可细分，插入茎节点；
    // 3.cell可细分，插入叶节点；
    public void insert(Cell cell) {
        // 在子节点中的象限位置
        int direction = this.direction_of_subNode_for(cell.getPosition(), cell.getRadius());

        // 若cell不可继续划分到子树，放入当前节点的cells_no，结束（新增no的点不会造成yes拥挤）
        if (direction == 0) {
            this.cells_no.add(cell);
            return;
        }

        // 若cell在子树可继续划分

        // 插入茎节点：直接传递到子节点，最后转化为插入叶节点
        if (!this.isExternal()) {
            this.subNode(direction).insert(cell);
        }

        // 插入叶节点
        // 先插入,放入当前节点的cells_yes，忽视冲突,再判断split
        else {
            this.cells_yes.add(cell);
            // 如果cells_yes点数过多，执行split
            if (cells_yes.size() > 1) {
                this.split();
            }
        }
    }


    // 单次分裂
    // 一颗树的node里cell_yes包含个数过多(2个)，则将root里cells_yes的cells放到子节点去
    // 不考虑cell_no的元素过多的情况，是因为cell_no中的cell不可放入更细的矩形，不参与split
    public void split() {
        // 初始化子树
        this.NWsubnode = new QuadTree(this.level + 1, this.rectangle.NW(), this);
        this.NEsubnode = new QuadTree(this.level + 1, this.rectangle.NE(), this);
        this.SWsubnode = new QuadTree(this.level + 1, this.rectangle.SW(), this);
        this.SEsubnode = new QuadTree(this.level + 1, this.rectangle.SE(), this);


        // 把cell_yes的cells逐个放入子树
        for (int i = 0; i < this.cells_yes.size(); i++) {
            // 每个cell可以放入的子象限
            int direction = this.direction_of_subNode_for(cells_yes.get(i).getPosition(), cells_yes.get(i).getRadius());
            // 放入子象限，删除父node cells_yes中的对应cell
            this.subNode(direction).insert(this.cells_yes.get(i));
        }
        this.cells_yes.clear();
    }

    // delete cell方法
    // 去掉非叶节点的不可细分节点：        直接去掉，不影响树的正常结构，因为影响此层是否分裂的是此层的yes个数
    // 去掉叶节点里的cell：      直接去掉之后这层永远不会为空（>=1），因为是分裂后的结果
    //                       去掉之后叶节点只有在与叶节点同阶只有一个点的情况下，才可能退化
    public void delete(Cell cell) {
        QuadTree tree = this;
        int direction = tree.direction_of_subNode_for(cell.getPosition(), cell.getRadius());
        // 不能继续划分
        if (direction == 0 || tree.isExternal()) {
            tree.cells_yes.remove(cell);
            tree.cells_no.remove(cell);
            // 返回删除cell后的子树,该层只有1个点时，删掉此层结构
            // 只有删掉叶节点会影响层数
            if (tree.level != 0 && tree.isExternal()) {
                List<Cell> list = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    list.addAll(tree.parent.subNode(i + 1).get_Cells());
                }
                if (list.size() == 1) {
                    tree.parent.cells_yes.addAll(list);
                    tree.parent.NWsubnode = null;
                    tree.parent.NEsubnode = null;
                    tree.parent.SWsubnode = null;
                    tree.parent.SEsubnode = null;
                }
            }
        }   //继续划分
        else {
            tree.subNode(direction).delete(cell);
        }

    }


    // 判断cell是否可以再继续放入（node所属）矩形的子矩形
    // 依此和四个子矩形判断交叉，任意有一个子象限可以放入cell即可
    // 若不能继续放入，则返回0；若可以，则根据可以放入的子象限返还int
    public int direction_of_subNode_for(Position position, double radius) {
        if (this.rectangle.NE().close_can_contain_cell(position, radius)) return 1;
        if (this.rectangle.NW().close_can_contain_cell(position, radius)) return 2;
        if (this.rectangle.SW().close_can_contain_cell( position, radius)) return 3;
        if (this.rectangle.SE().close_can_contain_cell(position, radius)) return 4;
        return 0;
    }

    // 判断矩形是否可以再继续放入（node所属）矩形的子矩形
    // 如果没有则返回0
    public int direction_of_subNode_for(Rectangle rectangle) {
        if (this.rectangle.NE().close_can_contain_rectangle(rectangle)) return 1;
        if (this.rectangle.NW().close_can_contain_rectangle(rectangle)) return 2;
        if (this.rectangle.SW().close_can_contain_rectangle(rectangle)) return 3;
        if (this.rectangle.SE().close_can_contain_rectangle(rectangle)) return 4;
        return 0;
    }

    // 按照象限寻找子树
    public QuadTree subNode(int direction) {
        switch (direction) {
            case 1:
                return NEsubnode;
            case 2:
                return NWsubnode;
            case 3:
                return SWsubnode;
            case 4:
                return SEsubnode;
            default:
                return this;
        }
    }

    // 搜索给定细胞初始。结束位置，所在的最小树节点
    // 从当前树根部开始寻找，逐级下降
    public QuadTree search_node_given_cells(Position position1, Position position2, Double radius) {
        QuadTree node = this;
        for (int i = 0; i < 4; i++) {
            QuadTree subnode = node.subNode(i + 1);
            if (!node.isExternal() &&!subnode.isExternal() && subnode.getRectangle().close_can_contain_cell(position1, radius) && subnode.getRectangle().close_can_contain_cell(position2, radius)) {
                node = subnode;
                return node.search_node_given_cells(position1, position2, radius);
            }
        }
        if (node.level == 0) return node;
        return node.parent;

//        Rectangle endrec = new Rectangle(position2.getX(), position2.getY(),2*radius,2*radius);
//        QuadTree node = search_node_given_rectangle(endrec);
//        if (node.level!=0) node = node.parent;
////        if (!node.rectangle.open_can_contain_cell(position2,radius)) System.out.println("shit");
//        while (node.level!=0 &&!node.getRectangle().close_can_contain_cell(position1,radius)) node = node.parent;
//        return node;
    }


    public QuadTree search_move_node_given_rectangle(Rectangle rec) {
        QuadTree node = this;
        for (int i = 0; i < 4; i++) {
            QuadTree subnode = node.subNode(i + 1);
            if (subnode != null && !subnode.isExternal() && subnode.getRectangle().open_can_contain_rectangle(rec)) {
                node = subnode;
                return node.search_move_node_given_rectangle(rec);
            }
        }
        if (node.level == 0) return node;
        return node.parent;
    }

    public QuadTree search_node_given_rectangle(Rectangle rectangle) {
        QuadTree node = this;
        int direction = node.direction_of_subNode_for(rectangle);
        boolean open_can_contain = node.getRectangle().open_can_contain_rectangle(rectangle);
        while (direction != 0 && !node.isExternal() && open_can_contain) {
            node = node.subNode(direction);
            direction = node.direction_of_subNode_for(rectangle);
            open_can_contain = node.getRectangle().open_can_contain_rectangle(rectangle);
        }
        if (node.level == 0) return node;
        // 只能确定node的闭矩形包含rectangle，但需要考虑其他细胞刚好接触到闭矩形的边界，所以向上再提升一级
        // 可能最后找到的叶节点为空，需要提升一级
        return node.parent;
    }

    // 输出一个list，包含当前节点下所有细胞
    // 每个节点包含的细胞 = 当前节点 cells_yes（若为叶节点） & cells_no + 四个子节点包含的细胞
    public List<Cell> get_Cells() {
        List<Cell> list = new ArrayList<>();
        if (this.cells_no != null) list.addAll(this.cells_no);
        if (this.cells_yes != null) list.addAll(this.cells_yes);

        if (isExternal()) return list;
        list.addAll(NWsubnode.get_Cells());
        list.addAll(NEsubnode.get_Cells());
        list.addAll(SWsubnode.get_Cells());
        list.addAll(SEsubnode.get_Cells());
        return list;
    }

    // 判断叶节点
    private boolean isExternal() {
        return (NWsubnode == null && NEsubnode == null && SWsubnode == null && SEsubnode == null);
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    @Override
    public String toString() {
        return "QuadTree cells:" + this.get_Cells().toString();
    }

    public static void main(String[] args) {
        QuadTree tree = new QuadTree(0, new Rectangle(10, 10), null);
        Cell cell1 = new Cell(0, new Position(1, 1), 0.5, 1, Colors.Y);
        Cell cell2 = new Cell(1, new Position(9, 9), 0.5, 1, Colors.Y);
        Cell cell3 = new Cell(2, new Position(8, 8), 0.1, 1, Colors.Y);

        // 插入测试
        tree.insert(cell1);
        tree.insert(cell2);
        tree.insert(cell3);
        System.out.println(tree);
        System.out.println();

        // 删除测试
        tree.delete(cell1);
        System.out.println(tree);
        System.out.println(tree.SWsubnode);
        tree.delete(cell2);
        System.out.println(tree);
        System.out.println();

        // 搜索测试
        System.out.println(tree.search_node_given_rectangle(new Rectangle(7.3, 7.3, 0.1, 0.1)));
        System.out.println(tree.search_move_node_given_rectangle(new Rectangle(7.3, 7.3, 0.1, 0.1)));
    }
}

