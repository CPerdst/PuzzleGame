package com.l1akr.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LoginJFrame extends javax.swing.JFrame {

    public LoginJFrame() throws IOException {
        setSize(408, 480);
        // 设置标题
        this.setTitle("拼图 登录");
        // 设置界面置顶
        this.setAlwaysOnTop(true);
        // 设置界面居中
        this.setLocationRelativeTo(null);
        // 设置游戏关闭
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        this.add(panel);

        JLabel label1 = new JLabel();
        JLabel label2 = new JLabel();
        panel.add(label1);
        panel.add(label2);

        // 转换大小200*200
        BufferedImage bufferedImage = ImageIO.read(new File("/Users/zwj1/Pictures/IMG_20241123_103909.jpg"));
        BufferedImage scaledBufferedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = scaledBufferedImage.getGraphics();
        graphics.drawImage(bufferedImage, 0, 0, 200, 200, null);
        graphics.dispose();


        label1.setIcon(new ImageIcon(scaledBufferedImage));
        label2.setText("asdaaaaaaaaaaaaaaaaaaa");

        label1.setBounds(0, 0, 200, 200);
        label2.setBounds(0, 0, 200, 20);

        panel.setLayout(null);
        panel.setComponentZOrder(label1, 1);
        panel.setComponentZOrder(label2, 0);

        panel.setBounds(0, 0, this.getWidth(), this.getHeight());

        this.setLayout(null);

        setVisible(true);
    }


}
