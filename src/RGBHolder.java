import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class RGBHolder {
	
	String imgpath;
	int height, width;

	double[][]redPixels;
	double[][]greenPixels;
	double[][]bluePixels;
	double[][]alphaPixels;
	
	//top left coordinates
	int tlx;
	int tly;
	
	
	public RGBHolder()  {
		
		this.height=0;
		this.width=0;
		//an imageHolder can be empty
		
		//default reference system
		tlx=0;
		tly=0;
		
	}//end constructor

	public void setImage(RGBHolder ih){
		
		this.height=ih.getHeight();
		this.width=ih.getWidth();
		
		bluePixels= new double[ih.getHeight()][ih.getWidth()];
		greenPixels= new double[ih.getHeight()][ih.getWidth()];
		redPixels= new double[ih.getHeight()][ih.getWidth()];
		alphaPixels	= new double[ih.getHeight()][ih.getWidth()];
		
		//get Image matrix
		for (int h=0; h<ih.getHeight();h++){
			for (int w=0; w<ih.getWidth();w++){
				redPixels[h][w]=ih.getRedMatrix()[h][w];
				greenPixels[h][w]=ih.getGreenMatrix()[h][w];
				bluePixels[h][w]=ih.getBlueMatrix()[h][w];
				alphaPixels[h][w]=ih.getAlphaMatrix()[h][w];
			}//end height			
		}// end width
		
	}//end setImage
	
	public void setBufferedImage(BufferedImage image){
		
		height=image.getHeight();
		width=image.getWidth();
		
		bluePixels= new double[height][width];
		greenPixels= new double[height][width];
		redPixels= new double[height][width];
		alphaPixels	= new double[height][width];
		
		//get Image matrix
		for (int h=0; h<height;h++){
			for (int w=0; w<width;w++){
				redPixels[h][w]=this.getPixelColour(image,w, h).getRed();
				greenPixels[h][w]=this.getPixelColour(image,w, h).getGreen();
				bluePixels[h][w]=this.getPixelColour(image,w, h).getBlue();
				alphaPixels[h][w]=this.getPixelColour(image,w, h).getAlpha();			
			}//end height			
		}// end width
		
	}//end setbufferedimage
	
	public void setImageFromFile(String imgpath) throws IOException{
		
		File myImg = new File(imgpath);
		BufferedImage image = ImageIO.read(myImg);
		
		this.setBufferedImage(image);
			
	} //end getImageFrom
	
	public void setImgFromLinearArray(double[] array ){
		
		//linear format is R1,G1,B1, R2,G2,B2, R3..... 
		
		bluePixels= new double[height][width];
		greenPixels= new double[height][width];
		redPixels= new double[height][width];
		alphaPixels	= new double[height][width];
		
		int count =0;
		
		for (int h=0; h<this.getHeight();h++){
			for (int w=0; w<this.getWidth();w++){
				redPixels[h][w]=(int)array [count];
				greenPixels[h][w]=(int)array[count+1];
				bluePixels[h][w]=(int)array[count+2];
				alphaPixels[h][w]=255;
				count=count+3;
			}//end width
		}//end height
		
	}
	
	public double[] getlinearArray(){

		int pixels=this.height * this.width;
		double [] lineararray = new double [pixels*3];
				
		int count=0;
	
		//linearize the matrix [ R1,G1,B1, R2,G2,B2, R3..... 
		for (int h=0; h<this.height;h++){
			for (int w=0; w<this.width;w++){			
				lineararray[count]=redPixels[h][w];
				lineararray[count+1]=greenPixels[h][w];
				lineararray[count+2]=bluePixels[h][w];
				count=count+3;
			}//end width
		}//end height
		
		return lineararray;

	}//end getlinearArray
	
	public double[] getAVGvalue() {
		
		double[] rgbAVG = new double[3];	
		double sumR,sumG, sumB;
		
		sumR=0;
		sumG=0;
		sumB=0;
		
		double n = this.height*this.width;
		
		for (int h=0; h<this.height;h++){
			for (int w=0; w<this.width;w++){			
				sumR=sumR+redPixels[h][w];
				sumG=sumG+greenPixels[h][w];
				sumB=sumB+bluePixels[h][w];			
			}//end width
		}//end height 
		
	
		rgbAVG[0]=(sumR)/n;
		rgbAVG[1]=(sumG)/n;
		rgbAVG[2]=(sumB)/n;
		
		return rgbAVG;
		
	}
	

	private Color getPixelColour(BufferedImage image, int x, int y){
		
		Color colour = new Color(image.getRGB(x, y));
		return colour;
		
	}//end getpixelcolour
	
	public void printImage(String filepath) throws IOException{
		
		System.out.println("Printing image file....");
		
		//prepare the buffered output image
		BufferedImage imgBuf = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);;
		
		for (int h=0; h<height;h++){
			for (int w=0; w<width;w++){
				int rgb = (((int)alphaPixels[h][w]<<24) | ((int)redPixels[h][w]) << 16 | ((int)greenPixels[h][w]) << 8 | ((int)bluePixels[h][w]));      
				imgBuf.setRGB(w, h, rgb);            
           }//end h
        }//end w
		
        File file = new File(filepath);
        file.getParentFile().mkdirs();
        ImageIO.write(imgBuf, "png", file);

	}//end write image

	public BufferedImage getBufferedImage() throws IOException{
		
		System.out.println("Printing image file....");
		
		//prepare the buffered output image
		BufferedImage imgBuf = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);;
		
		for (int h=0; h<height;h++){
			for (int w=0; w<width;w++){
				int rgb = (((int)alphaPixels[h][w]<<24) | ((int)redPixels[h][w]) << 16 | ((int)greenPixels[h][w]) << 8 | ((int)bluePixels[h][w]));      
				imgBuf.setRGB(w, h, rgb);            
           }//end h
        }//end w
		
       return imgBuf;

	}//end write image
	
	// get and set
	public void setRedMatrix(double [][] newMatrix){
		
		redPixels =new double[newMatrix.length][newMatrix[0].length];
		redPixels=this.copyMatrix(newMatrix);
		if(newMatrix.length>height) height=newMatrix.length;
		if(newMatrix[0].length>width) width=newMatrix[0].length;
	}
	
	public void setGreenMatrix(double [][] matrix){ 
		greenPixels =new double[matrix.length][matrix[0].length];
		greenPixels=this.copyMatrix(matrix);
		if(matrix.length>height) height=matrix.length;
		if(matrix[0].length>width) width=matrix[0].length;
	}
	
	public void setBlueMatrix(double [][] matrix){ 
		bluePixels =new double[matrix.length][matrix[0].length];
		bluePixels=this.copyMatrix(matrix);
		if(matrix.length>height) height=matrix.length;
		if(matrix[0].length>width) width=matrix[0].length;
	}
	
	public void setAlphaMatrix(double [][] matrix){ 
		alphaPixels =new double[matrix.length][matrix[0].length];
		alphaPixels=this.copyMatrix(matrix);
		if(matrix.length>height) height=matrix.length;
		if(matrix[0].length>width) width=matrix[0].length;
	}
	
	public void setAlphaMax(){
		alphaPixels =new double[this.height][this.width];
		for(int h=0; h<this.height; h++){
			for(int w=0; w<this.width;w++){
				alphaPixels[h][w]=255;
			}//for h
		}//for w
		
	}//end setAlphaMax
	
	
	public double[][] getRedMatrix(){ return redPixels;}
	public double[][] getGreenMatrix(){ return greenPixels;}
	public double[][] getBlueMatrix(){ return bluePixels;}
	public double[][] getAlphaMatrix(){ return alphaPixels;}
	
	public int getHeight(){ return height;}
	public int getWidth(){ return width;}
	
	public void setHeight(int h){ this.height=h;}
	public void setWidth(int w){ this.width=w;}
	
	public int getTlx(){ return this.tlx;}
	public int getTly(){ return this.tly;}
	
	public void setTlx( int x){ this.tlx=x;}
	public void setTly(int y ){ this.tly=y;}
	
	
	//additional tools
	private double[][] copyMatrix(double[][] matrix){
		
		double[][] output=new double[matrix.length][matrix[0].length];
		
		for(int i=0; i<matrix.length;i++){
			for(int j=0; j<matrix[0].length; j++) output[i][j]=matrix[i][j];
		}
		
		return output;
	}//end copyMatrix
		
	public double getInfo(){
		
		double sigmar, sigmag, sigmab;
		sigmar=this.stdDevMatrix(redPixels);
		sigmag=this.stdDevMatrix(greenPixels);
		sigmab=this.stdDevMatrix(bluePixels);
		
		return sigmar+sigmag+sigmab;
	
	}//end getinfo
	
	private double averageMatrix(double [][] matrix){
		
		double sum=0;
		
		for(int j=0; j<matrix.length;j++){
			for(int i=0;i<matrix[0].length;i++){
				
				sum=sum+matrix[j][i];
						
			}//i
		}//j
		
		int n=matrix.length*matrix[0].length;
		return sum/n;
		
	}//end average
	
	private double stdDevMatrix(double [][] matrix){
		
		double avg= this.averageMatrix(matrix);
		double sum=0;
		
		for(int j=0; j<matrix.length;j++){
			for(int i=0;i<matrix[0].length;i++){
				
				sum=sum + Math.pow(matrix[j][i]-avg, 2);
						
			}//i
		}//j
		
		double n = matrix[0].length*matrix.length;
		
		return Math.sqrt(sum/n);
		
	
	}//end standard deviation
		
}//end class
	
	
	


