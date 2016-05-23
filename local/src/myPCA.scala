import java.io.PrintWriter

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by augta on 2016/5/5.
 */
object myPCA {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("myPCA")
    //     conf.setMaster("spark://192.168.79.129:7077")
    conf.setMaster("local")
    //     conf.setJars(Seq("E:\\workspace\\spark\\ieda\\out\\artifacts\\myPCA_jar\\myPCA.jar"))
    System.setProperty("hadoop.home.dir", "E:\\hadoop")
    val sc = new SparkContext(conf)
    println("all ready")
    //
    val dataPath = "C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\result\\featuresWithLabel"
    val rawData = sc.textFile(dataPath)
    val vectors = rawData.map {
      r =>
        val line = r.split(" ")
        val features = line(1).split(",").map(_.toDouble)
        Vectors.dense(features)
    }
    val matrix = new RowMatrix(vectors)
    println(matrix.numCols(), matrix.numRows())
    val K = 2 //新的维度
    val pca = matrix.computePrincipalComponents(K)
    println(pca.numCols, pca.numRows)
    //做投影计算
    val projected = matrix.multiply(pca)
    println(projected.numCols(), projected.numRows())
    //

    val pcaPath = "C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\pca2d"
    val pcaFile = new PrintWriter(pcaPath)
    val pcato = projected.rows.collect()
    pcato.map { x =>
      pcaFile.println(x.toArray(0) + "," + x.toArray(1))
    }
    pcaFile.close()

    sc.stop()
  }


}
