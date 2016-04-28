import java.io.{File, PrintWriter}

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.{SparkConf, SparkContext}
import org.opencv.core.Core

import scala.io.Source

/**
 * Created by augta on 2016/4/13.
 */
object mySVM {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("mySVM")
    //conf.setMaster("spark://192.168.79.128:7077")
    conf.setMaster("local")
    conf.setJars(Seq("E:\\workspace\\spark\\ieda\\out\\artifacts\\local_jar\\local.jar"))
    System.setProperty("hadoop.home.dir", "E:\\hadoop")
    val sc = new SparkContext(conf)
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    println("all ready")
    // 生成分类目录
    //    val trainPath = "C:\\Users\\augta\\Desktop\\datasets\\data\\train"
    val trainPath = "C:\\Users\\augta\\Desktop\\datasets\\4categories\\data"
    val categoryPath = getCategories(trainPath)
    //生成每个文件词频汇总文件
    //    val dataPath = "C:\\Users\\augta\\Desktop\\datasets\\mysvm"
    val dataPath = "C:\\Users\\augta\\Desktop\\datasets\\4categories\\result"
    val featuresPath = preTextFilter(dataPath)
    //对上面的汇总文件每条信息进行分类信息替换
    val numCategory = markLabels(categoryPath, featuresPath, dataPath)
    //训练svm模型

    val rawData = sc.textFile(dataPath + "\\" + "featuresWithLabel")
    val trainData = rawData.map {
      r =>
        val line = r.split(" ")
        val label = line(0).toInt
        val features = line(1).split(",").map(_.toDouble)
        // println(features)
        LabeledPoint(label, Vectors.dense(features))
    }
    val numIterations = 200
    val svmModule = SVMMultiClassOVAWithSGD.train(trainData, numIterations)
    val predicionAndLabel = trainData.map(p => (svmModule.predict(p.features), p.label))
    val accuracy = 1.0 * predicionAndLabel.filter(x => x._1 == x._2).count() / trainData.count()
    println(accuracy)
    //spark任务正常结束
    sc.stop()
  }

  //根据子目录下的文件计算图片分类的种类
  def getCategories(trainPath: String): String = {
    //    val trainPath = "C:\\Users\\augta\\Desktop\\datasets\\data\\train"
    val category = new PrintWriter(trainPath + "\\" + "category")
    val trainDir = new File(trainPath)
    for (d <- loopDir(trainDir)) {
      category.print(d.getName + " ")
      for (f <- loopFile(d)) {
        category.print(f.getName + " ")
      }
      category.println()
    }
    category.close()
    trainDir + "\\" + "category"
  }

  //访问目录中的子目录
  def loopDir(dir: File): Array[File] = {
    val dirs = dir.listFiles.filter(_.isDirectory)
    dirs
  }

  //访问目录中所有文件
  def loopFile(dir: File): Array[File] = {
    val files = dir.listFiles.filter(_.isFile)
    files
  }

  //对词频向量的文本预处理
  def preTextFilter(featuresPath: String): String = {
    //    val dataPath = "C:\\Users\\augta\\Desktop\\datasets\\mysvm"
    val dataFileDirectory = new File(featuresPath)
    val features = new PrintWriter(featuresPath + "\\" + "features")
    for (d <- loopFile(dataFileDirectory) if d.getName.substring(d.getName.length - 3, d.getName.length) == "xml") {
      //只处理xml结尾的文件 对于其他文件进行忽略
      features.print(d.getName.substring(0, d.getName.length - 4) + " ")
      //使用普通文件流读写
      val line1 = Source.fromFile(d).mkString.split("data")(1).split(">")(1).split("<")(0)
      //去除文本中的换行符以及空格
      val line2 = line1.filter(_ != '\n').map {
        x =>
          if (x == ' ') ',' else x
      }.substring(4)
      //使用正则表达式去除文本中连续三个逗号
      val nocommaPattern = ",{4}".r
      val line3 = nocommaPattern.replaceAllIn(line2, ",")
      //使用正则表达式 使得文本中"0." 变为 "0" 注意scala本身也要对字符进行转义。。。。。
      val nodotPattern = "0\\.".r
      val line4 = nodotPattern.replaceAllIn(line3, "0")
      /*使用xml文件读写
      val txt = XML.loadFile(d)
      println(txt\\"data" )
      */
      //验证每个图的向量是一样长的
      //      println(line4.split(",").length)
      //向量的值
      features.println(line4)

    }
    features.close()
    featuresPath + "\\" + "features"
  }

  //把按图片名字命名的 特征向量行名字子 置换为数字类别  其数字的大小由目录排列顺序决
  def markLabels(categoryPath: String, featuresPath: String, dataPath: String): Int = {
    val category = Source.fromFile(categoryPath)
    val categoryLineItr = category.getLines()
    val featuresWithLabel = new PrintWriter(dataPath + "\\" + "featuresWithLabel") //存贮标记过类别的特征向量文件位置
    var index = 0 //种类的标签 labels
    for (l1 <- categoryLineItr) {
      var categorys = l1.mkString.split(" ")
      val feature = Source.fromFile(featuresPath)
      val featuresLineItr = feature.getLines()
      for (l2 <- featuresLineItr) {
        var features = l2.mkString.split(" ")
        for (i <- 1 until categorys.length) {
          if (features(0) == categorys(i)) {
            features(0) = index.toString + " "
            featuresWithLabel.println(features.mkString)
          }
        }
      }
      index += 1 //类别的标签labels
    }
    featuresWithLabel.close()
    //    println(index) //显示种类的数目
    index

  }
}
