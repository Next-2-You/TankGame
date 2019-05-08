package com.chen.entity;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.chen.GamePanel;
import com.chen.util.Constant;

public class Heart extends Common{

	public Heart(int x, int y, GamePanel gamePanel) {
		super(x, y, gamePanel);
		try {
			image=ImageIO.read(new File(Constant.BLOOD));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
