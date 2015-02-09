/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.talentica.kinetic;

import com.talentica.kinetic.srt.subtitleFile.Caption;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IRational;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;
import subtitleFile.TimedTextObject;

/**
 *
 * @author KUNAL
 */
public class VideoGenerator {

    public static double FRAME_RATE = 20;

    public static int SECONDS_TO_RUN_FOR = 20;

    private static final String outputFilename = "C:\\Users\\KUNAL\\Music\\yes  boss\\myVideo.mp4";

    private static Dimension screenBounds;

    private static Map<String, File> imageMap = new HashMap<>();
    private static List<String> imageList=new ArrayList<>();
    private static Map<Integer,Long> imageTime=new HashMap<>();
    
    
    public static void setimageTime(int key,long value){
        imageTime.put(key, value);
    }
    public static void main(String[] args) {
        
    }
    public static void generatevideo(TimedTextObject tto) throws InterruptedException {

        final IMediaWriter writer = ToolFactory.makeWriter(outputFilename);

        screenBounds = Toolkit.getDefaultToolkit().getScreenSize();

//        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4,screenBounds.width / 2, screenBounds.height / 2);
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4,IRational.make(FRAME_RATE),screenBounds.width / 2, screenBounds.height / 2);
        final FilenameFilter filenamefilter=new FilenameFilter() {

            public boolean accept(File dir, String name) {
                //throw new UnsupportedOperationException("Not supported yet.");
                System.out.println("Getting the base names=>"+FilenameUtils.getBaseName(name));
                return FilenameUtils.getBaseName(name).matches("\\d+");
            }
        };
        File folder = new File("C:\\Users\\KUNAL\\Music\\yes  boss\\images");
        File[] listOfFiles = folder.listFiles(filenamefilter);

        int indexVal = 0;
        Arrays.sort(listOfFiles, new Comparator<File>() {

            @Override
            public int compare(File o1, File o2) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                int n1 = Integer.parseInt(FilenameUtils.getBaseName(o1.getName()));
                int n2 = Integer.parseInt(FilenameUtils.getBaseName(o2.getName()));
                return n1 - n2;
            }
        });
        for (File file : listOfFiles) {
            if (file.isFile()) {
                indexVal++;
                System.out.println("file.getName() :"+file.getName());
                imageMap.put(file.getName(), file);
                imageList.add(file.getName());
            }
        }
        int index=1;long timeindex=0;
        //for (int index = 1; index <= SECONDS_TO_RUN_FOR * FRAME_RATE; index++) {
        //for (int index = 1; index <= listOfFiles.length; index++) {
        Caption caption=new Caption();
        long timeinbetween=0,lastendtime=0,starttime=System.currentTimeMillis();
        for(int key :tto.captions.keySet()){
            caption=tto.captions.get(key);
            if(lastendtime==0){
                timeinbetween=caption.start.mseconds; 
                
            }else{
               timeinbetween=caption.start.mseconds-lastendtime; 
            }
            lastendtime=caption.end.mseconds;
            BufferedImage bgrScreen=null;
            
            BufferedImage screen=null;
            
            if(timeinbetween>0){
                System.out.println("Getting the blank image");
                screen = getImage(index,true);
                bgrScreen = convertToType(screen, BufferedImage.TYPE_3BYTE_BGR);
                
                timeindex+=timeinbetween;
                System.out.println("Time for the blank image=>"+timeinbetween);
                writer.encodeVideo(0, bgrScreen, System.currentTimeMillis()-starttime, TimeUnit.MILLISECONDS);
                Thread.sleep(timeinbetween);
            }
            
            screen = getImage(index,false);

            //BufferedImage bgrScreen = convertToType(screen, BufferedImage.TYPE_3BYTE_BGR);
            bgrScreen = convertToType(screen, BufferedImage.TYPE_3BYTE_BGR);
            System.out.println("Time for the image=>"+imageTime.get(key));
            timeindex+=imageTime.get(key);
            System.out.println("Timeindex for the image=>"+timeindex);
            
            writer.encodeVideo(0, bgrScreen, System.currentTimeMillis()-starttime, TimeUnit.MILLISECONDS);
            Thread.sleep(imageTime.get(key));

            index++;
        }
        // tell the writer to close and write the trailer if needed
        writer.close();
        System.out.println("Video Created");

    }

    public static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
        BufferedImage image;
        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        }
        else {
            image = new BufferedImage(sourceImage.getWidth(),
            sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }
        return image;
    }

    private static BufferedImage getImage(int index,boolean blank) {

        try {
//            if(index>=imageList.size()){
//                final java.util.Random generator = new java.util.Random();
//                final int MIN = 1;
//                final int MAX = 10;
//                int randomNumber = generator.nextInt(MAX - MIN) + MIN;
//                
//                System.out.println("Index was greater than array"+index+" chnaged to =>"+randomNumber);
//                index=randomNumber;
//            }
            File img =null;
            if(!blank){            
                System.out.println("index :" + (index-1));
                String fileName=imageList.get(index-1);
                System.out.println("fileName :" + fileName);
                img = imageMap.get(fileName);
            }else{
                img=new File("C:\\Users\\KUNAL\\Music\\yes  boss\\images\\blank.jpg");
            }

            BufferedImage in=null;
            if (img != null) {
                System.out.println("img :"+img.getName());
                in = ImageIO.read(img);
            }else
            {
                System.out.println("++++++++++++++++++++++++++++++++++++++index :" + index);
                img = imageMap.get("1");
                in = ImageIO.read(img);
            }
            return in;

        }

        catch (Exception e) {

            e.printStackTrace();

            return null;

        }

    }

}