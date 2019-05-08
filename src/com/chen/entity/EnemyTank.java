package com.chen.entity;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import com.chen.GamePanel;
import com.chen.type.DirectionType;
import com.chen.type.WallType;
import com.chen.util.Constant;

public class EnemyTank extends Tank {
	private Random random = new Random();
	protected static Map<DirectionType, Image> enemyTankMap;
	private AutoAction autoAction;

	static {
		try {
			enemyTankMap = new HashMap<>();
			enemyTankMap.put(DirectionType.Up, ImageIO.read(new File(Constant.ENEMYU)));
			enemyTankMap.put(DirectionType.Left, ImageIO.read(new File(Constant.ENEMYL)));
			enemyTankMap.put(DirectionType.Down, ImageIO.read(new File(Constant.ENEMYD)));
			enemyTankMap.put(DirectionType.Right, ImageIO.read(new File(Constant.ENEMYR)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public EnemyTank(){
		this.direction = DirectionType.Down;
		setImage(direction);
		this.ifLive = true;
		this.ifCooling = false;
		this.speed = 4;
		autoAction=new AutoAction();
	}
	public EnemyTank(int x, int y, GamePanel gamePanel) {
		this.x = x;
		this.y = y;
		this.gamePanel = gamePanel;
		this.direction = DirectionType.Down;
		this.ifLive = true;
		this.ifCooling = false;
		this.speed = 4;
		setImage(direction);
		autoAction=new AutoAction();
		new Thread(autoAction).start();
	}

	public void setImage(DirectionType direction) {
		image = enemyTankMap.get(direction);
	}
	
	

	private class AutoAction implements Runnable {
		int number;
		int bulletNumber;

		@Override
		public void run() {
			while (ifLive) {
				ifRun();
				number = random.nextInt(6);
				switch (number) {
				case 0:
					actionUp();
					break;
				case 1:
					actionDown();
					break;
				case 2:
					actionLeft();
					break;
				case 3:
					actionRight();
					break;
				case 4:
				case 5:
					bulletNumber = random.nextInt(10);
					for (int i = 0; i < bulletNumber; i++) {
						attach();
					}
				}

			}
		}
	}
	public AutoAction getAutoAction() {
		return autoAction;
	}
	public void moveUp() {
		int space = random.nextInt(20);
		for (int i = 0; i < space; i++) {
			try {
				Thread.sleep(100);
				if (!ifTransboundary(x, y - speed) && !ifTouchTank(x, y - speed) && !ifTouchWall(x, y - speed)) {
					y -= speed;
					if(this.ifLive&&ifGetBlood()){
						gamePanel.setEnemyNumber(gamePanel.getEnemyNumber()+1);
					}
				} else {
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public void moveDown() {
		int space = random.nextInt(20);
		for (int i = 0; i < space; i++) {
			try {
				Thread.sleep(100);
				if (!ifTransboundary(x, y + speed) && !ifTouchTank(x, y + speed) && !ifTouchWall(x, y + speed)) {
					y += speed;
					if(this.ifLive&&ifGetBlood()){
						gamePanel.setEnemyNumber(gamePanel.getEnemyNumber()+1);
					}
				} else {
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public void moveRight() {
		int space = random.nextInt(20);
		for (int i = 0; i < space; i++) {
			try {
				Thread.sleep(100);
				if (!ifTransboundary(x + speed, y) && !ifTouchTank(x + speed, y) && !ifTouchWall(x + speed, y)) {
					x += speed;
					if(this.ifLive&&ifGetBlood()){
						gamePanel.setEnemyNumber(gamePanel.getEnemyNumber()+1);
					}
				} else {
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public void moveLeft() {
		int space = random.nextInt(20);
		for (int i = 0; i < space; i++) {
			try {
				Thread.sleep(100);
				if (!ifTransboundary(x - speed, y) && !ifTouchTank(x - speed, y) && !ifTouchWall(x - speed, y)) {
					x -= speed;
					if(this.ifLive&&ifGetBlood()){
						gamePanel.setEnemyNumber(gamePanel.getEnemyNumber()+1);
					}
				} else {
					break;
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// 判断是否碰到坦克
	public boolean ifTouchTank(int next_x, int next_y) {
		List<EnemyTank> tanks = gamePanel.getTanks();
		EnemyTank tank = null;
		for (int i = 0; i < tanks.size(); i++) {
			tank = tanks.get(i);
			if (!tank.equals(this)) {// 判断是不是自己
				if (tank.isIfLive() && ifTouch(tank, next_x, next_y))
					return true;
			}
		}
		if (ifTouch(gamePanel.getMyTank(), next_x, next_y))// 是否会撞到玩家坦克
			return true;
		return false;
	}

}
