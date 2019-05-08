package com.chen.entity;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import com.chen.GamePanel;
import com.chen.type.DirectionType;
import com.chen.type.WallType;
import com.chen.type.WinType;
import com.chen.util.Constant;

public class Bullet extends Common implements Runnable {
	private DirectionType direction;
	private int speed;
	private Tank tank;

	public Bullet(int x, int y, GamePanel gamePanel, DirectionType direction, Tank tank) {
		super(x, y, gamePanel);
		this.direction = direction;
		this.speed = 10;
		this.tank = tank;
		try {
			this.image = ImageIO.read(new File(Constant.BULLET));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (ifLive) {
			try {
				ifRun();
				Thread.sleep(50);
				if (direction.equals(DirectionType.Up)) {
					if (!ifTouchWall(x, y - speed) && !ifTouchTank(x, y - speed) && !ifTransboundary(x, y - speed)) {
						y = y - speed;
						continue;
					}
					ifLive = false;
					break;
				} else if (direction.equals(DirectionType.Right)) {
					if (!ifTouchWall(x + speed, y) && !ifTouchTank(x + speed, y) && !ifTransboundary(x + speed, y)) {
						x = x + speed;
						continue;
					}
					ifLive = false;
					break;
				} else if (direction.equals(DirectionType.Left)) {
					if (!ifTouchWall(x - speed, y) && !ifTouchTank(x - speed, y) && !ifTransboundary(x - speed, y)) {
						x = x - speed;
						continue;
					}
					ifLive = false;
					break;
				} else {
					if (!ifTouchWall(x, y + speed) && !ifTouchTank(x, y + speed) && !ifTransboundary(x, y + speed)) {
						y = y + speed;
						continue;
					}
					ifLive = false;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	//创建点是否有普通墙或者敌人
	public boolean firstRun(){
		if(ifTouchWall(x, y)){
			return true;
		};
		if(ifTouchTank(x, y)){
			return true;
		}
		return false;
	}
	
	

	// 子弹是否打到墙
	public boolean ifTouchWall(int next_x, int next_y) {
		List<Wall> walls = gamePanel.getWalls();
		Wall wall = null;
		for (int i = 0; i < walls.size(); i++) {
			wall = walls.get(i);
			if (wall.isIfLive() && ifTouch(wall, next_x, next_y)) {// 墙活着并且碰到墙
				if (wall.getType().equals(WallType.BRUSHWOOD))// 子弹可以穿过草地、 河流(河流存在rivers中，所以不用判断）
					return false;
				else if (wall.getType().equals(WallType.COMMONWALL)) {// 达到普通墙
					wall.ifLive = false;
					this.ifLive = false;
					return true;
				} else {// 铁墙
					this.ifLive = false;
					return true;
				}
			}
		}
		return false;
	}

	// 判断是否打到坦克
	public boolean ifTouchTank(int next_x, int next_y) {
		List<EnemyTank> tanks = gamePanel.getTanks();
		EnemyTank tank = null;
		Tank myTank = gamePanel.getMyTank();
		for (int i = 0; i < tanks.size(); i++) {
			tank = tanks.get(i);
			if (this.tank instanceof EnemyTank) {// 如果子弹是敌人发出的
				if (tank.isIfLive() && ifTouch(tank, next_x, next_y)) {
					this.ifLive = false;//只有子弹销毁
					return true;
				} else {
					if (myTank.isIfLive() && ifTouch(myTank, next_x, next_y)) {
						myTank.ifLive = false;
						this.ifLive = false;
						Bomb bomb = new Bomb(myTank.getX(), myTank.getY(), gamePanel);
						gamePanel.getBombs().add(bomb);
						new Thread(bomb).start(); // 开启炸弹线程
						if(gamePanel.getBirthCount()!=0){
							gamePanel.setMyTank(new Tank(454, 725, gamePanel));
							gamePanel.setBirthCount(gamePanel.getBirthCount()-1);
						}else{
							gamePanel.setWinType(WinType.COMPUTER);
							gamePanel.setFinish(true);// 游戏结束
						}
						return true;
					}
					if(gamePanel.getBase().ifLive&&ifTouch(gamePanel.getBase(), next_x, next_y)){
						gamePanel.getBase().setIfLive(false);
						this.ifLive=false;
						Bomb bomb = new Bomb(gamePanel.getBase().getX(), gamePanel.getBase().getY(), gamePanel);
						gamePanel.getBombs().add(bomb);
						gamePanel.setWinType(WinType.COMPUTER);
						new Thread(bomb).start(); // 开启炸弹线程
						gamePanel.setFinish(true);//游戏结束
					}
				
				}
			} else {// 如果子弹是自己发出的
				if (tank.isIfLive() && ifTouch(tank, next_x, next_y)) {
					tank.ifLive = false;
					this.ifLive = false;
					gamePanel.setCurrentNumber(gamePanel.getCurrentNumber()-1);   //在场数量减1
					if(ifCreateHeart()){  //是否产生心形道具
						Heart blood = new Heart(tank.getX(),tank.getY(),gamePanel);
						gamePanel.getBloods().add(blood);
					}
					Bomb bomb = new Bomb(tank.getX(), tank.getY(), gamePanel);		
					gamePanel.getBombs().add(bomb);
					new Thread(bomb).start(); // 开启炸弹线程
					return true;
				} else {
					if (myTank.isIfLive() && ifTouch(myTank, next_x, next_y)) {
						this.ifLive = false;//只有子弹销毁
						return true;
					}
					if(gamePanel.getBase().ifLive&&ifTouch(gamePanel.getBase(), next_x, next_y)){
						gamePanel.getBase().setIfLive(false);
						this.ifLive=false;
						Bomb bomb = new Bomb(gamePanel.getBase().getX(), gamePanel.getBase().getY(), gamePanel);
						gamePanel.getBombs().add(bomb);
						new Thread(bomb).start(); // 开启炸弹线程
						gamePanel.setWinType(WinType.COMPUTER);
						gamePanel.setFinish(true);//游戏结束
					}
				}

			}

		}
		return false;
	}

	// 判断是否打到基地
	public boolean ifTouchBase(int next_x, int next_y) {
		Base base = gamePanel.getBase();
		return ifTouch(base, next_x, next_y);
	}
	
	//是否产生心形道具
	public boolean ifCreateHeart(){
		Random r = new Random();
		int i = r.nextInt(4);
		if(i==0){
			return true;
		}
		return false;
	}
	
	

}
