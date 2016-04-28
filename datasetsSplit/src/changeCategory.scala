import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.{SparkConf, SparkContext}


/**
 * Created by augta on 2016/4/7.
 */
object changeCategory {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("chageCategory")
    conf.setMaster("local")
    System.setProperty("hadoop.home.dir", "E:\\hadoop")
    val sc = new SparkContext(conf)
    println("all ready")

    //core
    //    val raw = sc.textFile("C:\\Users\\augta\\Desktop\\datasets\\split\\mnist")
    val filePath = "C:\\Users\\augta\\Desktop\\datasets\\split\\mnist"
    var raw = MLUtils.loadLibSVMFile(sc, filePath)
    val finalLabel = raw.map {
      x =>
        var tempLabel = x.label
        if (x.label == "-1".toDouble) tempLabel = 0
        LabeledPoint(tempLabel, x.features)
    }
    MLUtils.saveAsLibSVMFile(finalLabel, "C:\\Users\\augta\\Desktop\\datasets\\split\\mnist2")
    //   finalLabel.saveAsTextFile("C:\\Users\\augta\\Desktop\\datasets\\split\\mnist2")
    //    val result = new PrintWriter("C:\\Users\\augta\\Desktop\\datasets\\split\\mnist2") //≤‚ ‘ ‰≥ˆ
    //    result.println(lines(0) + l1.mkString.substring(temp.size)) //≤‚ ‘ ‰≥ˆ
    //    val original = Source.fromFile(filePath)
    //    val originalyLineItr = original.getLines()
    //    val result = for (l1 <- originalyLineItr) {
    //      var lines = l1.mkString.split(" ")
    //      val temp = lines(0)
    //      if (lines(0) == "-1") lines(0) = "x"
    //      //      result.println(lines(0) +l1.mkString.substring(temp.size))//≤‚ ‘ ‰≥ˆ
    //      lines(0) + l1.mkString.substring(temp.size)
    //    }

    //    println(out.take(10).mkString)
    sc.stop()
  }
}
