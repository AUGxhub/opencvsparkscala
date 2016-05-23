import java.io.FileWriter

import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.{SparkConf, SparkContext}

import scala.math._

/**
 * Created by augta on 2016/5/19.
 */
object gasiK2 {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("gasiK")
    conf.setMaster("local")
    //    conf.setJars(Seq("E:\\workspace\\spark\\ieda\\out\\artifacts\\local_jar\\local.jar"))
    System.setProperty("hadoop.home.dir", "E:\\hadoop")
    val sc = new SparkContext(conf)
    println("all ready")
    //
    val trainDataPath = "C:\\Users\\augta\\Desktop\\datasets\\二分类数据集\\a2a\\a2a.t"
    //    val testDataPath = "C:\\Users\\augta\\Desktop\\datasets\\二分类数据集\\a2a\\a2a.t"
    var rawTrain = MLUtils.loadLibSVMFile(sc, trainDataPath)
    //    var rawTest = MLUtils.loadLibSVMFile(sc, testDataPath)
    val raw = rawTrain.map {
      x =>
        var tempLabel = x.label
        if (x.label == "-1".toDouble) tempLabel = 0
        if (x.label == "+1".toDouble) tempLabel = 1
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
    val pramPath = "C:\\Users\\augta\\Desktop\\datasets\\二分类数据集\\a2a\\a2a_prams"
    val pramsResults = new FileWriter(pramPath, true)
    for (i <- 1 to 100) {
      val o = 0.0000001 * pow(10, i)
      val predictionAndLabel = test.map(p => (svmModule.predict(p.features, o), p.label))
      val accuracy = 1.0 * predictionAndLabel.filter(x => x._1 == x._2).count() / test.count()
      pramsResults.write(o + " " + accuracy + "\n")
    }
    pramsResults.close()
    //
    //
    sc.stop()
  }

}
