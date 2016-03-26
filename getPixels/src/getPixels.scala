import java.awt.image.BufferedImage
import java.net.URI
import javax.imageio.ImageIO

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.{SparkConf, SparkContext}
import org.opencv.core._

/**
 * Created by augta on 2016/3/24.
 */
object getPixels {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("process_image")
    conf.setMaster("spark://192.168.79.128:7077")
    System.setProperty("hadoop.home.dir", "E:\\hadoop");
    conf.setJars(Seq("E:\\workspace\\spark\\ieda\\out\\artifacts\\getPixels_jar\\getPixels.jar"))
    val sc = new SparkContext(conf)
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    println("all ready")
    val aePath = "hdfs://192.168.79.128:9000/bili.jpg"
    val matImage = loadImageMatFromFile(aePath)
    //    System.out.println("mat = " + matImage.dump());
    //save
    val dstPath = "hdfs://192.168.79.128:9000/temp/bili.jpg"
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




