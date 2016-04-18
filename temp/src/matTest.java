import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 * Created by augta on 2016/4/5.
 */
public class matTest {
    public static void main(String args[]) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat test = Mat.eye(2, 2, CvType.CV_8UC3);
        String temp = test.dump();
        String temp2 = null;
        for (int i = 0; i < temp.length(); i++) {
            char item = temp.charAt(i);
            if (item == '\n') i++;
            else {
                temp2 += temp.charAt(i);
            }
        }
        String t = "[1,1,1,1,1,1;1,1,1,1,1,1]";
        t = t.substring(1, t.length() - 1);
        String lines[] = t.split(";");
        System.out.println(lines[0]);
        System.out.println(lines[1]);

        char[] ch = lines[0].split(",");
        ch += lines[1].split(",");
        System.out.println(test.rows());
        System.out.println(test.cols());

        double te[][] = new double[test.rows()][test.cols()];
//        for (int i = 0; i < test.rows(); i++) {
//            for (int j = 0; j < test.cols(); j++) {
//                te[i + j][0] = Integer.parseInt(ch[i + j + 0]);
//                te[i + j][1] = Integer.parseInt(ch[i + j + 1]);
//                te[i + j][2] = Integer.parseInt(ch[i + j + 2]);
//            }
//        }
//        test.put(1,1,buf);
        System.out.println(test.dump());

//        System.out.println(test.type());
//        System.out.println(test.toString());
//        System.out.println(test.dump());
    }

    private Mat toMat(String str) {
        Mat result = null;
        int roww, cols = 0;

        return result;
    }

}
