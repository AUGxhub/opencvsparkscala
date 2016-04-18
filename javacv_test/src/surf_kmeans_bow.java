//import org.opencv.core.Mat;
//import org.opencv.core.MatOfKeyPoint;
//import org.opencv.features2d.DescriptorExtractor;
//import org.opencv.features2d.FeatureDetector;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_features2d;

import java.util.List;


/**
 * Created by augta on 2016/3/29.
 * TODO ���߽�
 */
public class surf_kmeans_bow {

    //���ֺ����õ��Ĳ���
    int wordCount;//�ʴ����͵�����
    String detetorType;//���ü����㷨
    String decriptorType;
    String matcherType;

    /*
    * ��Ŀ���б���
    * ���� ÿһ��ͼ�� ������ keyPoints �� ������ descriptors
    * ѵ��һ���ʴ�����
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
            System.out.println("��ǰ�����е�Ŀ¼·���� " + categories.get(index));
            String currentCategory = dataBasePath + '\\' + categories.get(index);
            List<String> fileList = null;
            opencv_core.Mat descriptions;
            //TODO ��ȡĿ¼�������ļ���·��
            for (int fileIndex = 0; fileIndex != fileList.size(); ++fileIndex) {
                String filepath = currentCategory + '\\' + fileIndex;
                opencv_core.Mat image = imread(filepath);
                if (image.empty()) {
                    continue; //�п����Ƿ�ͼƬ�ļ� ֱ������
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
            System.out.println("��������Ŀ¼�Ĵ�������" + categories.get(index));
        }
        System.out.println("���ɾ���");
        //ע��һ�·���������javacv ���ɿ�
        opencv_features2d.BOWKMeansTrainer bowTrainer = new opencv_features2d.BOWKMeansTrainer(wordCount);
        Mat vocabulary = bowTrainer.cluster(allDescriptors);

        return vocabulary;

    }

}
