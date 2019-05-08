package com.chen;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.security.Key;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.chen.util.Constant;
import com.chen.util.MusicUtil;
import com.chen.util.MusicUtil.MusicThread;

public class TankClient extends JFrame implements KeyListener {

	private JPanel jPanel = null;
	private static boolean musicState;//控制音乐

	public TankClient() {
		super("坦克大战");
		this.setSize(Constant.CLIENT_WIDTH, Constant.CLIENT_HIGH);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setResizable(false);// 防止拉伸
		addKeyListener(this);
	}

	public JPanel getjPanel() {
		return jPanel;
	}

	public void setjPanel(JPanel jPanel) {
		Container c = this.getContentPane();
		c.removeAll();
		c.add(jPanel);
		c.validate();// 重新加载 调用paint
	}

	public static void main(String[] args) {
		TankClient tankClient = new TankClient();
		StartPanel startPanel = new StartPanel(tankClient);
		tankClient.setjPanel(startPanel);
		new MusicUtil().new MusicThread().start();//播放音乐
		musicState=true;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		switch (code) {
		case KeyEvent.VK_Z:
			if (musicState == false) {
				new MusicUtil().new MusicThread().start();
				musicState=true;
			}
			break;
		case KeyEvent.VK_X:
			if(musicState==true){
				MusicUtil.EndMusic();
				musicState=false;
			}
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
