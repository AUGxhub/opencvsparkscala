import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}

import scala.io.Source

/**
 * Created by augta on 2016/5/22.
 */
class AUGpredict {
  private var appName = "aug"
  //应用名称
  private var master = "local"
  //master参数
  private var jar_path = ""
  //jar包位置
  private var hadoop_prams = "E:\\hadoop"
  //hadoop文件夹位置
  private var category_path = "C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\forpredict\\category"
  //参考分类目录位置

  //hadoop 文件夹位置
  def predictThisPoint(testFile: String, modelPath: String): String = {
    val conf = new SparkConf().setAppName(appName)
    conf.setMaster(master)
    //    conf.setJars(Seq(jar_path))
    System.setProperty("hadoop.home.dir", hadoop_prams)
    val sc = new SparkContext(conf)
    println("spark is ready")
    val model = SVMMultiClassOVAWithSGD.load(modelPath)
    //处理输入的文件
    val line0 = testFile.substring(1).substring(0, testFile.size - 2)
    val line1 = Vectors.dense(line0.split(",").map(_.toDouble * 10000))
    //类别判断
    val category_result = model.predict(line1)
    //转换类别为类别名
    val category = Source.fromFile(category_path)
    val categoryLineItr = category.getLines()
    var index = 0
    var name = "error"
    for (l1 <- categoryLineItr) {
      if (index == category_result) {

        name = l1.mkString.split(" ")(0)
      }
      index += 1
    }
    sc.stop()
    name
  }
}
