import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by augta on 2016/3/31.
 */
public class utils {
    /*
    * 获取文件夹下所有文件路径 不包括子文件夹的路径
    */
    public static List<String> getFileNames(String directory) {
        File fd = null;
        fd = new File(directory);
        File[] files = fd.listFiles();//获取所有文件
        List<String> list = new ArrayList<String>();
        for (File file : files) {
            if (file.isDirectory()) {
                //如果是当前路径是个文件夹 则循环读取文件夹下所有的文件
                getFileNames(file.getAbsolutePath());
            } else {
                list.add(file.toString());
            }
        }
        return list;
    }

    /*
    * 把矩阵 MAT 按照尾部追加的方式 添加到外部存贮文件中
    * 这样可以在第二次以后处理时 使用前一次的结果 加速程序运行
    */
    public static boolean saveMatrix(String filename, Mat matrix, String matrixName) throws IOException {
        //对Mat进行预处理 去掉回车字符
        String temp = matrix.dump();
        String matStr = null;
        for (int i = 0; i < temp.length(); i++) {
            char item = temp.charAt(i);
            if (item == '\n') i++;
            else {
                matStr += temp.charAt(i);
            }
        }
        matStr = matStr.substring(4);//去掉初始化时的 null字符们
        //matStr 形如[1, 0, 0, 0, 0，0; 0, 0, 0, 1, 0, 0]
        //CvType 一律使用CV_8UC3 即三通道RGB
        //打开文件描述符
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, true), "UTF-8"));
        bw.write(matrixName + "matrix" + matrix.cols() + "matrix" + matrix.rows() + "matrix" + matStr + "matrix" + "\n\r");
        bw.flush();
        bw.close();
        return false;
    }

    /*
    * 从外部存贮文件中读入mat矩阵
    */
    public static Mat readMatrix(String filename, String matrixName) {
        Mat matrix = null;
        int rows = 0, cols = 0;//矩阵的行列数
        String line = "";
        String[] args = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
            while ((line = br.readLine()) != null) {
                args = line.split("matrix");
                if (matrixName == args[0]) {
                    cols = Integer.getInteger(args[1]);
                    rows = Integer.getInteger(args[2]);
                    line = args[3];
                    line = line.substring(1, line.length() - 1);//去头和尾部的[]
                    matrix = new Mat(rows, cols, CvType.CV_8UC3);
                    matrix.put(rows, cols, line);
                }
            }
            br.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return matrix;
    }

    //模块测试
    public static void main(String args[]) {
        String test = "Mat [ " +
                "rows()" + "*" + "cols()" + "*" + "CvType.typeToString(type())" +
                ", isCont=" + "isContinuous()" + ", isSubmat=" + "isSubmatrix()" +
                ", nativeObj=0x" + "Long.toHexString(nativeObj)" +
                ", dataAddr=0x" + "Long.toHexString(dataAddr())" +
                " ]";

    }

    /*
    *把string 转化为mat格式
    * Mat toString 格式化的内容
    * "Mat [ " +
                rows() + "*" + cols() + "*" + CvType.typeToString(type()) +
                ", isCont=" + isContinuous() + ", isSubmat=" + isSubmatrix() +
                ", nativeObj=0x" + Long.toHexString(nativeObj) +
                ", dataAddr=0x" + Long.toHexString(dataAddr()) +
                " ]";
     */
    private Mat toMat(String str) {
        Mat result = null;
        int roww, cols = 0;

        return result;
    }
}

