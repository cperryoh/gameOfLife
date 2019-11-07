package gameOfLife.gameOfLife;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.naming.ldap.Rdn;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.FlatteningPathIterator;
import java.awt.Rectangle;
import java.awt.Robot;

import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class gameOfLife {

	private JFrame frame;
	JLabel sliderValue = new JLabel();
	Timer timer = new Timer ();
	private boolean paused=false;
	/**
	 * Launch the application.
	 */
	Cell[][] cells;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					gameOfLife window = new gameOfLife();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public gameOfLife() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		//Randomizer for r key
		KeyAdapter randomKeyAdapter= new KeyAdapter() {
			@Override
			
			public void keyPressed(KeyEvent arg0) {
				if(paused) {
					if(KeyEvent.VK_R==arg0.getKeyCode()) {
						Random rnd = new Random();
						
						for (int y = 0; y < cells.length; y++) {
							for (int x = 0; x < cells[0].length; x++) {
								int randomNumrnd = rnd.nextInt(4);
								if(randomNumrnd==3) {
									cells[y][x].alive();
								}
								else {
									cells[y][x].dead();
								}
							}
						}
						
						
					}
				}
			}
		};
		//TODO set up auto flyer placer
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addKeyListener(randomKeyAdapter);
		frame.requestFocus();
		frame.getContentPane().setLayout(null);
		int height=50;
		int width=75;
		
		//initializes all cells
		int y = 0, x = 0;
		//first sets up cells and put it into array
		cells = new Cell[height][width];
		for (y = 0; y < height; y++) {
			for (x = 0; x <width; x++) {
				Cell cell = new Cell();
				cell.dead();
				cell.setBounds(x * 12, y * 12, 10, 10);
				cell.setVisible(true);
				frame.getContentPane().add(cell);
				// System.out.println(x+","+y);
				cells[y][x] = cell;

				// frame.pack();
			}
			
		}
		
		//sets up neighbors
		int maxY = cells.length;
		int maxX = cells[0].length;
		for (int a = 0; a < cells.length; a++) {
			for (int b = 0; b < cells[0].length; b++) {
				for (int i = -1; i < 2; i++) {
					for (int j = -1; j < 2; j++) {
						int translationX = b - j;
						int translationY = a - i;
						if (!(i == 0 && j == 0) && translationX != maxX && translationX!=-1&& translationY != maxY && translationY!=-1) {
							cells[a][b].neighbors.add(cells[translationY][translationX]);
						}
					}
				}
			}
		}
		int xDif= frame.getWidth()-frame.getContentPane().getWidth();
		int yDif = 41;
		int interfaceSpace=40;
		frame.pack();
		frame.setSize(cells[cells.length-1][cells[0].length-1].getX()+15+xDif,cells[cells.length-1][cells[0].length-1].getY()+yDif+interfaceSpace);
		frame.setResizable(false);
		JButton btnPause = new JButton("Pause");
		btnPause.setFocusable(false);
		btnPause.setBounds((int) (frame.getContentPane().getWidth() / 2.0) - (int) (115.0 / 2.0), frame.getHeight() - 25-yDif, 115, 25);
		btnPause.setVisible(true);
		
		//pause button action
		btnPause.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				paused = !paused;
				pause(paused);
				if (paused) {
					btnPause.setText("Unpause");
				} else {
					btnPause.setText("Pause");
				}
			}
		});
		JButton clearButton = new JButton("Clear");
		clearButton.setFocusable(false);
		clearButton.setBounds(btnPause.getX()+115+10,btnPause.getY(),115, 25);
		clearButton.setVisible(true);
		frame.getContentPane().add(clearButton);
		//clear button action
		clearButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				for(int y =0; y < cells.length; y++) {
					for (int x = 0; x < cells[0].length; x++) {
						cells[y][x].dead();
						
					}
				}
				
			}
		});
		frame.getContentPane().add(btnPause);
		
		JSlider slider = new JSlider();
		slider.setFocusable(false);
		slider.setMinimum(1);
		slider.setBounds(clearButton.getWidth()+clearButton.getX()+10,clearButton.getY(),150,25);
		slider.setMaximum(1000);
		//slider click activation
		slider.addMouseListener(new MouseAdapter() {@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			Point p = e.getPoint();
            double percent = p.x / ((double) slider.getWidth());
            int range = slider.getMaximum() - slider.getMinimum();
            double newVal = range * percent;
            int result = (int)(slider.getMinimum() + newVal);
            slider.setValue(result);
		}
		});
		frame.getContentPane().add(slider);
		sliderValue.setBounds( slider.getWidth()+slider.getX()+10,slider.getY(), 69, 20);
		sliderValue.setVisible(true);
		frame.getContentPane().add(sliderValue);
		//gets icon for the frame
		ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("the-glider.png"));
		frame.setIconImage(icon.getImage());
		//timer for the app
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if(!paused) {
					for(int y= 0; y <cells.length;y++) {
						for (int x = 0; x < cells[0].length; x++) {
							Cell cell = cells[y][x];
							if(cell.willBeAliveNextGen) {
								cell.alive();
							}
							else {
								cell.dead();
							}
							cell.willBeAliveNextGen=false;
							
						}
					}
					for (int y = 0; y < cells.length; y++) {
						for (int x = 0; x < cells[0].length; x++) {
							Cell cell = cells[y][x];
							int aliveCount = cell.getAliveCount();
							if(cell.alive) {
								if(aliveCount==2||aliveCount==3) {
									cell.willBeAliveNextGen=true;
								}
								else if(aliveCount>3||aliveCount<2) {
									cell.willBeAliveNextGen=false;
								}
							}
							else {
								if(aliveCount==3) {
									cell.willBeAliveNextGen=true;
								}
							}
						}
					}
				}
			}
		}, 500, 500);
		//sets slider label initial
		sliderValue.setText(slider.getValue()+"ms");
		//when slider changes reset timer
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				timer.cancel();
				timer = new Timer();
				sliderValue.setText(slider.getValue()+"ms");
				timer.scheduleAtFixedRate(new TimerTask() {
					public void run() {
						if(!paused) {
							for(int y= 0; y <cells.length;y++) {
								for (int x = 0; x < cells[0].length; x++) {
									Cell cell = cells[y][x];
									if(cell.willBeAliveNextGen) {
										cell.alive();
									}
									else {
										cell.dead();
									}
									cell.willBeAliveNextGen=false;
									
								}
							}
							for (int y = 0; y < cells.length; y++) {
								for (int x = 0; x < cells[0].length; x++) {
									Cell cell = cells[y][x];
									int aliveCount = cell.getAliveCount();
									if(cell.alive) {
										if(aliveCount==2||aliveCount==3) {
											cell.willBeAliveNextGen=true;
										}
										else if(aliveCount>3||aliveCount<2) {
											cell.willBeAliveNextGen=false;
										}
									}
									else {
										if(aliveCount==3) {
											cell.willBeAliveNextGen=true;
										}
									}
								}
							}
						}
					}
				}, slider.getValue(), slider.getValue());
			}
		});


		slider.setValue(500);
	}
	//pause action
	void pause(boolean state) {
		for (int y = 0; y < cells.length; y++) {
			for (int x = 0; x < cells[0].length; x++) {
				cells[y][x].paused = state;
			}
		}
	}
}
