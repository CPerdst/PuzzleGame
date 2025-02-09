package com.l1akr.puzzle.ui.game;

import javax.swing.*;
import java.awt.*;

public class PuzzleImageIcon extends ImageIcon {
    int idx;
    PuzzleImageIcon(int idx) {
        this.idx = idx;
    }
    PuzzleImageIcon(Image image, int idx) {
        super(image);
        this.idx = idx;
    }
}
