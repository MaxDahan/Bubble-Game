import java.awt.Color;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class BoobolMain {

	public static void main(String[] args) throws Exception {
		new BoobolMain();
	}
	private BoobolMain() throws Exception {
		f = new JFrame();
		b = new ArrayList<JLabel>();
		rand = new Random();
		generate = true;
		score = 0;
		if (!new Scanner(new File("data/High.txt")).hasNext())
			high = 0;
		else
			high = new Scanner(new File("data/High.txt")).nextInt();
		out = new PrintStream(new File("data/High.txt"));
		scoreText = new JLabel("Boobols Popped > " + score);
		highText = new JLabel("Best Score > " + high);
		
		f.setUndecorated(true);
		f.getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK, 50));
		f.setBackground(new Color(0, 0, 0, 100));
		f.setSize(1280, 720);
		f.setLayout(null);
		f.setAlwaysOnTop(true);
		f.setLocationRelativeTo(null);
		f.addKeyListener(new KeyListener() {{}
		
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			
			if (key == KeyEvent.VK_C || key == KeyEvent.VK_ESCAPE) {
				System.exit(0);
			}
		}
		
		public void keyReleased(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {}
		});
		
		scoreText.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
		scoreText.setBounds(f.getWidth()/2-200, 350, 500, 500);
		scoreText.setForeground(Color.WHITE);
		f.getContentPane().add(scoreText);
		
		highText.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
		highText.setBounds(f.getWidth()/2-180, -234, 500, 500);
		highText.setForeground(Color.WHITE);
		f.getContentPane().add(highText);
		
		f.setVisible(true);
		f.addMouseListener(new MouseDrag());
		
		new Timer().start();
		new BoobolGenerate().start();
	}
	
	private void slep(int i) {
		try {Thread.sleep(i);} catch (InterruptedException e) {e.printStackTrace();}
	}
	
	//Timers
	private class Timer extends Thread {
		public void run() {
			while(true) {
				slep(50000);
				generate = false;
				slep(4000);
				f.getContentPane().removeAll();
				f.repaint();
				new CloseFrame().start();
			}
		}
	}
	private class BoobolGenerate extends Thread {
		public void run() {
			while (generate) {
				int i = rand.nextInt(3) + 1;
				int x = rand.nextInt(f.getWidth());
				int y = rand.nextInt(f.getHeight());
				slep(i*250);
				try {
					b.add(new JLabel(new ImageIcon(ImageIO.read(new File("data/Boobol1.png")))));
					b.get(b.size() - 1).setBounds(x, y, 200, 200);
					b.get(b.size() - 1).addMouseListener(new MouseBoobol(b.size() - 1));
					new BoobFloat(b.size() - 1).start();
					f.getContentPane().add(b.get(b.size()-1));
					f.repaint();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//Transitions
	private class CloseFrame extends Thread {
		public void run() {
			double i = 50;
			out.print(high);
			while (true) {
				if (i > 0) i--;
				slep(5);
				f.setSize(f.getWidth() - (int) i, f.getHeight());
				f.setLocation(f.getX() + (int) i/2, f.getY());
				f.repaint();
				if(i <= 0) {
					f.dispose();
					System.exit(0);
				}
			}
		}
	}
	private class BoobFloat extends Thread {
		private int boob;
		public BoobFloat(int BOOB) {
			boob = BOOB;
		}
		public void run() {
			int i = 1;
			int j = 1;
			boolean tru = true;
			while (tru) {
				slep(5);
				i++;
				if (i % 50 == 0)
					j += 1;
				b.get(boob).setLocation(b.get(boob).getX(), b.get(boob).getY() - (int) j);
				if (b.get(boob).getY() - b.get(boob).getHeight()  + 500 < 0) {
					b.set(boob, null);
					tru = false;
				}
			}
		}
	}	
	private class MouseBoobol extends MouseAdapter {
		private int boob;
		public MouseBoobol (int BOOB) {
			boob = BOOB;
		}
		public void mousePressed(MouseEvent e) {
			new CloseBoobol(boob).start();
		}
	}
		private class CloseBoobol extends Thread {
			private boolean goob;
			private int boob;
			public CloseBoobol(int BOOB) {
				boob = BOOB;
			}
			public void run() {
				score++;
				scoreText.setText("Boobols Popped > " + score);
				if (high < score) {
					high++;
					highText.setText("Best Score > " + high);
				}
				
				goob = true;
				while (goob) {
					slep(2);
					b.get(boob).setSize(b.get(boob).getWidth()-16, b.get(boob).getHeight());
					b.get(boob).setLocation(b.get(boob).getX()+12, b.get(boob).getY());
					if(b.get(boob).getWidth() <= 0) {
						goob = false;
						b.set(boob, null);
					}
				}
			}
		}
		
		
	//Mouse follow functions
	private class MouseDrag extends MouseAdapter {
		private Thread follow;
		public void mousePressed(MouseEvent e) {
			follow = new Follow();
			follow.start();
		}
		public void mouseReleased(MouseEvent e) {
			follow.stop();
		}
	}
	private class Follow extends Thread {
		private boolean first = true;
		public void run() {
			int mouseLastX = 0;
			int mouseLastY = 0;
			while (true) {
				slep(5);
				Point mousePos = MouseInfo.getPointerInfo().getLocation();
				if (!first) {
					mouseLastX = (int) mousePos.getX() - mouseLastX;
					mouseLastY = (int) mousePos.getY() - mouseLastY;
					f.setLocation(f.getX() + mouseLastX, f.getY() + mouseLastY);
				}
				first = false;
				mouseLastX = (int) mousePos.getX();
				mouseLastY = (int) mousePos.getY();
			}
		}
	}
	
	//Fields
	//JComponents
		//JFrame
		private JFrame f;
		//JLabels
		private List<JLabel> b;
		private JLabel scoreText;
		private JLabel highText;
	//Classics
		private PrintStream out;
		private Random rand;
		private int score, high;
		private boolean generate;
}
