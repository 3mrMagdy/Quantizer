import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.io.*;
import java.util.*;

public class Run 
{
	public static void main (String arg[])
	{
		new Run().Gui(); 
	}
	
	void Gui()
	{
		JFrame frm = new JFrame ("LZW");
		frm.setSize(500,300);
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel pnl = new JPanel();
		frm.add(pnl);
		
		pnl.setLayout(null);
		
		JLabel l = new JLabel("File path");
		l.setBounds(211, 51, 71, 31);
		pnl.add(l);
		
		JTextField tf = new JTextField();
		tf.setBounds(99, 122, 277, 33);
		pnl.add(tf);
		
		JTextField Itf = new JTextField();
		Itf.setBounds(99, 77, 277, 33);
		pnl.add(Itf);

		
		JButton b1 = new JButton("Compress");
		b1.setBounds(33, 177, 111, 51);
		pnl.add(b1);
		
		JButton b2 = new JButton("Decompress");
		b2.setBounds(333, 177, 111, 51);
		pnl.add(b2);
		
		b1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				Comp(tf.getText(),Integer.parseInt(Itf.getText()));
				JOptionPane.showMessageDialog(null, "Done!!");
			}
		});
		
		b2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				Decomp(tf.getText(),Itf.getText());
				JOptionPane.showMessageDialog(null, "Done!!");
			}
		});

		frm.setVisible(true);
		
	}
	
	void Comp (String path, int levels)
	{
		int pixels[][]= new Run().readImage(path), mean=0, mn, ind;
		
		ArrayList <Integer> tmp = new ArrayList <Integer>();
		ArrayList <Integer> means = new ArrayList <Integer>();
		ArrayList <Integer> ranges = new ArrayList <Integer>();


		
		ArrayList < ArrayList <Integer> > cal = new ArrayList < ArrayList <Integer> >();
		ArrayList < ArrayList <Integer> > Tcal = new ArrayList < ArrayList <Integer> >();

		
		
		for(int i=0 ; i < pixels.length ; i++)
			for(int j=0 ; j<pixels[i].length ; j++)
				tmp.add(pixels[i][j]);
		
		cal.add(tmp);
		
		for(int i=0 ; i<levels ; i++)
		{
			means.clear();
			
			for(int j=0 ; j<cal.size() ; j++)
			{
				mean=0;
				
				for(int k=0 ; k<cal.get(j).size() ; k++)
					mean += cal.get(j).get(k);
				
				if(cal.get(j).size()!=0)
					mean /= cal.get(j).size();
				
				means.add(mean-1);
				means.add(mean+1);
			}
						
			cal.clear();
			
			for(int j=0 ; j<means.size() ; j++)
			{
				cal.add(new ArrayList<Integer>());
			}
			
			for(int j=0 ; j<tmp.size() ; j++)
			{
				mn = 1000000000;
				ind = 0;
				
				for(int k=0 ; k<means.size() ; k++)
				{
					if(Math.abs(tmp.get(j)-means.get(k)) < mn)
					{
						mn = Math.abs(tmp.get(j)-means.get(k));
						ind = k;
					}
				}
				
				cal.get(ind).add(tmp.get(j));
			}
		}
		
		means.clear();
		
		for(int j=0 ; j<cal.size() ; j++)
		{
			mean=0;
			
			for(int k=0 ; k<cal.get(j).size() ; k++)
				mean += cal.get(j).get(k);
			
			if(cal.get(j).size()!=0)
				mean /= cal.get(j).size();
			
			means.add(mean);
		}
		
		while (cal.equals(Tcal))
		{
			if(!Tcal.isEmpty())
				cal = Tcal;
			
			Tcal.clear();
			
			for(int j=0 ; j<means.size() ; j++)
			{
				Tcal.add(new ArrayList<Integer>());
			}
			
			for(int j=0 ; j<tmp.size() ; j++)
			{
				mn = 1000000000;
				ind = 0;
				
				for(int k=0 ; k<means.size() ; k++)
				{
					if(Math.abs(tmp.get(j)-means.get(k)) < mn)
					{
						mn = Math.abs(tmp.get(j)-means.get(k));
						ind = k;
					}
				}
				
				Tcal.get(ind).add(mn);
			}
		}
		
				
		for(int i=0 ; i<means.size()-1 ; i++)
			ranges.add((means.get(i+1)+means.get(i))/2);

		for (int i=0 ; i<pixels.length ; i++)
			for(int j=0 ; j<pixels[i].length ; j++)
			{
				for(int k=ranges.size()-1 ; k>=0 ; k--)
					if(pixels[i][j]>ranges.get(k))
					{
						pixels[i][j] = k+1;
						break;
					}
					else if(k==0)
						pixels[i][j] = 0;
			}
						
		
		new Run().write(pixels);
		new Run().writeToFile(means);
		
			
	}
	void write (int pixels[][])
	{
		File data = new File ("comdata.txt");
		
		try
		{
			data.createNewFile();
			FileWriter wr= new FileWriter (data);
			
			wr.write(pixels.length + " " + pixels[0].length + " ");
			
			for(int i=0 ; i<pixels.length ; i++)
			{
				for(int j=0 ; j<pixels[i].length ; j++)
					wr.write(pixels[i][j] + " ");
			}
			
			wr.close();
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(null, "Error");
		}
	}
	
	void writeToFile (ArrayList<Integer> means)
	{
		File data = new File ("data.txt");
		
		try
		{
			data.createNewFile();
			FileWriter wr= new FileWriter (data);
			
			for(int i=0 ; i<means.size() ; i++)
				wr.write(means.get(i) + " ");
			
			wr.close();
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(null, "Error");
		}
	}
	
	void Decomp (String path,String path2)
	{
		int pixels[][]= new Run().readI(path);
		ArrayList <Integer> means = new Run().read(path2);
		
		for(int i=0 ; i < pixels.length ; i++)
			for(int j=0 ; j<pixels[i].length ; j++)
				pixels[i][j] = means.get(pixels[i][j]);
		
		new Run().writeImage(pixels, pixels[0].length, pixels.length);
		
	}
	
	int [][] readI (String path)
	{
		int pixels[][] = new int [1][1], x, y;
		
		File data = new File (path);
		
		try
		{
			Scanner in = new Scanner (data);
			
			x = in.nextInt();
			y = in.nextInt();
			
			pixels = new int [x][y];
			
			for(int i=0 ; i<x ; i++)
			{
				for(int j=0 ; j<y ; j++)
					pixels[i][j] = in.nextInt();
			}
					
			in.close();
				
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(null, "Error777777");
		}
		
		return pixels;

	}
	
	ArrayList <Integer> read (String path)
	{
		int tmp;
		ArrayList <Integer> means = new ArrayList <Integer> ();
		
		File data = new File (path);
		
		try
		{
			Scanner in = new Scanner (data);
			
			while (in.hasNextInt())
			{
				tmp = in.nextInt();
				means.add(tmp);
			}
			
			in.close();
				
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(null, "Error");
		}
		
		return means;

	}

	public int[][] readImage(String filePath)
	{

		int width=0;
		int height=0;
	    
		File file=new File(filePath);
	   
		BufferedImage image=null;
	   
		try
		{
			image=ImageIO.read(file);
			}
		catch (IOException e)
		{	   
			e.printStackTrace();
		}
	    
		width=image.getWidth();
		height=image.getHeight();
	     
		int[][] pixels=new int[height][width];
	    
		for(int x=0;x<width;x++)
		{
			for(int y=0;y<height;y++)
			{
				int rgb=image.getRGB(x, y);
				int alpha=(rgb >> 24) & 0xff;	            
				int r = (rgb >> 16) & 0xff;	    	
				int g = (rgb >> 8) & 0xff;
				int b = (rgb >> 0) & 0xff;	    		
				pixels[y][x]=r;
	    		}
	     	}
	  
	    return pixels;
	}
	 
	
	 public void writeImage(int[][] pixels,int width,int height)
     {
		 File fileout=new File("comImg.jpg");
         BufferedImage image2=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB );

         for(int x=0;x<width ;x++)
         {
             for(int y=0;y<height;y++)
             {
                 image2.setRGB(x,y,(pixels[y][x]<<16)|(pixels[y][x]<<8)|(pixels[y][x]));
             }
         }
         try
         {
             ImageIO.write(image2, "jpg", fileout);
         }
         catch (IOException e)
         {
             e.printStackTrace();
         }
     }	 
}

