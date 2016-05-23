import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by augta on 2016/5/5.
 */
object myPCA {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("myPCA")
    conf.setMaster("spark://192.168.79.129:7077")
    conf.set("spark.executor.memory", "6g")
    conf.set("spark.storage.memoryFraction", "0")
    //    conf.setMaster("local")
    conf.setJars(Seq("E:\\workspace\\spark\\ieda\\out\\artifacts\\myPCA_jar\\myPCA.jar"))
    System.setProperty("hadoop.home.dir", "E:\\hadoop")
    val sc = new SparkContext(conf)
    println("all ready")
    //
    val dataPath = "hdfs://192.168.79.129:9000/datasets/featuresWithLabel"
    val rawData = sc.textFile(dataPath)
    rawData.cache()
    val rawArray = rawData.map {
      r =>
        val line = r.split(" ")
        val features = line(1).split(",").map(_.toDouble * 10000)
        Vectors.dense(features)

    }
    //    val array = transposeDouble(rawArray)
    //
    //    val vectors = sc.parallelize(array.map(x => Vectors.dense(x)))
    val matrix = new RowMatrix(rawArray)

    println(matrix.numRows(), matrix.numCols())
    val K = 2 //新的维度
    val pca = matrix.computePrincipalComponents(K)
    //val filePath = "hdfs://192.168.79.129:9000/datasets/pca"
    println(pca.numRows, pca.numCols)

    //
    sc.stop()
  }

  //转置矩阵
  def transposeDouble(xss: Array[Array[Double]]): Array[Array[Double]] =
    for (i <- Array.range(0, xss(0).length)) yield
    for (xs <- xss) yield xs(i)
}
