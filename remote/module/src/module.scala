import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by augta on 2016/1/10.
 */
object module {
def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("WordCount")
    conf.setMaster("spark://aug:7077")
    conf.setJars(Seq("D:\\workspace\\spark\\ieda\\remote\\out\\artifacts\\module_jar\\module.jar"))
    val sc = new SparkContext(conf)
    val rawData = sc.textFile("/root/spark/workspace/train_noheader.tsv")
    val records = rawData.map(line => line.split("\t"))
    println(records.first().toString)
    println("all ready")
}
}
