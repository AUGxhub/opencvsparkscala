import java.awt.image.BufferedImage
import java.net.URI
import javax.imageio.ImageIO

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.{SparkConf, SparkContext}
import org.opencv.core._
import org.opencv.objdetect.CascadeClassifier
import tools.BufImgToMat

/**
 * Created by augta on 2016/3/24.
 */
object process_and_svm {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("process_image")
    conf.setMaster("spark://192.168.79.129:7077")
    conf.setExecutorEnv("executor-memory", "2048m")
    System.setProperty("hadoop.home.dir", "E:\\hadoop")
    conf.setJars(Seq("E:\\workspace\\spark\\ieda\\out\\artifacts\\process_and_train_svm_jar\\process_and_train_svm.jar"))
    val sc = new SparkContext(conf)
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    println("all ready")
    //read
    //    val aePath = "hdfs://192.168.79.128:9000/temp/bili.jpg"
    val aePath = "hdfs://192.168.79.129:9000/datasets/lfw/Avril_Lavigne/Avril_Lavigne_0001.jpg"
    var matImage = loadImageMatFromFile(aePath)
    //    System.out.println("mat = " + matImage.dump());

    //get features algorithm
    matImage = detectFace(matImage)
    //save
    val dstPath = "hdfs://192.168.79.129:9000/temp/l.jpg"
    if (saveMatToPath(matImage, dstPath)) println("save success")
    sc.stop()
  }

  //load a MatImage from hdfs filesystem and reutrn a BufferedImage
  def loadImageMatFromFile(path: String): Mat = {
    val conf_hdfs = new Configuration()
    val hdfs = FileSystem.get(URI.create(path), conf_hdfs)
    val in = hdfs.open(new Path(path))
    val bImage = ImageIO.read(in)
    val bm = new BufImgToMat(bImage, bImage.getType, CvType.CV_8UC3)
    try {
      bm.getMat
    } finally {
      in.close()
    }

  }

  //save a MatImage to hdfs
  def saveMatToPath(matImage: Mat, path: String): Boolean = {
    val conf_hdfs = new Configuration()
    val hdfs = FileSystem.get(URI.create(path), conf_hdfs)
    val out = hdfs.create(new Path(path))
    val mb = new MatToBufImg(matImage, ".jpg")
    try {
      ImageIO.write(mb.getImage, "jpg", out)
    } finally {
      out.close()
    }
  }

  /*photo process algorithms
  *  1 DetectFace
  */
  //Detect face
  def detectFace(srcImage: Mat): Mat = {
    //    val decPath = "hdfs://192.168.79.128:9000/lbpcascade_frontalface.xml"
    val decPath = "E:\\tool\\spark_runtime\\lib\\opencv\\sources\\data\\lbpcascades\\lbpcascade_frontalface.xml"
    val faceDetector = new CascadeClassifier(decPath)
    val faceDetections = new MatOfRect()
    faceDetector.detectMultiScale(srcImage, faceDetections)
    println("Detected " + faceDetections.toArray().length + " faces")
    // Draw a bounding box around each face.
    for (rect <- faceDetections.toArray()) {
      Core.rectangle(srcImage, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
    }
    srcImage
  }

  //load a img from hdfs filesystem and reutrn a BufferedImage
  def loadImageFromFile(path: String): BufferedImage = {
    val conf_hdfs = new Configuration()
    val hdfs = FileSystem.get(URI.create(path), conf_hdfs)
    val in = hdfs.open(new Path(path))
    try {
      ImageIO.read(in)
    } finally {
      in.close()
    }
  }

  //save a BufferedImage to hdfs
  def saveImageToPath(image: BufferedImage, path: String): Boolean = {
    val conf_hdfs = new Configuration()
    val hdfs = FileSystem.get(URI.create(path), conf_hdfs)
    val out = hdfs.create(new Path(path))
    try {
      ImageIO.write(image, "jpg", out)
    } finally {
      out.close()
    }
  }

}




