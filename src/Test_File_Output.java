import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

public class Test_File_Output {
    public static void main(String[] args) {
        try {
            compareTwoFile("./sample\\sample3_out.txt",
                    "./results\\Results_sample3.txt"
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * 对比两个文件的差异，并且输出新文件与旧文件的差异
     *
     * @param oldFile 旧的文件
     * @param newFile 当前新文件
     * @throws IOException 异常
     */
    public  static void compareTwoFile(String oldFile, String newFile) throws IOException {

        File fileOld = new File(oldFile);
        File fileNew = new File(newFile);

        FileInputStream inputStream1 = new FileInputStream(fileOld);
        int size1 = inputStream1.available();
        byte[] buffer1 = new byte[size1];
        inputStream1.read(buffer1);
        inputStream1.close();
        String fileOldStr = new String(buffer1, "UTF-8");

        FileInputStream inputStream2 = new FileInputStream(fileNew);
        int size2 = inputStream2.available();
        byte[] buffer2 = new byte[size2];
        inputStream2.read(buffer2);
        inputStream2.close();
        String fileNewStr = new String(buffer2, "UTF-8");

        if (fileNewStr.equals(fileOldStr)) {
            System.out.println("两个文件完全相同！");
            return;
        }

        String[] oldSplit = fileOldStr.split("\n");//("\\|\\|");
        String[] newSplit = fileNewStr.split("\n");


        if (newSplit != null && oldSplit != null) {
            System.out.println("================Results===================");
            for (int i = 0; i < newSplit.length; i++) {
                if (i < oldSplit.length) {
                    if (!newSplit[i].equals(oldSplit[i])) {
//                        System.out.println((newSplit[i]));
//                        System.out.println(oldSplit[i]);


                        String[] s1 = oldSplit[i].split(" ");
                        String[] s2 = newSplit[i].split(" ");
                        double[] a = new double[2];
                        double[] b = new double[2];
                        a[0] = Double.valueOf(s1[0]);
                        a[1] = Double.valueOf(s1[1]);
                        b[0] = Double.valueOf(s2[0]);
                        b[1] = Double.valueOf(s2[1]);
                        if (Math.abs(a[0]-b[0])<0.001 && Math.abs(a[1]-b[1])<0.001 && s1[2].charAt(0)==s2[2].charAt(0)) {
                            System.out.println("一致");
                        }else
                        {System.out.println((i+1) + ":"+oldSplit[i]+":" + newSplit[i]);}

                    }
                } else {
                    System.out.println(i + ":" + newSplit[i]);
                }
            }
        }
    }
}

