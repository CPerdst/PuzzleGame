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
        return this;
    }

    public ImageUtilityImpl scaleImage(int width, int height) {
        if(image == null || width <= 0 || height <= 0) {
            return null;
        }
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        scaledImage.getGraphics().drawImage(image, 0, 0, width, height, null);
        image = scaledImage;
        return this;
    }

    public SubImageIconList get() {
        return dividedImages;
    }

    private static Boolean checkIsPhoto(String filename) {
        return filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg") || filename.endsWith(".gif");
    }
}
