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
    * ��ȡ�ļ����������ļ�·�� ���������ļ��е�·��
    */
    public static List<String> getFileNames(String directory) {
        File fd = null;
        fd = new File(directory);
        File[] files = fd.listFiles();//��ȡ�����ļ�
        List<String> list = new ArrayList<String>();
        for (File file : files) {
            if (file.isDirectory()) {
                //����ǵ�ǰ·���Ǹ��ļ��� ��ѭ����ȡ�ļ��������е��ļ�
                getFileNames(file.getAbsolutePath());
            } else {
                list.add(file.toString());
            }
        }
        return list;
    }

    /*
    * �Ѿ��� MAT ����β��׷�ӵķ�ʽ ��ӵ��ⲿ�����ļ���
    * ���������ڵڶ����Ժ���ʱ ʹ��ǰһ�εĽ�� ���ٳ�������
    */
    public static boolean saveMatrix(String filename, Mat matrix, String matrixName) throws IOException {
        //��Mat����Ԥ���� ȥ���س��ַ�
        String temp = matrix.dump();
        String matStr = null;
        for (int i = 0; i < temp.length(); i++) {
            char item = temp.charAt(i);
            if (item == '\n') i++;
            else {
                matStr += temp.charAt(i);
            }
        }
        matStr = matStr.substring(4);//ȥ����ʼ��ʱ�� null�ַ���
        //matStr ����[1, 0, 0, 0, 0��0; 0, 0, 0, 1, 0, 0]
        //CvType һ��ʹ��CV_8UC3 ����ͨ��RGB
        //���ļ�������
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, true), "UTF-8"));
        bw.write(matrixName + "matrix" + matrix.cols() + "matrix" + matrix.rows() + "matrix" + matStr + "matrix" + "\n\r");
        bw.flush();
        bw.close();
        return false;
    }

    /*
    * ���ⲿ�����ļ��ж���mat����
    */
    public static Mat readMatrix(String filename, String matrixName) {
        Mat matrix = null;
        int rows = 0, cols = 0;//�����������
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
                    line = line.substring(1, line.length() - 1);//ȥͷ��β����[]
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

    //ģ�����
    public static void main(String args[]) {
        String test = "Mat [ " +
                "rows()" + "*" + "cols()" + "*" + "CvType.typeToString(type())" +
                ", isCont=" + "isContinuous()" + ", isSubmat=" + "isSubmatrix()" +
                ", nativeObj=0x" + "Long.toHexString(nativeObj)" +
                ", dataAddr=0x" + "Long.toHexString(dataAddr())" +
                " ]";

    }

    /*
    *��string ת��Ϊmat��ʽ
    * Mat toString ��ʽ��������
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

