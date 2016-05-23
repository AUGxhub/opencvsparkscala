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
    val filePath = "C:\\Users\\augta\\Desktop\\datasets\\二分类数据集\\a2a\\a2a"
    var raw = MLUtils.loadLibSVMFile(sc, filePath)
    val finalLabel = raw.map {
      x =>
        var tempLabel = x.label
        if (x.label == "-1".toDouble) tempLabel = 0
        if (x.label == "+1".toDouble) tempLabel = 1
        LabeledPoint(tempLabel, x.features)
    }
    MLUtils.saveAsLibSVMFile(finalLabel, "C:\\Users\\augta\\Desktop\\datasets\\二分类数据集\\a2a\\a2a.01category")

    sc.stop()
  }
}
