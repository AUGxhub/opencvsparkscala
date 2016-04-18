import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.{SparkConf, SparkContext}
import org.opencv.core.Core

/**
 * Created by augta on 2016/4/12.
 */
object SVM {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("svm_categorization")
    //    conf.setMaster("spark://192.168.79.128:7077")
    conf.setMaster("local")
    conf.setJars(Seq("E:\\workspace\\spark\\ieda\\out\\artifacts\\SVM_jar\\SVM.jar"))
    System.setProperty("hadoop.home.dir", "E:\\hadoop")
    val sc = new SparkContext(conf)
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    println("all ready")
    //
    //    val traiFilePath = "hdfs://192.168.79.128:9000/datasets/train_noheader.tsv"
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
    //    data.saveAsTextFile("C:\\Users\\augta\\Desktop\\datasets\\data.tsv")
    data.cache()
    val numData = data.count()
    //tarin a svm module with SGD
    val numIterations = 10
    val svmModule = SVMWithSGD.train(data, numIterations)
    //use this module to foretell a point whetcher is it a right point
    val dataPoint = data.first()
    val prediction = svmModule.predict(dataPoint.features)
    //caculate total acuraccy
    val svmTotalCorrest = data.map {
      point =>
        if (svmModule.predict(point.features) == point.label) 1 else 0
    }.sum
    val svmAccuracy = svmTotalCorrest / numData
    println("svm accruay is : " + svmAccuracy)
    //
    sc.stop()

  }
}
