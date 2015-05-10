import java.awt.AWTEvent;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.imageio.*;

import java.io.*;
import java.awt.image.*;

public class Smaller{	
	public static void main(String[] args) {
		new ImageView();
	}
	
}

class ImageView extends JFrame {
	
	ImageView () {
		super("Smaller by Ivan");
		setLayout(null);
		setSize(630, 400);
		
		Image ivImage = null;
		try {
			ivImage = ImageIO.read(new File("VENUS.BMP"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		BufferedImage bImage = (BufferedImage) ivImage;
		ImageSmaller imgSmlr = new ImageSmaller(bImage);
		
		JLabel imageBefore = new JLabel(new ImageIcon(bImage));
		imageBefore.setBounds(10,10,300,150);
		add(imageBefore);
		JLabel imageAfter = new JLabel(new ImageIcon(imgSmlr.smlrImg));
		imageAfter.setBounds(320,10,300,150);
		add(imageAfter);
		
		JLabel titleLbl = new JLabel(bImage.getWidth() + " x " + bImage.getHeight());
		titleLbl.setBounds(10, 170, 300, 20);
		add(titleLbl);
		
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		validate();
		setVisible(true);
		setResizable(false);
		setLocationRelativeTo(null);
	}
		
	protected void processWindowEvent (WindowEvent e){
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			setVisible(false);
			dispose();
			System.exit(0);
		} else {
			super.processWindowEvent(e);
		}
	}
}

class ImageSmaller {
	BufferedImage smlrImg = null;
	
	ImageSmaller (BufferedImage bImage){
		this.smlrImg = cloneBuffer(bImage);
	}
	
	BufferedImage cloneBuffer (BufferedImage bImage) {
		BufferedImage b = new BufferedImage(bImage.getWidth(), bImage.getHeight(), bImage.getType());
		Graphics g = b.getGraphics();
		g.drawImage(bImage, 0, 0, null);
		g.dispose();
		return b;
	}
	
	
} 

// 色相、饱和度、亮度对图片体积的影响（by BGLL - 知乎）：http://is.gd/Wo76gN









