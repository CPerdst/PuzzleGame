package com.l1akr.puzzle.ui.game;

import com.l1akr.puzzle.config.GameMode;
import com.l1akr.puzzle.config.GlobalConfig;
import com.l1akr.puzzle.utility.ImageUtilityImpl;
import com.l1akr.puzzle.utility.PuzzleSubImage;
import com.l1akr.puzzle.utility.SubImageIconList;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class PuzzleJPanel extends JPanel {
    private SubImagePanelList subImagePanelList = null;

    private int subImagePanelPressedIdx1 = -1;
    private int subImagePanelPressedIdx2 = -1;

    private Boolean isDragging = false;
    private AbstractMap.SimpleEntry<Integer, Integer> dragBeginPos = null;
    private AbstractMap.SimpleEntry<Integer, Integer> dragEndPos = null;

    // 是否按住A键
    private Boolean isHoldingA = false;

    private Boolean test = false;

    private PuzzlePanelGameListener puzzlePanelGameListener = null;

    private int rows;
    private int cols;

    private int scaledImageWidth;
    private int scaledImageHeight;

    private int panelWidth;
    private int panelHeight;

    PuzzleJPanel() throws IOException, InterruptedException {
        this(500, 500, "/Users/zwj1/Pictures/IMG_20241123_103909.jpg", 4, 4, 500, 500, null);
    }

    PuzzleJPanel(String filePath) throws IOException, InterruptedException {
        this(500, 500, filePath, 4, 4, 500, 500, null);
    }

    PuzzleJPanel(int panelWidth, int panelHeight, String imagePath, int rows, int cols, int scaledImageWidth, int scaledImageHeight, PuzzlePanelGameListener listener) throws IOException, InterruptedException {
        super();
        this.rows = rows;
        this.cols = cols;
        this.scaledImageWidth = scaledImageWidth;
        this.scaledImageHeight = scaledImageHeight;
        this.puzzlePanelGameListener = listener;
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        init();
        subImagePanelList = new SubImagePanelList(imagePath, rows, cols, scaledImageWidth, scaledImageHeight);
        // 添加keyBind按键监听
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
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
                super.keyReleased(e);
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
        subImagePanelList.shuffleJPanelList(null, null);
    }

    private void init() throws IOException {
        this.setLayout(null);
        this.setSize(panelWidth, panelHeight);
        this.setName("PuzzleJPanel#" + hashCode());
        this.setVisible(true);
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    private void redrawSubImagePanelList() {
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

    private void exchangeSubImagePanel(AbstractMap.SimpleEntry<Integer, Integer> oldPos,
                                       AbstractMap.SimpleEntry<Integer, Integer> newPos) throws InterruptedException {
        if(verifyPointValida(oldPos) && verifyPointValida(newPos)) {
            int idx1 = oldPos.getKey() * subImagePanelList.cols + oldPos.getValue();
            int idx2 = newPos.getKey() * subImagePanelList.cols + newPos.getValue();
            Component[] components = this.getComponents();
            List<JPanel> panels = this.subImagePanelList.jPanelList;

            int subImageHeight = subImagePanelList.getSubImageIconList().getSubImageHeight();
            int subImageWidth = subImagePanelList.getSubImageIconList().getSubImageWidth();
            int topMargin = (this.getHeight() - subImagePanelList.getSubImageIconList().getImageFullHeight()) / 2;
            int leftMargin = (this.getWidth() - subImagePanelList.getSubImageIconList().getImageFullWidth()) / 2;

            if(Arrays.stream(components).anyMatch(component -> component instanceof JPanel panel
                    && component.getName().equals("PuzzleSubImageJPanel")
                    && panel == panels.get(idx1))
            && Arrays.stream(components).anyMatch(component -> component instanceof JPanel panel
                    && component.getName().equals("PuzzleSubImageJPanel")
                    && panel == panels.get(idx2))) {
                panels.get(idx1).setBounds(newPos.getValue() * subImageWidth + leftMargin,
                        newPos.getKey() * subImageHeight + topMargin,
                        subImageWidth,
                        subImageHeight);
                panels.get(idx2).setBounds(oldPos.getValue() * subImageWidth + leftMargin,
                        oldPos.getKey() * subImageHeight + topMargin,
                        subImageWidth,
                        subImageHeight);

                Collections.swap(subImagePanelList.jPanelList, idx1, idx2);

                this.revalidate();
                this.repaint();
            }
        }
    }

    private Boolean verifyPointValida(AbstractMap.SimpleEntry<Integer, Integer> point) {
        return point.getKey() >= 0 && point.getValue() < subImagePanelList.cols
                && point.getValue() >= 0 && point.getValue() < subImagePanelList.rows;
    }

    private JPanel getJPanel(int i) {
        JPanel panel = subImagePanelList.jPanelList.get(i);

        int subImageHeight = subImagePanelList.getSubImageIconList().getSubImageHeight();
        int subImageWidth = subImagePanelList.getSubImageIconList().getSubImageWidth();
        int topMargin = (this.getHeight() - subImagePanelList.getSubImageIconList().getImageFullHeight()) / 2;
        int leftMargin = (this.getWidth() - subImagePanelList.getSubImageIconList().getImageFullWidth()) / 2;
        int subImagePanelTopPos = topMargin + (i / subImagePanelList.getCols()) * subImageHeight;
        int subImagePanelLeftPos = leftMargin + (i % subImagePanelList.getCols()) * subImageWidth;

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

    // 内部类与接口 //

    private class SubImagePanelList {
        private SubImageIconList subImageIconList = null;
        private List<JPanel> jPanelList = new ArrayList<>();

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
            int containerWidth = scaledWidth; // 为了避免分歧，这里应该传入的是容器的宽高，用于缩放image
            int containerHeight = scaledHeight;
            subImageIconList = new ImageUtilityImpl()
                    .openImage(filepath)
                    .scaleImage(containerWidth, containerHeight)
                    .divideImage(rows, cols).get();
            // 将最后一块子图置换成纯白块
            replaceLastDividedImages();
            // 初始化JPanelLists
            initJPanelList();
        }

        private void replaceLastDividedImages() {
            subImageIconList.getImages().remove(rows * cols - 1);
            int subImageHeight = subImageIconList.getSubImageHeight();
            int subImageWidth = subImageIconList.getSubImageWidth();
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
                                panel.setBorder(BorderFactory.createLoweredBevelBorder());
                            }
                        }
                        @Override
                        public void mouseReleased(MouseEvent e) {
                            super.mouseReleased(e);
                            int idx = jPanelList.indexOf(panel);
                            int row = idx / subImagePanelList.getCols();
                            int col = idx % subImagePanelList.getCols();

                            if (isDragging) {
                                isDragging = false;
                                dragEndPos = new AbstractMap.SimpleEntry<>(e.getX(), e.getY());
                                if (getLengthBetweenPoints(dragBeginPos, dragEndPos) > 50) {
                                    boolean legal = true;
                                    AbstractMap.SimpleEntry<Integer, Integer> endPoint = null;
                                    double degree = getDegreeBetweenPoints(dragBeginPos, dragEndPos);
                                    if(degree >= -45 && degree < 45){ // →
                                        System.out.println("→");
                                        endPoint = new AbstractMap.SimpleEntry<>(row, col + 1);
                                    }else if(degree >= 45 && degree < 135){ // ↓
                                        System.out.println("↓");
                                        endPoint = new AbstractMap.SimpleEntry<>(row + 1, col);
                                    }else if(degree >= -135 && degree <45){ // ↑
                                        System.out.println("↑");
                                        endPoint = new AbstractMap.SimpleEntry<>(row - 1, col);
                                    }else { // ←
                                        System.out.println("←");
                                        endPoint = new AbstractMap.SimpleEntry<>(row, col - 1);
                                    }
                                    if(row < 0 || col < 0 || endPoint.getKey() >= rows || endPoint.getValue() >= cols){
                                        legal = false;
                                    }
                                    if(legal){
                                        System.out.printf("Dragging %s(%d, %d) to %s(%d, %d) is %f\n",
                                                dragBeginPos, row, col,
                                                dragEndPos, endPoint.getKey(), endPoint.getValue(),
                                                getLengthBetweenPoints(dragBeginPos, dragEndPos));
                                        boolean flag = checkCanSwap(new AbstractMap.SimpleEntry<>(row, col), endPoint);
//                                        if(flag){
//                                            Collections.swap(subImagePanelList.getPanelList(), row * cols + col, endPoint.getKey() * cols + endPoint.getValue());
//                                        }
                                        if (flag) {
                                            test = true;
                                            try {
                                                exchangeSubImagePanel(new AbstractMap.SimpleEntry<>(row, col), endPoint);
                                            } catch (InterruptedException ex) {
                                                throw new RuntimeException(ex);
                                            }
//                                            redrawSubImagePanelList();
                                        }
                                    }else {
                                        System.out.printf("Dragging %s(%d, %d) to %s(%d, %d) is illegal\n",
                                                dragBeginPos, row, col,
                                                dragEndPos, endPoint.getKey(), endPoint.getValue()
                                        );
                                    }
                                    panel.setBorder(null);
                                    return;
                                }
                            }

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
                            return Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
                        }
                    });
                    panel.addMouseMotionListener(new MouseMotionAdapter() {
                        @Override
                        public void mouseDragged(MouseEvent e) {
                            if (!GlobalConfig.gameMode.equals(GameMode.Debug)) {
                                return;
                            }
                            if (dragBeginPos != null) {
//                                System.out.println("dragBeginPos: " + dragBeginPos);
                                AbstractMap.SimpleEntry<Integer, Integer> curPoint = new AbstractMap.SimpleEntry<>(e.getX(), e.getY());
                                Graphics2D g2d = (Graphics2D) panel.getGraphics();
                                g2d.drawLine(dragBeginPos.getKey(), dragBeginPos.getValue() , curPoint.getKey(), curPoint.getValue());
                                panel.paintComponents(g2d);
                            }
                        }
                    });
                    jPanelList.add(panel);
                }
            }
        }

        public SubImagePanelList shuffleJPanelList(Integer deep, SubImagePanelListListener listener) throws InterruptedException {
            enum Direction {
                Up, Down, Left, Right
            }
            if(deep == null){
                deep = rows * cols * 3;
                if(deep.equals(0)){
                    return this;
                }
            }
            Random rand = new Random();
            Direction directionCache = null;
            AbstractMap.SimpleEntry<Integer, Integer> curPoint = new AbstractMap.SimpleEntry<>(cols - 1, rows - 1);
            AbstractMap.SimpleEntry<Integer, Integer> nxtPoint = null;
            for(int i = 0; i < deep; i++){
                while (true) {
                    // 获取随机上下左右
                    Direction direction = Direction.values()[rand.nextInt(Direction.values().length)];
                    // 判断是否可行
                    if ((direction == Direction.Up && curPoint.getValue() - 1 >= 0)
                    || (direction == Direction.Down && curPoint.getValue() + 1 < rows)
                    || (direction == Direction.Left && curPoint.getKey() - 1 >= 0)
                    || (direction == Direction.Right && curPoint.getKey() + 1 < cols)) {
                        if (directionCache != null && directionCache == direction) {
                            continue;
                        }
                        if (direction == Direction.Up) {
                            nxtPoint = new AbstractMap.SimpleEntry<>(curPoint.getKey(), curPoint.getValue() - 1);
                        }else if (direction == Direction.Down) {
                            nxtPoint = new AbstractMap.SimpleEntry<>(curPoint.getKey(), curPoint.getValue() + 1);
                        }else if (direction == Direction.Left) {
                            nxtPoint = new AbstractMap.SimpleEntry<>(curPoint.getKey() - 1, curPoint.getValue());
                        }else if (direction == Direction.Right) {
                            nxtPoint = new AbstractMap.SimpleEntry<>(curPoint.getKey() + 1, curPoint.getValue());
                        }
                        directionCache = direction;
                        // 可行则退出
                        break;
                    }
                }
                if(listener != null){
                    listener.onShuffleExchangeSubImage(curPoint, nxtPoint);
                    curPoint = nxtPoint;
                    continue;
                }
                Collections.swap(jPanelList,
                        curPoint.getValue() * cols + curPoint.getKey(),
                        nxtPoint.getValue() * cols + nxtPoint.getKey());
                curPoint = nxtPoint;
            }
            return this;
        }

        public SubImageIconList getSubImageIconList() {
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
            public abstract void onShuffleExchangeSubImage(AbstractMap.SimpleEntry<Integer, Integer> oldPoint,
                                                           AbstractMap.SimpleEntry<Integer, Integer> newPoint) throws InterruptedException;
        }

        public abstract class SubImagePanelListListenerAdapter implements SubImagePanelListListener {
            @Override
            public void onShuffleExchangeSubImage(AbstractMap.SimpleEntry<Integer, Integer> oldPoint,
                                                  AbstractMap.SimpleEntry<Integer, Integer> newPoint) {
                return;
            }
        }
    }

    private interface PuzzlePanelGameListener {
        public abstract void onPuzzleGameStarted();
        public abstract void onPuzzleGameWin();
        public abstract void onPuzzleGameTimeout();
    }

    public abstract class PuzzlePanelGameListenerAdapter implements PuzzlePanelGameListener {
        @Override
        public void onPuzzleGameStarted() {
            return;
        }

        @Override
        public void onPuzzleGameWin() {
            return;
        }

        @Override
        public void onPuzzleGameTimeout() {
            return;
        }
    }
}
