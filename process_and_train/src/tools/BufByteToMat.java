package tools;

import org.opencv.core.Mat;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Created by augta on 2016/3/26.
 * todo complete change ByteBuffer to Mat
 */
public class BufByteToMat {
    BufferedImage original;
    int itype;
    int mtype;

    /**
     * @param image
     * @param imgType bufferedImage'type eg: BufferedImage.TYPE_3BYTE_BGR
     * @param matType mat'type eg: CvType.CV_8UC3
     */
    public BufByteToMat(BufferedImage image, int imgType, int matType) {
        original = image;
        itype = imgType;
        mtype = matType;
    }

    public Mat getMat() {
        if (original == null) {
            throw new IllegalArgumentException("original == null");
        }

        // Don't convert if it already has correct type
        if (original.getType() != itype) {

            // Create a buffered image
            BufferedImage image = new BufferedImage(original.getWidth(),
                    original.getHeight(), itype);

            // Draw the image onto the new buffer
            Graphics2D g = image.createGraphics();
            try {
                g.setComposite(AlphaComposite.Src);
                g.drawImage(original, 0, 0, null);
            } finally {
                g.dispose();
            }
        }

        byte[] pixels = ((DataBufferByte) original.getRaster().getDataBuffer()).getData();
        Mat mat = Mat.eye(original.getHeight(), original.getWidth(), mtype);
        mat.put(0, 0, pixels);
        return mat;
    }
}
