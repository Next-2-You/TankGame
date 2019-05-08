package com.chen.entity;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.chen.GamePanel;
import com.chen.util.Constant;

public class Base extends Common{

	public Base(int x, int y, GamePanel gamePanel) {
		super(x,y,gamePanel);
		try {
			this.image=ImageIO.read(new File(Constant.HOME));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
