package com.l1akr.puzzle.ui.game;

import com.l1akr.puzzle.utility.ImageUtilityImpl;
import com.l1akr.puzzle.utility.SubImageIconList;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PuzzleJPanel extends JPanel {
    SubImagePanelList subImagePanelList = null;

    PuzzleJPanel() throws IOException{
        this(500, 500, "/Users/zwj1/Pictures/IMG_20241123_103909.jpg", 4, 4, 500, 500);
    }

    PuzzleJPanel(int panelWidth, int panelHeight, String imagePath, int rows, int cols, int scaledImageWidth, int scaledImageHeight) throws IOException {
        super();
        init(panelWidth, panelHeight);
        subImagePanelList = new SubImagePanelList(imagePath, rows, cols, scaledImageWidth, scaledImageHeight);
        redrawSubImagePanelList();
    }

    private void init(int width, int height) throws IOException {
        this.setLayout(null);
        this.setSize(width, height);
        this.setName("PuzzleJPanel#" + hashCode());
    }

    public void redrawSubImagePanelList() {
        if(subImagePanelList == null
                || subImagePanelList.jPanelList == null
        || subImagePanelList.jPanelList.isEmpty()) {
            return;
        }
        for (Component component : this.getComponents()) {
            if (component instanceof JPanel panel) {
                this.remove(panel);
            }
        }
        for (int i = 0; i < subImagePanelList.jPanelList.size(); i++) {
            JPanel panel = subImagePanelList.jPanelList.get(i);

            int row = i / subImagePanelList.getRows()
            panel.setBounds();
            this.add(panel);
        }
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
                    .divideImage(rows, cols).get().shuffleImages();
            // 初始化JPanelLists
            initJPanelList();
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

        private void initJPanelList() {
            if (subImageIconList == null || subImageIconList.getImages().isEmpty()) {
                return;
            }
            for (int i = 0; i < subImageIconList.getImages().size(); i++) {
                if (subImageIconList.getImages().get(i) instanceof PuzzleImageIcon image) {
                    JPanel panel = new JPanel();
                    panel.setLayout(null);
                    panel.setVisible(true);

                    JLabel label = new JLabel();
                    label.setIcon(image);

                    panel.add(label);
                    jPanelList.add(panel);
                }
            }

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
