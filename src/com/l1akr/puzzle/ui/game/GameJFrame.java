package com.l1akr.puzzle.ui.game;

import com.l1akr.puzzle.utility.ImageUtilityImpl;
import com.l1akr.puzzle.utility.SubImageIconList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
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

    GameJPanel gameJPanel = null;

    public GameJFrame() throws IOException, InterruptedException {
        // 初始化界面
        init();
        // 初始化菜单
        initMenuBar();
        // 初始化GameJPanel
        gameJPanel = new GameJPanel(520, this.getContentPane().getHeight() - 100, 500, this.getContentPane().getHeight() - 100);
        gameJPanel.setBounds((this.getContentPane().getWidth() - gameJPanel.getWidth()) / 2,
                (this.getContentPane().getHeight() - gameJPanel.getHeight()) / 2,
                520, this.getContentPane().getHeight() - 100);
        this.add(gameJPanel);
        // 初始化完毕，执行一次重绘，刷新上屏
        repaint();
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
        setSize(600, 680);
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
        // 设置focus
        this.setFocusable(true);
        // 初始化完毕，才能设置可见
        setVisible(true);
    }
}
