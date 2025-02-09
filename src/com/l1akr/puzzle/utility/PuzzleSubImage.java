package com.l1akr.puzzle.utility;

import javax.swing.*;
import java.awt.*;

public class PuzzleSubImage extends ImageIcon {
    Integer id = null;

    PuzzleSubImage(Image image, Integer id) {
        super(image);
        id = id;
    }
}
