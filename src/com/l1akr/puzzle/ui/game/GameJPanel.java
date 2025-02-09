package com.l1akr.puzzle.ui.game;

import com.l1akr.puzzle.utility.ImageUtilityImpl;
import com.l1akr.puzzle.utility.SubImageIconList;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GameJPanel extends JPanel {
    int panelWidth = 510;
    int panelHeight = 510;

    int imageWidth = 500;
    int imageHeight = 500;

    GameJPanel() throws IOException {
        super();
        // 初始化本身
        init();
        this.setVisible(false);
    }

    public void draw() {
        if(!this.isVisible()){
            drawSubImageIcon();
            this.setVisible(true);
        }
    }

    public void close() {
        if(this.isVisible()){
            this.setVisible(false);
        }
    }

    private void init() {
        this.setLayout(null);
        this.setSize(panelWidth, panelHeight);
        this.setName("GameJPanel#" + hashCode());
    }

    private void drawSubImageIcon() {

    }
}
