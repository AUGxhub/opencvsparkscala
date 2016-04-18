package algorithm;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import java.util.List;

import static org.opencv.highgui.Highgui.imread;

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
    protected Mat getBuildVovabulary(
            String dataBasePath,
            List<String> categories,
            FeatureDetector detector,
            DescriptorExtractor extractor,
            int wordCount
    ) {
        Mat allDescriptors = null;
        for (int index = 0; index != categories.size(); ++index) {
            System.out.println("当前处理中的目录路径是 " + categories.get(index));
            String currentCategory = dataBasePath + '\\' + categories.get(index);
            List<String> fileList = null;
            Mat descriptions;
            //TODO 获取目录下所有文件的路径
            for (int fileIndex = 0; fileIndex != fileList.size(); ++fileIndex) {
                String filepath = currentCategory + '\\' + fileIndex;
                Mat image = imread(filepath);
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

        Mat vocabulary = null;
        return vocabulary;

    }



}
