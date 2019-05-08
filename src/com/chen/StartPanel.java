package com.chen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.chen.util.Constant;

public class StartPanel extends JPanel implements KeyListener{

	private TankClient tankClient;
	private Image bgImage;
	private Image pointAt;
	private int pointAt_x=210;
	private int pointAt_y=618;
	private int type=1;
	private String[] strs=new String[]{"开始游戏","帮助","退出游戏"};
	
	public StartPanel(TankClient tankClient) {
		this.tankClient=tankClient;
		init();
	}
	
	public void init(){
		tankClient.addKeyListener(this);
		try {
			bgImage= ImageIO.read(new File(Constant.STARTBACKGROUND));
			pointAt= ImageIO.read(new File(Constant.POINTAT));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void paint(Graphics g) {
		g.drawImage(bgImage, 0, 0, bgImage.getWidth(null), bgImage.getHeight(null),this);
		g.drawImage(pointAt, pointAt_x, pointAt_y, pointAt.getWidth(null), pointAt.getHeight(null),this);
		g.setFont(new Font("微软雅黑", ALLBITS,40));
		g.setColor(Color.YELLOW);
		for(int i=0;i<strs.length;i++){
			g.drawString(strs[i], 250+i*250, 650);
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
				JOptionPane.showMessageDialog(this, Constant.HELP);
			}else{
				System.exit(0);
			}
		}	
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		
	}
	
	
}
