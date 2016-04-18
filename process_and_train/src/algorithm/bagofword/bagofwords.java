package algorithm;

import org.opencv.core.Mat;
import org.opencv.features2d.Features2d;

import java.util.List;
import java.util.Vector;

/**
 * Created by augta on 2016/3/29.
 * 实现bowtrainer方法
 * TODO 对边界进行检查 特列进行特殊处理
 */
public class bagofwords extends Features2d {
    int size = 0;
   Vector<Mat> descriptors = null;

    //构造方法 传入必要参数
    public bagofwords(Vector<Mat> work_descriptors) {
        descriptors = work_descriptors;
    }

    public void add(Mat _descriptors) {

        if (!_descriptors.empty()) {
            size += _descriptors.rows();
        } else {
            size = _descriptors.rows();
        }
        descriptors.push_back(_descriptors);
    }

    public Mat getDescriptors() {
        return descriptors;
    }

    public int descripotorsCount() {
        return descriptors.empty() ? 0 : size;
    }

    public void clear() {
        //todo 查原clear实现方法

    }
    //
    public void  BOWKMeansTrainer() {

    }
    public  Mat cluster() {
        int descCount = 0;
        for( long i = 0; i < descriptors.size(); i++ )
            descCount += descriptors[i].rows;

        Mat mergedDescriptors( descCount, descriptors[0].cols, descriptors[0].type() );
        for( long i = 0, start = 0; i < descriptors.size(); i++ )
        {
            Mat submut = mergedDescriptors.rowRange((int)start, (int)(start + descriptors[i].rows));
            descriptors[i].copyTo(submut);
            start += descriptors[i].rows;
        }
        return cluster( mergedDescriptors );
    }
}
