import java.io.{FileInputStream, ObjectInputStream}

import org.apache.spark.{SparkConf, SparkContext}
import org.opencv.core.Core

/**
 * Created by augta on 2016/4/26.
 */
object predictFromSVMModule {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("predictFromSVMModule")
    //conf.setMaster("spark://192.168.79.128:7077")
    conf.setMaster("local")
    conf.setJars(Seq("E:\\workspace\\spark\\ieda\\out\\artifacts\\local_jar\\local.jar"))
    System.setProperty("hadoop.home.dir", "E:\\hadoop")
    val sc = new SparkContext(conf)
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    println("all ready")
    //读取模型的参数
    val serial_in = new ObjectInputStream(new FileInputStream("C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\result\\svm_model.obj"))
    val saved_model = serial_in.readObject().asInstanceOf[SVMMultiClassOVAModel]
    //预测一张图是哪一类？
    val toDetectPicPath = "im28.jpg"
    val categoryPath = "C:\\Users\\augta\\Desktop\\datasets\\mirflickr25k\\result\\features"

    //
    sc.stop()
  }
}
