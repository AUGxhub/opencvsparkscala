//import org.opencv.core.Mat;
//import org.opencv.core.MatOfKeyPoint;
//import org.opencv.features2d.DescriptorExtractor;
//import org.opencv.features2d.FeatureDetector;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_features2d;

import java.util.List;


/**
 * Created by augta on 2016/3/29.
 * TODO 检查边界
 */
public class surf_kmeans_bow {

    //各种后面用到的参数
    int wordCount;//词袋类型的总数
    String detetorType;//设置检测的算法
    String decriptorType;
    String matcherType;

    /*
    * 对目进行遍历
    * 计算 每一个图的 特征点 keyPoints 和 描述符 descriptors
    * 训练一个词袋聚类
    */
    protected opencv_core.Mat getBuildVovabulary(
            String dataBasePath,
            List<String> categories,
            FeatureDetector cvdetector,
            DescriptorExtractor extractor,
            int wordCount
    ) {
        opencv_core.Mat allDescriptors = null;
        for (int index = 0; index != categories.size(); ++index) {
            System.out.println("当前处理中的目录路径是 " + categories.get(index));
            String currentCategory = dataBasePath + '\\' + categories.get(index);
            List<String> fileList = null;
            opencv_core.Mat descriptions;
            //TODO 获取目录下所有文件的路径
            for (int fileIndex = 0; fileIndex != fileList.size(); ++fileIndex) {
                String filepath = currentCategory + '\\' + fileIndex;
                opencv_core.Mat image = imread(filepath);
                if (image.empty()) {
                    continue; //有可能是非图片文件 直接跳过
                }
                MatOfKeyPoint keyPoints = null;
                Mat descriptors = null;
                detector.detect(image, keyPoints);
                extractor.compute(image, keyPoints, descriptors);
                if (allDescriptors.empty()) {
                    allDescriptors.create(0, descriptors.cols(), descriptors.type());
                }
                allDescriptors.push_back(descriptors);
            }
            System.out.println("已完成这个目录的处理任务" + categories.get(index));
        }
        System.out.println("生成聚类");
        //注意一下方法调用了javacv 不可靠
        opencv_features2d.BOWKMeansTrainer bowTrainer = new opencv_features2d.BOWKMeansTrainer(wordCount);
        Mat vocabulary = bowTrainer.cluster(allDescriptors);

        return vocabulary;

    }

}
