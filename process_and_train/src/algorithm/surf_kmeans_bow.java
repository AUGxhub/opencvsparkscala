package algorithm;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import java.util.List;

import static org.opencv.highgui.Highgui.imread;

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
    protected Mat getBuildVovabulary(
            String dataBasePath,
            List<String> categories,
            FeatureDetector detector,
            DescriptorExtractor extractor,
            int wordCount
    ) {
        Mat allDescriptors = null;
        for (int index = 0; index != categories.size(); ++index) {
            System.out.println("��ǰ�����е�Ŀ¼·���� " + categories.get(index));
            String currentCategory = dataBasePath + '\\' + categories.get(index);
            List<String> fileList = null;
            Mat descriptions;
            //TODO ��ȡĿ¼�������ļ���·��
            for (int fileIndex = 0; fileIndex != fileList.size(); ++fileIndex) {
                String filepath = currentCategory + '\\' + fileIndex;
                Mat image = imread(filepath);
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

        Mat vocabulary = null;
        return vocabulary;

    }



}
