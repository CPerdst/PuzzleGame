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

    GameJPanel gameJPanel = new GameJPanel();
    SuccessPanel successPanel = new SuccessPanel();

    public GameJFrame() throws IOException {
        // 初始化界面
        init();
        // 初始化菜单
        initMenuBar();
        // 初始化GameJPanel
        this.add(gameJPanel);
        // 初始化胜利面板
        this.add(successPanel);
        // 初始化全局key监听器
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyTyped(e);
                if(e.getKeyChar() == 'a') {
                    if(!isHoldingA){
                        isHoldingA = true;
                        // 显示提示
                        showTips();
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyTyped(e);
                if(e.getKeyChar() == 'a') {
                    if(isHoldingA){
                        isHoldingA = false;
                        // 显示提示
                        closeTips();
                    }
                } else if(e.getKeyChar() == 'q') {
                    autoPuzzle();
                }
            }
        });
        // 显示界面
        setVisible(true);
    }

    private void firstLoadImage(String filename) throws IOException {
        // 禁用布局管理器，使用绝对定位
        this.setLayout(null);

        loadImageFromSubImages();
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
    }

    void loadImageFromSubImages() {
        System.out.println("loadImageFromSubImages");
        for(Component component : this.getContentPane().getComponents()) {
            if(component instanceof JPanel && "SubImagesPanel".equals(((JPanel) component).getName())) {
                this.remove((Component) component);
                this.revalidate();   // 重新验证布局
                this.repaint();      // 重新绘制
            }
        }
        int leftPos = (this.getWidth() - 500) / 2;
        int topPos = (this.getHeight() - 500) / 2;
        // 遍历图片列表并设置位置
        for (int i = 0; i < subImageList.images.size(); i++) {
            JPanel imagePanelContainer = getJPanel(i, leftPos, topPos);

            imagePanelContainer.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    super.mousePressed(e);
                    Arrays.stream(imagePanelContainer.getComponents())
                            .filter(component -> component.getName().equals("imageLabel"))
                            .findFirst()
                            .ifPresent(component -> {
                                if(component instanceof JLabel imageLabel) {
                                    if(imageLabel.getIcon() instanceof PuzzleImageIcon image) {
                                        Integer realPos = image.idx;
                                        Integer curPos = subImageList.images.indexOf(image);
                                        System.out.printf("mousePressed: %d, %d \ncurPos: %d \nrealPos: %d%n", e.getX(), e.getY(), curPos, realPos);
                                        if(firstPressedImageIdx != -1) {
                                            // 说明这是第二次点击，更换位置
                                            secondPressedImageIdx = curPos;
                                            // 然后刷新上屏
                                            if(ifCanSwap()) {
                                                Collections.swap(subImageList.images, secondPressedImageIdx, firstPressedImageIdx);
                                            }
                                            // 更新变量
                                            firstPressedImageIdx = -1;
                                            secondPressedImageIdx = -1;
                                        }else{
                                            firstPressedImageIdx = curPos;
                                        }
                                        loadImageFromSubImages();
                                    }
                                }
                            });
                }
                private Boolean ifCanSwap() {
                    return new ArrayList<Integer>(){
                        {
                            add(firstPressedImageIdx);
                            add(secondPressedImageIdx);
                        }
                    }.stream().anyMatch(n -> subImageList.images.get(n).idx == subImageList.rows * subImageList.cols - 1)
                            && (firstPressedImageIdx + 1 == secondPressedImageIdx
                            || firstPressedImageIdx - 1 == secondPressedImageIdx
                            || firstPressedImageIdx + subImageList.cols == secondPressedImageIdx
                            || firstPressedImageIdx - subImageList.cols == secondPressedImageIdx);
                }
            });

            // 添加到界面
            this.add(imagePanelContainer);
        }

        if(success()){
            // 展示胜利面板
            showSuccessPanel();

            // 移除panel点击事件
        }
    }

    private void showSuccessPanel() {
        System.out.println("showSuccessPanel");
        Arrays.stream(this.getContentPane().getComponents())
                .filter(component -> component instanceof JPanel && component.getName().equals("SuccessPanel"))
                .findFirst()
                .ifPresent(component -> {
                    component.setVisible(true);
                });
    }

    private JPanel getJPanel(int i, int leftPos, int topPos) {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel imageLabel = new JLabel();
        PuzzleImageIcon image = subImageList.images.get(i);
        imageLabel.setIcon(image);
        imageLabel.setName("imageLabel");

        // 设置位置和大小
        imageLabel.setBounds(0, 0, subImageList.width, subImageList.height);

        // 计算每个小图的位置
        int row = i / subImageList.cols;
        int col = i % subImageList.cols;
        int x = leftPos + col * subImageList.width;
        int y = topPos + row * subImageList.height;

        panel.setBounds(x, y, subImageList.width, subImageList.height);

        JLabel posLabel = new JLabel();
        posLabel.setText("RealPos: " + image.idx);
        posLabel.setVisible(false);
        posLabel.setName("posLabel");
        posLabel.setBounds(0, 0, subImageList.width, 20);
        panel.add(posLabel);

        panel.setName("SubImagesPanel");
        panel.setComponentZOrder(imageLabel, 1);
        panel.setComponentZOrder(posLabel, 0);
        return panel;
    }

    private boolean success() {
        for(int i = 0; i < subImageList.images.size(); i++) {
            if(subImageList.images.get(i).idx != i){
                return false;
            }
        }
        return true;
    }

    void showTips() {
        System.out.println("showTips");
        for(Component component : this.getContentPane().getComponents()) {
            if(component instanceof JPanel panel) {
                Arrays.stream(panel.getComponents()).filter(n -> n instanceof JLabel && "posLabel".equals(((JLabel) n).getName()))
                        .findFirst()
                        .ifPresent(n -> {
                            JLabel posLabel = (JLabel) n;
                            if(!posLabel.isVisible()) {
                                posLabel.setVisible(true);
                            }
                        });
            }
        }
    }

    void closeTips() {
        System.out.println("closeTips");
        for(Component component : this.getContentPane().getComponents()) {
            if(component instanceof JPanel panel) {
                Arrays.stream(panel.getComponents()).filter(n -> n instanceof JLabel && "posLabel".equals(((JLabel) n).getName()))
                        .findFirst()
                        .ifPresent(n -> {
                            JLabel posLabel = (JLabel) n;
                            if(posLabel.isVisible()) {
                                posLabel.setVisible(false);
                            }
                        });
            }
        }
    }

    void autoPuzzle() {
        subImageList.images.sort(new Comparator<PuzzleImageIcon>() {
            @Override
            public int compare(PuzzleImageIcon o1, PuzzleImageIcon o2) {
                return o1.idx - o2.idx;
            }
        });
        loadImageFromSubImages();
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
