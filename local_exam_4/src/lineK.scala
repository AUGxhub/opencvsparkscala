import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by augta on 2016/5/19.
 */
object lineK {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("lineK")
    conf.setMaster("local")
    //    conf.setJars(Seq("E:\\workspace\\spark\\ieda\\out\\artifacts\\local_jar\\local.jar"))
    System.setProperty("hadoop.home.dir", "E:\\hadoop")
    val sc = new SparkContext(conf)
    println("all ready")
    //处理数据改标签
    val trainDataPath = "C:\\Users\\augta\\Desktop\\datasets\\二分类数据集\\svmguide1"
    //    val testDataPath = "C:\\Users\\augta\\Desktop\\datasets\\二分类数据集\\a2a\\a2a.t"
    var rawTrain = MLUtils.loadLibSVMFile(sc, trainDataPath)
    //    var rawTest = MLUtils.loadLibSVMFile(sc, testDataPath)
    val raw = rawTrain.map {
      x =>
        var tempLabel = x.label
        if (x.label == "1".toDouble) tempLabel = 0
        if (x.label == "0".toDouble) tempLabel = 1
        LabeledPoint(tempLabel, x.features)
    }
    val splits = raw.randomSplit(Array(7, 3), 1)
    val test = splits(1)
    val train = splits(0)
    //    val test = rawTest.map {
    //      x =>
    //        var tempLabel = x.label
    //        if (x.label == "-1".toDouble) tempLabel = 0
    //        if (x.label == "+1".toDouble) tempLabel = 1
    //        LabeledPoint(tempLabel, x.features)
    //    }
    val svmModule = SVMWithSGD.train(train, 100)
    val numData = test.count()
    println(numData)
    var svmTotalCorrest = 0.0

    val predictionAndLabel = test.map(p => (svmModule.predict(p.features), p.label))
    val accuracy = 1.0 * predictionAndLabel.filter(x => x._1 == x._2).count() / test.count()
    println("svm lineK accruay is : " + accuracy)
    //
    sc.stop()
  }
}
