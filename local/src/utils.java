import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by augta on 2016/4/13.
 */
public class utils {
    public final Mat loadMat(String path) {
        try {
            int cols;
            float[] data;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
                cols = (int) ois.readObject();
                data = (float[]) ois.readObject();
            }
            Mat mat = new Mat(data.length / cols, cols, CvType.CV_32F);
            mat.put(0, 0, data);
            return mat;
        } catch (IOException | ClassNotFoundException | ClassCastException ex) {
            System.err.println("ERROR: Could not load mat from file: " + path);
//            Logger.getLogger(this.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}


