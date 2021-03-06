/**
 * Created by augta on 2016/4/13.
 * TODO finish by myself
 */

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}

import org.apache.spark.mllib.classification.{ClassificationModel, SVMModel, SVMWithSGD}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

class SVMMultiClassOVAModel(classModels: Array[SVMModel]) extends ClassificationModel with Serializable {

  val classModelsWithIndex = classModels.zipWithIndex
  val sita = 100

  /**
   * Predict values for the given data set using the model trained.
   *
   * @param testData RDD representing data points to be predicted
   * @return an RDD[Double] where each entry contains the corresponding prediction
   */
  override def predict(testData: RDD[Vector]): RDD[Double] = {

    val localClassModelsWithIndex = classModelsWithIndex

    val bcClassModels = testData.context.broadcast(localClassModelsWithIndex)
    testData.mapPartitions { iter =>
      val w = bcClassModels.value
      iter.map(v => predictPoint(v, w))
    }
  }

  def predictPoint(testData: Vector, models: Array[(SVMModel, Int)]): Double = {
    val localSita = sita
    models
      .map { case (classModel, classNumber) => (classModel.predict(testData, localSita), classNumber) }
      .maxBy { case (score, classNumber) => score }
      ._2
  }

  /**
   * Predict values for a single data point using the model trained.
   *
   * @param testData array representing a single data point
   * @return predicted category from the trained model
   */
  override def predict(testData: Vector): Double = predictPoint(testData, classModelsWithIndex)


}


object SVMMultiClassOVAWithSGD {

  /**
   * Train a Multiclass SVM model given an RDD of (label, features) pairs,
   * using One-vs-Rest method - create one SVMModel per class with SVMWithSGD.
   *
   * @param input RDD of (label, array of features) pairs.
   * @param stepSize Step size to be used for each iteration of Gradient Descent.
   * @param regParam Regularization parameter.
   * @param numIterations Number of iterations of gradient descent to run.
   * @return a SVMModel which has the weights and offset from training.
   */
  def train(input: RDD[LabeledPoint], numIterations: Int, stepSize: Double, regParam: Double): SVMMultiClassOVAModel =
    train(input, numIterations, stepSize, regParam, 1.0)

  /**
   * Train a Multiclass SVM model given an RDD of (label, features) pairs,
   * using One-vs-Rest method - create one SVMModel per class with SVMWithSGD.
   *
   * @param input RDD of (label, array of features) pairs.
   * @param numIterations Number of iterations of gradient descent to run.
   * @return a SVMModel which has the weights and offset from training.
   */
  def train(input: RDD[LabeledPoint], numIterations: Int): SVMMultiClassOVAModel = train(input, numIterations, 1.0, 0.01, 1.0)

  /**
   * Train a Multiclass SVM model given an RDD of (label, features) pairs,
   * using One-vs-Rest method - create one SVMModel per class with SVMWithSGD.
   * data – 训练集，RDD[LabeledPoint]
   * iterations – 迭代次数，默认100
   * step – SGD步长，默认为1.0
   * regParam – 正则化参数，默认0.01T
   * miniBatchFraction – 每一轮迭代，参入训练的样本比例，默认1.0（全部参入）.
   * initialWeights – 初始取值，默认是0向量
   * regType – 正则化类型，默认”l2″
   * @param input RDD of (label, array of features) pairs.
   * @param numIterations Number of iterations of gradient descent to run.
   * @param stepSize Step size to be used for each iteration of gradient descent.
   * @param regParam Regularization parameter.
   * @param miniBatchFraction Fraction of data to be used per iteration.
   */
  def train(
             input: RDD[LabeledPoint],
             numIterations: Int,
             stepSize: Double,
             regParam: Double,
             miniBatchFraction: Double): SVMMultiClassOVAModel = {
    val numClasses = input.map(_.label).max().toInt

    val classModels = (0 until numClasses).map { classId =>

      val inputProjection = input.map { case LabeledPoint(label, features) =>
        LabeledPoint(if (label == classId) 1.0 else 0.0, features)
      }.cache()
      val model = SVMWithSGD.train(inputProjection, numIterations, stepSize, regParam, miniBatchFraction)
      inputProjection.unpersist(false)

      model.clearThreshold()
      model

    }.toArray

    new SVMMultiClassOVAModel(classModels)

  }

  /*
  * @param path where to save module
  * @param module SVMMultiClassOVAModel instance
  */
  def save(path: String, module: SVMMultiClassOVAModel) = {
    val serial_out = new ObjectOutputStream(new FileOutputStream(path))
    serial_out.writeObject(module)
    serial_out.close()
  }

  /*
  * @param path where to laod module
  * @reutrn  a SVMMultiClassOVAModel instance
  */
  def load(path: String): SVMMultiClassOVAModel = {
    val serial_in = new ObjectInputStream(new FileInputStream(path))
    val saved_model = serial_in.readObject().asInstanceOf[SVMMultiClassOVAModel]
    serial_in.close()
    saved_model
  }

}