import java.io._

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.{SparkConf, SparkContext}

import scala.io.Source

/**
 * Created by augta on 2016/4/21.
 */
object mf25KSVM100 {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("mf25KSVM")
    //conf.setMaster("spark://192.168.79.128:7077")
    conf.setMaster("local[8]") //使用8个本地线程
    conf.setJars(Seq("E:\\workspace\\spark\\ieda\\out\\artifacts\\local_jar\\local.jar"))
    System.setProperty("hadoop.home.dir", "E:\\hadoop")
    val sc = new SparkContext(conf)
    //System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    println("all ready")
    //生成分类目录
    val dataPath = "C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\mirflickr25k_categories"
    //    val categoryPath = getCategories(dataPath)
    //生成所有分类及所属文件的汇总文件
    val trainPath = "C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\result500"
    //val featuresPath = preTextFilter(trainPath)
    //给每一张图片制作标签 可能一个图片有多个标签 （即一副图片里既有树又有鸟 所以这张图片 既属于树的图片 又属于鸟的图片）
    //val featuresWithLabel = markLabels("C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\mirflickr25k_categories\\category_single", "C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\result500\\features", trainPath)//test
    //    val featuresWithLabel = markLabels(categoryPath, featuresPath, trainPath)
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
    //    //缩放所有的特征值
    //    val scaler = new StandardScaler(withMean = true, withStd = true)
    //    val scaleredRawDate = scaler.fit(tempData) //
    //    val trainData = rawData.map {
    //        r =>
    //          val line = r.split(" ")
    //          val label = line(0).toInt
    //          LabeledPoint(label, scaleredRawDate.transform(Vectors.dense(line(1).split(",").map(_.toDouble ))))
    //      }

    //  trainData.saveAsTextFile("C:\\Users\\augta\\Desktop\\datasets\\trainData.tsv")//暂存以便查看
    val splits = trainData.randomSplit(Array(4.6, 95.4), 1)
    val testSet = splits(1)
    val trainSet = splits(0)
    val pramPath = "C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\prams500"
    val pramsResults = new FileWriter(pramPath, true)
    //    for (i <- 1 to 100) {
    val numIterations = 1000000
    val step = 0.001
    val regParam = 0.07
    val miniBatchFraction = 1.0
    val svmModule = SVMMultiClassOVAWithSGD.train(trainSet, numIterations) //,step, regParam, miniBatchFraction)
    //保存模型
    //    val serial_out = new ObjectOutputStream(new FileOutputStream("C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\result\\svm_model.obj"))
    //    serial_out.writeObject(svmModule)
    //    serial_out.close()
    //读取模型
    //    val serial_in = new ObjectInputStream(new FileInputStream("C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\result\\svm_model.obj"))
    //    val saved_model = serial_in.readObject().asInstanceOf[SVMMultiClassOVAModel]
    //
    val predicionAndLabel = testSet.map(p => (svmModule.predict(p.features), p.label))
    val accuracy = 1.0 * predicionAndLabel.filter(x => x._1 == x._2).count() / trainData.count()
    pramsResults.write("numIterations is " + numIterations
      + " accuracy is " + accuracy
      + " step is " + step
      + " regParam is " + regParam
      + " miniBatchFraction is " + miniBatchFraction + "No scalered train4.60 test95.4 *10000" + "\n")
    //    }
    pramsResults.close()

    //程序运行结束
    sc.stop()
  }

  //计算图片分类的种类
  def getCategories(dataPath: String): String = {
    val category = new PrintWriter(dataPath + "\\" + "category")
    val categoryDir = new File(dataPath)
    for (f <- loopFile(categoryDir) if f.getName.substring(f.getName.length - 3, f.getName.length) == "txt") {
      category.print(f.getName.substring(0, f.getName.length - 4) + " ")
      //只处理txt结尾的文件 对于其他文件进行忽略
      //去除换行符 并 转换成一行
      val line1 = Source.fromFile(f).mkString
      // println(line1)
      val line2 = line1.filter(_ != '\n').map {
        x =>
          if (x == '\r') ' ' else x
      }
      //为数字前后添加前后缀
      val numPattern = "\\d+".r //匹配至少一位的数字
      val line3 = numPattern.replaceAllIn(line2, { x => "im" + x.toString() + ".jpg" })
      category.println(line3)
    }
    category.close()
    dataPath + "\\" + "category"
  }

  //对词频向量的文本预处理
  def preTextFilter(featuresPath: String): String = {
    //    val dataPath = "C:\\Users\\augta\\Desktop\\datasets\\mysvm"
    val dataFileDirectory = new File(featuresPath)
    val features = new PrintWriter(featuresPath + "\\" + "features")
    for (d <- loopFile(dataFileDirectory) if d.getName.substring(d.getName.length - 3, d.getName.length) == "xml") {
      //只处理xml结尾的文件 对于其他文件进行忽略
      //println(d.getName)
      features.print(d.getName.substring(0, d.getName.length - 4) + " ")
      //使用普通文件流读写
      val line1 = Source.fromFile(d).mkString.split("data")(1).split(">")(1).split("<")(0)
      //去除文本中的换行符以及空格
      //对无法提取特征点的图像，所有词频手动设置为0
      if (line1 == "") {
        // 1000个零 与词典中词的种类多少一致
        val line4 = "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0"

        features.println(line4)
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
    features.close()
    featuresPath + "\\" + "features"
  }

  //访问目录中所有文件
  def loopFile(dir: File): Array[File] = {
    val files = dir.listFiles.filter(_.isFile)
    files
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

  //访问目录中的子目录
  def loopDir(dir: File): Array[File] = {
    val dirs = dir.listFiles.filter(_.isDirectory)
    dirs
  }
}
