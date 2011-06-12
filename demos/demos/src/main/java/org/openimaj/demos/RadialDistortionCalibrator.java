package org.openimaj.demos;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;

public class RadialDistortionCalibrator {
	private static final int SLIDER_MAX = 1000;
	private MBFImage outImage;
	private MBFImage image;
	private int midX;
	private int midY;
	private float alphaY;
	private float alphaX;
	private JFrame outFrame;



	public RadialDistortionCalibrator(MBFImage image){
		int padding = 200;
		this.outImage = image.newInstance(image.getWidth()+padding , image.getHeight()+padding);
		this.image = image;
		this.midX = outImage.getWidth()/2;
		this.midY = outImage.getHeight()/2;
		this.alphaX = 0.02f; this.alphaY = 0.08f;
		regenAndDisplay();
		createControlWindow();
	}
	


	private void createControlWindow() {
		JFrame control = new JFrame();
		control.setBounds(this.outFrame.getWidth(), 0, 700, 200);
		Container cpane = control.getContentPane();
		cpane.setLayout(new GridLayout(2,1));
		Container alphaXSlider = createSlider(new AlphaXChanger());
		Container alphaYSlider = createSlider(new AlphaYChanger());
		cpane.add(alphaXSlider);
		cpane.add(alphaYSlider);
		control.setVisible(true);
	}
	
	abstract class Changer implements ChangeListener, ActionListener{
		JTextField text = null;
		JSlider slider = null;
		public abstract String getName();
		public abstract boolean setNewValue(float value);
		public float min(){return -1f;}
		public float max(){return 1f;}
		public float range(){return max()-min();}
		public abstract float def();
		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider)e.getSource();
		    if (!source.getValueIsAdjusting()) {
		        int val = (int)source.getValue();
		        float prop = (float)val/(float)SLIDER_MAX;
		        float toSet = min() + range()*prop;
		        if(setNewValue(toSet))
		        {
		        	text.setText(toSet + "");
		        }
		    }
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			JTextField field = (JTextField) e.getSource();
			float toSet = Float.parseFloat(field.getText());
			if(setNewValue(toSet))
			{
				slider.setValue((int) (SLIDER_MAX * (toSet - min())/range()));
			}
		}
		
	}
	
	class AlphaXChanger extends Changer{
		@Override public String getName() { return "alpha X";}
		@Override public float def() { return alphaX;}
		@Override
		public boolean setNewValue(float value) {
			boolean change = value != alphaX;
			if(change)
			{
				alphaX = value;
				regenAndDisplay();
			}
			return change;
		}
		

		
	}
	class AlphaYChanger extends Changer{
		@Override public String getName() { return "alpha Y";}
		@Override public float def() { return alphaY;}
		@Override
		public boolean setNewValue(float value) { 
			boolean change = value != alphaY;
			if(change)
			{
				alphaY = value;
				regenAndDisplay();
			}
			return change;
		}
	}

	private Container createSlider(Changer changer) {
		JSlider slider = new JSlider(JSlider.HORIZONTAL,0,SLIDER_MAX,(int)(SLIDER_MAX * ((changer.def() - changer.min())/changer.range())));
		Hashtable<Integer,JLabel> labelTable = new Hashtable<Integer,JLabel>();
		labelTable.put( new Integer( 0 ), new JLabel("" + changer.min()) );
		labelTable.put( new Integer( SLIDER_MAX ), new JLabel("" + changer.max()) );
		for(float i = 1; i < 10f; i++){
			float prop = ( i / 10f ) ;
			String s = String.format("%.2f", (changer.min() + changer.range() * prop ));
			labelTable.put( new Integer( (int)(prop * SLIDER_MAX) ), new JLabel(s) );
		}
		slider.setLabelTable(labelTable);
		slider.setPaintLabels(true);
		slider.setBorder(BorderFactory.createTitledBorder(changer.getName()));
		slider.addChangeListener(changer);
		JPanel sliderHolder = new JPanel();
		SpringLayout layout = new SpringLayout();
		sliderHolder.setLayout(layout);
		sliderHolder.add(slider);
		JTextField text = new JTextField(""+changer.def(),10);
		text.addActionListener(changer);
		sliderHolder.add(text);
		layout.putConstraint(SpringLayout.WEST,slider, 5, SpringLayout.WEST, sliderHolder);
		layout.putConstraint(SpringLayout.WEST, text,5,SpringLayout.EAST, slider);
		layout.putConstraint(SpringLayout.EAST, sliderHolder,5,SpringLayout.EAST, text);
		layout.putConstraint(SpringLayout.WEST, sliderHolder,10,SpringLayout.WEST, slider);
		changer.slider = slider;
		changer.text = text;
		return sliderHolder;
	}



	private void regenAndDisplay() {
		for (float y=0;y<outImage.getHeight();y++) {
			for (float x=0;x<outImage.getWidth();x++) {
				// this pixel relative to the padding
				float paddingX = x;
				float paddingY = y;
				// Normalise x and y such that they are in a -1 to 1 range
				float normX = (paddingX - midX) / (image.getWidth()/2.0f);
				float normY = (paddingY - midY) / (image.getHeight()/2.0f);
				
				float radiusSquare = normX * normX + normY * normY;
				
				float xRatio = normX / (1 - alphaX * radiusSquare);
				float yRatio = normY / (1 - alphaY * radiusSquare);
				
				float normDistortedX = normX / (1 - alphaX * (xRatio * xRatio + yRatio * yRatio));
				float normDistortedY = normY / (1 - alphaY * (xRatio * xRatio + yRatio * yRatio));
				
				float distortedX = ((1 + normDistortedX)/ 2) * image.getWidth();
				float distortedY = ((1 + normDistortedY)/ 2) * image.getHeight();
				
				outImage.setPixel((int)x, (int)y, image.getPixelInterp(distortedX, distortedY,RGBColour.BLACK));
			}
		}
		if(this.outFrame==null){
			outFrame = DisplayUtilities.display(outImage);
		}
		else{
			DisplayUtilities.display(outImage,outFrame);
		}
	}



	public static void main(String args[]) throws IOException{
		if(args.length == 0)
			new RadialDistortionCalibrator(ImageUtilities.readMBF(RadialDistortionCalibrator.class.getResourceAsStream("/org/openimaj/image/data/35smm_original.jpg")));
		else
			new RadialDistortionCalibrator(ImageUtilities.readMBF(new File(args[0])));
	}
}
