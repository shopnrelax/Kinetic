/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.talentica.kinetic;

import com.talentica.kinetic.srt.ReadSrt;
import com.talentica.kinetic.srt.subtitleFile.Caption;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import subtitleFile.Time;
import subtitleFile.TimedTextObject;

/**
 *
 * @author KUNAL
 */
public class TextToGraphics {
    
    public static void main(String[] args) {
        TimedTextObject tto;String text;
        Caption caption = new Caption();
//        System.out.println("text Length=>"+text.length());
//        texttoimage(text, "C:\\Users\\KUNAL\\Music\\yes  boss\\images\\2.jpg");
        
        try {
            tto=ReadSrt.getsrtcaption("C:\\\\Users\\\\KUNAL\\\\Music\\\\yes  boss\\\\srt\\\\Avengers.2012.Eng.Subs.srt");
            long totaltime=0;
            int index=0;
            for(int entry  : tto.captions.keySet()){
                index++;
                System.out.println(tto.captions.get(entry).content);
                text=tto.captions.get(entry).content.replaceAll("\\<.*?>","");
                texttoimage(text, "C:\\Users\\KUNAL\\Music\\yes  boss\\images\\"+entry+".jpg");
                
                caption = tto.captions.get(entry);
                long end=caption.end.mseconds;
                System.out.println("End  time=>"+end);
                
                long start=caption.start.mseconds;
                System.out.println("Start  time=>"+start);
                if(index==tto.captions.size())
                    totaltime=tto.captions.get(entry).end.mseconds;
                VideoGenerator.setimageTime(entry, (end-start+1));
            }
            //writing a blank image 
            texttoimage("", "C:\\Users\\KUNAL\\Music\\yes  boss\\images\\blank.jpg");
            System.out.println("total time=>"+(totaltime/1000));
            VideoGenerator.FRAME_RATE=20;
            VideoGenerator.SECONDS_TO_RUN_FOR=(int)totaltime/1000+1;
            VideoGenerator.generatevideo(tto);
            //texttoimage("hello1", "C:\\Users\\KUNAL\\Music\\yes  boss\\images\\hello2.png");
        } catch (Exception ex) {
            Logger.getLogger(TextToGraphics.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void texttoimage(String text,String output_file) {
        //String text = "Hello";

        /*
           Because font metrics is based on a graphics context, we need to create
           a small, temporary image so we can ascertain the width and height
           of the final image
         */
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = img.createGraphics();
        Font font = new Font("Arial", Font.PLAIN, 30);
        //g2d.setFont(font);
        FontMetrics fm = null;
        //int width = fm.stringWidth(text);
        int width=720;
        //int height = fm.getHeight();
        int height=480;
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(Color.WHITE);
        StringBuilder formattedtext=new StringBuilder("");
        System.out.println("Found Text size =>"+fm.stringWidth(text));
        if(fm.stringWidth(text)>700){
            System.out.println("Found Text size >700");
            String textsplit[]=text.split("\\s+");
            
            int splitcount=0,timessplit=fm.stringWidth(text)/600;
            int checksplit=timessplit;
            while(checksplit*100>400){
                int size_font=(checksplit*100-400/(checksplit*100))*30;
                System.out.println("New Font size=>"+size_font);
                font = new Font("Arial", Font.PLAIN, size_font);
                checksplit--;
            }
            g2d.setFont(font);
            fm = g2d.getFontMetrics();
            while(splitcount<textsplit.length){
                if(fm.stringWidth(formattedtext.toString())>500){
                    System.out.println("Reset count=>"+(400-timessplit*100));
                    g2d.drawString(formattedtext.toString(), 0, (400-timessplit*100)+fm.getAscent());
                    formattedtext=new StringBuilder(" ");
                    timessplit--;
                }
                if(splitcount!=0){
                    formattedtext.append(" ").append(textsplit[splitcount]);
                }else{
                   formattedtext.append(textsplit[splitcount]); 
                }
                
                splitcount++;
            }
            
            g2d.drawString(formattedtext.toString(), 0, 400+fm.getAscent());
        }else if(fm.stringWidth(text)>400){
            g2d.drawString(text, 0, 400+fm.getAscent());
        }else{
           g2d.drawString(text, 30, 400+fm.getAscent()); 
        }
        
        g2d.dispose();
        try {
            ImageIO.write(img, "jpg", new File(output_file));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}
