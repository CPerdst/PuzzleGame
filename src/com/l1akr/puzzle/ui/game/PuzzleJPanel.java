package com.l1akr.puzzle.ui.game;

import com.l1akr.puzzle.utility.ImageUtilityImpl;
import com.l1akr.puzzle.utility.PuzzleSubImage;
import com.l1akr.puzzle.utility.SubImageIconList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class PuzzleJPanel extends JPanel {
    SubImagePanelList subImagePanelList = null;

    int subImagePanelPressedIdx1 = -1;
    int subImagePanelPressedIdx2 = -1;

    Boolean isDragging = false;
    AbstractMap.SimpleEntry<Integer, Integer> dragBeginPos = null;
    AbstractMap.SimpleEntry<Integer, Integer> dragEndPos = null;

    // 是否按住A键
    Boolean isHoldingA = false;

    Boolean test = false;

    PuzzleJPanel() throws IOException{
        this(500, 500, "/Users/zwj1/Pictures/IMG_20241123_103909.jpg", 4, 4, 500, 500);
    }

    PuzzleJPanel(int panelWidth, int panelHeight, String imagePath, int rows, int cols, int scaledImageWidth, int scaledImageHeight) throws IOException {
        super();
        init(panelWidth, panelHeight);
        subImagePanelList = new SubImagePanelList(imagePath, rows, cols, scaledImageWidth, scaledImageHeight);
        // 添加keyBind按键监听
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyTyped(e);
                System.out.println(e.getKeyChar());
                if(e.getKeyChar() == 'a') {
                    if(!isHoldingA){
                        isHoldingA = true;
                        // 显示提示
                        showSubPanelTips();
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
                        closeSubPanelTips();
                    }
                } else if(e.getKeyChar() == 'q') {
                    autoPuzzle();
                }
            }
        });
        redrawSubImagePanelList();
    }

    private void init(int width, int height) throws IOException {
        this.setLayout(null);
        this.setSize(width, height);
        this.setName("PuzzleJPanel#" + hashCode());
        this.setVisible(true);
        this.setBounds(0, 0, width, height);
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
    }

    public void redrawSubImagePanelList() {
        if(subImagePanelList == null
                || subImagePanelList.getPanelList() == null
        || subImagePanelList.getPanelList().isEmpty()) {
            return;
        }
        for (Component component : this.getComponents()) {
            if (component instanceof JPanel panel && panel.getName().equals("PuzzleSubImageJPanel")) {
                this.remove(component);
            }
        }
        for (int i = 0; i < subImagePanelList.jPanelList.size(); i++) {
            JPanel panel = getJPanel(i);
            this.add(panel);
        }
        this.revalidate();
        this.repaint();
    }

    private JPanel getJPanel(int i) {
        JPanel panel = subImagePanelList.jPanelList.get(i);

        int subImageHeight = (subImagePanelList.getScaledImageHeight() / subImagePanelList.getRows());
        int subImageWidth = (subImagePanelList.getScaledImageWidth() / subImagePanelList.getCols());
        int subImagePanelTopPos = (i / subImagePanelList.getCols()) * subImageHeight;
        int subImagePanelLeftPos = (i % subImagePanelList.getCols()) * subImageWidth;

        panel.setBounds(subImagePanelLeftPos, subImagePanelTopPos, subImageWidth, subImageHeight);
        panel.setVisible(true);
        return panel;
    }

    public void addSubImagePanelKeyBindListener(MouseAdapter listener) {
        for (int i = 0; i < subImagePanelList.jPanelList.size(); i++) {
            JPanel panel = getJPanel(i);
            panel.addMouseListener(listener);
        }
    }

    public void showSubPanelTips() {
        for (int i = 0; i < subImagePanelList.jPanelList.size(); i++) {
            Arrays.stream(subImagePanelList.getPanelList().get(i).getComponents())
                    .filter(component -> component instanceof JLabel && ((JLabel) component).getName().equals("PuzzleSubTextLabel"))
                    .findFirst()
                    .ifPresent(component -> {
                        if (!((JLabel)component).isVisible()){
                            ((JLabel)component).setVisible(true);
                        }
                    });
        }
    }

    public void closeSubPanelTips() {
        for (int i = 0; i < subImagePanelList.jPanelList.size(); i++) {
            Arrays.stream(subImagePanelList.getPanelList().get(i).getComponents())
                    .filter(component -> component instanceof JLabel && ((JLabel) component).getName().equals("PuzzleSubTextLabel"))
                    .findFirst()
                    .ifPresent(component -> {
                        if (((JLabel)component).isVisible()){
                            ((JLabel)component).setVisible(false);
                        }
                    });
        }
    }

    public void autoPuzzle() {
        subImagePanelList.getPanelList().sort(new Comparator<JPanel>() {
            @Override
            public int compare(JPanel o1, JPanel o2) {
                int x1 = Arrays.stream(o1.getComponents())
                        .filter(component -> component instanceof JLabel && component.getName().equals("PuzzleSubImageLabel"))
                        .map(component -> (PuzzleSubImage) (((JLabel) component).getIcon()))
                        .findFirst()
                        .map(PuzzleSubImage::getId)
                        .orElse(0);
                int x2 = Arrays.stream(o2.getComponents())
                        .filter(component -> component instanceof JLabel && component.getName().equals("PuzzleSubImageLabel"))
                        .map(component -> (PuzzleSubImage) (((JLabel) component).getIcon()))
                        .findFirst()
                        .map(PuzzleSubImage::getId)
                        .orElse(0);
                return x1 - x2;
            }
        });
        redrawSubImagePanelList();
    }

    private class SubImagePanelList {
        private SubImageIconList subImageIconList = null;
        private List<JPanel> jPanelList = new ArrayList<>();

        private SubImagePanelListListener subImagePanelListListener = null;

        private int rows;
        private int cols;

        private int scaledImageWidth;
        private int scaledImageHeight;

        private String imagePath;

        SubImagePanelList() throws IOException {
            this("/Users/zwj1/Pictures/IMG_20241123_103909.jpg", 4, 4, 500, 500);
        }

        SubImagePanelList(String filepath) throws IOException {
            this(filepath, 4, 4, 500, 500);
        }

        SubImagePanelList(String filepath, int rows, int cols, int scaledWidth, int scaledHeight) throws IOException {
            // 记录变量
            imagePath = filepath;
            this.rows = rows;
            this.cols = cols;
            this.scaledImageWidth = scaledWidth;
            this.scaledImageHeight = scaledHeight;
            // 初始化子图集,并将图片重缩放多分割存入
            subImageIconList = new ImageUtilityImpl()
                    .openImage(filepath)
                    .scaleImage(scaledWidth, scaledHeight)
                    .divideImage(rows, cols).get();
            // 将最后一块子图置换成纯白块
            replaceLastDividedImages();
            // 初始化JPanelLists
            initJPanelList();
        }

        private void replaceLastDividedImages() {
            subImageIconList.getImages().remove(rows * cols - 1);
            int subImageHeight = scaledImageHeight / rows;
            int subImageWidth = scaledImageWidth / cols;
            // 创建纯白块
            BufferedImage whiteBlock = new BufferedImage(subImageWidth, subImageHeight, BufferedImage.TYPE_INT_ARGB);
            whiteBlock.getGraphics().setColor(Color.WHITE);
            whiteBlock.getGraphics().fillRect(0, 0, subImageWidth, subImageHeight);
            // 添加纯白块到最后一个
            subImageIconList.addImage(new PuzzleSubImage(whiteBlock, rows * cols - 1));
        }

        private void initJPanelList() {
            if (subImageIconList == null || subImageIconList.getImages().isEmpty()) {
                return;
            }
            for (int i = 0; i < subImageIconList.getImages().size(); i++) {
                if (subImageIconList.getImages().get(i) instanceof PuzzleSubImage image) {
                    JPanel panel = new JPanel();
                    panel.setLayout(null);
                    panel.setSize(image.getIconWidth(), image.getIconHeight());
                    panel.setName("PuzzleSubImageJPanel");
                    panel.setBackground(Color.GREEN);
                    panel.setVisible(true);

                    JLabel imageLabel = new JLabel();
                    imageLabel.setIcon(image);
                    imageLabel.setBounds(0, 0, image.getIconWidth(), image.getIconHeight());
                    imageLabel.setName("PuzzleSubImageLabel");

                    JLabel textLabel = new JLabel();
                    textLabel.setText("Pos: " + image.id);
                    textLabel.setBounds(0, 0, image.getIconWidth(), image.getIconHeight());
                    textLabel.setName("PuzzleSubTextLabel");
                    textLabel.setVisible(false);

                    panel.add(imageLabel);
                    panel.add(textLabel);
                    panel.setComponentZOrder(imageLabel, 1);
                    panel.setComponentZOrder(textLabel, 0);
                    panel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                            if (!isDragging) {
                                isDragging = true;
                                dragBeginPos = new AbstractMap.SimpleEntry<>(e.getX(), e.getY());
                            }
                        }
                        @Override
                        public void mouseReleased(MouseEvent e) {
                            super.mouseReleased(e);
                            if (isDragging) {
                                isDragging = false;
                                dragEndPos = new AbstractMap.SimpleEntry<>(e.getX(), e.getY());
                                if (getLengthBetweenPoints(dragBeginPos, dragEndPos) > 50) {
                                    System.out.println("Dragging from " + dragBeginPos + " to " + dragEndPos + "is " + getLengthBetweenPoints(dragBeginPos, dragEndPos));

                                    return;
                                }
                            }

                            int idx = jPanelList.indexOf(panel);
                            int row = idx / subImagePanelList.getCols();
                            int col = idx % subImagePanelList.getCols();
                            System.out.println("pressed row: " + row + ", col: " + col);
                            AbstractMap.SimpleEntry<Integer, Integer> curPos = new AbstractMap.SimpleEntry<Integer, Integer>(row, col);
                            if(subImagePanelPressedIdx1 == -1){
                                subImagePanelPressedIdx1 = idx;
                            } else {
                                int row1 = subImagePanelPressedIdx1 / subImagePanelList.getCols();
                                int col1 = subImagePanelPressedIdx1 % subImagePanelList.getCols();
                                AbstractMap.SimpleEntry<Integer, Integer> curPos1 = new AbstractMap.SimpleEntry<Integer, Integer>(row1, col1);
                                subImagePanelPressedIdx2 = idx;
                                boolean flag = checkCanSwap(curPos, curPos1);
                                if(flag){
                                    System.out.printf("swap pos: (%d, %d) and (%d, %d)\n",
                                            curPos.getKey(),
                                            curPos.getValue(),
                                            curPos1.getKey(),
                                            curPos1.getValue());
                                    Collections.swap(subImagePanelList.getPanelList(), subImagePanelPressedIdx1, subImagePanelPressedIdx2);
                                }
                                subImagePanelPressedIdx1 = -1;
                                subImagePanelPressedIdx2 = -1;
                                if (flag) {
                                    test = true;
                                    redrawSubImagePanelList();
                                }
                            }
                        }
                        private Boolean checkCanSwap(AbstractMap.SimpleEntry<Integer, Integer> pos,
                                                     AbstractMap.SimpleEntry<Integer, Integer> pos1) {
                            int idx1 = pos.getKey() * cols + pos.getValue();
                            int idx2 = pos1.getKey() * cols + pos1.getValue();
                            return (idx1 - 1 == idx2
                                    || idx1 + 1 == idx2
                                    || idx1 - cols == idx2
                                    || idx1 + cols == idx2)
                                    && Stream.of(pos, pos1).anyMatch(p -> Arrays
                                    .stream(jPanelList.get(p.getKey() * cols + p.getValue()).getComponents())
                                    .anyMatch(s -> s.getName().equals("PuzzleSubImageLabel")
                                            && ((JLabel)s).getIcon() instanceof PuzzleSubImage
                                    && Objects.equals(((PuzzleSubImage) ((JLabel) s).getIcon()).id, rows * cols - 1)));
                        }

                        private double getLengthBetweenPoints(AbstractMap.SimpleEntry<Integer, Integer> pos1,
                                                            AbstractMap.SimpleEntry<Integer, Integer> pos2) {
                            return Math.sqrt(Math.pow(pos1.getKey() - pos2.getKey(), 2) + Math.pow(pos1.getValue() - pos2.getValue(), 2));
                        }
                        // 获取pos1指向pos2的方向
                        private double getDegreeBetweenPoints(AbstractMap.SimpleEntry<Integer, Integer> pos1,
                                                              AbstractMap.SimpleEntry<Integer, Integer> pos2) {
                            int x1 = pos1.getKey();
                            int y1 = pos1.getValue();
                            int x2 = pos2.getKey();
                            int y2 = pos2.getValue();
//                            Math.toDegrees(Math.tan())
                            return 1.0;
                        }
                    });
                    jPanelList.add(panel);
                }
            }
        }

        public void shuffleJPanelList() {
            Collections.shuffle(jPanelList);
        }

        public void setSubImagePanelListListener(SubImagePanelListListener subImagePanelListListener) {
            this.subImagePanelListListener = subImagePanelListListener;
        }

        public SubImageIconList getSubImagePanelList() {
            return subImageIconList;
        }

        public List<JPanel> getPanelList() {
            return jPanelList;
        }

        public int getRows() {
            return rows;
        }

        public int getCols() {
            return cols;
        }

        public int getScaledImageWidth() {
            return scaledImageWidth;
        }

        public int getScaledImageHeight() {
            return scaledImageHeight;
        }

        public String getImagePath() {
            return imagePath;
        }

        public interface SubImagePanelListListener {

        }

    }
}
