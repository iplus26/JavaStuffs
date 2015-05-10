import java.util.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import sun.audio.*;
import java.net.URL;
import java.io.*;

public class Clock extends Frame implements Runnable, ActionListener{
	int h, m, s;
	Label instime = new Label();
	int setH = 25, setM = 61, setS = 61;
	URL url;
	AudioStream as;

	TextField htf = new TextField("00");
	TextField mtf = new TextField("00");
	
	public Clock(){
		super("Clock");
		/*
			GUI CREATING
		*/
		setSize(500, 200);
		
		setLayout(new BorderLayout());
		
		instime.setFont(new Font("Menlo",0,100));
		add("North", instime);
		Panel panel = new Panel();
		add("South", panel);
		panel.setFont(new Font("Mistral", 0, 25));
		
		//TextField stf = new TextField();
		panel.add(new Label("Set your time"));
		panel.add(htf);
		panel.add(new Label(":"));
		panel.add(mtf);
		//panel.add(stf);
		//ActionListener al = ...;
		Button confirm = new Button("ok");
		panel.add(confirm);
		confirm.addActionListener(this);
		Button closeit = new Button("close!");
		panel.add(closeit);
		closeit.addActionListener(this);
		/*
			GUI CREATED
		*/
		
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		validate();
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e){
		String arg = e.getActionCommand();
		if(arg.equals("ok")){
			setH = Integer.parseInt(htf.getText());
			setM = Integer.parseInt(mtf.getText());
		}else if(arg.equals("close!")){
			stopsound();
		}
	}
	
	public void processWindowEvent(WindowEvent e){
		if(e.getID() == WindowEvent.WINDOW_CLOSING){
			setVisible(false);
			dispose();
			System.exit(0);
		}else
			super.processWindowEvent(e);
	}
	
	public void run(){
		try{
			for(;;){

				Calendar cal = Calendar.getInstance();
				h = cal.get(cal.HOUR_OF_DAY);
				m = cal.get(cal.MINUTE);
				s = cal.get(cal.SECOND);
				
				if(h == setH && m == setM && s == 00){
					playsound();
				}
				if(h == setH && m == setM+1){
					stopsound();
				}
				
				instime.setText(((h > 9) ? String.valueOf(h) : ("0"+String.valueOf(h)))
					 + ":" + ((m > 9)?String.valueOf(m):("0"+String.valueOf(m)))
					+ ":" + ((s > 9)?String.valueOf(s):("0"+String.valueOf(s))));
				Thread.sleep(1000);
			}
		}
		catch(InterruptedException e){
			System.out.println("出现了一点小错误");
		}
	}
	
	public void playsound(){
		try{
			//FileInputStream fileau = new FileInputStream("/users/ivan/music/1.wav");
			url = new URL("file:" + "/users/ivan/music/1.wav");
			as = new AudioStream(//fileau
				url.openStream());
			AudioPlayer.player.start(as);
		}
		catch(FileNotFoundException e){
			System.out.println("file not found!");
		}
		catch(IOException e){
			System.out.println("ioexception" + e);
		}
		catch(Exception e){
			System.out.println("exception");
		}
	}
	
	public void stopsound(){
		try{
			AudioPlayer.player.stop(as);
			setH = 25;
			setM = 61;
		}
		catch(Exception e){
			System.out.println("exception");
		}
	}
	
	public static void main(String[] args){
		Clock clock = new Clock();
		Thread clockThread = new Thread(clock);
		clockThread.start();
	}
}

