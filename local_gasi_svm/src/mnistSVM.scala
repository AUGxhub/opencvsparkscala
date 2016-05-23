import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.{SparkConf, SparkContext}

/**
 * author :https://github.com/Bekbolatov/spark/blob/463d73323d5f08669d5ae85dc9791b036637c966/mllib/src/main/scala/org/apache/spark/mllib/classification/SVMMultiClass.scala
 * Created by augta on 2016/4/13.
 */
object mnistSVM {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("ministSVM")
    //    conf.setMaster("spark://192.168.79.128:7077")
    conf.setMaster("local")
    conf.setJars(Seq("E:\\workspace\\spark\\ieda\\out\\artifacts\\local_jar\\local.jar"))
    System.setProperty("hadoop.home.dir", "E:\\hadoop")
    val sc = new SparkContext(conf)
    //    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    println("all ready")
    //
    val trainD = MLUtils.loadLibSVMFile(sc, "C:\\Users\\augta\\Desktop\\datasets\\mnist\\mnist")
    val splits = trainD.randomSplit(Array(6, 4), 1)
    val testSet = splits(0)
    val trainSet = splits(1)
    val model = SVMMultiClassOVAWithSGD.train(trainSet, 100)
    val predictionAndLabel = testSet.map(p => (model.predict(p.features), p.label))
    val accuracy = 1.0 * predictionAndLabel.filter(x => x._1 == x._2).count() / testSet.count()
    //    val scoreAndLabels = testD.map { point =>
    //      val score = model.predict(point.features)
    //      (score, point.label)
    //    }
    println(accuracy)
    //    println(scoreAndLabels.take(5).mkString)
    //
    sc.stop()
  }
}
