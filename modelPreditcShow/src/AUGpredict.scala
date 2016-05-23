import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}

import scala.io.Source

/**
 * Created by augta on 2016/5/22.
 */
class AUGpredict {
  private var appName = "aug"
  //Ӧ������
  private var master = "local"
  //master����
  private var jar_path = ""
  //jar��λ��
  private var hadoop_prams = "E:\\hadoop"
  //hadoop�ļ���λ��
  private var category_path = "C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\forpredict\\category"
  //�ο�����Ŀ¼λ��

  //hadoop �ļ���λ��
  def predictThisPoint(testFile: String, modelPath: String): String = {
    val conf = new SparkConf().setAppName(appName)
    conf.setMaster(master)
    //    conf.setJars(Seq(jar_path))
    System.setProperty("hadoop.home.dir", hadoop_prams)
    val sc = new SparkContext(conf)
    println("spark is ready")
    val model = SVMMultiClassOVAWithSGD.load(modelPath)
    //����������ļ�
    val line0 = testFile.substring(1).substring(0, testFile.size - 2)
    val line1 = Vectors.dense(line0.split(",").map(_.toDouble * 10000))
    //����ж�
    val category_result = model.predict(line1)
    //ת�����Ϊ�����
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
