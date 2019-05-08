package com.chen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.chen.entity.Base;
import com.chen.entity.Heart;
import com.chen.entity.Bomb;
import com.chen.entity.Bullet;
import com.chen.entity.Common;
import com.chen.entity.EnemyTank;
import com.chen.entity.Tank;
import com.chen.entity.Wall;
import com.chen.type.DirectionType;
import com.chen.type.WallType;
import com.chen.type.WinType;
import com.chen.util.Constant;
import com.chen.util.ReadMapUtil;

/**
 * 游戏面板
 * 
 * @author Next 2 You
 * @2018年6月23日 上午11:08:02
 */
public class GamePanel extends JPanel implements KeyListener {

	private BufferedImage mainImage;
	private Graphics2D g2;
	private TankClient tankClient;
	private List<Bomb> bombs;
	private List<Bullet> bullets;
	private List<EnemyTank> tanks;
	private List<Wall> walls;// 普通墙、铁墙、灌木丛
	private List<Wall> rivers;// 河 （和普通墙、铁墙、灌木丛分开存，解决子弹从河下穿过的BUG）
	private List<Heart> bloods;
	private Tank myTank;
	private Base base;
	private boolean finish = false; // 游戏是否结束
	private int enemyNumber; // 剩余数量
	private int currentNumber; // 在场数量
	private WinType winType; // 判断游戏输赢
	private int[][] birthPlace = { { 65, 157 }, { 574, 244 }, { 999, 58 }, { 905, 400 } };// 电脑坦克随机出现的地方
	private int birthCount;// 剩余复活数
	private boolean state;// true运行 false暂停

	public GamePanel(TankClient tankClient) {
		super();
		this.tankClient = tankClient;
		tankClient.addKeyListener(this);
		init();
		new reflesh(this).start();
	}

	public void init() {
		// 利用双缓冲技术
		mainImage = new BufferedImage(Constant.CLIENT_WIDTH, Constant.CLIENT_HIGH, BufferedImage.TYPE_INT_BGR);
		g2 = (Graphics2D) mainImage.getGraphics();
		bombs = new ArrayList<>();
		bullets = new ArrayList<>();
		tanks = new ArrayList<>();
		walls = new ArrayList<>();
		bloods = new ArrayList<>();
		rivers = new ArrayList<>();
		enemyNumber = 16;
		currentNumber = 4;
		birthCount = 2;
		state = true;
		winType = WinType.UNKNOWN;
		myTank = new Tank(454, 725, this); // 初始化己方坦克
		initMap(); // 初始化地图
		tanks.add(new EnemyTank(68, 157, this));// 初始化敌方坦克
		tanks.add(new EnemyTank(574, 244, this));
		tanks.add(new EnemyTank(999, 58, this));
		tanks.add(new EnemyTank(905, 400, this));

		bloods.add(new Heart(672, 306, this));// 初始化心道具
	}

	// 初始化地图
	public void initMap() {
		base = new Base(597, 681, this);// 初始化基地
		Properties mapProperties = ReadMapUtil.getMapProperties();
		String str = null;
		int[][] array = null;

		str = (String) mapProperties.getProperty("COMMONWALL");// 初始化普通墙
		array = getArray(str);
		createWall(array, WallType.COMMONWALL);

		str = (String) mapProperties.getProperty("METALWALL");// 初始化铁墙
		array = getArray(str);
		createWall(array, WallType.METALWALL);

		str = (String) mapProperties.getProperty("RIVER");// 初始化河流
		array = getArray(str);
		createWall(array, WallType.RIVER);

		//放最后，不然在灌木丛与其他地图组件的交接处会出现坦克处于地图组件下面的BUG
		str = (String) mapProperties.getProperty("BRUSHWOOD");// 初始化灌木丛
		array = getArray(str);
		createWall(array, WallType.BRUSHWOOD);
	}

	public int[][] getArray(String str) {
		String[] myStr = str.split(";");
		String[] saveStr;
		int[][] saveInt = new int[myStr.length][2];
		for (int i = 0; i < myStr.length; i++) {
			saveStr = myStr[i].split(",");
			if (saveStr != null) {
				saveInt[i][0] = Integer.valueOf(saveStr[0]);
				saveInt[i][1] = Integer.valueOf(saveStr[1]);
			}
		}
		return saveInt;
	}

	public void createWall(int[][] array, WallType wallType) {
		if (!wallType.equals(WallType.RIVER)) {
			for (int i = 0; i < array.length; i++) {
				walls.add(new Wall(array[i][0], array[i][1], this, wallType));
			}
		} else {
			for (int i = 0; i < array.length; i++) {
				rivers.add(new Wall(array[i][0], array[i][1], this, wallType));
			}
		}
	}

	public void drawMainImage() {
		g2.clearRect(0, 0, Constant.CLIENT_WIDTH, Constant.CLIENT_HIGH);//清除画面
		g2.setBackground(Color.white);
		if (myTank.isIfLive()) {
			myTank.draw(g2);
		}
		if (base.isIfLive()) {
			base.draw(g2);
		}

		for (int i = 0; i < tanks.size(); i++) {
			if (tanks.get(i).isIfLive()) {
				tanks.get(i).draw(g2);
			} else {
				tanks.remove(i);
			}
		}

		for (int i = 0; i < rivers.size(); i++) {   //和要比子弹先画
			if (!rivers.get(i).isIfLive()) {
				rivers.remove(i);
			} else {
				rivers.get(i).draw(g2);
			}
		}

		for (int i = 0; i < bullets.size(); i++) {
			if (!bullets.get(i).isIfLive()) {
				bullets.remove(i);
			} else {
				bullets.get(i).draw(g2);
			}
		}

		for (int i = 0; i < walls.size(); i++) {   //灌木丛要比子弹和坦克后画
			if (!walls.get(i).isIfLive()) {
				walls.remove(i);
			} else {
				walls.get(i).draw(g2);
			}
		}

		for (int i = 0; i < bombs.size(); i++) {
			if (!bombs.get(i).isIfLive()) {
				bombs.remove(i);
			} else {
				bombs.get(i).draw(g2);
			}

		}
		for (int i = 0; i < bloods.size(); i++) {
			if (!bloods.get(i).isIfLive()) {
				bloods.remove(i);
			} else {
				bloods.get(i).draw(g2);
			}
		}

		drawNumber(g2);// 画敌人数量
		drawBirthCount(g2);// 画剩余复活次数

		if (enemyNumber == 0 && currentNumber == 0) {
			winType = WinType.PLAYER;
		}

		if (!winType.equals(WinType.UNKNOWN)) {// 出结果
			drawResult();
			finish = true;
		}

		if (enemyNumber != 0 && currentNumber != 4) { // 生成敌人
			EnemyTank tank = null;
			while (tank == null) {
				tank = creatEnemy();
			}
			tanks.add(tank);
			currentNumber++;
			enemyNumber--;
			new Thread(tank.getAutoAction()).start();// 启动线程
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		drawMainImage();
		g.drawImage(mainImage, 0, 0, Constant.CLIENT_WIDTH, Constant.CLIENT_HIGH, this);
	}

	private class reflesh extends Thread {
		private GamePanel gamePanel;//用来移除键盘监听
		
		public reflesh(GamePanel gamePanel) {
			super();
			this.gamePanel = gamePanel;
		}
		@Override
		public void run() {
			while (!finish) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				repaint();
			}
			repaint();
			tankClient.removeKeyListener(gamePanel);
			//closeAllThread();//真正停止其他线程（爆炸，子弹，电脑坦克）
			tankClient.setjPanel(new EndPanel(tankClient, mainImage));
		}
	}

	public void drawResult() {
		g2.setFont(new Font("微软雅黑", ALLBITS, 80));
		g2.setColor(Color.YELLOW);
		if (WinType.COMPUTER.equals(winType)) {
			g2.drawString("YOU FAILED", this.getWidth() / 2 - 220, this.getHeight() / 2 - 80);
		} else {
			g2.drawString("YOU WIN", this.getWidth() / 2 - 170, this.getHeight() / 2 - 80);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (state) {
			switch (code) {
			case KeyEvent.VK_W:
				myTank.actionUp();
				break;
			case KeyEvent.VK_S:
				myTank.actionDown();
				break;
			case KeyEvent.VK_A:
				myTank.actionLeft();
				break;
			case KeyEvent.VK_D:
				myTank.actionRight();
				break;
			case KeyEvent.VK_J:
				myTank.attach();
				break;
			case KeyEvent.VK_SPACE:
				Common.state = false;//让线程wait（子弹、爆炸、电脑坦克）
				state = false;//己方坦克失去键盘控制
				break;
			case KeyEvent.VK_ESCAPE:
				Common.state = false;
				state = false;
				pressEscKey();
			}
		} else {
			if (code == KeyEvent.VK_SPACE) {
				state = true;//己方坦克恢复键盘控制
				startGame();//唤醒所有线程
				return;
			}
			if(code==KeyEvent.VK_ESCAPE){
				pressEscKey();
			}
		}
	}
	
	public void pressEscKey(){
		int showConfirmDialog = JOptionPane.showConfirmDialog(this,"是否停止游戏并返回主页？", "",
				JOptionPane.YES_OPTION, JOptionPane.NO_OPTION);
		if (showConfirmDialog == JOptionPane.YES_OPTION) {
			System.out.println("进来了");
			startGame();//先唤醒所有线程
			closeAllThread();////真正停止其他线程（爆炸，子弹，电脑坦克）
			tankClient.removeKeyListener(this);
			tankClient.setjPanel(new StartPanel(tankClient));	
		}else{
			state = true;//己方坦克恢复键盘控制
			startGame();//唤醒所有线程
		}
	}
	
	
	//关闭所有线程
	public void closeAllThread(){
		for(int i=0;i<bombs.size();i++){
			bombs.get(i).setIfLive(false);
		}
		for(int i=0;i<bullets.size();i++){
			bullets.get(i).setIfLive(false);
		}
		for(int i=0;i<tanks.size();i++){
			tanks.get(i).setIfLive(false);
		}
	}
	
	
	
	private void startGame() {
		synchronized (Common.class) {
			Common.state = true;
			Common.class.notifyAll();
		}
	}

	public void drawNumber(Graphics g) {
		g.setFont(new Font("微软雅黑", ALLBITS, 20));
		g.setColor(Color.BLACK);
		g.drawString("剩余未出现敌人的数量: " + enemyNumber, this.getWidth() / 2 - 80, 20);
	}

	public void drawBirthCount(Graphics g) {
		g.setFont(new Font("微软雅黑", ALLBITS, 20));
		g.setColor(Color.BLACK);
		g.drawString("剩余复活次数: " + birthCount, this.getWidth() / 2 + 300, 20);
	}

	// 随机创建敌人
	public EnemyTank creatEnemy() {
		Random r = new Random();
		int placeNumber = r.nextInt(4);
		int x = birthPlace[placeNumber][0];
		int y = birthPlace[placeNumber][1];
		EnemyTank enemyTank = null;
		Bullet bullet = null;
		Wall wall = null;
		EnemyTank birthEnemey = new EnemyTank();

		if (myTank.isIfLive() && birthEnemey.ifTouch(myTank, x, y)) {// 位置是否和我的坦克重叠
			return null;
		}

		if (base.isIfLive() && birthEnemey.ifTouch(base, x, y)) {// 位置是否和基地重叠
			return null;
		}

		for (int i = 0; i < tanks.size(); i++) { // 位置是否和敌人坦克重叠
			enemyTank = tanks.get(i);
			if (enemyTank.isIfLive() && birthEnemey.ifTouch(enemyTank, x, y)) {
				return null;
			}
		}

		for (int i = 0; i < bullets.size(); i++) {// 位置是否和子弹重叠
			bullet = bullets.get(i);
			if (bullet.isIfLive() && birthEnemey.ifTouch(bullet, x, y)) {
				return null;
			}
		}
		for (int i = 0; i < walls.size(); i++) {// 位置是否和地图物体重叠
			wall = walls.get(i);
			if (wall.isIfLive() && birthEnemey.ifTouch(wall, x, y)) {
				return null;
			}
		}

		birthEnemey.setX(x);
		birthEnemey.setY(y);
		birthEnemey.setGamePanel(this);
		return birthEnemey;
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	public List<Bullet> getBullets() {
		return bullets;
	}

	public void setBullets(List<Bullet> bullets) {
		this.bullets = bullets;
	}

	public List<EnemyTank> getTanks() {
		return tanks;
	}

	public void setTanks(List<EnemyTank> tanks) {
		this.tanks = tanks;
	}

	public List<Wall> getWalls() {
		return walls;
	}

	public void setWalls(List<Wall> walls) {
		this.walls = walls;
	}

	public Tank getMyTank() {
		return myTank;
	}

	public void setMyTank(Tank myTank) {
		this.myTank = myTank;
	}

	public List<Bomb> getBombs() {
		return bombs;
	}

	public void setBombs(List<Bomb> bombs) {
		this.bombs = bombs;
	}

	public boolean isFinish() {
		return finish;
	}

	public void setFinish(boolean finish) {
		this.finish = finish;
	}

	public Base getBase() {
		return base;
	}

	public void setBase(Base base) {
		this.base = base;
	}

	public int getEnemyNumber() {
		return enemyNumber;
	}

	public void setEnemyNumber(int enemyNumber) {
		this.enemyNumber = enemyNumber;
	}

	public int getCurrentNumber() {
		return currentNumber;
	}

	public void setCurrentNumber(int currentNumber) {
		this.currentNumber = currentNumber;
	}

	public WinType getWinType() {
		return winType;
	}

	public void setWinType(WinType winType) {
		this.winType = winType;
	}

	public List<Heart> getBloods() {
		return bloods;
	}

	public void setBloods(List<Heart> bloods) {
		this.bloods = bloods;
	}

	public int getBirthCount() {
		return birthCount;
	}

	public void setBirthCount(int birthCount) {
		this.birthCount = birthCount;
	}

	public List<Wall> getRivers() {
		return rivers;
	}

	public void setRivers(List<Wall> rivers) {
		this.rivers = rivers;
	}

	
}
