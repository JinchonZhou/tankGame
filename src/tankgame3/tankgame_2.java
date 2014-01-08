/* 
 * File:   tankgame_2.cpp
 * Usage:  main Java program
 * Author: Jinchong Zhou
 *
 * Created on March, 2012
 * Modified on Jan, 2014
 */


package tankgame3;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.*;
import java.io.*;
import javax.sound.sampled.*;

class status {

	final static int totalLevel = 7;

	int level;

	// 0:nothing, 1:lose 2:win 3:prevlose 4: prevwin
	int win;

	//
	boolean change;

	status() {
		this.level = 0;
		this.win = 0;
		this.change = false;
	}
}

public class tankgame_2 extends JFrame implements ActionListener, Runnable {

	MyPanel mp = null;
	MyStartPanel msp = null;
	winPanel wp = null;
	winPanel2 wp2 = null;
	losePanel lp = null;
	JMenuBar jmb = null;
	JMenu jm1 = null;
	JMenuItem jmi1 = null;
	JMenuItem jmi2 = null;
	JMenuItem jmi3 = null;
	JMenuItem jmi4 = null;
	JMenuItem jmi5 = null;

	// Recorder rec = null;

	status currStatus = new status();

	public static void main(String[] args) {

		tankgame_2 myGame = new tankgame_2();

		Thread tt = new Thread(myGame);
		tt.start();
	}

	public tankgame_2() {

		// Recorder rec = new Recorder();
		jmb = new JMenuBar();
		jm1 = new JMenu("Game");
		jm1.setMnemonic('G');

		jmi1 = new JMenuItem("start new game");
		jmi3 = new JMenuItem("next round");
		jmi4 = new JMenuItem("try again");
		jmi5 = new JMenuItem("exit");

		jmi5.addActionListener(this);
		jmi5.setActionCommand("exit");
		jmi3.addActionListener(this);
		jmi3.setActionCommand("next");
		jmi4.addActionListener(this);
		jmi4.setActionCommand("again");
		jmi3.setEnabled(false);
		jmi4.setEnabled(false);

		jm1.add(jmi1);

		jm1.add(jmi3);
		jm1.add(jmi4);
		jm1.add(jmi5);
		jmb.add(jm1);

		msp = new MyStartPanel();
		Thread tt = new Thread(msp);
		tt.start();

		jmi1.addActionListener(this);
		jmi1.setActionCommand("new");
		this.setJMenuBar(jmb);
		this.add(msp);

		this.setSize(500, 450);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("new")) {

			if (currStatus.level > 0)
				this.remove(mp);
			else
				this.remove(msp);

			if (currStatus.win == 4)
				this.remove(wp);

			if (currStatus.win == 3)
				this.remove(lp);

			mp = new MyPanel(1, currStatus);
			Thread t = new Thread(mp);
			this.addKeyListener(mp);
			this.add(mp);
			currStatus.level = 1;
			t.start();
			this.setVisible(true);
			jmi2.setEnabled(false);
		}

		if (e.getActionCommand().equals("exit")) {

			System.exit(0);
		}

		if (e.getActionCommand().equals("next")) {

			currStatus.level++;
			currStatus.change = true;
		}

		if (e.getActionCommand().equals("again")) {

			currStatus.change = true;

		}

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		while (true) {

			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (currStatus.change) {

				currStatus.change = false;

				if (currStatus.win == 1) {

					jmi4.setEnabled(true);
					jmi3.setEnabled(false);
					lp = new losePanel();
					this.add(lp);
					this.remove(mp);
					currStatus.win = 3;

					this.setVisible(true);
				}

				else if (currStatus.win == 2) {

					jmi3.setEnabled(true);
					jmi4.setEnabled(false);
					if (currStatus.level == currStatus.totalLevel) {

						wp2 = new winPanel2();
						jmi3.setEnabled(false);
						this.add(wp2);

					}

					else {

						wp = new winPanel();
						this.add(wp);
					}
					this.remove(mp);
					currStatus.win = 4;

					this.setVisible(true);
				}

				else {

					if (currStatus.win == 4) {
						this.remove(wp);

					} else if (currStatus.win == 3) {
						this.remove(lp);
					}
					currStatus.win = 0;
					mp = new MyPanel(currStatus.level, currStatus);
					this.addKeyListener(mp);
					this.add(mp);
					Thread t = new Thread(mp);
					this.remove(msp);
					t.start();
					this.setVisible(true);
				}
			}
		}
	}
}

// my panel
class MyPanel extends JPanel implements java.awt.event.KeyListener, Runnable {

	status myStatus;
	final static int width = 400;
	final static int height = 300;
	Hero hero = null;
	Vector<EnemyTank> ets = new Vector<EnemyTank>();
	final static int enNum[] = { 10, 14, 17, 22, 28, 32, 35, 40 };
	int enSize;

	// three image makes one bomb

	Image image1, image2, image3 = null;

	Vector<Bomb> bombs = new Vector<Bomb>();

	int mylife = 3;

	private FileReader fr = null;

	private BufferedReader br = null;

	public MyPanel(int level, status parentStatus) {

		enSize = enNum[level];

		myStatus = parentStatus;

		image1 = Toolkit.getDefaultToolkit().getImage(
				Panel.class.getResource("/bomb_1.gif"));

		image2 = Toolkit.getDefaultToolkit().getImage(
				Panel.class.getResource("/bomb_2.gif"));

		image3 = Toolkit.getDefaultToolkit().getImage(
				Panel.class.getResource("/bomb_3.gif"));

		hero = new Hero((int) (Math.random() * width),
				(int) (Math.random() * height));

		for (int i = 0; i < enSize; i++) {

			EnemyTank et = new EnemyTank((int) (Math.random() * width),
					(int) (Math.random() * height));
			Thread t = new Thread(et);
			t.start();
			ets.add(et);

		}
	}

	public void showinfo(Graphics g) {

		this.drawTank(70, 320, g, 0, 1);
		g.setColor(Color.black);
		g.drawString(enSize + " ", 95, 340);
		this.drawTank(120, 320, g, 0, 0);
		g.setColor(Color.black);
		g.drawString(mylife + " ", 145, 340);
		g.drawString("level: " + myStatus.level, 185, 340);
	}

	public void paint(Graphics g) {

		super.paint(g);
		this.showinfo(g);

		if (mylife > 0)
			this.drawTank(hero.getX(), hero.getY(), g, hero.getDirect(), 0);

		for (int i = 1; i <= hero.s.size(); i++) {

			if (hero.s.get(i - 1).isLive) {
				g.setColor(Color.red);
				g.draw3DRect(hero.s.get(i - 1).x, hero.s.get(i - 1).y, 1, 1,
						false);
			} else {
				hero.s.remove(i - 1);
				i--;
			}
		}

		for (int i = 0; i < ets.size(); i++) {

			if (ets.get(i).isLive) {
				this.drawTank(ets.get(i).getX(), ets.get(i).getY(), g, ets.get(
						i).getDirect(), 1);

				for (int j = 1; j <= ets.get(i).s.size(); j++) {
					if (ets.get(i).s.get(j - 1).isLive == true) {
						g.setColor(Color.black);
						g.draw3DRect(ets.get(i).s.get(j - 1).x, ets.get(i).s
								.get(j - 1).y, 1, 1, false);
					} else {
						ets.get(i).s.remove(j - 1);
						j--;
					}
				}
			} else {
				ets.remove(i);
				i--;
			}
		}

		for (int i = 0; i < bombs.size(); i++) {

			Bomb b = bombs.get(i);

			if (b.life > 6)
				g.drawImage(image1, b.x, b.y, 30, 30, this);

			else if (b.life > 3)
				g.drawImage(image2, b.x, b.y, 30, 30, this);

			else
				g.drawImage(image3, b.x, b.y, 30, 30, this);

			b.lifeDown();
			if (b.life == 0) {
				bombs.remove(i);
				i--;
			}
		}
	}

	// function to judge whether a bullet has shot the tank
	public void hittank(Shot s, Tank et) {
		switch (et.getDirect()) {
		case 0:
		case 2:
			if (s.x >= et.x && s.x <= et.x + 20 && s.y >= et.y
					&& s.y <= et.y + 30) {
				s.isLive = false;
				et.isLive = false;
				enSize--;

				// create a bomb
				Bomb newbomb = new Bomb(et.getX(), et.getY());
				bombs.add(newbomb);
			}
			break;
		case 1:
		case 3:
			if (s.x > et.x && s.x < et.x + 30 && s.y > et.y && s.y < et.y + 20) {
				s.isLive = false;
				et.isLive = false;
				enSize--;

				// create a bomb
				Bomb newbomb = new Bomb(et.getX(), et.getY());
				bombs.add(newbomb);
			}
		}
	}

	public void hitmytank(Shot s, Tank et) {
		switch (et.getDirect()) {
		case 0:
		case 2:
			if (s.x >= et.x && s.x <= et.x + 20 && s.y >= et.y
					&& s.y <= et.y + 30) {
				s.isLive = false;
				mylife--;

				// create a bomb
				Bomb newbomb = new Bomb(et.getX(), et.getY());
				bombs.add(newbomb);
			}
			break;

		case 1:
		case 3:

			if (s.x > et.x && s.x < et.x + 30 && s.y > et.y && s.y < et.y + 20) {

				s.isLive = false;
				mylife--;

				// create a bomb
				Bomb newbomb = new Bomb(et.getX(), et.getY());
				bombs.add(newbomb);
			}
		}
	}

	public void drawTank(int x, int y, Graphics g, int direct, int type) {
		switch (type) {
		case 0:
			g.setColor(Color.yellow);
			break;
		case 1:
			g.setColor(Color.blue);
		}

		switch (direct) {
		case 0:
			g.fill3DRect(x, y, 5, 30, false);
			g.fill3DRect(x + 15, y, 5, 30, false);
			g.fill3DRect(x + 5, y + 5, 10, 20, false);
			g.fillOval(x + 4, y + 10, 10, 10);
			g.drawLine(x + 9, y + 15, x + 9, y - 4);
			break;

		case 1:
			g.fill3DRect(x + 5, y + 5, 30, 5, false);
			g.fill3DRect(x + 5, y + 20, 30, 4, false);
			g.fill3DRect(x + 10, y + 10, 20, 10, false);
			g.fillOval(x + 15, y + 10, 10, 10);
			g.drawLine(x + 20, y + 15, x + 40, y + 15);
			break;

		case 2:
			g.fill3DRect(x, y, 5, 30, false);
			g.fill3DRect(x + 15, y, 5, 30, false);
			g.fill3DRect(x + 5, y + 5, 10, 20, false);
			g.fillOval(x + 4, y + 10, 10, 10);
			g.drawLine(x + 9, y + 15, x + 9, y + 28);
			break;

		case 3:
			g.fill3DRect(x + 5, y + 5, 30, 5, false);
			g.fill3DRect(x + 5, y + 20, 30, 4, false);
			g.fill3DRect(x + 10, y + 10, 20, 10, false);
			g.fillOval(x + 15, y + 10, 10, 10);
			g.drawLine(x + 20, y + 15, x - 5, y + 15);
			break;
		}
	}

	public void keyPressed(KeyEvent e) {

		if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S)
				&& hero.y < 280) {

			hero.setDirect(2);
			hero.setY(hero.getY() + hero.getSpeed());

			this.repaint();
		}

		else if ((e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W)
				&& hero.y > 0) {

			hero.setDirect(0);
			hero.setY(hero.getY() - hero.getSpeed());

			this.repaint();
		}

		else if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A)
				&& hero.x > 0) {

			hero.setDirect(3);
			hero.setX(hero.getX() - hero.getSpeed());
			this.repaint();
		}

		else if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D)
				&& hero.x < 380) {

			hero.setDirect(1);
			hero.setX(hero.getX() + hero.getSpeed());
			this.repaint();
		}

		if (e.getKeyCode() == KeyEvent.VK_J) {
			if (this.hero.s.size() < 5) {
				hero.shotEnemy();
				this.repaint();
			}
		}

		if (e.getKeyCode() == KeyEvent.VK_L) {
			// this.getRecording();
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {

	}

	public void run() {

		while (!this.myStatus.change) {

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			for (int i = 0; i < ets.size(); i++) {
				for (int j = 0; j < hero.s.size(); j++)
					this.hittank(hero.s.get(j), ets.get(i));
			}

			for (int i = 0; i < ets.size(); i++) {

				EnemyTank t = ets.get(i);
				for (int j = 0; j < t.s.size(); j++)
					this.hitmytank(t.s.get(j), hero);
			}

			this.repaint();

			if (mylife == 0) {

				this.myStatus.win = 1;
				this.myStatus.change = true;

			}

			if (ets.size() == 0) {

				this.myStatus.win = 2;
				this.myStatus.change = true;
			}
		}
	}
}

class MyStartPanel extends JPanel implements Runnable {

	int n = 0;

	public MyStartPanel() {
	}

	public void paint(Graphics g) {

		super.paint(g);
		g.fillRect(0, 0, 400, 300);

		if (n % 2 == 0) {

			Font myFont = new Font("Times New Roman", Font.BOLD, 20);
			g.setFont(myFont);
			g.setColor(Color.yellow);
			g.drawString("click start new game to start", 70, 120);
		}
	}

	public void run() {

		while (true) {
			try {
				Thread.sleep(800);
				n++;
				repaint();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

class winPanel extends JPanel {

	public winPanel() {
	}

	public void paint(Graphics g) {

		super.paint(g);
		g.fillRect(0, 0, 400, 300);

		Font myFont = new Font("Times New Roman", Font.BOLD, 20);
		g.setFont(myFont);
		g.setColor(Color.yellow);
		g.drawString("You win!", 120, 120);
		g.drawString("Click next round or exit", 70, 150);
	}
}

class losePanel extends JPanel {

	public losePanel() {
	}

	public void paint(Graphics g) {

		super.paint(g);
		g.fillRect(0, 0, 400, 300);

		Font myFont = new Font("Times New Roman", Font.BOLD, 20);
		g.setFont(myFont);
		g.setColor(Color.yellow);
		g.drawString("You lose! Click try again or exit", 55, 120);
	}
}

class winPanel2 extends JPanel {

	public winPanel2() {
	}

	public void paint(Graphics g) {

		super.paint(g);
		g.fillRect(0, 0, 400, 300);
		Font myFont = new Font("Times New Roman", Font.BOLD, 20);
		g.setFont(myFont);
		g.setColor(Color.yellow);
		g.drawString("You passed all rounds, Great!", 50, 120);

	}
}
