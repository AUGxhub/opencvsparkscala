
/**
 * Created by augta on 2016/3/22.
 */


import java.io.{FileOutputStream, BufferedOutputStream}
import java.net.URL
import org.apache.spark.{SparkContext, SparkConf}
import java.awt.image.BufferedImage
import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import javax.imageio.ImageIO
import java.io.File
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.Matrix
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.feature.StandardScaler


object testFromBokk {
  //process image
  //main
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("testFromBook")
    conf.setMaster("spark://192.168.79.128:7077")
    System.setProperty("hadoop.home.dir", "E:\\hadoop");
    conf.setJars(Seq("E:\\workspace\\spark\\ieda\\out\\artifacts\\img_jar\\img.jar"))
    val sc = new SparkContext(conf)
    println("all ready")
    val path = "hdfs://192.168.79.128:9000/datasets/lfw/*"
    val rdd = sc.wholeTextFiles(path)
    sc.setLogLevel("WARN")
    val first = rdd.first()
    // println(first)
    val files = rdd.map { case (fileName, content) =>
      fileName.replace("file", "")
    }
    //println(files.first())
    //println(files.count())
    val aePath = "hdfs://192.168.79.128:9000/datasets/lfw/AJ_Cook/AJ_Cook_0001.jpg"
    //read hdfs as iostream
    val conf_hdfs = new Configuration()
    val hdfs = FileSystem.get(URI.create(aePath), conf_hdfs)
    val in = hdfs.open(new Path(aePath))
    val aeImage = ImageIO.read(in)
    //    println(aeImage)
    val grayImage = processImage(aeImage, 100, 100)
    //    println(grayImage)
    IOUtils.closeStream(in) //remeber cloase steram after use

    val dst = "hdfs://192.168.79.128:9000/temp/gray.jpg"
    val out = hdfs.create(new Path(dst))
    //    ImageIO.write(grayImage, "jpg", out)
    IOUtils.closeStream(out)
    val pixels = files.map(f => extractPixels(f, 50, 50))
    //    println(pixels.take(10).map(_.take(10).mkString("", ",", ", ...")).mkString("\n"))
    val vectors = pixels.map(p => Vectors.dense(p))
    vectors.setName("image")
    vectors.cache()

    val scaler = new StandardScaler(withMean = true,withStd = false).fit(vectors)
    val scaledVectors = vectors.map(v =>scaler.transform(v))

    val matrix = new RowMatrix(scaledVectors)
    val K =10
    val pc = matrix.computePrincipalComponents(K)
    print(pc)
    sc.stop()
  }

  //get Pixesls
  def getPixelsFromImage(image: BufferedImage): Array[Double] = {
    val width = image.getWidth
    val height = image.getHeight
    val pixels = Array.ofDim[Double](width * height)
    image.getData.getPixels(0,0,width,height,pixels)
  }

  //
  def extractPixels(path: String, width: Int, height: Int): Array[Double] = {
    val raw = loadImageFromFile(path)
    val processed = processImage(raw, width, height)
    getPixelsFromImage(processed)
  }

  //chage color image to gray image
  def processImage(image: BufferedImage, width: Int, height: Int): BufferedImage = {
    val bwImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)
    val g = bwImage.getGraphics
    g.drawImage(image, 0, 0, width, height, null)
    g.dispose()
    bwImage
  }

  def loadImageFromFile(path: String): BufferedImage = {
    val conf_hdfs = new Configuration()
    val hdfs = FileSystem.get(URI.create(path), conf_hdfs)
    val in = hdfs.open(new Path(path))
    ImageIO.read(in)
  }
}
