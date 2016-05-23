import org.apache.spark.mllib.classification.NaiveBayes
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by augta on 2016/5/21.
 */
object mnistBayes {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("mnistBayes")
    //    conf.setMaster("spark://192.168.79.128:7077")
    conf.setMaster("local")
    conf.setJars(Seq("E:\\workspace\\spark\\ieda\\out\\artifacts\\local_jar\\local.jar"))
    System.setProperty("hadoop.home.dir", "E:\\hadoop")
    val sc = new SparkContext(conf)
    //    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    println("all ready")

    val trainD = MLUtils.loadLibSVMFile(sc, "C:\\Users\\augta\\Desktop\\datasets\\mnist\\mnist")
    val testD = MLUtils.loadLibSVMFile(sc, "C:\\Users\\augta\\Desktop\\datasets\\mnist\\mnist.t")
    val model = NaiveBayes.train(trainD)
    val predictionAndLabel = testD.map(p => (model.predict(p.features), p.label))
    val accuracy = 1.0 * predictionAndLabel.filter(x => x._1 == x._2).count() / testD.count()
    val scoreAndLabels = testD.map { point =>
      val score = model.predict(point.features)
      (score, point.label)
    }
    println(accuracy)
    println(scoreAndLabels.take(5).mkString)

    sc.stop()
  }
}
