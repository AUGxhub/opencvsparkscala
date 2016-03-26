import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;

/**
 * Created by augta on 2016/3/26.
 */


//
// Detects faces in an image, draws boxes around them, and writes the results
// to "faceDetection.png".
//
class DetectFaceDemo {
    public void run() {
        System.out.println("\nRunning DetectFaceDemo");

        // Create a face detector from the cascade file in the resources
        // directory.
        CascadeClassifier faceDetector = new CascadeClassifier("E:\\workspace\\spark\\ieda\\opencv_test\\src\\lbpcascade_frontalface.xml");
        Mat image = Highgui.imread("E:\\workspace\\spark\\ieda\\opencv_test\\src\\lena1.png");
        // Detect faces in the image.
        // MatOfRect is a special container class for Rect.
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections);

        System.out.println("Detected" + faceDetections.toArray().length + " faces");

        // Draw a bounding box around each face.
        for (Rect rect : faceDetections.toArray()) {
            Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
        }

        // Save the visualized detection.
        String filename = "faceDetection.png";
        System.out.println("Writing" + filename);
        Highgui.imwrite(filename, image);
    }
}

public class face_detection {
    public static void main(String[] args) {
        System.out.println("Hello, OpenCV");

        // Load the native library.
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new DetectFaceDemo().run();
    }
}
