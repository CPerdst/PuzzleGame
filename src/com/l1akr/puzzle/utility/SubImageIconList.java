package com.l1akr.puzzle.utility;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubImageIconList {
    private List<ImageIcon> images = new ArrayList<>();

    public SubImageIconList() {}

    public void addImage(ImageIcon image) {
        images.add(image);
    }

    public SubImageIconList shuffleImages() {
        Collections.shuffle(images);
        return this;
    }

    public List<ImageIcon> getImages() {
        return images;
    }
}
