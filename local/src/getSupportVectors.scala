import java.io.{FileInputStream, ObjectInputStream}

import org.apache.spark.{SparkConf, SparkContext}
import org.opencv.core.Core

/**
 * Created by augta on 2016/5/5.
 */
object getSupportVectors {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("getSupportVecotors")
    //conf.setMaster("spark://192.168.79.128:7077")
    conf.setMaster("local[8]") //使用8个本地线程
    conf.setJars(Seq("E:\\workspace\\spark\\ieda\\out\\artifacts\\local_jar\\local.jar"))
    System.setProperty("hadoop.home.dir", "E:\\hadoop")
    val sc = new SparkContext(conf)
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    println("all ready")
    //
    //读取模型
    val serial_in = new ObjectInputStream(new FileInputStream("C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\result\\svm_model.obj"))
    val saved_model = serial_in.readObject().asInstanceOf[SVMMultiClassOVAModel]
    println(saved_model.classModelsWithIndex.mkString)
    //
    sc.stop()
  }
}
