import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;


public class Hold60s {
	
	public static void main(String[] args) {
		
		Badguy badguys[] = new Badguy[100];
		for (int i = 0; i < 100; i++) {
			badguys[i] = new Badguy();
		}
		
		final BattleCanvas canvas = new BattleCanvas(badguys);
		canvas.setSize(400, 400);
		
		Frame frame = new Frame();
		frame.setSize(400,400);
		frame.add(canvas);
		frame.pack();
		frame.setVisible(true);
		
		frame.addKeyListener(new KeyListener() {
			
		int w = 0, a = 0, s = 0, d = 0;
			@Override
			public void keyTyped(KeyEvent e) {
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
//				System.out.println();
				if (e.getKeyChar() == 'w' || w == 1) {
					w = 1;
//					System.out.println("向上");
					canvas.hero.ypos -= 5;
				} 
				if (e.getKeyChar() == 'a' || a == 1) {
					a = 1;
//					System.out.println("向左");
					canvas.hero.xpos -= 5;
				} 
				if (e.getKeyChar() == 's' || s == 1) {
					s = 1;
//					System.out.println("向下");
					canvas.hero.ypos += 5;
				}
				if (e.getKeyChar() == 'd' || d == 1) {
					d = 1;
//					System.out.println("向右");
					canvas.hero.xpos += 5;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyChar() == 'w')			w = 0;
				else if (e.getKeyChar() == 'a')		a = 0;
				else if (e.getKeyChar() == 's') 	s = 0;
				else if (e.getKeyChar() == 'd') 	d = 0;
			}
		});


		
		new Thread() {
			public void run() {
				while (true) {
					canvas.repaint();
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

}

class BattleCanvas extends Canvas implements ActionListener {
	int width;
	int height;
	Badguy[] badguys;
	Image bi;
	Graphics bg;
	Hero hero;
	int flag;
	
	BattleCanvas (Badguy[] badguys) {
		this.badguys = badguys;
		this.height  = 400;
		this.width   = 400;
		
		this.hero = new Hero();
		this.flag = 0;
		System.out.println("Success");
	}
	
	public void paint(Graphics g) {
		bi = createImage(width, height);
		bg = bi.getGraphics();
		
		bg.drawOval(hero.xpos, hero.ypos, hero.width, hero.height);
//		bg.drawOval(0, 0, 100, 100);
		
		for (Badguy badguy : badguys) {
			
				if (badguy.start <= 0) {
					
					bg.drawOval(badguy.xpos, badguy.ypos, badguy.width, badguy.height);
					
					if (flag == 0) {
						if (Math.pow(badguy.xpos + badguy.width/2 - hero.xpos - hero.width/2, 2) + 
							Math.pow(badguy.ypos + badguy.height/2 - hero.ypos - hero.height/2, 2) < 
							Math.pow(badguy.width/2 + hero.width/2, 2)) {
							System.out.println("啊，被击中了！");
							flag++;
						}
						
						badguy.ypos += badguy.speed;
					} else {
						bg.setColor(Color.red);
					}
					
					
				}
				
				if (flag == 0) {
					--badguy.start;
				}
				
			
			
			
		}
		g.drawImage(bi, 0, 0, this);
		
	}
	
	public void make () {
		
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}
}

class Hero { 
	int xpos;
	int ypos;
	int width;
	int height;
	
	Hero() {
		this.xpos = 195;
		this.ypos = 370;
		this.width = 20;
		this.height = 20;
	}
}


class Badguy {
	int xpos;
	int ypos;
	int width;
	int height;
	int speed;
	int start;
	
	Badguy(){
		Random random = new Random();
		this.xpos = random.nextInt(390);
		this.speed = random.nextInt(2) + 1;
		this.start = random.nextInt(600);
		this.ypos = 10;
		this.width = 10;
		this.height = 10;
//		System.out.println(this.xpos);
	}
	
}