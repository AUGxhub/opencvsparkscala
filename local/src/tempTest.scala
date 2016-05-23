import org.apache.spark.{SparkConf, SparkContext}
import org.opencv.core.Core

import scala.math._

/**
 * Created by augta on 2016/5/12.
 */
object tempTest {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("tempTest")
    //conf.setMaster("spark://192.168.79.128:7077")
    conf.setMaster("local[8]") //使用8个本地线程
    conf.setJars(Seq("E:\\workspace\\spark\\ieda\\out\\artifacts\\local_jar\\local.jar"))
    System.setProperty("hadoop.home.dir", "E:\\hadoop")
    val sc = new SparkContext(conf)
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    println("all ready")
    println(E)
    //    val str_0= ",0"
    //    var line = "0"
    //    for(i <- 1 until(10)){
    //      line = line+str_0
    //    }
    //    println(line)
    //    val rawData = sc.textFile("C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\result\\featuresWithLabel")
    //    val trainData = rawData.map {
    //      r =>
    //        val line = r.split(" ")
    //        val label = line(0).toInt
    //        val features = line(1).split(",").map(_.toDouble * 10000)
    //        // println(features)
    //        LabeledPoint(label, Vectors.dense(features))
    //    }
    //    println(trainData.count())
    // trainData.saveAsTextFile("C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\result\\labelPoints")
    sc.stop()
  }
}
