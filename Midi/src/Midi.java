import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import org.jfugue.Player;


public class Midi extends Frame implements ActionListener, KeyListener{
	private static final long serialVersionUID = 1L;
	MidiKey keys[] = new MidiKey[21];	// 21个琴键
	
	public Midi(){
		setLayout(null);
		setSize(433, 130);
		addKeyListener(this);
		setBackground(new Color(33,186,181));
		
		for(int i = 14; i < 21; i++){
			if(i==14){
				keys[i] = new MidiKey('b');
			} else {
				keys[i] = new MidiKey('b', keys[i-1]);
			}
			add(keys[i]);
			keys[i].addMouseListener(new MouseAdapter() { 
	            public void mousePressed(MouseEvent me) { 
	            	// modified from keyTyped(KeyEvent e) method
	                int typed = ((MidiKey) me.getComponent()).getPos();
	                if(typed != -1){
	                	new KeyThread(keys[typed]);
	                }
	            } 
	            
	            public void mouseReleased(MouseEvent me){
	            	// modified from keyReleased(KeyEvent e) method
	                int released = ((MidiKey) me.getComponent()).getPos();
	                if (released != -1) {
	        			if (released < 14)
	        				keys[released].setBackground(Color.white);
	        			else 
	        				keys[released].setBackground(Color.black);
	        		}
	            }
			});
		}
		
		for(int i = 0; i < 14; i++){
			if(i==0){
				keys[i] = new MidiKey('w');
			} else {
				keys[i] = new MidiKey('w', keys[i-1]);
			}
			add(keys[i]);
			keys[i].addMouseListener(new MouseAdapter() { 
	            public void mousePressed(MouseEvent me) { 
	            	// modified from keyTyped(KeyEvent e) method
	                int typed = ((MidiKey) me.getComponent()).getPos();
	                if(typed != -1){
	                	new KeyThread(keys[typed]);
	                }
	            }
	            
	            public void mouseReleased(MouseEvent me){
	            	// modified from keyReleased(KeyEvent e) method
	                int released = ((MidiKey) me.getComponent()).getPos();
	                if (released != -1) {
	        			if (released < 14)
	        				keys[released].setBackground(Color.white);
	        			else 
	        				keys[released].setBackground(Color.black);
	        		}
	            }
			});
		}

		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		validate();
		setVisible(true);
		setResizable(false);
		setLocationRelativeTo(null);
	}
	

	public void processWindowEvent(WindowEvent e){
		if(e.getID() == WindowEvent.WINDOW_CLOSING){
			setVisible(false);
			dispose();
			System.exit(0);
		}else
			super.processWindowEvent(e);
	}

	public static void main(String[] args) {
		new Midi();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		int typed = charToPos(e.getKeyChar());
		if (typed != -1) {
			new KeyThread(keys[typed]);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// sound(e);
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		int released = charToPos(e.getKeyChar());
		if (released != -1) {
			if (released < 14)
				keys[released].setBackground(Color.white);
			else 
				keys[released].setBackground(Color.black);
		}
	}
	
	public int charToPos(char keyChar){
		int keyPos = -1;
		switch (keyChar) {
			case 'q': keyPos = 0; break;
			case 'w': keyPos = 1; break;
			case 'e': keyPos = 2; break;
			case 'r': keyPos = 3; break;
			case 'a': keyPos = 4; break;
			case 's': keyPos = 5; break;
			case 'd': keyPos = 6; break;
			case 'f': keyPos = 7; break;
			case 'g': keyPos = 8; break;
			case 'h': keyPos = 9; break;
			case 'j': keyPos = 10; break;
			case 'k': keyPos = 11; break;
			case 'l': keyPos = 12; break;
			case ';': keyPos = 13; break;
			case 't': keyPos = 14; break;
			case 'y': keyPos = 15; break;
			case 'u': keyPos = 16; break;
			case 'i': keyPos = 17; break;
			case 'o': keyPos = 18; break;
			case 'p': keyPos = 19; break;
			case '[': keyPos = 20; break;
			default: break;
		}
		return keyPos;
	}

}

class MidiKey extends Label {
	private static final long serialVersionUID = 1L;
	private int KEY_WIDTH = 30, KEY_HEIGHT = 130;
	private int pos = -1;
	private String[] tones = {"F4","G4","A4","B4","C5","D5","E5","F5","G5","A5","B5","C6","D6","E6","C#","D#","E#","F#","G#","A#","B#" };
	private String tone = "";
	
	MidiKey(char ch){
		super();
		if(ch == 'w'){
			this.pos = 0;
			setBounds(0, 0, KEY_WIDTH, KEY_HEIGHT);
			setBackground(Color.white);
		} else {
			this.pos = 14;
			setBounds((int) (KEY_WIDTH * 0.75), 0, KEY_WIDTH/2, KEY_HEIGHT/3*2);
			setBackground(Color.black);
		}
		this.tone = tones[this.pos];
		
	}
	
	MidiKey(char ch, MidiKey midiKey){
		super();
		if(ch == 'w'){
			this.pos = midiKey.pos + 1;
			setBounds(midiKey.getX() + KEY_WIDTH + 1, 0, KEY_WIDTH, KEY_HEIGHT);
			setBackground(Color.white);
		} else {
			this.pos = midiKey.pos + 1;
			setBounds(midiKey.getX() + (KEY_WIDTH + 1) * 2, 0, KEY_WIDTH/2, KEY_HEIGHT/3*2);
			setBackground(Color.black);
		}
		this.tone = tones[this.pos];
		
	}
	
	String getTone(){
		return this.tone;
	}
	
	int getPos(){
		return this.pos;
	}
}

class KeyThread implements Runnable {
	Thread t;
	MidiKey key;
	Player player = new Player();
	
	KeyThread(MidiKey key) {
		this.key = key;
//		this.player = player;
		t = new Thread(this, "KeyThread");
		t.start();
	}

	@Override
	public void run() {
		String opt = "";
		key.setBackground(new Color(133, 215, 212));
		player.play(key.getTone() + opt);
		player.close();	
	}
}
