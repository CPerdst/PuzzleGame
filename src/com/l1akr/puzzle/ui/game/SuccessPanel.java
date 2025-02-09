package com.l1akr.puzzle.ui.game;

import javax.swing.*;
import java.awt.*;

public class SuccessPanel extends javax.swing.JPanel {
    SuccessPanel() {
        this.setLayout(null);
        this.setBounds((this.getWidth() - 300) / 2, (this.getHeight() - 300) / 2, 300, 300);
        this.setBackground(Color.WHITE);
        this.setName("SuccessPanel");
        this.setVisible(false);

        JLabel label = new JLabel();
        label.setBounds(0, 0, 300, 300);
        label.setText("Success!");
        label.setFont(new Font("Arial", Font.BOLD, 20));
        this.add(label);
    }
}
