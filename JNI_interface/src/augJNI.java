/**
 * Created by augta on 2016/5/22.
 */
public class augJNI {
    static {
        System.loadLibrary("augJNI");
    }

    public native static String getDescriptor(String testFilePath, String vocabualryFilePath);

    public static void main(String[] args) {
        //test
        String testPath = "C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\forpredict\\im315.jpg";
        String vocaPath = "C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\forpredict\\vocabulary";
        System.out.println(getDescriptor(testPath, vocaPath));
    }
}
