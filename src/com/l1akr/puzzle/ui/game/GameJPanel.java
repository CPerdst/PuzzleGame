package com.l1akr.puzzle.ui.game;

import com.l1akr.puzzle.utility.ImageUtilityImpl;
import com.l1akr.puzzle.utility.SubImageIconList;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class GameJPanel extends JPanel {
    private int panelWidth = 510;
    private int panelHeight = 510;

    private int imageWidth = 500;
    private int imageHeight = 500;

    private PuzzleJPanel puzzleJPanel = null;

    GameJPanel() throws IOException, InterruptedException {
        this(500, 680, 500, 680);
    }

    GameJPanel(int panelWidth, int panelHeight, int imageWidth, int imageHeight) throws IOException, InterruptedException {
        super();
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        // 初始化GameJPanel
        init();
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                System.out.println(e.getKeyChar());
            }
        });
        // 初始化PuzzlePanel
        String filePath = "C:\\Users\\l1Akr\\IdeaProjects\\PuzzleGame\\resource\\my_pretty_mouse.jpg";
        puzzleJPanel = new PuzzleJPanel(imageWidth, imageHeight, filePath, 4, 4, imageWidth, imageHeight, null);
        puzzleJPanel.addSubImagePanelKeyBindListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
//                System.out.println("mouseReleased");
            }
        });
        puzzleJPanel.setBounds((panelWidth - imageWidth) / 2, (panelHeight - imageHeight) / 2, imageWidth, imageHeight);
        this.add(puzzleJPanel);
        this.setBorder(BorderFactory.createLoweredBevelBorder());
    }

    private void init() {
        this.setLayout(null);
        this.setSize(panelWidth, panelHeight);
        this.setName("GameJPanel#" + hashCode());
        this.setBackground(Color.GREEN);
        this.setVisible(true);
        this.setFocusable(true);
    }

    private void drawSubImageIcon() {

    }
}
