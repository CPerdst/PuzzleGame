package com.l1akr.puzzle.utility;

import javax.swing.*;
import java.awt.*;

public class PuzzleSubImage extends ImageIcon {
    public Integer id = null;

    public PuzzleSubImage(Image image, Integer id) {
        super(image);
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
