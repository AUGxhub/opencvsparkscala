import org.apache.spark._

import scala.collection.mutable
import scala.util.Random

/**
  * Created by augta on 2015/12/30.
  */
object SparkPippp {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("WordCount")
    conf.setMaster("local")
    val sc = new SparkContext(conf)
    println("all ready")

  }

}
