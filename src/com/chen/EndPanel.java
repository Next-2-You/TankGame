package com.chen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.chen.util.Constant;

public class EndPanel extends JPanel implements KeyListener{
	
	private TankClient tankClient;
	private BufferedImage mainImage;
	private Image pointAt;
	private int pointAt_x=210;
	private int pointAt_y=568;
	private int type=1;
	private String[] strs=new String[]{"重新开始","回到主页","退出游戏"};
	
	
	public EndPanel(TankClient tankClient, BufferedImage mainImage) {
		super();
		this.tankClient = tankClient;
		this.mainImage = mainImage;
		tankClient.addKeyListener(this);
		try {
			this.pointAt = ImageIO.read(new File(Constant.POINTAT));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.clearRect(0, 0, Constant.CLIENT_WIDTH, Constant.CLIENT_HIGH);
		g.drawImage(mainImage, 0, 0, mainImage.getWidth(null), mainImage.getHeight(null),this);
		g.drawImage(pointAt, pointAt_x, pointAt_y, pointAt.getWidth(null), pointAt.getHeight(null),this);
		g.setFont(new Font("微软雅黑", ALLBITS,40));
		g.setColor(Color.YELLOW);
		for(int i=0;i<strs.length;i++){
			g.drawString(strs[i], 250+i*250, 600);
		}
	}
	

	@Override
	public void keyTyped(KeyEvent e) {
		
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		switch(code){
		case KeyEvent.VK_RIGHT:
			if(pointAt_x<640){
				pointAt_x+=250;
				type++;
			}
			repaint();
			break;
		case KeyEvent.VK_LEFT:
			if(pointAt_x>210){
				pointAt_x-=250;
				type--;
			}
			repaint();
			break;
		case KeyEvent.VK_ENTER:
			if(type==1){
				tankClient.removeKeyListener(this);
				GamePanel gamePanel = new GamePanel(tankClient);
				tankClient.setjPanel(gamePanel);
			}else if(type==2){
				tankClient.removeKeyListener(this);
				StartPanel startPanel = new StartPanel(tankClient);
				tankClient.setjPanel(startPanel);
			}else{
				System.exit(0);
			}
		}	
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		
	}
	
	

}
