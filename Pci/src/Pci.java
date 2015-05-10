import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.imageio.*;

import java.io.*;

public class Pci {
	
	public static void main(String[] args) {
		new ImageView();
	}
}

 @SuppressWarnings("serial")
class ImageView extends JFrame {
	
	 ImageView(){
		super("Pci");
		setLayout(null);
		setSize(580, 600);
		
		// 读取图片
		Image rawImg = null;
		try {
//			亲爱的老师
//			其他的事情我都不会说的
//			你只要把这里的4
//			随意改成01234567里面你喜欢的数字就好了
//			0是社会前进的终极动力
//			1是冰冰姐的美照
//			2是我们头顶上的浩瀚星空
//			3是我的桌面
//			4是一只大鸟
//			5是我看到这个实验时候的表情
//			6是我在做这个实验时候的表情
//			7是我做完实验之后的表情
//			>>******重要*******>>建议您看4,观赏效果最佳
			rawImg = ImageIO.read(new File("4.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedImage bImage = (BufferedImage) rawImg;
		ImageModifier imgMdf = new ImageModifier(bImage);
		
		
		// 加载滤镜的框框
		JLabel mdfImageLbl[];
		JLabel descriptionLbl[];
		BufferedImage[] mdfImages = {bImage, imgMdf.coldHue(), imgMdf.warmHub(), imgMdf.enhance(), imgMdf.darkerCorner(), imgMdf.lomo(), imgMdf.brighter(), imgMdf.blur(), imgMdf.bw()};
		String[] descriptions = {"原图", "冷色(H =.7)", "暖色", "色彩增强", "暗角", "LOMO相机", "美白", "模糊", "B&W"};
		int lomoNum = 9;
		mdfImageLbl = new JLabel[lomoNum];
		descriptionLbl = new JLabel[lomoNum];
		for (int i = 0; i < lomoNum; i++) {
			mdfImageLbl[i] = new JLabel(new ImageIcon(mdfImages[i]));
			mdfImageLbl[i].setBounds(10+190*(i-i/3*3), 10+i/3*190, 180, 180);
			descriptionLbl[i] = new JLabel(descriptions[i]);
			descriptionLbl[i].setBounds(12+190*(i-i/3*3), 10+i/3*190, 180, 20);
			descriptionLbl[i].setFont(new Font("Verdana", Font.BOLD, 12));
			descriptionLbl[i].setForeground(Color.white);
			add(descriptionLbl[i]);
			add(mdfImageLbl[i]);
		}
	
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

class ImageModifier {
	int height = 180, width = 180;
	int rgbMatrix[][][];
	float hsbMatrix[][][];
	BufferedImage original;
	
	ImageModifier (BufferedImage bImg){
		this.original = bImg;
//		this.height = bImg.getHeight();
//		this.width = bImg.getWidth();
		this.rgbMatrix = new int[3][this.width][this.height];
		this.hsbMatrix = new float[3][this.width][this.height];
		for( int i = 0; i < this.width; i++ ){
			for ( int j = 0; j < this.height; j++){
				int pixel = bImg.getRGB(i, j);
				this.rgbMatrix[0][i][j] = (pixel & 0xff0000) >> 16;
				this.rgbMatrix[1][i][j] = (pixel & 0xff00) >> 8;
				this.rgbMatrix[2][i][j] = (pixel & 0xff);
				float hsbvals[] = new float[3];
				Color.RGBtoHSB(this.rgbMatrix[0][i][j], this.rgbMatrix[1][i][j], this.rgbMatrix[2][i][j], 
					hsbvals);
				this.hsbMatrix[0][i][j] = hsbvals[0];
				this.hsbMatrix[1][i][j] = hsbvals[1];
				this.hsbMatrix[2][i][j] = hsbvals[2];
			}
		}
	}
	
	BufferedImage cloneBuffer () {
		BufferedImage b = new BufferedImage(this.width, this.height, this.original.getType());
		Graphics g = b.getGraphics();
		g.drawImage(this.original, 0, 0, null);
		g.dispose();
		return b;
	}
	
	BufferedImage blur (){
		BufferedImage mdfImage = this.cloneBuffer();
		int rgbMatResized[][][] = new int[3][this.width][this.height];
		// smaller x3
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				// get the rgb of the resized image 
				int baseX = i / 3 * 3;
				int baseY = j / 3 * 3;
				for (int k = 0; k < 3; k++) {
					rgbMatResized[k][i][j] = (this.rgbMatrix[k][baseX][baseY] + 
					this.rgbMatrix[k][baseX+1][baseY] + 
					this.rgbMatrix[k][baseX+2][baseY] + 
					this.rgbMatrix[k][baseX][baseY+1] + 
					this.rgbMatrix[k][baseX+1][baseY+1] + 
					this.rgbMatrix[k][baseX+2][baseY+1] + 
					this.rgbMatrix[k][baseX][baseY+2] + 
					this.rgbMatrix[k][baseX+1][baseY+2] + 
					this.rgbMatrix[k][baseX+2][baseY+2])/9;
				}
				// set the rgb of the resized image
				int rgb = new Color((rgbMatResized[0][i][j]+this.rgbMatrix[0][i][j])/2, 
					(rgbMatResized[1][i][j]+this.rgbMatrix[1][i][j])/2, 
					(rgbMatResized[2][i][j]+this.rgbMatrix[2][i][j])/2).getRGB();
				mdfImage.setRGB(i, j, rgb);
			}
		}
		
		return mdfImage;
	}
	
	BufferedImage enhance(){
		// 色彩增强
		BufferedImage mdfImage = this.cloneBuffer();
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				float s = (float)(this.hsbMatrix[1][i][j] + Math.pow(this.hsbMatrix[1][i][j], 1.5));
				Color co = Color.getHSBColor(this.hsbMatrix[0][i][j], s > 1 ? 1 : s, this.hsbMatrix[2][i][j]);
				mdfImage.setRGB(i, j, co.getRGB());
			}
		}
		return mdfImage;
	}
	
	BufferedImage coldHue(){
		// 变色
		BufferedImage mdfImage = this.cloneBuffer();
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				Color co = Color.getHSBColor((float)(.7), this.hsbMatrix[1][i][j], this.hsbMatrix[2][i][j]);
				mdfImage.setRGB(i, j, co.getRGB());
			}
		}
		return mdfImage;
	}
	BufferedImage warmHub(){
		// 变色
		BufferedImage mdfImage = this.cloneBuffer();
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				Color co = Color.getHSBColor((float)(30.0/360), this.hsbMatrix[1][i][j], this.hsbMatrix[2][i][j]);
				mdfImage.setRGB(i, j, co.getRGB());
			}
		}
		return mdfImage;
	}
	BufferedImage bw(){
		// 变色
		BufferedImage mdfImage = this.cloneBuffer();
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				Color co = Color.getHSBColor(this.hsbMatrix[0][i][j], (float)0, this.hsbMatrix[2][i][j]);
				mdfImage.setRGB(i, j, co.getRGB());
			}
		}
		return mdfImage;
	}
	
	BufferedImage darkerCorner(){
		// 暗角
		BufferedImage mdfImage = this.cloneBuffer();
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				float ratio = (float)(1 - 
					(Math.pow(i-this.width/2, 2) + 
					Math.pow(j-this.height/2, 2)) / 
					(Math.pow(this.width/2, 2) + 
					Math.pow(this.height/2, 2)));
				Color co = Color.getHSBColor(this.hsbMatrix[0][i][j], this.hsbMatrix[1][i][j], (float)(this.hsbMatrix[2][i][j]*ratio));
				mdfImage.setRGB(i, j, co.getRGB());
			}
		}
		return mdfImage;
	}
	
	BufferedImage blurCorner(){
		BufferedImage mdfImage = this.blur();
		
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				float ratio = (float)(1 - 
					(Math.pow(i-this.width/2, 2) + 
					Math.pow(j-this.height/2, 2)) / 
					(Math.pow(this.width/2, 2) + 
					Math.pow(this.height/2, 2)));
				Color co = new Color(
					(int)((this.rgbMatrix[0][i][j] * ratio) + (this.rgbMatrix[0][i][j] * (1-ratio))),
					(int)((this.rgbMatrix[1][i][j] * ratio) + (this.rgbMatrix[1][i][j] * (1-ratio))), 
					(int)((this.rgbMatrix[2][i][j] * ratio) + (this.rgbMatrix[2][i][j] * (1-ratio)))
				);
				mdfImage.setRGB(i, j, co.getRGB());
			}
		}
		
		return mdfImage;
	}
	
	BufferedImage lomo(){
		// LOMO camera
		BufferedImage mdfImage = this.cloneBuffer();
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				float ratio = (float)(1 - 
					(Math.pow(i-this.width/2, 2) + 
					Math.pow(j-this.height/2, 2)) / 
					(Math.pow(this.width/2, 2) + 
					Math.pow(this.height/2, 2)));
				float s = (float)(this.hsbMatrix[1][i][j] + Math.pow(this.hsbMatrix[1][i][j], 1.5));
				Color co = Color.getHSBColor(this.hsbMatrix[0][i][j], s > 1 ? 1 : s, (float)(this.hsbMatrix[2][i][j]*ratio));
				mdfImage.setRGB(i, j, co.getRGB());
			}
		}
		return mdfImage;
	}
	
	BufferedImage brighter(){
		BufferedImage mdfImage = this.cloneBuffer();
		
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				float b = (float)(this.hsbMatrix[2][i][j] + Math.pow(this.hsbMatrix[2][i][j], 10));
				Color co = Color.getHSBColor(this.hsbMatrix[0][i][j], this.hsbMatrix[1][i][j], b > 1 ? 1 : b);
				mdfImage.setRGB(i, j, co.getRGB());
			}
		}
		
		return mdfImage;
	}
	
}
