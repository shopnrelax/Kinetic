/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.talentica.kinetic;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.AudioSamplesEvent;
import com.xuggle.mediatool.event.IAddStreamEvent;
import com.xuggle.mediatool.event.IAudioSamplesEvent;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.mediatool.event.VideoPictureEvent;
import com.xuggle.xuggler.IAudioResampler;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IPixelFormat.Type;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import java.io.File;

/**
 *
 * @author KUNAL
 */
public class ConvertVideo extends MediaToolAdapter implements Runnable {
 private int VIDEO_WIDTH = 712;
 private int VIDEO_HEIGHT = 428;
 
 private IMediaWriter writer;
 private IMediaReader reader;
 private File outputFile;
 
 public ConvertVideo(File inputFile, File outputFile) {
 this.outputFile = outputFile;
 reader = ToolFactory.makeReader(inputFile.getAbsolutePath());
 reader.addListener(this);
 
// IMediaWriter writer = ToolFactory.makeWriter(outputFile.toString(),reader);
// int sampleRate = 44100;
// int channels = 1;
// writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, channels, sampleRate);
// while (reader.readPacket() == null);

}
 
 private IVideoResampler videoResampler = null;
 private IAudioResampler audioResampler = null;
 
 @Override
 public void onAddStream(IAddStreamEvent event) {
 int streamIndex = event.getStreamIndex();
 IStreamCoder streamCoder = event.getSource().getContainer().getStream(streamIndex).getStreamCoder();
 if (streamCoder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
 writer.addAudioStream(streamIndex, streamIndex, 2, 44100);
 } else if (streamCoder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
 streamCoder.setWidth(VIDEO_WIDTH);
 streamCoder.setHeight(VIDEO_HEIGHT);
 writer.addVideoStream(streamIndex, streamIndex, VIDEO_WIDTH, VIDEO_HEIGHT);
 }
 super.onAddStream(event);
 }
 
 @Override
 public void onVideoPicture(IVideoPictureEvent event) {
 IVideoPicture pic = event.getPicture();
 Type pixelType=Type.YUV420P;
 if (videoResampler == null) {
 //videoResampler = IVideoResampler.make(VIDEO_WIDTH, VIDEO_HEIGHT, pic.getPixelType(), pic.getWidth(), pic.getHeight(), pic.getPixelType());
     videoResampler = IVideoResampler.make(VIDEO_WIDTH, VIDEO_HEIGHT, pixelType, pic.getWidth(), pic.getHeight(), pic.getPixelType());
 }
 //IVideoPicture out = IVideoPicture.make(pic.getPixelType(), VIDEO_WIDTH, VIDEO_HEIGHT);
 IVideoPicture out = IVideoPicture.make(pixelType, VIDEO_WIDTH, VIDEO_HEIGHT);
 videoResampler.resample(out, pic);
 
 IVideoPictureEvent asc = new VideoPictureEvent(event.getSource(), out, event.getStreamIndex());
 super.onVideoPicture(asc);
 out.delete();
 }
 
 @Override
 public void onAudioSamples(IAudioSamplesEvent event) {
 IAudioSamples samples = event.getAudioSamples();
 if (audioResampler == null) {
 audioResampler = IAudioResampler.make(2, samples.getChannels(), 44100, samples.getSampleRate());
 }
 if (event.getAudioSamples().getNumSamples() > 0) {
 IAudioSamples out = IAudioSamples.make(samples.getNumSamples(), samples.getChannels());
 audioResampler.resample(out, samples, samples.getNumSamples());
 
 AudioSamplesEvent asc = new AudioSamplesEvent(event.getSource(), out, event.getStreamIndex());
 super.onAudioSamples(asc);
 out.delete();
 }
 }
 
 @Override
 public void run() {
 writer = ToolFactory.makeWriter(outputFile.getAbsolutePath(), reader);
 this.addListener(writer);
 while (reader.readPacket() == null) {
 }
 }
 
}
