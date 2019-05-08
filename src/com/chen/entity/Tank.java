package com.chen.entity;

import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.chen.GamePanel;
import com.chen.type.DirectionType;
import com.chen.type.WallType;
import com.chen.util.Constant;

public class Tank extends Common {
	protected DirectionType direction;
	protected int speed = 5;
	protected static Map<DirectionType, Image> map;
	protected boolean ifCooling=false;
	
	
	static{
		try {
			map = new HashMap<>();
			map.put(DirectionType.Up, ImageIO.read(new File(Constant.TANKU)));
			map.put(DirectionType.Left, ImageIO.read(new File(Constant.TANKL)));
			map.put(DirectionType.Down, ImageIO.read(new File(Constant.TANKD)));
			map.put(DirectionType.Right, ImageIO.read(new File(Constant.TANKR)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public Tank() {
	}

	public Tank(int x, int y, GamePanel gamePanel) {
		super(x, y, gamePanel);	
		this.direction = DirectionType.Up;
		setImage(this.direction);
	}

	public DirectionType getDirection() {
		return direction;
	}

	public void setDirection(DirectionType direction) {
		this.direction = direction;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	//改变图片
	public void setImage(DirectionType direction) {
		image = map.get(direction);
	}


	public void moveUp() {
		y -= speed;
	}

	public void moveDown() {
		y += speed;
	}

	public void moveRight() {
		x += speed;
	}

	public void moveLeft() {
		x -= speed;
	}

	
	//攻击
	public void attach(){
		if(!ifCooling){
			Bullet bullet = createBullet();
			if(!bullet.firstRun()){  //是否创建点刚好有敌人或者普通墙   ， 要这个，不然有BUG
				this.gamePanel.getBullets().add(bullet);//添加子弹到游戏面板
				new Thread(bullet).start();
			}
			ifCooling=true;
			new Thread(new cooling()).start();
		}
	}


	// 创建子弹
	public Bullet createBullet() {
		Bullet bullet = null;	
		if (this.direction.equals(DirectionType.Up)) {
			bullet = new Bullet(x+(this.image.getWidth(null)-17)/2, y-17, gamePanel, DirectionType.Up,this);
		} else if (this.direction.equals(DirectionType.Right)) {
			bullet = new Bullet(x + this.image.getWidth(null), y +(this.image.getHeight(null)-17)/2, gamePanel, DirectionType.Right,this);
		} else if (this.direction.equals(DirectionType.Left)) {
			bullet = new Bullet(x - 17, y + (this.image.getHeight(null)-17)/2, gamePanel, DirectionType.Left,this);
		} else {
			bullet = new Bullet(x + (this.image.getWidth(null)-17)/2, y + this.image.getHeight(null), gamePanel, DirectionType.Down,this);
		}
		return bullet;
	}
	

	private class cooling implements Runnable{
		@Override
		public void run() {
			while(ifCooling){
				try {
					Thread.sleep(200);
					ifCooling=false;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}	
			}	
		}
	}
	
	//向上动作
	public void actionUp(){
		if (direction.equals(DirectionType.Up)
				&& !ifTransboundary(x, y - speed)&&!ifTouchTank(x, y-speed)&&!ifTouchWall(x, y-speed)&&!ifTouchBase(x, y-speed)) {
			moveUp();// 移动
			if(this.ifLive&&ifGetBlood()){
				gamePanel.setBirthCount(gamePanel.getBirthCount()+1);
			}
		} else {
			setDirection(DirectionType.Up);
			setImage(DirectionType.Up);// 转换方向
		}
	}
	
	//向下动作
	public void actionDown(){
		if (direction.equals(DirectionType.Down)
				&& !ifTransboundary(x, y + speed)&&!ifTouchTank(x, y+speed)&&!ifTouchWall(x, y+speed)&&!ifTouchBase(x, y+speed)) {
			moveDown();
			if(this.ifLive&&ifGetBlood()){
				gamePanel.setBirthCount(gamePanel.getBirthCount()+1);
			}
		} else {
			setDirection(DirectionType.Down);
			setImage(DirectionType.Down);// 转换方向
		}
	}
	
	//向左动作
	public void actionLeft(){
		if (direction.equals(DirectionType.Left)
				&& !ifTransboundary(x - speed, y)&&!ifTouchTank(x-speed,y)&&!ifTouchWall(x-speed, y)&&!ifTouchBase(x-speed, y)) {
			moveLeft();
			if(this.ifLive&&ifGetBlood()){
				gamePanel.setBirthCount(gamePanel.getBirthCount()+1);
			}
		} else {
			setDirection(DirectionType.Left);
			setImage(DirectionType.Left);// 转换方向
		}
		
	}
	//向右动作
	public void actionRight(){
		if (direction.equals(DirectionType.Right)
				&& !ifTransboundary(x + speed, y)&&!ifTouchTank(x+speed, y)&&!ifTouchWall(x+speed, y)&&!ifTouchBase(x+speed, y)) {
			moveRight();
			if(this.ifLive&&ifGetBlood()){
				gamePanel.setBirthCount(gamePanel.getBirthCount()+1);
			}
		} else {
			setDirection(DirectionType.Right);
			setImage(DirectionType.Right);// 转换方向
		}
		
	}
	
	//判断是否碰到墙
	public boolean ifTouchWall(int next_x,int next_y){
		List<Wall> walls = gamePanel.getWalls();
		Wall wall=null;
		for(int i=0;i<walls.size();i++){
			wall=walls.get(i);
			if(wall.isIfLive()&&ifTouch(wall,next_x,next_y)){//墙活着并且碰到墙
				if(wall.getType().equals(WallType.BRUSHWOOD))//灌木丛
					return false;
				else
					return true;//普通墙，铁墙（河流存在rivers中）
			}
		}
		if(ifTouchRiver(next_x,next_y)){
			return true;
		}
		return false;
	}
	public boolean ifTouchRiver(int next_x,int next_y){
		List<Wall> rivers = gamePanel.getRivers();
		Wall river=null;
		for(int i=0;i<rivers.size();i++){
			river=rivers.get(i);
			if(river.isIfLive()&&ifTouch(river,next_x,next_y)){//河活着并且碰到河
				return true;
			}
		}
		return false;
	}
	
	
	
	
	//判断是否碰到坦克
	public boolean ifTouchTank(int next_x,int next_y){
		List<EnemyTank> tanks = gamePanel.getTanks();
		EnemyTank tank=null;
		for(int i=0;i<tanks.size();i++){
			tank=tanks.get(i);
			if(tank.isIfLive()&&ifTouch(tank,next_x,next_y))
					return true;
		}
		return false;
	}
	
	//坦克是否碰到基地
	public boolean ifTouchBase(int next_x,int next_y){
		return ifTouch(gamePanel.getBase(), next_x, next_y);
	}
	
	//是否得到心形道具
	public boolean ifGetBlood(){
		List<Heart> bloods = gamePanel.getBloods();
		Heart blood=null;
		for(int i=0;i<bloods.size();i++){
			blood=bloods.get(i);
			if(ifTouch(blood, x, y)){//当前位置是否吃到心道具
				blood.setIfLive(false);
				return true;
			}
		}
		return false;
	}
	
	
	

	@Override
	public String toString() {
		return "Tank [direction=" + direction + ", x=" + x + ", y=" + y + "]";
	}
	
	
	
	

}
