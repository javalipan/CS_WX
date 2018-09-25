package com.block.utils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class ImgUtil {
	private Image img;  
    private int width;  
    private int height;
    private int cutWith=1000;
    private int cutHeight=800;
    
    public ImgUtil(String fileName) throws IOException {  
        File file = new File(fileName);// 读入文件  
        img = ImageIO.read(file);      // 构造Image对象  
        width = img.getWidth(null);    // 得到源图宽  
        height = img.getHeight(null);  // 得到源图长  
    }  

    /** 
     * 按照宽度还是高度进行压缩 
     * @param w int 最大宽度 
     * @param h int 最大高度 
     */  
    public void resizeFix(int w, int h,OutputStream os) throws IOException {  
        if (width / height > w / h) {  
            resizeByWidth(w,os);  
        } else {  
            resizeByHeight(h,os);  
        }  
    }  
    /** 
     * 以宽度为基准，等比例放缩图片 
     * @param w int 新宽度 
     */  
    public void resizeByWidth(int w,OutputStream os) throws IOException {  
        int h = (int) (height * w / width);  
        resize(w, h,os);  
    }  
    
    public void resizeAuto(OutputStream os) throws IOException {  
    	int expectWidth = this.width;  
		int expectHeight = this.height; 
		if (this.width > this.height && this.width > cutWith) {  
            expectWidth = cutWith;  
            expectHeight = expectWidth * this.height / this.width;  
        } else if (this.height > this.width && this.height > cutHeight) {  
            expectHeight = cutHeight;   
            expectWidth = expectHeight * this.width / this.height;  
        }
		resize(expectWidth,expectHeight,os);
    }
    /** 
     * 以高度为基准，等比例缩放图片 
     * @param h int 新高度 
     */  
    public void resizeByHeight(int h,OutputStream os) throws IOException {  
        int w = (int) (width * h / height);  
        resize(w, h,os);  
    }  
    /** 
     * 强制压缩/放大图片到固定的大小 
     * @param w int 新宽度 
     * @param h int 新高度 
     */  
    public void resize(int w, int h,OutputStream os) throws IOException {  
        // SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢  
        BufferedImage image = new BufferedImage(w, h,BufferedImage.TYPE_INT_RGB );   
        image.getGraphics().drawImage(img, 0, 0, w, h, null); // 绘制缩小后的图  
        // 可以正常实现bmp、png、gif转jpg  
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);  
        encoder.encode(image); // JPEG编码  
    }  
}
