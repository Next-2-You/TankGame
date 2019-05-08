package com.chen.util;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.chen.TankClient;


public class MusicUtil {
	private static File file = null;
	private static AudioInputStream audioInputStream = null;
	private static AudioFormat audioFormat = null;
	private static SourceDataLine soureDataLine = null;
	private static DataLine.Info info = null;
	private static boolean state;

	public static void EndMusic() {
		if (soureDataLine != null) {
			state = false;
			soureDataLine.stop();
		}
	}

	public static void PlayMusic(String url) {
		file = new File(url);
		System.out.println(file.getPath());
		System.out.println(file.getAbsolutePath());
		try {
			audioInputStream = AudioSystem.getAudioInputStream(file);
		} catch (UnsupportedAudioFileException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		audioFormat = audioInputStream.getFormat();
		info = new DataLine.Info(SourceDataLine.class, audioFormat);

		try {
			soureDataLine = (SourceDataLine) AudioSystem.getLine(info);
			soureDataLine.open(audioFormat);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		soureDataLine.start();
		int nBytesRead = 0;
		// 这是缓冲
		byte[] abData = new byte[512];

		try {
			while (nBytesRead != -1) {
				nBytesRead = audioInputStream.read(abData, 0, abData.length);
				if (nBytesRead >= 0)
					soureDataLine.write(abData, 0, nBytesRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} finally {
			soureDataLine.drain();
			soureDataLine.close();
		}
	}

	public class MusicThread extends Thread {
		@Override
		public void run() {
			state = true;
			while (state) {
				PlayMusic(Constant.MUSIC);
			}
		}
	}

}
