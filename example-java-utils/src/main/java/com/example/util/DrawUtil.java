package com.example.util;

import com.google.common.io.ByteSource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @Author: zhouyuyang
 * @Date: 2020/11/20 17:00
 */
public class DrawUtil {
    private final static BasicStroke STOKE_LINE = new BasicStroke(5f);
    private final static String FORMAT_NAME = "jpg";
    private static DrawRectangle drawRectangle;
    private static DrawPolygon drawPolygon;

    public DrawUtil(){
        DrawUtil.drawPolygon = new DrawPolygon();
        DrawUtil.drawRectangle = new DrawRectangle();
    }

    private static byte[] doDraw(byte[] imageBytes, Draw draw) throws IOException {
        BufferedImage bg = ImageIO.read(ByteSource.wrap(imageBytes).openStream());
        BufferedImage img = new BufferedImage(bg.getWidth(), bg.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D) img.getGraphics();
        graphics.drawImage(bg.getScaledInstance(bg.getWidth(), bg.getHeight(), Image.SCALE_DEFAULT), 0, 0, null);
        graphics.setColor(Color.RED);
        graphics.setStroke(STOKE_LINE);
        draw.doDraw(graphics);
        graphics.dispose();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(img, FORMAT_NAME, stream);
        return stream.toByteArray();
    }

    /**
     * 在图片上画矩形
     * @param imageBytes 图片字节
     * @param x 左上的x
     * @param y 左上的y
     * @param width 宽度
     * @param height 高度
     * @return
     * @throws IOException
     */
    public static byte[] drawRectangle(byte[] imageBytes, int x, int y, int width, int height) throws IOException {
        return doDraw(imageBytes, drawRectangle.buildPoint(x, y, width, height));
    }


    /**
     * 在图片上画多边形
     * @param imageBytes 图片字节
     * @param xPoints 多边形的x点位
     * @param yPoints 多边形的y点位
     * @return
     * @throws IOException
     */
    public static byte[] drawPolygon(byte[] imageBytes, int[] xPoints, int[] yPoints) throws IOException {
        return doDraw(imageBytes, drawPolygon.buildPoint(xPoints, yPoints));
    }

    private abstract class Draw{
        protected abstract void doDraw(Graphics graphics);
    }

    private class DrawRectangle extends Draw{
        int x, y, width, height;
        @Override
        protected void doDraw(Graphics graphics) {
            graphics.drawRect(x, y, width, height);
        }

        DrawRectangle buildPoint(int x, int y, int width, int height){
            this.x = x;
            this.y = y;
            this.height = height;
            this.width = width;
            return this;
        }
    }

    private class DrawPolygon extends Draw{
        int[] xPoints, yPoints;
        @Override
        protected void doDraw(Graphics graphics) {
            graphics.drawPolygon(xPoints, yPoints, xPoints.length);
        }

        DrawPolygon buildPoint(int[] xPoints, int[] yPoints){
            this.xPoints = xPoints;
            this.yPoints = yPoints;
            return this;
        }
    }

}
