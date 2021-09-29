import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 
 * @author Jeremy Giese
 *
 */
public class WavGUI extends JFrame {
	String toHideFilename="";
	String wavFilename="";
	String outputFilename="";
	byte[] wavContent=new byte[0];
	byte[] toWrite=new byte[0];
	int byteRate=0;
	private JPanel contentPane;
	private JTextField wavFileTextField;
	private JTextField toHideTextField;
	private JTextField outputTextField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WavGUI frame = new WavGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static boolean writeFile(String filename, byte[] fileBytes) {
		File file=new File(filename);
		Scanner aScan=new Scanner(System.in);
		boolean written=false;

		
			
			
		
		try {

			FileOutputStream fos=new FileOutputStream(filename);   
			fos.write(fileBytes);    
			fos.close();    
			written=true;



		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
		


		return written;
	}
	/**
	 * Create the frame.
	 */
	public WavGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JLayeredPane layeredPane = new JLayeredPane();
		contentPane.add(layeredPane, BorderLayout.CENTER);

		wavFileTextField = new JTextField();
		wavFileTextField.setBounds(171, 36, 130, 26);
		layeredPane.add(wavFileTextField);
		wavFileTextField.setColumns(10);

		toHideTextField = new JTextField();
		toHideTextField.setBounds(171, 74, 130, 26);
		layeredPane.add(toHideTextField);
		toHideTextField.setColumns(10);

		outputTextField = new JTextField();
		outputTextField.setBounds(171, 114, 130, 26);
		layeredPane.add(outputTextField);
		outputTextField.setColumns(10);

		JRadioButton rdbtnEncodeFile = new JRadioButton("Hide a file in *.wav");

		rdbtnEncodeFile.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				
			}
		});

		rdbtnEncodeFile.setSelected(true);
		rdbtnEncodeFile.setBounds(5, 235, 252, 23);
		layeredPane.add(rdbtnEncodeFile);
		
		JRadioButton rdbtnDecode = new JRadioButton("Decode from .wav");
		rdbtnDecode.setBounds(5, 165, 252, 23);
		layeredPane.add(rdbtnDecode);

		JRadioButton rdbtnSixBitTxt = new JRadioButton("Hide a Text File with Loss of data");
		rdbtnSixBitTxt.setBounds(5, 200, 318, 23);
		layeredPane.add(rdbtnSixBitTxt);

		JLabel lblwavFilename = new JLabel(".wav Filename:");
		lblwavFilename.setBounds(6, 41, 130, 16);
		layeredPane.add(lblwavFilename);

		JLabel lblToHide = new JLabel("Name of file to hide: ");
		lblToHide.setBounds(6, 79, 153, 16);
		layeredPane.add(lblToHide);

		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outputFilename=outputTextField.getText();
				wavFilename=wavFileTextField.getText();
				toHideFilename=toHideTextField.getText();
				boolean written=false;
				int retry=JOptionPane.YES_OPTION;
				while(retry==JOptionPane.YES_OPTION) {
				if(rdbtnEncodeFile.isSelected()) {
					File wavFile=new File(wavFilename);
					File toHideFile=new File(toHideFilename);
					File outputFile=new File(outputFilename);
					
					//if(wavFile.exists()&&toHideFile.exists()&&!outputFile.exists()) {
			
					if(wavFile.exists()&&toHideFile.exists()) {
					try {
						
						wavContent=HideWav.readFile(wavFilename);
						String toHideString=HideWav.makeFileBinString(toHideFilename);
						byteRate=(int)wavContent[33]+(int)wavContent[32];
						
						if((int)(toHideString.length())-1<=Math.abs(HideWav.possibleStringByteLength(wavContent.length))) {
							
							toWrite=HideWav.createEncodedFileCorrectly(wavContent, toHideString,byteRate);
							
							written =false;
							if(toWrite.length>0) {
								if(outputFile.exists()) {
									int[] optionArray=new int[] {JOptionPane.YES_OPTION,JOptionPane.NO_OPTION};
									int removeFile=JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(), "The filename chosen to write already exists, would you like to overwrite it?","Overwrite?",JOptionPane.YES_NO_OPTION);
									if(removeFile==optionArray[0]) {
										try {
											Files.delete(outputFile.toPath());
										}catch(IOException exc){
											JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),"Could not delete the file", "File not Deleted", JOptionPane.ERROR_MESSAGE);
										}
										
									}
								}
								if(outputFilename.contains(".wav")) {
									written=HideWav.writeFile(outputFilename,toWrite);
								}else {
									written = writeFile(outputFilename+".wav",toWrite);
								}
								if(written) {
									JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "File was written as "+outputFilename, "File encoded successfully", JOptionPane.INFORMATION_MESSAGE);
									retry=JOptionPane.NO_OPTION;
								}
							}
							else {
								throw new Exception();
							}
						}
					}catch(Exception exc){
						JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "The File was not encoded", "An error has occurred",JOptionPane.ERROR_MESSAGE);
					}
					}

				}
				else if(rdbtnDecode.isSelected()) {
					File wavFile=new File(wavFilename);
					File outputFile=new File(outputFilename);
					
					
					 if(wavFile.exists()) {
						try {
							wavContent=HideWav.readFile(wavFilename);
							byteRate=(int)wavContent[33]+(int)wavContent[32];
							String ext=HideWav.findExtFromWav(wavContent, byteRate);
							if(outputFilename.contains(".")) {
								outputFilename=outputFilename.split("\\.")[0];
							}
							toWrite=HideWav.retrieveEncodedFileCorrectly(wavContent, outputFilename,byteRate);
							written =false;
							if(toWrite.length>0) {
								if(outputFilename.contains(".")) {
									outputFilename=outputFilename.split("\\.")[0];
								}
								if(outputFile.exists()) {
									int[] optionArray=new int[] {JOptionPane.YES_OPTION,JOptionPane.NO_OPTION};
									int removeFile=JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(), "The filename chosen to write already exists, would you like to overwrite it?","Overwrite?",JOptionPane.YES_NO_OPTION);
									if(removeFile==optionArray[0]) {
										try {
											Files.delete(outputFile.toPath());
										}catch(IOException exc){
											JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),"Could not delete the file", "File not Deleted", JOptionPane.ERROR_MESSAGE);
										}
										
									}
								}
									written = writeFile(outputFilename+ext,toWrite);

									if(written) {
										JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "File was written as "+outputFilename+ext, "File encoded successfully", JOptionPane.INFORMATION_MESSAGE);
										retry=JOptionPane.NO_OPTION;
									}
								
								else {
									throw new Exception();
								}
							}
						}catch(Exception exc){
							exc.printStackTrace();
							JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "The File was not decoded", "An error has occurred",JOptionPane.ERROR_MESSAGE);
						}
					}

				}
				else if(rdbtnSixBitTxt.isSelected()) {
					File wavFile=new File(wavFilename);
					File toHideFile=new File(toHideFilename);
					File outputFile=new File(outputFilename);
					
					if(wavFile.exists()&&toHideFile.exists()) {

						try {
							wavContent=HideWav.readFile(wavFilename);
							String toHideString=HideWav.makeFileSixBitASCIIString(toHideFilename);
							byteRate=(int)wavContent[33]+(int)wavContent[32];
							if(HideWav.findFileSize(toHideFilename)<Math.abs(HideWav.possibleStringSixBitASCIILength(wavContent.length))) {
								toWrite=HideWav.createEncodedFileCorrectly(wavContent, toHideString,byteRate);
								written =false;
								System.out.println(toWrite.length);
								if(toWrite.length>0) {
									if(outputFile.exists()) {
										int[] optionArray=new int[] {JOptionPane.YES_OPTION,JOptionPane.NO_OPTION};
										int removeFile=JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(), "The filename chosen to write already exists, would you like to overwrite it?","Overwrite?",JOptionPane.YES_NO_OPTION);
										if(removeFile==optionArray[0]) {
											try {
												Files.delete(outputFile.toPath());
											}catch(IOException exc){
												JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),"Could not delete the file", "File not Deleted", JOptionPane.ERROR_MESSAGE);
											}
											
										}
									}
									
									if(outputFilename.contains(".wav")) {
										written=writeFile(outputFilename,toWrite);
										
									}else {
										written = writeFile(outputFilename+".wav",toWrite);
										outputFilename=outputFilename+".wav";
									}
									if(written) {
										JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "File was written as "+outputFilename, "File encoded successfully", JOptionPane.INFORMATION_MESSAGE);
										retry=JOptionPane.NO_OPTION;
									}
								}
								else {
									throw new Exception();
								}
							}
						}catch(Exception exc){
							JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "The File was not encoded", "An error has occurred",JOptionPane.ERROR_MESSAGE);
						}
					}

				}
				if(!written&&retry==JOptionPane.YES_OPTION) {
					retry=JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(), "The operation did not complete properly, would you like to try again","Retry?",JOptionPane.YES_NO_OPTION);
				}
				}
			}
		});
		btnStart.setBounds(303, 229, 117, 29);
		layeredPane.add(btnStart);

		JLabel lblOutputLabel = new JLabel("Name of file to output:");
		lblOutputLabel.setBounds(6, 119, 153, 16);
		layeredPane.add(lblOutputLabel);
		rdbtnEncodeFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbtnDecode.setSelected(false);
				rdbtnSixBitTxt.setSelected(false);
				toHideTextField.setVisible(true);
				wavFileTextField.setVisible(true);
				outputTextField.setVisible(true);
			}
		});
		rdbtnDecode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbtnEncodeFile.setSelected(false);
				rdbtnSixBitTxt.setSelected(false);
				toHideTextField.setVisible(false);
				wavFileTextField.setVisible(true);
				outputTextField.setVisible(true);
			}
		});
		rdbtnSixBitTxt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbtnDecode.setSelected(false);
				rdbtnEncodeFile.setSelected(false);
				toHideTextField.setVisible(true);
				wavFileTextField.setVisible(true);
				outputTextField.setVisible(true);
			}
		});
	}
}
