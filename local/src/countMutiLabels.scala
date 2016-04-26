import java.io.PrintWriter

import org.apache.spark.{SparkConf, SparkContext}
import org.opencv.core.Core

import scala.io.Source

/**
 * Created by augta on 2016/4/25.
 */
object countMutiLabels {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("countMutiLabels")
    //conf.setMaster("spark://192.168.79.128:7077")
    conf.setMaster("local")
    //conf.setJars(Seq("E:\\workspace\\spark\\ieda\\out\\artifacts\\local_jar\\local.jar"))
    System.setProperty("hadoop.home.dir", "E:\\hadoop")
    val sc = new SparkContext(conf)
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    println("all ready")
    //
    val categoryPath = "C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\mirflickr25k_categories\\category"
    val textFile = sc.textFile(categoryPath)
    val counts = textFile.flatMap(line => line.split(" "))
      .map(word => (word, 1))
      .reduceByKey(_ + _).filter(x => x._2 > 1)
    counts.saveAsTextFile("C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\mirflickr25k_categories\\counts")

    val singleLabel = new PrintWriter(categoryPath + "_single")
    val categoryLineItr = Source.fromFile(categoryPath).getLines()
    for (l2 <- categoryLineItr) {
      val line1 = l2.mkString
      var line2 = line1
      for ((s, c) <- counts.toLocalIterator) {
        println("throw..." + s)
        val strPattern = (s + " ").r
        line2 = strPattern.replaceAllIn(line2, "")
      }
      singleLabel.println(line2)
    }
    singleLabel.close()
    //验证是否已去除多标签项
    val temp = sc.textFile(categoryPath + "_single")
    val counts_ = temp.flatMap(line => line.split(" "))
      .map(word => (word, 1))
      .reduceByKey(_ + _).filter(x => x._2 > 1)
    //    counts_.saveAsTextFile(categoryPath + "counts_")
    println(counts_)
    if (counts_.isEmpty()) println("succesfully remove all mutilabel item")
    //
    sc.stop()
  }
}
