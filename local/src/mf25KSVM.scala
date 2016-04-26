import java.io.{File, PrintWriter}

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.{SparkConf, SparkContext}
import org.opencv.core.Core

import scala.io.Source

/**
 * Created by augta on 2016/4/21.
 */
object mf25KSVM {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("mf25KSVM")
    //conf.setMaster("spark://192.168.79.128:7077")
    conf.setMaster("local")
    conf.setJars(Seq("E:\\workspace\\spark\\ieda\\out\\artifacts\\local_jar\\local.jar"))
    System.setProperty("hadoop.home.dir", "E:\\hadoop")
    val sc = new SparkContext(conf)
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    println("all ready")
    //���ɷ���Ŀ¼
    val dataPath = "C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\mirflickr25k_categories"
    //    val categoryPath = getCategories(dataPath)
    //�������з��༰�����ļ��Ļ����ļ�
    val trainPath = "C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\result"
    //    val featuresPath = preTextFilter(trainPath)
    //��ÿһ��ͼƬ������ǩ ����һ��ͼƬ�ж����ǩ ����һ��ͼƬ������������� ��������ͼƬ ����������ͼƬ ���������ͼƬ��
    //        val featuresWithLabel = markLabels("C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\mirflickr25k_categories\\category_single", "C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\result\\features", trainPath)//test
    //    val featuresWithLabel = markLabels(categoryPath, featuresPath, trainPath)
    //ѵ��svm ������׼ȷ��
    val rawData = sc.textFile(trainPath + "\\" + "featuresWithLabel")
    val trainData = rawData.map {
      r =>
        val line = r.split(" ")
        val label = line(0).toInt
        val features = line(1).split(",").map(_.toDouble)
        // println(features)
        LabeledPoint(label, Vectors.dense(features))
    }
    val pramPath = "C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\prams"
    val pramsResults = new PrintWriter(pramPath)
    for (i <- 1 to 100) {
      val numIterations = i * 10
      val svmModule = SVMMultiClassOVAWithSGD.train(trainData, numIterations)
      val predicionAndLabel = trainData.map(p => (svmModule.predict(p.features), p.label))
      val accuracy = 1.0 * predicionAndLabel.filter(x => x._1 == x._2).count() / trainData.count()
      pramsResults.println("numIterations is " + numIterations + "accuracy is " + accuracy)
    }
    pramsResults.close()

    //�������н���
    sc.stop()
  }

  //����ͼƬ���������
  def getCategories(dataPath: String): String = {
    val category = new PrintWriter(dataPath + "\\" + "category")
    val categoryDir = new File(dataPath)
    for (f <- loopFile(categoryDir) if f.getName.substring(f.getName.length - 3, f.getName.length) == "txt") {
      category.print(f.getName.substring(0, f.getName.length - 4) + " ")
      //ֻ����txt��β���ļ� ���������ļ����к���
      //ȥ�����з� �� ת����һ��
      val line1 = Source.fromFile(f).mkString
      // println(line1)
      val line2 = line1.filter(_ != '\n').map {
        x =>
          if (x == '\r') ' ' else x
      }
      //Ϊ����ǰ�����ǰ��׺
      val numPattern = "\\d+".r //ƥ������һλ������
      val line3 = numPattern.replaceAllIn(line2, { x => "im" + x.toString() + ".jpg" })
      category.println(line3)
    }
    category.close()
    dataPath + "\\" + "category"
  }

  //�Դ�Ƶ�������ı�Ԥ����
  def preTextFilter(featuresPath: String): String = {
    //    val dataPath = "C:\\Users\\augta\\Desktop\\datasets\\mysvm"
    val dataFileDirectory = new File(featuresPath)
    val features = new PrintWriter(featuresPath + "\\" + "features")
    for (d <- loopFile(dataFileDirectory) if d.getName.substring(d.getName.length - 3, d.getName.length) == "xml") {
      //ֻ����xml��β���ļ� ���������ļ����к���
      //println(d.getName)
      features.print(d.getName.substring(0, d.getName.length - 4) + " ")
      //ʹ����ͨ�ļ�����д
      val line1 = Source.fromFile(d).mkString.split("data")(1).split(">")(1).split("<")(0)
      //ȥ���ı��еĻ��з��Լ��ո�
      //���޷���ȡ�������ͼ�����д�Ƶ�ֶ�����Ϊ0
      if (line1 == "") {
        // 1000���� ��ʵ��дʵ��������һ��
        val line4 = "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0"
        features.println(line4)
      } else {
        val line2 = line1.filter(_ != '\n').map {
          x =>
            if (x == ' ') ',' else x
        }.substring(4)
        //ʹ��������ʽȥ���ı���������������
        val nocommaPattern = ",{4}".r
        val line3 = nocommaPattern.replaceAllIn(line2, ",")
        //ʹ��������ʽ ʹ���ı���"0." ��Ϊ "0" ע��scala����ҲҪ���ַ�����ת�塣��������
        val nodotPattern = "0\\.".r
        val line4 = nodotPattern.replaceAllIn(line3, "0")
        //������ֵд���ļ�
        //      println(line4.split(",").length)
        features.println(line4)
      }
    }
    features.close()
    featuresPath + "\\" + "features"
  }

  //����Ŀ¼�������ļ�
  def loopFile(dir: File): Array[File] = {
    val files = dir.listFiles.filter(_.isFile)
    files
  }

  //��ÿһ��ͼƬ���ǩ
  def markLabels(categoryPath: String, featuresPath: String, trainPath: String): String = {
    val filePath = trainPath + "\\" + "featuresWithLabel"
    val featuresWithLabel = new PrintWriter(filePath)
    val category = Source.fromFile(categoryPath)
    val categoryLineItr = category.getLines()
    var index = 0 //����ı�ǩ
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
      index += 1 //���ı�ǩlabels
    }
    featuresWithLabel.close()
    filePath
  }

  //����Ŀ¼�е���Ŀ¼
  def loopDir(dir: File): Array[File] = {
    val dirs = dir.listFiles.filter(_.isDirectory)
    dirs
  }
}
