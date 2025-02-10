package com.l1akr.puzzle.utility;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Optional;

public class ImageUtilityImpl implements ImageDivided {
    BufferedImage image = null;
    SubImageIconList dividedImages = null;

    public ImageUtilityImpl() {}
    public ImageUtilityImpl openImage(String path) throws IOException {
        if(path.isEmpty()) {
            return null;
        }
        File file = new File(path);
        if(!file.exists() || !file.isFile() || !checkIsPhoto(file.getName())) {
            return null;
        }
        image = ImageIO.read(file);
        return this;
    }
    public ImageUtilityImpl divideImage(int rows, int cols) {
        if(image == null || rows <= 0 || cols <= 0) {
            return null;
        }
        dividedImages = new SubImageIconList();
        int width = image.getWidth();
        int height = image.getHeight();
        int subWidth = width / cols;
        int subHeight = height / rows;
        int id = 0;
        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                BufferedImage subImage = image.getSubimage(col * subWidth, row * subHeight, subWidth, subHeight);
                PuzzleSubImage imageIcon = new PuzzleSubImage(subImage, id++);
                dividedImages.addImage(imageIcon);
            }
        }
        dividedImages.setSubImageWidth(subWidth);
        dividedImages.setSubImageHeight(subHeight);
        return this;
    }

    public ImageUtilityImpl scaleImage(int width, int height) {
        if(image == null || width <= 0 || height <= 0) {
            return null;
        }
        BufferedImage scaledImage = null;
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        // 如果image的宽或高大于容器的宽或高，则执行缩
        if(imageWidth > width || imageHeight > height) {
            if(imageWidth > width && imageHeight <= height) {
                // 求取缩后高
                double scaledHeight = imageHeight * ((double)width / imageWidth);
                scaledImage = new BufferedImage(width, (int)scaledHeight, BufferedImage.TYPE_INT_RGB);
            }else if(imageHeight > height && imageWidth <= width) {
                // 求取缩后宽
                double scaledWidth = imageWidth * ((double)height / imageHeight);
                scaledImage = new BufferedImage((int)scaledWidth, height, BufferedImage.TYPE_INT_RGB);
            }else {
                // 如果图片宽高都大于容器宽高
                // 首先将图片高缩放成height，获取缩后宽，看看行不行
                double scaledWidth = imageWidth * ((double)height / imageHeight);
                if(scaledWidth <= width) {
                    // 可以的话，直接用scaledWidth
                    scaledImage = new BufferedImage((int)scaledWidth, height, BufferedImage.TYPE_INT_RGB);
                }else {
                    // 否则就将图片的宽度缩放成width，获取缩后高
                    double scaledHeight = imageHeight * ((double)width / imageWidth);
                    scaledImage = new BufferedImage(width, (int)scaledHeight, BufferedImage.TYPE_INT_RGB);
                }
            }
        }else{
            // 否则执行放
            // 首先将图片高缩放成height，获取缩后宽，看看行不行
            double scaledWidth = imageWidth * ((double)height / imageHeight);
            if(scaledWidth <= width) {
                // 可以的话，直接用scaledWidth
                scaledImage = new BufferedImage((int)scaledWidth, height, BufferedImage.TYPE_INT_RGB);
            }else {
                // 否则就将图片的宽度缩放成width，获取缩后高
                double scaledHeight = imageHeight * ((double)width / imageWidth);
                scaledImage = new BufferedImage(width, (int)scaledHeight, BufferedImage.TYPE_INT_RGB);
            }
        }
        // 执行缩放
        scaledImage.getGraphics().drawImage(image, 0, 0, width, height, null);
        image = scaledImage;
        return this;
    }

    public SubImageIconList get() {
        dividedImages.setImageFullWidth(image.getWidth());
        dividedImages.setImageFullHeight(image.getHeight());
        return dividedImages;
    }

    private static Boolean checkIsPhoto(String filename) {
        return filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg") || filename.endsWith(".gif");
    }
}
