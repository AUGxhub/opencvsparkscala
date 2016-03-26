import java.awt.image.BufferedImage
import java.net.URI
import javax.imageio.ImageIO
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{Path, FileSystem}
import org.apache.spark.{SparkContext, SparkConf}
import org.opencv.core._
import org.opencv.highgui.Highgui
import org.opencv.objdetect.CascadeClassifier

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
    val conf_hdfs = new Configuration()
    val hdfs = FileSystem.get(URI.create(aePath), conf_hdfs)
    val in = hdfs.open(new Path(aePath))
    val aeImage = ImageIO.read(in)
    val bm = new BufImgToMat(aeImage, aeImage.getType, CvType.CV_8UC3)
    val matImage = bm.getMat
    //    System.out.println("mat = " + matImage.dump());
    //save
    sc.stop()
  }

  def loadImageFromFile(path: String): BufferedImage = {
    val conf_hdfs = new Configuration()
    val hdfs = FileSystem.get(URI.create(path), conf_hdfs)
    val in = hdfs.open(new Path(path))
    ImageIO.read(in)
  }
}




