import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by augta on 2016/5/16.
 */
object getBinarySupportVector {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("getBinarySupportVector")
    //conf.setMaster("spark://192.168.79.128:7077")
    conf.setMaster("local")
    conf.setJars(Seq("E:\\workspace\\spark\\ieda\\out\\artifacts\\local_jar\\local.jar"))
    System.setProperty("hadoop.home.dir", "E:\\hadoop")
    val sc = new SparkContext(conf)
    println("all ready")
    //
    val traiFilePath = "C:\\Users\\augta\\Desktop\\datasets\\train.tsv"
    val rawData = sc.textFile(traiFilePath)
    println(rawData.count())
    val records = rawData.map(_.split("\t"))
    val data = records.map {
      r =>
        val trimmed = r.map(_.replaceAll("\"", ""))
        val label = trimmed(r.size - 1).toInt
        //        println("labe" + label)
        val features = trimmed.slice(4, r.size - 1).map(d => if (d == "?") 0.0 else d.toDouble)
        //        println(features.mkString)

        LabeledPoint(label, Vectors.dense(features))
    }
    data.saveAsTextFile("C:\\Users\\augta\\Desktop\\datasets\\data.tsv")
    data.cache()
    val numData = data.count()
    //tarin a svm module with SGD
    val numIterations = 10
    val svmModule = SVMWithSGD.train(data, numIterations)
    //caculate total acuraccy
    val svmTotalCorrest = data.map {
      point =>
        if (svmModule.predict(point.features, 1) == point.label) 1 else 0
    }.sum
    val svmAccuracy = svmTotalCorrest / numData
    println("svm accruay is : " + svmAccuracy)
    //
    sc.stop()
  }
}
