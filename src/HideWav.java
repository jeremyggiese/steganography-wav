
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.InputMismatchException;
import java.util.Scanner;
/**
 * The HideWav program allows the user to hide files(excluding "docx" and those with extensions greater than 4 characters)
 * within a file with the wav extension
 * 
 * @version 1.0
 * @author Jeremy Giese
 * @since 03-07-2019
 */
public class HideWav {
	static String fileExt="";
	static int byteRate=0;
	public enum CharToSixBitASCIIBit{
		//EOF(11110), A(00001),B(00010), C(00011),D(00100),E(00101),F(00110),G(00111),H(01000),I(01001),J(01010),K(01011),L(01100),M(01101),N(01110),O(01111),P(10000),Q(10001),R(10010),S(10011),T(10100),U(10101),V(10110),W(10111),X(11000),Y(11001),Z(11010);
		AT("000000"),SPACE("100000"),A("000001"),B("000010"),C("000011"),D("000100"),E("000101"),F("000110"),G("000111"),H("001000"),I("001001"),J("001010"),K("001011"),L("001100"),M("001101"),N("001110"),O("001111"),P("010000"),Q("010001"),R("010010"),S("010011"),T("010100"),U("010101"),V("010110"),W("010111"),X("011000"),Y("011001"),Z("011010"),
		ZERO("110000"),ONE("110001"),TWO("110010"),FOUR("110100"), THREE("110011"),FIVE("110101"),SIX("110110"),SEVEN("110111"),EIGHT("111000"),NINE("111001"),
		COLON("111010"),SEMICOL("111011"),LESSTHAN("111100"),EQUALS("111101"), GREATTHAN("111110"),QSTMRK("111111"),LEFTBRACK("011011"),BACKSLASH("011100"),RIGHTBRACK("011101"),
		NEWLINE("011110"),UNDERSCORE("011111"),EXCLPOINT("100001"),QUOTE("100010"),BLANK("100011"),DOLLAR("100100"),PERCENT("100101"),ANDSYM("100110"), SINGLEQUOTE("100111"),
		LEFTPAREN("101000"),RIGHTPAREN("101001"),STAR("101010"),PLUS("101011"),COMMA("101100"), DASH("101101"), PERIOD("101110"), FORWARDSLASH("101111");

		private String binaryValue="";

		CharToSixBitASCIIBit(String binaryValue){
			this.binaryValue=binaryValue;
		}
		/**
		 * 
		 * @return the value stored in the Enum
		 */
		public String getBinaryValue() {
			return binaryValue;
		}
	}
	/**
	 * The main String
	 * @param args not used
	 */
	public static void main(String[] args) {
		int choice=menu();
		boolean properRun=false;
		while(choice!=6) {
			switch(choice) {
			case 1: 
				properRun=setUpEncodeByte();
				if(!properRun) {
					System.out.println("There was an error during encoding");
					}
				choice=menu();
			break;
			case 2: 
				properRun=setUpDecode();
				if(!properRun) {
				System.out.println("There was an error during decoding.\nAre you sure the file you have chosen has hidden data?");
				} 
				choice=menu();
			break;
			case 3:
				properRun=setUpEncodeSixBitASCII(); 
				if(!properRun) {
					System.out.println("There was an error during encoding.");
				}
				choice=menu();
			break;
			case 4: 
				properRun=setUpEncodeDestructive(); 
			if(!properRun) {System.out.println("There was an error during encoding.");
			} 
			choice=menu();
			break;
			case 5: properRun=setUpDecodeDestructive(); 
			if(!properRun) {
				System.out.println("There was an error during decoding.\nAre you sure the file you have chosen has hidden data?");
				} 
			choice=menu();
			default: choice=menu();
			break;
			}
		}
		/*
		byte[] testArr=readFile("rio.wav");
		int j=0;
		System.out.println(testArr[33]+" "+ testArr[32]);
		for (int i = 0; i <44; i++) {
			if(testArr[i]==0x61) {
				System.out.println("d is at "+ i);
			}
		}*/
		

	}

	/*
	 * Chunk 0-3 -->
	 * Chunk Size 4-7 <--
	 * Format 8-11 -->
	 * Subchunk ID 12-15 -->
	 * Subchunk Size 16-19 <--
	 * Audio Format 20-21 <--
	 * Number of Channels 22-23 <--
	 * Sample Rate 23-26 <--
	 * Byte Rate 26-29 <--
	 * Block Align 30-31 <--
	 * Bits per Sample 32-33 <--
	 * Data Subchunk ID 36-39 -->
	 * Subchunk Size 40-43 <--
	 * DATA follows <--
	 */

	/**
	 * the method to print a string which represents the menu and accept the input from the user
	 * @return an integer which represents the users choice
	 */
	public static int menu() {
		int realChoice=0;
		Scanner aScan=new Scanner(System.in);
		System.out.println("Input the number corresponding to the option you would like to perform from the following:\n1. To store a file within a wav file.\n2. To retrieve a file from a Wav file.\n3. To store a substantially large text file within a wav file using the Letters from SixBitASCII standard(excludes lowercase and some special characters).\n4. To store a file using all bits possible, in some cases it is very noticeable.\n5. To retrieve a file that was stored using all bits possible.\n6. To exit.");
		String choice=aScan.nextLine();
		try {
			realChoice=Integer.parseInt(choice);
		}catch(Exception exc) {
			System.out.println("That input was not accepted");
			return 0;
		}
		return realChoice;
	}
	/**
	 * The method used to begin encoding a file within a .wav file
	 * @return a boolean which represents whether encoding was successful
	 */
	public static boolean setUpEncodeByte() {
		String toHideFileName="";
		String wavFileName="";
		String toHideBin="";
		byte[] wavFileContent=new byte[0];
		byte[] hiddenWavContent=new byte[0];
		String hiddenToSave="";
		Scanner aScan=new Scanner(System.in);
		while(!new File(toHideFileName).exists()) {
			System.out.println("Enter the name of the file you would like to hide:");
			toHideFileName=aScan.nextLine();
		}
		while(wavFileContent.length<1) {
			System.out.println("Enter the name of the .wav file you wish to hide data within:");
			wavFileName=aScan.nextLine();

			if(wavFileName.contains(".wav")) {
				wavFileContent=readFile(wavFileName);
			}else {
				wavFileContent=readFile(wavFileName+".wav");
			}
		}
		byteRate=(int)wavFileContent[33]+(int)wavFileContent[32];
		//System.out.println(findFileSize(toHideFileName));
		if(findFileSize(toHideFileName)<possibleStringByteLength(wavFileContent.length)) {
			toHideBin=makeFileBinString(toHideFileName);
		}else {
			double maxSize=(double)possibleStringByteLength(wavFileContent.length)/1000000.0;
			System.out.println("The file chosen is too large to encode.\nThe largest file this method can store using the chosen wav file is "+maxSize+"MB");
			return false;
		}
		
			hiddenWavContent=createEncodedFileCorrectly(wavFileContent, toHideBin, byteRate);
			System.out.println("Enter the name for the output file:");
			hiddenToSave=aScan.nextLine();
			if(hiddenToSave.contains(".wav")) {
				writeFile(hiddenToSave, hiddenWavContent);
			}else {
				writeFile(hiddenToSave+".wav",hiddenWavContent);

			}
			return true;
		

	}
	/**
	 * 
	 * @return
	 */
	public static boolean setUpEncodeDestructive() {
		String toHideFileName="";
		String wavFileName="";
		String toHideBin="";
		byte[] wavFileContent=new byte[0];
		byte[] hiddenWavContent=new byte[0];
		String hiddenToSave="";
		Scanner aScan=new Scanner(System.in);
		while(!new File(toHideFileName).exists()) {
			System.out.println("Enter the name of the file you would like to hide:");
			toHideFileName=aScan.nextLine();
		}
		while(wavFileContent.length<1) {
			System.out.println("Enter the name of the .wav file you wish to hide data within:");
			wavFileName=aScan.nextLine();

			if(wavFileName.contains(".wav")) {
				wavFileContent=readFile(wavFileName);
			}else {
				wavFileContent=readFile(wavFileName+".wav");
			}
		}
		byteRate=(int)wavFileContent[33]+(int)wavFileContent[32];
	
		if(findFileSize(toHideFileName)<possibleStringByteDestructive(wavFileContent.length)) {
			toHideBin=makeFileBinString(toHideFileName);
		}else {
			return false;
		}
		
			hiddenWavContent=createEncodedFileDestructive(wavFileContent, toHideBin);
			System.out.println("Enter the name for the output file:");
			hiddenToSave=aScan.nextLine();
			if(hiddenToSave.contains(".wav")) {
				writeFile(hiddenToSave, hiddenWavContent);
			}else {
				writeFile(hiddenToSave+".wav",hiddenWavContent);
				hiddenToSave+=".wav";

			}
			System.out.println("File was saved as "+hiddenToSave);;
			return true;
		

	}
	/**
	 * The method used to begin decoding a file from a .wav file
	 * @return a boolean which represents if the method worked or not
	 */
	public static boolean setUpDecode() {
		String wavFileName="";
		String toSaveName="";
		byte[] wavFileContent=new byte[0];
		byte[] hiddenFileContent=new byte[0];
		Scanner aScan=new Scanner(System.in);
		System.out.println("Enter the name of the .wav file:");
		wavFileName=aScan.nextLine();
		if(wavFileName.contains(".wav")) {
			wavFileContent=readFile(wavFileName);
		}else {
			wavFileContent=readFile(wavFileName+".wav");
		}
		if(wavFileContent.length<16) {
			System.out.println("Critical Error");
			return false;
		}
		byteRate=(int)wavFileContent[33]+(int)wavFileContent[32];
		System.out.println("Enter the name you would like to store the file under:");
		toSaveName=aScan.nextLine();
		if(toSaveName.contains(".")) {
			toSaveName=toSaveName.split("\\.")[0];
		}
		hiddenFileContent=retrieveEncodedFileCorrectly(wavFileContent, toSaveName, byteRate);
		//System.out.println(hiddenFileContent.length);
		if(hiddenFileContent.length>0) {
			System.out.println("Writing "+toSaveName+fileExt+" to Disk");
			writeFile(toSaveName+fileExt,hiddenFileContent);
			return true;
		}else {
			return false;
		}
	}
	/**
	 * 
	 * @return
	 */
	public static boolean setUpDecodeDestructive() {
		String wavFileName="";
		String toSaveName="";
		byte[] wavFileContent=new byte[0];
		byte[] hiddenFileContent=new byte[0];
		Scanner aScan=new Scanner(System.in);
		System.out.println("Enter the name of the .wav file:");
		wavFileName=aScan.nextLine();
		if(wavFileName.contains(".wav")) {
			wavFileContent=readFile(wavFileName);
		}else {
			wavFileContent=readFile(wavFileName+".wav");
		}
		if(wavFileContent.length<16) {
			System.out.println("Critical Error");
			return false;
		}
		byteRate=(int)wavFileContent[33]+(int)wavFileContent[32];
		System.out.println("Enter the name you would like to store the file under:");
		toSaveName=aScan.nextLine();
		if(toSaveName.contains(".")) {
			toSaveName=toSaveName.split("\\.")[0];
		}
		hiddenFileContent=retrieveEncodedFileDestructive(wavFileContent, toSaveName);
		//System.out.println(hiddenFileContent.length);
		if(hiddenFileContent.length>0) {
			System.out.println("Writing "+toSaveName+fileExt+" to Disk");
			writeFile(toSaveName+fileExt,hiddenFileContent);
			return true;
		}else {
			return false;
		}
	}
	/**
	 * The method used to begin encoding a .txt file within a .wav file with significant data loss
	 * @return a boolean of whether it successfully encoded
	 */
	public static boolean setUpEncodeSixBitASCII() {
		String toHideFileName="";
		String wavFileName="";
		String toHideBin="";
		byte[] wavFileContent=new byte[0];
		byte[] hiddenWavContent=new byte[0];
		String hiddenToSave="";
		Scanner aScan=new Scanner(System.in);
		while(toHideBin.length()<16) {
			System.out.println("Enter the name of the file you would like to hide:");
			toHideFileName=aScan.nextLine();
			if(toHideFileName.contains(".txt")) {
				toHideBin=makeFileSixBitASCIIString(toHideFileName);
			}else {
				System.out.println("This method only stores files in the .txt format successfully");
			}
		}
		while(wavFileContent.length<1) {
			System.out.println("Enter the name of the .wav file you wish to hide data within:");
			wavFileName=aScan.nextLine();
			if(wavFileName.contains(".wav")) {
				wavFileContent=readFile(wavFileName);
			}else {
				wavFileContent=readFile(wavFileName+".wav");
			}
		}
		byteRate=(int)wavFileContent[33]+(int)wavFileContent[32];
		if(toHideBin.length()-1<possibleStringSixBitASCIILength(wavFileContent.length)) {
			hiddenWavContent=createEncodedFileCorrectly(wavFileContent, toHideBin, byteRate);
			System.out.println("Enter the name for the output file:");
			hiddenToSave=aScan.nextLine();
			if(hiddenToSave.contains(".wav")) {
				writeFile(hiddenToSave, hiddenWavContent);
			}else {
				writeFile(hiddenToSave+".wav",hiddenWavContent);

			}
			return true;
		}else {
			System.out.println("File is too large to hide within the supplied wav in SixBitASCII encoding.");
			return false;
		}
	}
	/**
	 * The method used to read a file into the program
	 * @param filename the filename to be read entered by the user
	 * @return the bytes which represent the file
	 */

	public static byte[] readFile(String filename){
		//String toReturn="";
		byte[] fileContent=new byte[0];
		try {
			File file=new File(filename);
			fileContent=Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			System.out.println("File Not Found");
			return fileContent;
		}
		return fileContent;
	}
	/**
	 * The method used to modify a file's bytes(.wav) to contain another file
	 * @param originalFileContent The bytes of the wav file
	 * @param toHide A String containing the bits of the file the user wishes to hide
	 * @return The modified wav file in bytes
	 */
	public static byte[] createEncodedFileCorrectly(byte[] originalFileContent, String toHide,int byteRate){
		char[] toHideArr=toHide.toCharArray();
		
		//System.out.println(toHide.length());
//		System.out.println(toHide.substring(0,18));
//		System.out.println(toHide.substring(19, 41));
//		System.out.println(toHide.substring(42,75));
		int j=0;
		for (int i = 44; i < originalFileContent.length; i++) {
			if((i-(44+(byteRate/2)-1))%byteRate!=0&&(i-(44+(byteRate)-1))%byteRate!=0) {
			if(j<toHideArr.length&&toHideArr[j]=='1'&&Math.abs((int)(originalFileContent[i]))%2==0) {
				//even is 1
				
			}else if(j<toHideArr.length&&toHideArr[j]=='1'&&Math.abs((int)(originalFileContent[i]))%2==1){
				
					originalFileContent[i]=(byte)(originalFileContent[i]+1);
					//originalFileContent[i-1]=(byte)(originalFileContent[i-1]+1);
				
			}else if(j<toHideArr.length&&toHideArr[j]=='0'&&Math.abs((int)(originalFileContent[i]))%2==1){
				
			}else if(j<toHideArr.length&&toHideArr[j]=='0'&&Math.abs((int)(originalFileContent[i]))%2==0){
				
					originalFileContent[i]=(byte)(originalFileContent[i]+1);
					//originalFileContent[i-1]=(byte)(originalFileContent[i-1]+1);
					//odd is zero

			}
			j++;
		}
			
			//else
				//System.out.println(i);
		}
		
		return originalFileContent;
	}
	/**
	 * 
	 * @param originalFileContent
	 * @param toHide
	 * @return
	 */
	public static byte[] createEncodedFileDestructive(byte[] originalFileContent, String toHide){
		
		//System.out.println(toHide.length());
//		System.out.println(toHide.substring(0,18));
//		System.out.println(toHide.substring(19, 41));
//		System.out.println(toHide.substring(42,75));
		toHide=toHide.substring(18);
		
		toHide=CharToSixBitASCIIBit.QSTMRK.binaryValue+CharToSixBitASCIIBit.EXCLPOINT.binaryValue+CharToSixBitASCIIBit.COMMA.binaryValue+toHide;
		
		char[] toHideArr=toHide.toCharArray();
		
		for (int i = 44; i < originalFileContent.length; i++) {
			
			if(i-44<toHideArr.length&&toHideArr[i-44]=='1'&&Math.abs((int)(originalFileContent[i]))%2==0) {
				//even is 1
				
			}else if(i-44<toHideArr.length&&toHideArr[i-44]=='1'&&Math.abs((int)(originalFileContent[i]))%2==1){
				
					originalFileContent[i]=(byte)(originalFileContent[i]+1);
				
			}else if(i-44<toHideArr.length&&toHideArr[i-44]=='0'&&Math.abs((int)(originalFileContent[i]))%2==1){
				
			}else if(i-44<toHideArr.length&&toHideArr[i-44]=='0'&&Math.abs((int)(originalFileContent[i]))%2==0){
				
					originalFileContent[i]=(byte)(originalFileContent[i]+1);
				
					
				
					//odd is zero

			
			
		}
			//else
				//System.out.println(i);
		}
		
		return originalFileContent;
	}
	/**
	 * Finds the size of a file before reading the file into the program
	 * @param fileName the name of the file the user wishes to store
	 * @return The size of the file as an integer
	 */
	public static int findFileSize(String fileName) {
		File file=new File(fileName);
		if(file.exists())
			return (int)(file.length())*8;
		return 0;

	}
	/**
	 * Finds the size of a file before reading the file into the program
	 * @param fileName the name of the file the user wishes to store
	 * @return The size of the file as an integer
	 */
	public static int findFileSizeSixBit(String fileName) {
		File file=new File(fileName);
		if(file.exists())
			return (int)(file.length())*6;
		return 0;

	}
	/**
	 * The method used to read in a file stored in a wav file
	 * @param originalFileContent The Wav file that has a file stored within it
	 * @param fileNameToWrite The filename the user wishes to store the file under, used to describe whatis being retrieved
	 * @return The file which was stored in the wav file
	 */
	public static byte[] retrieveEncodedFileCorrectly(byte[] originalFileContent, String fileNameToWrite, int byteRate){

		StringBuilder hiddenBuilder=new StringBuilder(originalFileContent.length);
		String hidden="";
		int hiddenLength=0;
		String hiddenExt="";
		boolean sixBitASCIICrypt=false;
		boolean eightBitHidden=false;
		int j=44;
		//String toBreak=CharToSixBitASCIIBit.LTRS.binaryValue+CharToSixBitASCIIBit.NUL.binaryValue+CharToSixBitASCIIBit.Y.binaryValue+CharToSixBitASCIIBit.NUL.binaryValue+CharToSixBitASCIIBit.LTRS.binaryValue;
		for (int i = 44; i < originalFileContent.length; i++) {
			if((i-(44+(byteRate/2)-1))%byteRate!=0&&(i-(44+(byteRate)-1))%byteRate!=0) {
				if(Math.abs((int)(originalFileContent[i]))%2==0) {
					hiddenBuilder.append("1");
				}else if(Math.abs((int)(originalFileContent[i]))%2==1){
					hiddenBuilder.append("0");

				}if(j==(44+23+18)){

					hidden=hiddenBuilder.toString();
					//System.out.println(hidden+" At ext find");
					hiddenBuilder.setLength(0);
					hiddenExt=findSixBitASCIIInTxt(hidden);
					System.out.println("Retrieving file "+fileNameToWrite+hiddenExt);
					//System.out.println(hiddenExt);
					hidden="";
				}
				if(j==(44+24+32+18)) {
					hidden=hiddenBuilder.toString();
					//System.out.println(hidden +" At find size");
					hiddenBuilder.setLength(0);
					try {
					hiddenLength = Integer.parseInt(hidden, 2);
					}catch(InputMismatchException exc){
						System.out.println("This file may not have hidden data!");
						hidden ="";
						break;
					}
					
					//System.out.println(hiddenLength);
					hidden="";
				}if(j==(44+17)) {
					hidden=hiddenBuilder.toString();
					//System.out.println(hidden +" At find encodetype");
					hiddenBuilder.setLength(0);
					if(hidden.equals("111100101101111110")) {

						sixBitASCIICrypt=true;	
					}else if(hidden.equals("111110101011111100")) {
						
						eightBitHidden=true;
						
					}else {
						break;
					}
					//1101011110010110111
					//System.out.println(hidden);
					hidden="";
				}

				j++;
			}
			if(j>(hiddenLength+44+24+32+19)) {
				hidden=hiddenBuilder.toString();
				//System.out.println(hidden.length());
				break;
			}

		}
		//String hiddenExtBin=hidden.substring(0,24);


		//String hiddenExt=findSixBitASCIIInTxt(hiddenExtBin.substring(0, 23));
		//System.out.println(hiddenExt);
		fileExt=hiddenExt;
		byte[] hiddenContent=new byte[0];
		//System.out.println(hidden.substring(44, 72));
		if(sixBitASCIICrypt) {//hidden.substring(44, 71).equals("111111101111111")) {
			//System.out.println("here");
			String hiddenContentString=findSixBitASCIIInTxt(hidden).toUpperCase();
			hiddenContent=hiddenContentString.getBytes();
		}else if(eightBitHidden) {
			//hidden=hidden.substring(72,hidden.length());
			hiddenContent=new byte[(hidden.length())/8];
			try {
			for (int i = 0; i < hidden.length()-8; i+=8) {
				String binaryStringChunk = hidden.substring(i, i + 8);
				Integer byteAsInt = Integer.parseInt(binaryStringChunk, 2);
				hiddenContent[i/8] = byteAsInt.byteValue();
			}
			}catch(InputMismatchException exc) {
				System.out.println("The file you have chosen may not contain any hidden data");
				return new byte[0];
			}
		}
		return hiddenContent;
	}
	/**
	 * 
	 * @param originalFileContent
	 * @param fileNameToWrite
	 * @return
	 */
	public static byte[] retrieveEncodedFileDestructive(byte[] originalFileContent, String fileNameToWrite){

		StringBuilder hiddenBuilder=new StringBuilder(originalFileContent.length);
		String hidden="";
		int hiddenLength=0;
		String hiddenExt="";
		
		boolean eightBitHidden=false;
		//System.out.println(originalFileContent.length);
		for (int i = 44; i < originalFileContent.length; i++) {

			if(Math.abs((int)(originalFileContent[i]))%2==0) {
				hiddenBuilder.append("1");
			}else if(Math.abs((int)(originalFileContent[i]))%2==1){
				hiddenBuilder.append("0");

			}if(i==(44+23+18)){

				hidden=hiddenBuilder.toString();
				//System.out.println(hidden+" At ext find");
				hiddenBuilder.setLength(0);
				hiddenExt=findSixBitASCIIInTxt(hidden);
				System.out.println("Retrieving file "+fileNameToWrite+hiddenExt);
				//System.out.println(hiddenExt);
				hidden="";
			}
			if(i==(44+24+32+18)) {
				hidden=hiddenBuilder.toString();
				//System.out.println(hidden +" At find size");
				hiddenBuilder.setLength(0);
				try {
				hiddenLength = Integer.parseInt(hidden, 2);
				}catch(InputMismatchException exc) {
					System.out.println("This file may not contain any hidden data");
					hidden ="";
					break;
				}

				//System.out.println(hiddenLength);
				hidden="";
			}if(i==(44+17)) {
				hidden=hiddenBuilder.toString();
				//System.out.println(hidden +" At find encodetype");
				hiddenBuilder.setLength(0);
				if(hidden.equals("111111100001101100")) {
								//111111100001101100
					eightBitHidden=true;
					
				}else {
					break;
				}
				//1101011110010110111
				//System.out.println(hidden);
				hidden="";
			}

			if(i>(hiddenLength+44+24+32+18)) {
				hidden=hiddenBuilder.toString();
				//System.out.println(hidden.length());
				break;
			}

		}
		//String hiddenExtBin=hidden.substring(0,24);
		

		//String hiddenExt=findSixBitASCIIInTxt(hiddenExtBin.substring(0, 23));
		//System.out.println(hiddenExt);
		fileExt=hiddenExt;
		byte[] hiddenContent=new byte[0];
		//System.out.println(hidden.substring(44, 72));
		 if(eightBitHidden) {
			//hidden=hidden.substring(72,hidden.length());
			hiddenContent=new byte[(hidden.length()-1)/8];
			try {
			for (int i = 0; i < hidden.length()-8; i+=8) {
				String binaryStringChunk = hidden.substring(i, i + 8);
				Integer byteAsInt = Integer.parseInt(binaryStringChunk, 2);
				hiddenContent[i/8] = byteAsInt.byteValue();
			}
			}catch(InputMismatchException exc) {
				System.out.println("The file you have chosen may not contain any hidden data.");
				return new byte[0];
			}
		}
		return hiddenContent;
	}
	/**
	 * Finds the max size the selected wav file can hold with normal 8 bit storage
	 * @param fileContentLength
	 * @return the max size that the wav can hold accounting for bytes to represent what the file is holding
	 */
	public static int possibleStringByteLength(int fileContentLength) {
		int possibleToWrite=(((fileContentLength)/(byteRate-2))-74);
		
		return possibleToWrite;
	}
	
	public static int possibleStringByteDestructive(int fileContentLength) {
		int possibleToWrite=(((fileContentLength))-74);
		return possibleToWrite;
	}
	/**
	 *  Finds the max size the selected wav file can hold with 6 bit ascii character storage
	 * @param fileContentLength the length of the byte array representing the wav file
	 * @return the max size that the wav can hold accounting for bytes to represent what the file is holding
	 */
	public static int possibleStringSixBitASCIILength(int fileContentLength) {
		int possibleToWrite=(((fileContentLength-44)-74)/(byteRate-2));
		return possibleToWrite;
	}
	/**
	 * Finds the extension of a file
	 * @param fileName the name of the file to be stripped for its extension
	 * @return the extension in the filename
	 */
	public static String fileExt(String fileName) {
		String[] fileExtArray=fileName.split("\\.");
		String ext=fileExtArray[fileExtArray.length-1];
		return ext.toLowerCase();
	}
	/**
	 * the method used to convert from normal ascii to six bits representing each character
	 * @param ext the extension in plaintext
	 * @return the extension in six bit ascii to store in the file
	 */
	public static String findFileExtInSixBitASCII(String ext) {
		StringBuilder extStringBuilder=new StringBuilder(ext.length()*6);
		String extInBin="";
		char currentChar=' ';
		for (int i = 0; i < ext.length(); i++) {
			currentChar=ext.charAt(i);
			switch(currentChar) {
			case 'a': extStringBuilder.append(CharToSixBitASCIIBit.A.binaryValue);
			break;
			case 'b': extStringBuilder.append(CharToSixBitASCIIBit.B.binaryValue);
			break;
			case 'c': extStringBuilder.append(CharToSixBitASCIIBit.C.binaryValue);
			break;
			case 'd': extStringBuilder.append(CharToSixBitASCIIBit.D.binaryValue);
			break;
			case 'e': extStringBuilder.append(CharToSixBitASCIIBit.E.binaryValue);
			break;
			case 'f': extStringBuilder.append(CharToSixBitASCIIBit.F.binaryValue);
			break;
			case 'g': extStringBuilder.append(CharToSixBitASCIIBit.G.binaryValue);
			break;
			case 'h': extStringBuilder.append(CharToSixBitASCIIBit.H.binaryValue);
			break;
			case 'i': extStringBuilder.append(CharToSixBitASCIIBit.I.binaryValue);
			break;
			case 'j': extStringBuilder.append(CharToSixBitASCIIBit.J.binaryValue);
			break;
			case 'k': extStringBuilder.append(CharToSixBitASCIIBit.K.binaryValue);
			break;
			case 'l': extStringBuilder.append(CharToSixBitASCIIBit.L.binaryValue);
			break;
			case 'm': extStringBuilder.append(CharToSixBitASCIIBit.M.binaryValue);
			break;
			case 'n': extStringBuilder.append(CharToSixBitASCIIBit.N.binaryValue);
			break;
			case 'o': extStringBuilder.append(CharToSixBitASCIIBit.O.binaryValue);
			break;
			case 'p': extStringBuilder.append(CharToSixBitASCIIBit.P.binaryValue);
			break;
			case 'q': extStringBuilder.append(CharToSixBitASCIIBit.Q.binaryValue);
			break;
			case 'r': extStringBuilder.append(CharToSixBitASCIIBit.R.binaryValue);
			break;
			case 's': extStringBuilder.append(CharToSixBitASCIIBit.S.binaryValue);
			break;
			case 't': extStringBuilder.append(CharToSixBitASCIIBit.T.binaryValue);
			break;
			case 'u': extStringBuilder.append(CharToSixBitASCIIBit.U.binaryValue);
			break;
			case 'v': extStringBuilder.append(CharToSixBitASCIIBit.V.binaryValue);
			break;
			case 'w': extStringBuilder.append(CharToSixBitASCIIBit.W.binaryValue);
			break;
			case 'x': extStringBuilder.append(CharToSixBitASCIIBit.X.binaryValue);
			break;
			case 'y': extStringBuilder.append(CharToSixBitASCIIBit.Y.binaryValue);
			break;
			case 'z': extStringBuilder.append(CharToSixBitASCIIBit.Z.binaryValue);
			break;
			case ' ': extStringBuilder.append(CharToSixBitASCIIBit.SPACE.binaryValue);
			break;
			case '\n': extStringBuilder.append(CharToSixBitASCIIBit.NEWLINE.binaryValue);
			break;
			case '.': extStringBuilder.append(CharToSixBitASCIIBit.PERIOD.binaryValue);
			break;
			case ':': extStringBuilder.append(CharToSixBitASCIIBit.COLON.binaryValue);
			break;
			case '1': extStringBuilder.append(CharToSixBitASCIIBit.ONE.binaryValue);
			break;
			case '2': extStringBuilder.append(CharToSixBitASCIIBit.TWO.binaryValue);
			break;
			case '3': extStringBuilder.append(CharToSixBitASCIIBit.THREE.binaryValue);
			break;
			case '4': extStringBuilder.append(CharToSixBitASCIIBit.FOUR.binaryValue);
			break;
			case '5': extStringBuilder.append(CharToSixBitASCIIBit.FIVE.binaryValue);
			break;
			case '6': extStringBuilder.append(CharToSixBitASCIIBit.SIX.binaryValue);
			break;
			case '7': extStringBuilder.append(CharToSixBitASCIIBit.SEVEN.binaryValue);
			break;
			case '8': extStringBuilder.append(CharToSixBitASCIIBit.EIGHT.binaryValue);
			break;
			case '9': extStringBuilder.append(CharToSixBitASCIIBit.NINE.binaryValue);
			break;
			case ';': extStringBuilder.append(CharToSixBitASCIIBit.SEMICOL.binaryValue);
			break;
			case '\\': extStringBuilder.append(CharToSixBitASCIIBit.BACKSLASH.binaryValue);
			break;
			case '/': extStringBuilder.append(CharToSixBitASCIIBit.FORWARDSLASH.binaryValue);
			break;
			case '0': extStringBuilder.append(CharToSixBitASCIIBit.ZERO.binaryValue);
			break;
			case '<': extStringBuilder.append(CharToSixBitASCIIBit.LESSTHAN.binaryValue);
			break;
			case '>': extStringBuilder.append(CharToSixBitASCIIBit.GREATTHAN.binaryValue);
			break;
			case '=': extStringBuilder.append(CharToSixBitASCIIBit.EQUALS.binaryValue);
			break;
			case '?': extStringBuilder.append(CharToSixBitASCIIBit.QSTMRK.binaryValue);
			break;
			case '[': extStringBuilder.append(CharToSixBitASCIIBit.LEFTBRACK.binaryValue);
			break;
			case ']': extStringBuilder.append(CharToSixBitASCIIBit.RIGHTBRACK.binaryValue);
			break;
			case '_': extStringBuilder.append(CharToSixBitASCIIBit.UNDERSCORE.binaryValue);
			break;
			case '!': extStringBuilder.append(CharToSixBitASCIIBit.EXCLPOINT.binaryValue);
			break;
			case '"': extStringBuilder.append(CharToSixBitASCIIBit.QUOTE.binaryValue);
			break;
			case '$': extStringBuilder.append(CharToSixBitASCIIBit.DOLLAR.binaryValue);
			break;
			case '&': extStringBuilder.append(CharToSixBitASCIIBit.COLON.binaryValue);
			break;
			case '\'': extStringBuilder.append(CharToSixBitASCIIBit.SINGLEQUOTE.binaryValue);
			break;
			case '%': extStringBuilder.append(CharToSixBitASCIIBit.PERCENT.binaryValue);
			break;
			case '(': extStringBuilder.append(CharToSixBitASCIIBit.LEFTPAREN.binaryValue);
			break;
			case ')': extStringBuilder.append(CharToSixBitASCIIBit.RIGHTPAREN.binaryValue);
			break;
			case '*': extStringBuilder.append(CharToSixBitASCIIBit.STAR.binaryValue);
			break;
			case '+': extStringBuilder.append(CharToSixBitASCIIBit.PLUS.binaryValue);
			break;
			case ',': extStringBuilder.append(CharToSixBitASCIIBit.COMMA.binaryValue);
			break;
			case '-': extStringBuilder.append(CharToSixBitASCIIBit.DASH.binaryValue);
			break;
			case '@': extStringBuilder.append(CharToSixBitASCIIBit.AT.binaryValue);
			break;
			default: 
				break;
			}


		}
		extInBin=extStringBuilder.toString();

		for (int i = 0; i <2; i++) {
			if(extInBin.length()<24) {
				extInBin+=100011;
			}
		}


		return extInBin;
	}
	/**
	 * the method used to translate from six bit ascii back to plaintext(limit of 4 characters)
	 * @param ext The extension in bits extracted from the file
	 * @return the extension in plaintext
	 */
	public static String findSixBitASCIIInTxt(String ext) {
		String currentVal="";
		String extInTxt="";
		//System.out.println(ext);
		StringBuilder aSCIIBuilder=new StringBuilder(ext.length()+1/6);
		for (int i = 0; i < ext.length()-1; i+=6) {
			
			currentVal=ext.substring(i, i+6);
			switch(currentVal) {
			case "000001": aSCIIBuilder.append("a");
			break;
			case "000010": aSCIIBuilder.append("b");
			break;
			case "000011": aSCIIBuilder.append("c");
			break;
			case "000100": aSCIIBuilder.append("d");
			break;
			case "000101": aSCIIBuilder.append("e");
			break;
			case "000110": aSCIIBuilder.append("f");
			break;
			case "000111": aSCIIBuilder.append("g");
			break;
			case "001000": aSCIIBuilder.append("h");
			break;
			case "001001": aSCIIBuilder.append("i");
			break;
			case "001010": aSCIIBuilder.append("j");
			break;
			case "001011": aSCIIBuilder.append("k");
			break;
			case "001100": aSCIIBuilder.append("l");
			break;
			case "001101": aSCIIBuilder.append("m");
			break;
			case "001110": aSCIIBuilder.append("n");
			break;
			case "001111": aSCIIBuilder.append("o");
			break;
			case "010000": aSCIIBuilder.append("p");
			break;
			case "010001": aSCIIBuilder.append("q");
			break;
			case "010010": aSCIIBuilder.append("r");
			break;
			case "010011": aSCIIBuilder.append("s");
			break;
			case "010100": aSCIIBuilder.append("t");
			break;
			case "010101": aSCIIBuilder.append("u");
			break;
			case "010110": aSCIIBuilder.append("v");
			break;
			case "010111": aSCIIBuilder.append("w");
			break;
			case "011000": aSCIIBuilder.append("x");
			break;
			case "011001": aSCIIBuilder.append("y");
			break;
			case "011010": aSCIIBuilder.append("z");
			break;
			case "111011": aSCIIBuilder.append(";");
			break;
			case "100000": aSCIIBuilder.append(" ");
			break;
			case "011110": aSCIIBuilder.append("\n");
			break;
			case "110000": aSCIIBuilder.append("0");
			break;
			case "110001": aSCIIBuilder.append("1");
			break;
			case "110010": aSCIIBuilder.append("2");
			break;
			case "110011": aSCIIBuilder.append("3");
			break;
			case "110100": aSCIIBuilder.append("4");
			break;
			case "110101": aSCIIBuilder.append("5");
			break;
			case "110110": aSCIIBuilder.append("6");
			break;
			case "110111": aSCIIBuilder.append("7");
			break;
			case "111000": aSCIIBuilder.append("8");
			break;
			case "111001": aSCIIBuilder.append("9");
			break;
			case "111010": aSCIIBuilder.append(":");
			break;
			case "111100": aSCIIBuilder.append("<");
			break;
			case "111110": aSCIIBuilder.append(">");
			break;
			case "111101": aSCIIBuilder.append("=");
			break;
			case "111111": aSCIIBuilder.append("?");
			break;
			case "011011": aSCIIBuilder.append("[");
			break;
			case "011101": aSCIIBuilder.append("]");
			break;
			case "011100": aSCIIBuilder.append("\\");
			break;
			case "011111": aSCIIBuilder.append("_");
			break;
			case "100001": aSCIIBuilder.append("!");
			break;
			case "100010": aSCIIBuilder.append("\"");
			break;
			case "100011": aSCIIBuilder.append("");
			break;
			case "100100": aSCIIBuilder.append("$");
			break;
			case "100101": aSCIIBuilder.append("%");
			break;
			case "100110": aSCIIBuilder.append("&");
			break;
			case "100111": aSCIIBuilder.append("'");
			break;
			case "101000": aSCIIBuilder.append("(");
			break;
			case "101001": aSCIIBuilder.append(")");
			break;
			case "101010": aSCIIBuilder.append("*");
			break;
			case "101011": aSCIIBuilder.append("+");
			break;
			case "101100": aSCIIBuilder.append(",");
			break;
			case "101101": aSCIIBuilder.append("-");
			break;
			case "101111": aSCIIBuilder.append("/");
			break;
			case "101110": aSCIIBuilder.append(".");
			break;
			default: 
				break;
			}


		}

		//System.out.println(ext.length());
		if(ext.length()==24) {
			extInTxt="."+aSCIIBuilder.toString();
		}else {
			extInTxt=aSCIIBuilder.toString();
		}
		return extInTxt;
	}
	/**
	 * This is the method used to write files and checks if a file exists then overwrites if the user wishes
	 * @param filename The name to be written which is passed from the method the user selected
	 * @param fileContent The content of the file which is to be written
	 * @return This returns a boolean which corresponds to successfully writing the file
	 */
	public static boolean writeFile(String filename, byte[] fileBytes) {
		File file=new File(filename);
		Scanner aScan=new Scanner(System.in);
		boolean written=false;

		if(file.exists()) {
			System.out.println("File with the name "+filename+" already exists, would you like to overwrite?(y/n)");
			String overWrite=aScan.nextLine();
			if(overWrite.equals("n")) {
				return false;
			}
		}
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
	 *Reads a file in and converts it to a binary string
	 * @param fileName the file to be converted to a binary string
	 * @return The file as a binary string
	 */
	public static String makeFileBinString(String fileName) {
		String extInString=fileExt(fileName);

		String ext=findFileExtInSixBitASCII(extInString);

		byte[] fileContent=readFile(fileName);
		//System.out.println(fileContent.length);
		StringBuilder fileBinBuild=new StringBuilder(fileContent.length*12);

		//String testRead=String.valueOf(fileContent);

		//char[] fileInBin=new char[fileContent.length*8];
		String toReturn="";

		for (int i = 0; i < fileContent.length; i++) {
			fileBinBuild.append((Integer.toBinaryString((fileContent[i] & 0xFF)+0x100).substring(1)));



		}
		toReturn=fileBinBuild.toString();
		//System.out.println(toReturn.length());
		String binLength=(Integer.toBinaryString(toReturn.length()));

		for (int i = binLength.length(); i < 33; i++) {
			if(binLength.length()<33) {
				binLength="0"+binLength;
			}
		}
		//System.out.println("Ext  "+ext);
		//System.out.println("File Size Bit  "+binLength);
		//String appendedToEnd=CharToSixBitASCIIBit.GREATTHAN.binaryValue+CharToSixBitASCIIBit.PLUS.binaryValue+CharToSixBitASCIIBit.LESSTHAN.binaryValue;
		//System.out.println("Encoding Length "+ appendedToEnd);
		toReturn=CharToSixBitASCIIBit.GREATTHAN.binaryValue+CharToSixBitASCIIBit.PLUS.binaryValue+CharToSixBitASCIIBit.LESSTHAN.binaryValue+ext+binLength+toReturn;
		return toReturn;
	}
	/**
	 * Reads a file in and converts it to a six bit binary string
	 * @param fileName the file to be converted to a six bit binary string
	 * @return The file as a binary string
	 */
	public static String makeFileSixBitASCIIString(String fileName) {
		String extInString=fileExt(fileName);
		String ext=findFileExtInSixBitASCII(extInString);
		byte[] fileContent=readFile(fileName);
		String toReturn="";
		String toConvert=new String(fileContent);
		toReturn=findFileExtInSixBitASCII(toConvert.toLowerCase());

		String binLength=(Integer.toBinaryString(toReturn.length()));

		for (int i = binLength.length(); i < 33; i++) {
			if(binLength.length()<33) {
				binLength="0"+binLength;
			}
		}
		toReturn=CharToSixBitASCIIBit.LESSTHAN.binaryValue+CharToSixBitASCIIBit.DASH.binaryValue+CharToSixBitASCIIBit.GREATTHAN.binaryValue+ext+binLength+toReturn;

		return toReturn;
	}
	/**
	 * 
	 * @param wavFileBytes
	 * @param byteRate
	 * @return
	 */
	public static String findExtFromWav(byte[] wavFileBytes, int byteRate) {
		String hidden="";
		String hiddenExt="";
		StringBuilder hiddenBuilder=new StringBuilder(wavFileBytes.length/8);
		int j=44;
		for (int i = 44; i < 500; i++) {
			if((i-(44+(byteRate/2)-1))%byteRate!=0&&(i-(44+(byteRate/2)+1))%byteRate!=0) {
				if(Math.abs((int)(wavFileBytes[i]))%2==0) {
					hiddenBuilder.append("1");
				}else if(Math.abs((int)(wavFileBytes[i]))%2==1){
					hiddenBuilder.append("0");

				}
			if(j==(44+23+18)){

				hidden=hiddenBuilder.toString();
				
				
				hiddenBuilder.setLength(0);
				hiddenExt=findSixBitASCIIInTxt(hidden);
				
				//System.out.println(hiddenExt);
				//hidden="";
			}
			j++;
		}
		
		
		}return"." +hiddenExt.substring(3);
	}
}
