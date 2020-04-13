package com.zyy.utils;

import com.madgag.gif.fmsware.AnimatedGifEncoder;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * @author zhouyuyang_vendor
 */
public class TurnGifUtil {

    /**
     * 截取视频指定帧生成gif
     * @param videofile 视频文件
     * @param startFrame 开始帧
     * @param frameCount 截取帧数
     * @param frameRate 帧频率（默认：10）
     * @param margin 每截取一次跳过多少帧（默认：3）
     * @throws IOException 截取的长度超过视频长度
     */
    public static void buildGif(String videofile,Integer startFrame,Integer frameCount,Integer frameRate,Integer margin) throws IOException {
        if(Objects.isNull(margin)){
            margin = 3;
        }
        if(Objects.isNull(frameRate)){
            frameRate = 10;
        }
        if (Objects.isNull(startFrame)){
            startFrame = 0;
        }
        FileOutputStream targetFile = new FileOutputStream(videofile.substring(0,videofile.lastIndexOf("."))+".gif");
        FFmpegFrameGrabber ff = new FFmpegFrameGrabber(videofile);
        Java2DFrameConverter converter = new Java2DFrameConverter();
        ff.start();
        try {
            System.out.println("视频总帧数为：" + ff.getLengthInFrames());
            if (Objects.isNull(frameCount)){
                frameCount = ff.getLengthInFrames();
            }
            if(startFrame>ff.getLengthInFrames() & (startFrame+frameCount)>ff.getLengthInFrames()) {
                throw new RuntimeException("视频太短了");
            }
            ff.setFrameNumber(startFrame);
            AnimatedGifEncoder en = new AnimatedGifEncoder();
            en.setFrameRate(frameRate);
            en.start(targetFile);
            en.setRepeat(0);
            for(int i=0;i<frameCount;i++) {
                en.addFrame(converter.convert(ff.grab()));
                ff.setFrameNumber(ff.getFrameNumber()+margin);
            }
            en.finish();
        }finally {
            ff.stop();
            ff.close();
        }
    }




   public static void main(String[] args) throws Exception {
       long timeMillis = System.currentTimeMillis();
       //fetchFrame("D:\\zyy\\MP4\\test.mp4",".jpg",10);
       buildGif("D:\\zyy\\MP4\\record.mp4", 0, 20, 3,3);
       //Long videoTime = getVideoTime("D:\\zyy\\MP4\\test.mp4");
       System.out.println("耗时："+(System.currentTimeMillis() - timeMillis)/1000+"秒");
    }


}
