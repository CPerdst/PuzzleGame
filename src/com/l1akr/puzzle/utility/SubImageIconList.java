package com.l1akr.puzzle.utility;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubImageIconList {
    private List<ImageIcon> images = new ArrayList<>();

    private int imageFullWidth;
    private int imageFullHeight;

    private int subImageWidth;
    private int subImageHeight;

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

    public int getImageFullWidth() {
        return imageFullWidth;
    }

    public int getImageFullHeight() {
        return imageFullHeight;
    }

    public int getSubImageWidth() {
        return subImageWidth;
    }

    public int getSubImageHeight() {
        return subImageHeight;
    }

    public void setImageFullWidth(int imageFullWidth) {
        this.imageFullWidth = imageFullWidth;
    }

    public void setImageFullHeight(int imageFullHeight) {
        this.imageFullHeight = imageFullHeight;
    }

    public void setSubImageWidth(int subImageWidth) {
        this.subImageWidth = subImageWidth;
    }

    public void setSubImageHeight(int subImageHeight) {
        this.subImageHeight = subImageHeight;
    }
}
