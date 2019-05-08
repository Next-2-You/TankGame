package com.chen.entity;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import com.chen.GamePanel;
import com.chen.util.Constant;

public class Common {
	protected int x;

	protected int y;

	protected GamePanel gamePanel;

	protected Image image;

	protected boolean ifLive;

	public static boolean state;

	public Common() {
		super();
	}

	public Common(int x, int y, GamePanel gamePanel) {
		super();
		this.x = x;
		this.y = y;
		this.gamePanel = gamePanel;
		this.ifLive = true;
		this.state = true;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public boolean isIfLive() {
		return ifLive;
	}

	public void setIfLive(boolean ifLive) {
		this.ifLive = ifLive;
	}

	public GamePanel getGamePanel() {
		return gamePanel;
	}

	public void setGamePanel(GamePanel gamePanel) {
		this.gamePanel = gamePanel;
	}

	// 画图片
	public void draw(Graphics g) {
		g.drawImage(image, x, y, image.getWidth(null), image.getHeight(null), gamePanel);
	}

	// 判断是否越界
	public boolean ifTransboundary() {
		if (x < 0 || x > gamePanel.WIDTH - image.getWidth(null) || y < 0
				|| y > gamePanel.HEIGHT - image.getHeight(null))
			return true;
		return false;
	}

	// 判断是否越界
	public boolean ifTransboundary(int x, int y) {
		if (x < 0 || x > (gamePanel.getWidth() - this.image.getWidth(null)) || y < 0
				|| y > (gamePanel.getHeight() - this.image.getHeight(null))) {
			return true;
		}
		return false;
	}

	// 碰撞检测
	public boolean ifTouch(Common c, int next_x, int next_y) {
		Rectangle me = getRectanle(next_x, next_y);
		Rectangle you = c.getRectanle();
		return me.intersects(you);
	}

	public Rectangle getRectanle(int next_x, int next_y) {
		return new Rectangle(next_x, next_y, image.getWidth(null), image.getHeight(null));
	}

	public Rectangle getRectanle() {
		return new Rectangle(x, y, image.getWidth(null), image.getHeight(null));
	}

	public void ifRun() {
		if (!this.state) {
			synchronized (Common.class) {
				try {
					Common.class.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
