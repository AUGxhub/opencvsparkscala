import org.apache.spark.{SparkConf, SparkContext}


/**
 * Created by augta on 2016/4/7.
 */
object changeCategory {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("chageCategory")
    conf.setMaster("local")
    System.setProperty("hadoop.home.dir", "E:\\hadoop")
    val sc = new SparkContext(conf)
    println("all ready")

    //core
    val raw = sc.textFile("C:\\Users\\augta\\Desktop\\mnist")
    var temp = raw.zipWithIndex()

    //    temp.filter(_._1.charAt(0) =='5')
    //change '5' to the char you want to repalce from
    //change 'x' to the char you want to reform into
    val out = raw.map(
      x =>
        if (x.charAt(0) == '5') {
          var txt = x
          txt = 'x' + txt.substring(1)
          txt
        } else
          x
    )
    //    println(out.take(10).mkString)
    out.saveAsTextFile("C:\\Users\\augta\\Desktop\\mnist2")
    sc.stop()
  }
}
