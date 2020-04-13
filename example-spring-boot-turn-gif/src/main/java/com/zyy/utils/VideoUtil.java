package com.zyy.utils;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.global.opencv_objdetect;
import org.bytedeco.opencv.opencv_core.IplImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * @author zhouyuyang_vendor
 */
public class VideoUtil {

    /**
     * 将图片旋转指定度
     * @param bufferedimage 图片
     * @param degree 旋转角度
     * @return
     */
    public static BufferedImage rotateImage(BufferedImage bufferedimage, int degree){
        // 得到图片宽度。
        int w= bufferedimage.getWidth();
        // 得到图片高度。
        int h= bufferedimage.getHeight();
        // 得到图片透明度。
        int type= bufferedimage.getColorModel().getTransparency();
        BufferedImage img;// 空的图片。
        Graphics2D graphics2d;// 空的画笔。
        (graphics2d= (img= new BufferedImage(w, h, type))
                .createGraphics()).setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        // 旋转，degree是整型，度数，比如垂直90度。
        graphics2d.rotate(Math.toRadians(degree), w / 2, h / 2);
        // 从bufferedimagecopy图片至img，0,0是img的坐标。
        graphics2d.drawImage(bufferedimage, 0, 0, null);
        graphics2d.dispose();
        // 返回复制好的图片，原图片依然没有变，没有旋转，下次还可以使用。
        return img;
    }

    /**
     * 截取视频指定帧保存为指定格式的图片（图片保存在视频同文件夹下）
     * @param videofile 视频地址
     * @param imgSuffix 图片格式
     * @param indexFrame 第几帧（默认：5）
     * @throws Exception
     */
    public static void fetchFrame(String videofile,String imgSuffix,Integer indexFrame)throws Exception {
        if(indexFrame==null) {
            indexFrame=5;
        }
        FFmpegFrameGrabber ff = new FFmpegFrameGrabber(videofile);
        ff.start();
        try {
            int lenght = ff.getLengthInFrames();
            int i = 0;
            Frame f = null;
            while (i < lenght) {
                f = ff.grabFrame();
                if ((i > indexFrame) && (f.image != null)) {
                    break;
                }
                i++;
            }
            int owidth = f.imageWidth ;
            int oheight = f.imageHeight ;
            int width = 800;
            int height = (int) (((double) width / owidth) * oheight);
            Java2DFrameConverter converter =new Java2DFrameConverter();
            BufferedImage fecthedImage =converter.getBufferedImage(f);
            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            bi.getGraphics().drawImage(fecthedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH),
                    0, 0, null);
            bi=rotateImage(bi, 90);
            File targetFile = new File(videofile.substring(0,videofile.lastIndexOf("."))+imgSuffix);
            ImageIO.write(bi, "jpg", targetFile);
        }finally {
            ff.stop();
            ff.close();
        }
    }


    /**
     * 该变量建议设置为全局控制变量，用于控制录制结束
     */
    public volatile static boolean isStart = true;

    /**
     * 按帧录制视频
     *
     * @param inputFile-该地址可以是网络直播/录播地址，也可以是远程/本地文件路径
     * @param outputFile
     *            -该地址只能是文件地址，如果使用该方法推送流媒体服务器会报错，原因是没有设置编码格式
     * @param second 录制视频时间
     * @param countDownLatch 如果需要等待录制完成需要传入该参数
     */
    public static void frameRecord(String inputFile, String outputFile, int audioChannel, int second, CountDownLatch countDownLatch) throws Exception {
        // 获取视频源
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        // 流媒体输出地址，分辨率（长，高），是否录制音频（0:不录制/1:录制）
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, 1280, 720, audioChannel);
        // 开始取视频源
        new Thread(() -> {
            try {
                recordByFrame(grabber, recorder, countDownLatch);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        //前面的操作会浪费两秒，这里需要加上2秒才能录制到正确的秒数
        second += 2;
        Thread.sleep(second * 1000);
        VideoUtil.isStart = false;
    }


    private static void recordByFrame(FFmpegFrameGrabber grabber, FFmpegFrameRecorder recorder, CountDownLatch countDownLatch) throws Exception {
        try {//建议在线程中使用该方法
            grabber.start();
            recorder.start();
            Frame frame;
            while (isStart && (frame = grabber.grabFrame()) != null) {
                recorder.record(frame);
            }
            recorder.stop();
            grabber.stop();
        } finally {
            if (grabber != null) {
                grabber.stop();
            }
            if (Objects.nonNull(countDownLatch)){
                countDownLatch.countDown();
            }
        }
    }

    /**
     * 获取视频时长，单位为秒
     *
     * @param videoFile
     * @return 时长（s）
     */
    public static Long getVideoTime(String videoFile) {
        Long times = 0L;
        try {
            FFmpegFrameGrabber ff = new FFmpegFrameGrabber(new File(videoFile));
            ff.start();
            times = ff.getLengthInTime() / (1000 * 1000);
            ff.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return times;
    }

    /**
     * 按帧录制本机摄像头视频（边预览边录制，停止预览即停止录制）
     *
     * @author eguid
     * @param outputFile -录制的文件路径，也可以是rtsp或者rtmp等流媒体服务器发布地址
     * @param frameRate - 视频帧率
     * @throws Exception
     */
    public static void recordCamera(String outputFile, double frameRate) throws Exception{
        Loader.load(opencv_objdetect.class);
        FrameGrabber grabber = FrameGrabber.createDefault(0);//本机摄像头默认0，这里使用javacv的抓取器，至于使用的是ffmpeg还是opencv，请自行查看源码
        grabber.start();//开启抓取器

        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();//转换器
        IplImage grabbedImage = converter.convert(grabber.grab());//抓取一帧视频并将其转换为图像，至于用这个图像用来做什么？加水印，人脸识别等等自行添加
        int width = grabbedImage.width();
        int height = grabbedImage.height();

        FrameRecorder recorder = FrameRecorder.createDefault(outputFile, width, height);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); // avcodec.AV_CODEC_ID_H264，编码
        recorder.setFormat("flv");//封装格式，如果是推送到rtmp就必须是flv封装格式
        recorder.setFrameRate(frameRate);

        recorder.start();//开启录制器
        long startTime=0;
        long videoTS=0;
        CanvasFrame frame = new CanvasFrame("camera", CanvasFrame.getDefaultGamma() / grabber.getGamma());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        Frame rotatedFrame=converter.convert(grabbedImage);//不知道为什么这里不做转换就不能推到rtmp
        while (frame.isVisible() && (grabbedImage = converter.convert(grabber.grab())) != null) {
            rotatedFrame = converter.convert(grabbedImage);
            frame.showImage(rotatedFrame);
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }
            videoTS = 1000 * (System.currentTimeMillis() - startTime);
            recorder.setTimestamp(videoTS);
            recorder.record(rotatedFrame);
            Thread.sleep(40);
        }

        frame.dispose();
        recorder.stop();
        recorder.release();
        grabber.stop();

    }


    public static void main(String[] args) throws Exception {
        long timeMillis = System.currentTimeMillis();
        //fetchFrame("D:\\zyy\\MP4\\test.mp4",".jpg",10);
        //Long videoTime = getVideoTime("D:\\zyy\\MP4\\test.mp4");
        String inputFile = "rtsp://10.111.32.84:5454/W6.mp4";
        String outputFile = "D:\\zyy\\MP4\\record.mp4";
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        VideoUtil.frameRecord(inputFile, outputFile,0, 5, countDownLatch);
        countDownLatch.await();
        //recordCamera("D:\\zyy\\MP4\\test2.mp4",25);
        System.out.println("耗时："+(System.currentTimeMillis() - timeMillis)/1000+"秒");
    }

}
