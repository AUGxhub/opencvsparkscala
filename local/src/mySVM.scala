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
    // ���ɷ���Ŀ¼
    //    val trainPath = "C:\\Users\\augta\\Desktop\\datasets\\data\\train"
    val trainPath = "C:\\Users\\augta\\Desktop\\datasets\\4categories\\data"
    val categoryPath = getCategories(trainPath)
    //����ÿ���ļ���Ƶ�����ļ�
    //    val dataPath = "C:\\Users\\augta\\Desktop\\datasets\\mysvm"
    val dataPath = "C:\\Users\\augta\\Desktop\\datasets\\4categories\\result"
    val featuresPath = preTextFilter(dataPath)
    //������Ļ����ļ�ÿ����Ϣ���з�����Ϣ�滻
    val numCategory = markLabels(categoryPath, featuresPath, dataPath)
    //ѵ��svmģ��

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
    //spark������������
    sc.stop()
  }

  //������Ŀ¼�µ��ļ�����ͼƬ���������
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

  //����Ŀ¼�е���Ŀ¼
  def loopDir(dir: File): Array[File] = {
    val dirs = dir.listFiles.filter(_.isDirectory)
    dirs
  }

  //����Ŀ¼�������ļ�
  def loopFile(dir: File): Array[File] = {
    val files = dir.listFiles.filter(_.isFile)
    files
  }

  //�Դ�Ƶ�������ı�Ԥ����
  def preTextFilter(featuresPath: String): String = {
    //    val dataPath = "C:\\Users\\augta\\Desktop\\datasets\\mysvm"
    val dataFileDirectory = new File(featuresPath)
    val features = new PrintWriter(featuresPath + "\\" + "features")
    for (d <- loopFile(dataFileDirectory) if d.getName.substring(d.getName.length - 3, d.getName.length) == "xml") {
      //ֻ����xml��β���ļ� ���������ļ����к���
      features.print(d.getName.substring(0, d.getName.length - 4) + " ")
      //ʹ����ͨ�ļ�����д
      val line1 = Source.fromFile(d).mkString.split("data")(1).split(">")(1).split("<")(0)
      //ȥ���ı��еĻ��з��Լ��ո�
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
      /*ʹ��xml�ļ���д
      val txt = XML.loadFile(d)
      println(txt\\"data" )
      */
      //��֤ÿ��ͼ��������һ������
      //      println(line4.split(",").length)
      //������ֵ
      features.println(line4)

    }
    features.close()
    featuresPath + "\\" + "features"
  }

  //�Ѱ�ͼƬ���������� ���������������� �û�Ϊ�������  �����ֵĴ�С��Ŀ¼����˳���
  def markLabels(categoryPath: String, featuresPath: String, dataPath: String): Int = {
    val category = Source.fromFile(categoryPath)
    val categoryLineItr = category.getLines()
    val featuresWithLabel = new PrintWriter(dataPath + "\\" + "featuresWithLabel") //������ǹ��������������ļ�λ��
    var index = 0 //����ı�ǩ labels
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
    //    println(index) //��ʾ�������Ŀ
    index

  }
}
