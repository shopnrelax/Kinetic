/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.talentica.kinetic;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author KUNAL
 */
public class KineticApp {

    /**
     * @param args the command line arguments
     */
    
    public String loadtxt(String f) //loads the first SRT found
{
   IContainer container = IContainer.make();
 
   container.open(f, IContainer.Type.READ, null);
 
   // query how many streams the call to open found
   int numStreams = container.getNumStreams();
   int SRTStreamId = -1;
 
   for(int i = 0; i < numStreams; i++)
   {
         // Find the stream object
         IStream stream = container.getStream(i);
         // Get the pre-configured decoder that can decode this stream;
         IStreamCoder coder = stream.getStreamCoder();
         //SRT files appear with these codes
         if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_UNKNOWN &&
             coder.getCodecID() == ICodec.ID.CODEC_ID_NONE)
         {
             SRTStreamId = i;
 
             i = numStreams;//break
         }
    }
 
 
    IPacket packet = IPacket.make();
    String subs = "";
    
    while(container.readNextPacket(packet) >= 0)
    {
         /*
          * Now we have a packet, let's see if it belongs to our SRT stream
          */
         if (packet.getStreamIndex() == SRTStreamId)
         {
               try {
                   //this is where we get the text from the packet
                   byte[] bytes = packet.getData().getByteArray(0, packet.getSize());
                   subs = subs.concat(new String(bytes, "UTF-8"));
 
               } catch (UnsupportedEncodingException ex) {
                   Logger.getLogger(KineticApp.class.getName()).log(Level.SEVERE, null, ex);
               }
         }
 
     }
        return subs;
}
    public static void main(String[] args) {
        // TODO code application logic here
        
        ConvertVideo video=new ConvertVideo(new File("C:\\Users\\KUNAL\\Music\\yes  boss\\myVideo1.flv"), new File("C:\\Users\\KUNAL\\Music\\yes  boss\\myVideo1.mp4"));
        video.run();
    }
    
}
