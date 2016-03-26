import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by augta on 2016/1/10.
 */
object sparkpi {
def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("WordCount")
    conf.setMaster("spark://aug:7077")
    System.setProperty("hadoop.home.dir", "E:\\hadoop");
    conf.setJars(Seq("D:\\workspace\\spark\\ieda\\remote\\out\\artifacts\\sparkpi_jar\\sparkpi.jar"))
    val sc = new SparkContext(conf)
    println("all ready")

}


}
