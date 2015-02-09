/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.talentica.kinetic.srt;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.commons.io.FilenameUtils;
import subtitleFile.FormatASS;
import subtitleFile.FormatSCC;
import subtitleFile.FormatSRT;
import subtitleFile.FormatSTL;
import subtitleFile.FormatTTML;
import subtitleFile.TimedTextFileFormat;
import subtitleFile.TimedTextObject;

/**
 *
 * @author KUNAL
 */
public class ReadSrt {
    final String outputFormat="SRT";
    static String outputFile="C:\\Users\\KUNAL\\Music\\yes  boss\\srt\\out.srt";
    
    public static TimedTextObject getsrtcaption(String inputFile) throws Exception {
        TimedTextFileFormat ttff;
        TimedTextObject tto;
        //inputFile = "C:\\Users\\KUNAL\\Music\\yes  boss\\srt\\Avengers.2012.Eng.Subs.srt";
//        final FilenameFilter filenamefilter=new FilenameFilter() {
//
//            public boolean accept(File dir, String name) {
//                //throw new UnsupportedOperationException("Not supported yet.");
//                return name.endsWith(".jpg");
//            }
//        };
        String inputFormat = FilenameUtils.getExtension(inputFile);
        
        if ("SRT".equalsIgnoreCase(inputFormat)){
                ttff = new FormatSRT();
        } else if ("STL".equalsIgnoreCase(inputFormat)){
                ttff = new FormatSTL();
        } else if ("SCC".equalsIgnoreCase(inputFormat)){
                ttff = new FormatSCC();
        } else if ("XML".equalsIgnoreCase(inputFormat)){
                ttff = new FormatTTML();
        } else if ("ASS".equalsIgnoreCase(inputFormat)){
                ttff = new FormatASS();
        } else {
                throw new Exception("Unrecognized input format: "+inputFormat+" only [SRT,STL,SCC,XML,ASS] are possible");
        }
        if (!"SRT".equalsIgnoreCase(inputFormat)){
            File file = new File(inputFile);
            InputStream is = new FileInputStream(file);
            tto = ttff.parseFile(file.getName(), is);
            IOClass.writeFileTxt(outputFile, tto.toSRT());
        }else{
          outputFile=inputFile;  
        }
        
        File outsrt = new File(outputFile);
        InputStream is_outsrt = new FileInputStream(outsrt);
        ttff = new FormatSRT();
        tto = ttff.parseFile(outsrt.getName(), is_outsrt);
//        for(int entry  : tto.captions.keySet()){
//            System.out.println(tto.captions.get(entry).content);
//        }
        //System.out.println("");
        return tto;
    }
    
}
