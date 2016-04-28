import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by augta on 2016/4/7.
 */
object csvSplit {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("datassplit")
    conf.setMaster("local")
    System.setProperty("hadoop.home.dir", "E:\\hadoop")
    val sc = new SparkContext(conf)
    println("all ready")
    //
    val txtPath = "C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\result"
    val trainingData = sc.textFile(txtPath + "\\" + "featuresWithLabel.csv")
    val splits = trainingData.randomSplit(Array(0.1, 0.9), 7)
    val train = splits(1)
    val test = splits(0)
    train.coalesce(1, true).saveAsTextFile(txtPath + "\\" + "trainData")
    //    train.saveAsTextFile(txtPath+"\\"+"trainData")
    test.coalesce(1, true).saveAsTextFile(txtPath + "\\" + "testData")
    //    test.saveAsTextFile(txtPath+"\\"+"testData")
    // verify the output files
    val splitData1 = sc.textFile(txtPath + "\\" + "trainData")
    val splitData2 = sc.textFile(txtPath + "\\" + "testData")
    println("splitData1.first" + splitData1.first())
    println("data1.first" + train.first())
    println()
    println("splitData2.first" + splitData2.first())
    println("data2.first" + test.first())
    //
    sc.stop()
  }
}
