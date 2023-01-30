package com.keeper.homepage.global.util.thumbnail;


import static java.awt.Image.SCALE_SMOOTH;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;

import java.awt.Image;
import java.awt.image.BufferedImage;
import lombok.Getter;

@Getter
public enum ThumbnailType {
  DEFAULT(200, 200);

  private final int widthPixel;
  private final int heightPixel;

  ThumbnailType(int widthPixel, int heightPixel) {
    this.widthPixel = widthPixel;
    this.heightPixel = heightPixel;
  }

  public BufferedImage resizing(BufferedImage original) {
    Image scaledInstance = original.getScaledInstance(widthPixel, heightPixel, SCALE_SMOOTH);
    BufferedImage outputImage = new BufferedImage(widthPixel, heightPixel, TYPE_INT_RGB);
    outputImage.getGraphics().drawImage(scaledInstance, 0, 0, null);
    return outputImage;
  }
}
