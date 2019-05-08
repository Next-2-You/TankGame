package com.chen.entity;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.chen.GamePanel;
import com.chen.util.Constant;

public class Bomb extends Common implements Runnable{
	private static Image[] images;
	
	static{
		try {
			images=new Image[3];
			images[0]=ImageIO.read(new File(Constant.BOMB1));
			images[1]=ImageIO.read(new File(Constant.BOMB2));
			images[2]=ImageIO.read(new File(Constant.BOMB3));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public Bomb(int x, int y,GamePanel gamePanel) {
		super(x,y,gamePanel);	
		image=images[0];
	}

	@Override
	public void run() {
		for(int i=0;i<images.length;i++){
			try {
				ifRun();
				Thread.sleep(50);
				image=images[i];
			} catch (InterruptedException e) {			
				e.printStackTrace();
			}
		}
		ifLive=false;
	}

}
