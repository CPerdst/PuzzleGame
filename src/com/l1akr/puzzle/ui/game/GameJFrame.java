package com.l1akr.puzzle.ui.game;

import com.l1akr.puzzle.utility.ImageUtilityImpl;
import com.l1akr.puzzle.utility.SubImageIconList;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class GameJFrame extends javax.swing.JFrame {
    int firstPressedImageIdx = -1;
    int secondPressedImageIdx = -1;

    Boolean isHoldingA = false;

    PuzzleJPanel puzzleJPanel = new PuzzleJPanel();
    SuccessPanel successPanel = new SuccessPanel();

    public GameJFrame() throws IOException {
        // 初始化界面
        init();
        // 初始化菜单
        initMenuBar();
        // 初始化PuzzleJPanel
        puzzleJPanel.addSubImagePanelKeyBindListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
//                System.out.println("mouseReleased");
            }
        });
        this.add(puzzleJPanel);
        // 初始化胜利面板
//        this.add(successPanel);
        // 初始化全局key监听器

        // 显示界面
        setVisible(true);
    }

    private void initMenuBar() {
        // 初始化菜单
        JMenuBar menuBar = new JMenuBar();

        JMenu functionJMenu = new JMenu("功能");
        JMenu aboutJMenu = new JMenu("关于");

        JMenuItem replayItem = new JMenuItem("重新游戏");
        JMenuItem reLoginItem = new JMenuItem("重新登陆");
        JMenuItem closeItem = new JMenuItem("关闭游戏");
        JMenuItem accountItem = new JMenuItem("公众号");

        functionJMenu.add(replayItem);
        functionJMenu.add(reLoginItem);
        functionJMenu.add(closeItem);
        aboutJMenu.add(accountItem);

        menuBar.add(functionJMenu);
        menuBar.add(aboutJMenu);

        this.setJMenuBar(menuBar);
    }

    private void init() {
        // 设置宽高
        setSize(603, 680);
        // 设置标题
        this.setTitle("拼图单机版 v1.0");
        // 设置界面置顶
        this.setAlwaysOnTop(false);
        // 设置界面居中
        this.setLocationRelativeTo(null);
        // 设置游戏关闭
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        // 设置原始布局
        this.setLayout(null);
    }

    void autoPuzzle() {
//        subImageList.images.sort(new Comparator<PuzzleImageIcon>() {
//            @Override
//            public int compare(PuzzleImageIcon o1, PuzzleImageIcon o2) {
//                return o1.idx - o2.idx;
//            }
//        });
//        loadImageFromSubImages();
    }

    private static class SubImageList{
        List<PuzzleImageIcon> images = new ArrayList<>();
        final int rows;
        final int cols;
        int width;
        int height;
        SubImageList() throws IOException {
            this("/Users/zwj1/Pictures/IMG_20241123_103909.jpg", 500, 500, 4, 4);
        }
        SubImageList(String path) throws IOException {
            this(path, 500, 500, 4, 4);
        }
        SubImageList(String path, int scaledWidth, int scaledHeight, int rows, int cols) throws IOException {
            this.rows = rows;
            this.cols = cols;
            BufferedImage image = ImageIO.read(new File(path));
            BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
            Graphics g = scaledImage.getGraphics();
            g.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);
            g.dispose();
            splitImageToList(scaledImage);
        }
        void splitImageToList(BufferedImage image) {
            width = image.getWidth() / cols;
            height = image.getHeight() / rows;
            System.out.println("width: " + width + ", height: " + height);
            System.out.println("width: " + image.getWidth() + ", height: " + image.getHeight());
            int idx = 0;
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    if(row == rows - 1 && col == cols - 1) {
                        // 添加占位图
                        BufferedImage blankBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                        Graphics g = blankBufferedImage.getGraphics();
                        g.setColor(Color.WHITE);
                        g.fillRect(0, 0, width, height);
                        g.dispose();
                        images.add(new PuzzleImageIcon(blankBufferedImage, idx++));
                        break;
                    }
                    images.add(new PuzzleImageIcon(image.getSubimage(col * width, row * height, width, height), idx++));
                }
            }
        }
    }
}
