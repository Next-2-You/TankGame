package com.chen.entity;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.chen.GamePanel;
import com.chen.type.WallType;
import com.chen.util.Constant;

public class Wall extends Common{
	private WallType type;
	private static Map<WallType, Image> map;
	
	static{
		try {
			map=new HashMap<>();
			map.put(WallType.BRUSHWOOD, ImageIO.read(new File(Constant.BRUSHWOOD)));
			map.put(WallType.COMMONWALL, ImageIO.read(new File(Constant.COMMONWALL)));
			map.put(WallType.METALWALL, ImageIO.read(new File(Constant.METALWALL)));
			map.put(WallType.RIVER, ImageIO.read(new File(Constant.RIVER)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Wall(int x, int y, GamePanel gamePanel,WallType type) {
		super(x, y, gamePanel);
		this.type=type;
		setImage();
		
	}

	public WallType getType() {
		return type;
	}

	public void setType(WallType type) {
		this.type = type;
	}

	public void setImage(){
		image=map.get(type);
	}
	
	
	
	
}
