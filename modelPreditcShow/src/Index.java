import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by augta on 2016/5/22.
 */
public class Index extends JFrame {
    public Index() {
        JPanel top = new JPanel(new GridLayout(3, 2));
        final JPanel middle = new JPanel();
        JPanel bottom = new JPanel(new GridLayout(1, 2));
        final JFileChooser fileChooser = new JFileChooser();
        JButton select_btn = new JButton("选择文件");
        JButton predict_btn = new JButton("预测类别");
        JButton vocabulary_btn = new JButton("选择词典");
        JButton model_btn = new JButton("选择模型");

        final JTextField vocabularyPath_txt = new JTextField();
        final JTextField filePath_txt = new JTextField();
        final JTextField modelPath_txt = new JTextField();
        final JTextField predict_txt = new JTextField();
        final Component mycomponent = this.getContentPane();
        final JLabel finalPic = new JLabel();
        final File[] file = new File[3];//测试图片文件 和 词典文件 和 分类模型位置

        filePath_txt.setSize(400, 20);
        predict_txt.setSize(400, 20);
        vocabularyPath_txt.setSize(400, 20);
        top.add(select_btn);
        top.add(filePath_txt);
        top.add(vocabulary_btn);
        top.add(vocabularyPath_txt);
        top.add(model_btn);
        top.add(modelPath_txt);
        bottom.add(predict_btn);
        bottom.add(predict_txt);
        middle.add(finalPic);

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(top, BorderLayout.PAGE_START);
        this.getContentPane().add(top, BorderLayout.PAGE_START);
        this.getContentPane().add(middle, BorderLayout.CENTER);
        this.getContentPane().add(bottom, BorderLayout.PAGE_END);

        select_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showOpenDialog(mycomponent);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    file[0] = fileChooser.getSelectedFile();
                    finalPic.setIcon(new ImageIcon(file[0].getPath()));
                    filePath_txt.setText(file[0].getPath());
//                    System.out.println(file[0].getPath());
                }
            }
        });
        vocabulary_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showOpenDialog(mycomponent);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    file[1] = fileChooser.getSelectedFile();
                    vocabularyPath_txt.setText(file[1].getPath());
//                    System.out.println(file[0].getPath());
                }
            }
        });
        model_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showOpenDialog(mycomponent);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    file[2] = fileChooser.getSelectedFile();
                    modelPath_txt.setText(file[2].getPath());
//                    System.out.println(file[0].getPath());
                }
            }
        });
        predict_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String feature = augJNI.getDescriptor(file[0].getPath(), file[1].getPath());
                AUGpredict myAUGpredict = new AUGpredict();
                String result = myAUGpredict.predictThisPoint(feature, file[2].getPath());
                predict_txt.setText(result);
                System.out.println(result);
            }
        });
        this.setSize(800, 700);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("模型测试");
        this.setVisible(true);
    }

    public static void main(String[] args) {
        Index showImage = new Index();
    }
}
