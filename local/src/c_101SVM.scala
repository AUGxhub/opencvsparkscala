import java.io.{File, FileWriter, PrintWriter}

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.{SparkConf, SparkContext}

import scala.io.Source

/**
 * Created by augta on 2016/5/15.
 */
object c_101SVM {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("c_101SVM")
    //conf.setMaster("spark://192.168.79.128:7077")
    conf.setMaster("local[8]") //使用8个本地线程
    //conf.setJars(Seq("E:\\workspace\\spark\\ieda\\out\\artifacts\\local_jar\\local.jar"))
    System.setProperty("hadoop.home.dir", "E:\\hadoop")
    val sc = new SparkContext(conf)
    //System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    println("all ready")
    //
    val core = 10000 //聚类中心的数目
    //生成分类目录
    val dataPath = "C:\\Users\\augta\\Desktop\\datasets\\101category\\result10000"
    val categoryPath = getCategories(dataPath)
    //生成所有分类及所属文件的汇总文件
    val trainPath = "C:\\Users\\augta\\Desktop\\datasets\\101category\\result10000\\result"
    val featuresPath = preTextFilter(trainPath, core)
    //给每一张图片制作标签 可能一个图片有多个标签 （即一副图片里既有树又有鸟 所以这张图片 既属于树的图片 又属于鸟的图片）
    //val featuresWithLabel = markLabels(dataPath + "\\" + "category", trainPath + "\\" + "features", trainPath) //test
    val featuresWithLabel = markLabels(categoryPath, featuresPath, trainPath)
    //训练svm 并计算准确率
    val rawData = sc.textFile(trainPath + "\\" + "featuresWithLabel")
    val trainData = rawData.map {
      r =>
        val line = r.split(" ")
        val label = line(0).toInt
        val features = line(1).split(",").map(_.toDouble * 10000)
        // println(features)
        LabeledPoint(label, Vectors.dense(features))
    }
    val category_num = 2
    val testpart = 89.5
    val trainpart = 10.5
    val splits = trainData.randomSplit(Array(testpart, trainpart), 1)
    val testSet = splits(0)
    val trainSet = splits(1)
    val pramPath = dataPath + "\\10000prams"
    val pramsResults = new FileWriter(pramPath, true)
    //    for (i <- 1 to 100) {
    val numIterations = 100
    val step = 0.1
    val regParam = 0.07
    val miniBatchFraction = 1.0
    val svmModule = SVMMultiClassOVAWithSGD.train(trainSet, numIterations, step, regParam, miniBatchFraction)
    //保存模型
    //    SVMMultiClassOVAWithSGD.save("C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\result\\svm_model.obj",svmModule)
    //读取模型
    //    val saved_model = SVMMultiClassOVAWithSGD.load("C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\result\\svm_model.obj")

    val predicionAndLabel = testSet.map(p => (svmModule.predict(p.features), p.label))
    val accuracy = 1.0 * predicionAndLabel.filter(x => x._1 == x._2).count() / trainData.count()
    pramsResults.write("numIterations is " + numIterations
      + " accuracy is " + accuracy
      + " core " + core
      + " step is " + step
      + " regParam is " + regParam
      + " miniBatchFraction is " + miniBatchFraction
      + " train" + trainpart
      + " test" + testpart
      + " *10000"
      + " " + category_num + " category"
      + " SVMWithSGD" ++ "\n")
    //    }
    pramsResults.close()

    //
    sc.stop()
  }

  //计算图片分类的种类
  def getCategories(dataPath: String): String = {
    val category = new PrintWriter(dataPath + "\\" + "category")
    val categoryDir = new File(dataPath + "\\" + "result")
    for (d <- loopDir(categoryDir)) {
      val category_name = d.getName
      val current_category = dataPath + "\\" + "result" + "\\" + category_name
      println("当前目录" + current_category)
      category.print(category_name + " ")
      for (f <- loopFile(new File(current_category)) if f.getName.substring(f.getName.length - 3, f.getName.length) == "xml") {
        println("处理" + f.getName)
        category.print(category_name + f.getName.substring(0, f.getName.length - 4) + " ")
      }
      category.println()
    }
    category.close()
    dataPath + "\\" + "category"
  }

  //访问目录中所有文件
  def loopFile(dir: File): Array[File] = {
    val files = dir.listFiles.filter(_.isFile)
    files
  }

  //访问目录中的子目录
  def loopDir(dir: File): Array[File] = {
    val dirs = dir.listFiles.filter(_.isDirectory)
    dirs
  }

  //对词频向量的文本预处理
  def preTextFilter(featuresPath: String, core: Int): String = {
    //    val dataPath = "C:\\Users\\augta\\Desktop\\datasets\\mysvm"
    val dataFileDirectory = new File(featuresPath)
    val features = new PrintWriter(featuresPath + "\\" + "features")
    for (d <- loopDir(dataFileDirectory)) {
      val category_name = d.getName
      println("正在处理" + category_name)
      for (f <- loopFile((new File(featuresPath + "\\" + category_name))) if f.getName.substring(f.getName.length - 3, f.getName.length) == "xml") {
        println("处理文件" + f.getName)
        //只处理xml结尾的文件 对于其他文件进行忽略
        //println(d.getName)
        features.print(d.getName + f.getName.substring(0, f.getName.length - 4) + " ")
        //使用普通文件流读写
        val line1 = Source.fromFile(f).mkString.split("data")(1).split(">")(1).split("<")(0)
        //去除文本中的换行符以及空格
        //对无法提取特征点的图像，所有词频手动设置为0
        if (line1 == "") {
          // 10000个零 与词典中词的种类多少一致
          //   val line4 = "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0"
          val str_0 = ",0"
          var line4 = "0"
          for (i <- 1 until (core)) {
            line4 = line4 + str_0
          }
          features.println(line4.mkString)
        } else {
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
          //向量的值写入文件
          //      println(line4.split(",").length)
          features.println(line4)
        }
      }
    }
    features.close()
    featuresPath + "\\" + "features"
  }

  //对每一个图片打标签
  def markLabels(categoryPath: String, featuresPath: String, trainPath: String): String = {
    val filePath = trainPath + "\\" + "featuresWithLabel"
    val featuresWithLabel = new PrintWriter(filePath)
    val category = Source.fromFile(categoryPath)
    val categoryLineItr = category.getLines()
    var index = 0 //种类的标签
    for (l1 <- categoryLineItr) {
      var categorys = l1.mkString.split(" ")
      val feature = Source.fromFile(featuresPath)
      val featuresLineItr = feature.getLines()
      println("正在处理" + l1.mkString.split(" ")(0) + "这是第" + index + "个")
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
    filePath
  }
}
