import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class VisualLayer {

	RGBHolder image;
	public RGBHolder[][] sections; //[columns], [rows]  c and r are the coordinates in the picture
	RGBHolder optimalSections[];
	double [][] I; //quantity of information for each section
	
	
	public VisualLayer(RGBHolder image)  {
		
		this.image=image;
			
	}//end constructor
	
	public void setResolution(int n){
		
		//n matrices per side
		
		
		//height and width of sub matrixes
		int hs,ws;
				
		hs= (int) Math.floor(image.getHeight()/n);
		ws= (int) Math.floor(image.getWidth()/n);
		
		
		sections = new RGBHolder [(int) n][(int) n];

		//for each  section  c=columns r=rows
		for (int r=0; r<n; r++){
			for (int c=0; c<n; c++){
				
				double [][] tempR = new double[hs][ws];
				double [][] tempG = new double[hs][ws];
				double [][] tempB = new double[hs][ws];
				double [][] tempA = new double[hs][ws];
				
				for (int h=0; h<hs; h++){
					for (int w=0; w<ws; w++){
						
						int offsetH = (r)*hs;
						int offsetW = (c)*ws;		
						
						tempR[h][w]= image.getRedMatrix()[offsetH+h][offsetW+w];
						tempG[h][w]= image.getGreenMatrix()[offsetH+h][offsetW+w];
						tempB[h][w]= image.getBlueMatrix()[offsetH+h][offsetW+w];
						tempA[h][w]= image.getAlphaMatrix()[offsetH+h][offsetW+w];
	
					}	//end height
				}//end width
				
				sections[r][c]=new RGBHolder();		
				sections[r][c].setRedMatrix(tempR);
				sections[r][c].setGreenMatrix(tempG);
				sections[r][c].setBlueMatrix(tempB);
				sections[r][c].setAlphaMatrix(tempA);
				
				sections[r][c].setTlx((c)*ws-1); //-1 because the matrix is indexed from 0
				sections[r][c].setTly((r)*hs-1);
				
				
				tempR=null;
				tempG=null;
				tempB=null;
				tempA=null;
					
			}//end rows
		}//end columns
	

	}//end setResolution
	
	public double[] getInputLayer() {
		
		//the size equals one input for each section * number of sections * 3 colors
		int size = (int) Math.pow(sections.length, 2) *3; 
		double[] inputLayer = new double[size];
		
		System.out.println("InputLayer Size: "+ size + "n  >>  ...average of "+Math.pow(sections.length, 2)+ " sections * 3 color components ");
		
		int i = 0;
		
		//for each section
		for (int r=0; r<sections.length; r++){
			for (int c=0; c<sections[0].length; c++){
				
				double [] avgRGB=sections[r][c].getAVGvalue();
				
				inputLayer[i]=avgRGB[0];
				inputLayer[i+1]=avgRGB[1];
				inputLayer[i+2]=avgRGB[2];
				
				avgRGB=null;
				i=i+3;
				
			}//end columns
		}//end rows
		
		return inputLayer;
		
	}//end getinputlayer
	
	//get features
	
	public void getTopNfeatures( int N, int width, int height,String optionalPrintPath) throws IOException{
		
		this.finMaxInformation(N);
		
		for(int i=0; i<optimalSections.length; i++) {
			
			BufferedImage img = optimalSections[i].getBufferedImage();	
			Image newImage = img.getScaledInstance(width, height, Image.SCALE_DEFAULT);	 

		    // Create a buffered image with transparency
		    BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		    // Draw the image on to the buffered image
		    Graphics2D bGr = bimage.createGraphics();
		    bGr.drawImage(newImage, 0, 0, null);
		    bGr.dispose();
		    
		    //put the resized image back into the array
		    optimalSections[i].setBufferedImage(bimage);		    
		    
		    // print the  image
		    if(optionalPrintPath!=null) optimalSections[i].printImage(optionalPrintPath+"res"+i+".jpeg");
		  
		} //end for
		
		
		
	} //end getTop Features
	
	private void finMaxInformation(int fragments) throws IOException {
		
		//find top 3 coordinates containing max informaion
		this.calculateI();
		
		double maxvalues[] = new double [3]; // [max value,  r , c ]
		double[][] top = new double[fragments][3]; // top 3 maxvalues
		int[][] exclusionList; //set of coordinates
		
		exclusionList=null;
		System.out.println("Top 3:");

		
		for(int i=0; i<fragments; i++) {
	
				maxvalues = this.findMax(I, exclusionList);
				top[i][0]=maxvalues[0];
				top[i][1]=maxvalues[1];
				top[i][2]=maxvalues[2];
				System.out.println("First: ("+ top[i][1]+"," +top[i][2] +")");
				if(i==0)	exclusionList= new int [fragments][2];
				exclusionList[i][0]=(int) top[i][1]; // row coordinate
				exclusionList[i][1]=(int) top[i][2];  //column coordinate
				
		} //end for top3
		
		System.out.println("Optimising  sections...");
		
		optimalSections = new RGBHolder [fragments];
		for(int i=0; i<fragments; i++) {
			optimalSections[i]= new RGBHolder();
			optimalSections[i].setImage(this.optimiseSection(sections[(int) top[i][1]][(int) top[i][2]],0));
				
		} //optimisation	
	
	}//end calculate information
	
	private void calculateI() {
		
		I= new double [sections.length][sections[0].length];
		
		//for each section
		for (int r=0; r<sections.length; r++){
			for (int c=0; c<sections[0].length; c++){
					
				I[r][c]=sections[r][c].getInfo();
					
			}//for columns
		}//for rows
	
		System.out.println("");
	}
				
	//Information Optimiser
	
	private RGBHolder optimiseSection(RGBHolder section, int direction) {
		
		
		double info,infoL, infoS;
		double gradL, gradS;

		RGBHolder larger;
		RGBHolder smaller;
		
		larger=null;
		smaller=null;
		
		//speed of change
		int delta;
		delta =(int) Math.round(0.05*section.getHeight());
		
		if(delta%2!=0) delta=delta-1; //if odd
		
		gradL=0;
		gradS=0;
		//calculate gradient in each direction (if already moving only one direction is necessary)
		
		info=section.getInfo();
		if(direction>=0){
			larger=this.resizeSection(section, delta);
			infoL=larger.getInfo();
			gradL=infoL-info;
		}
		if(direction<=0){
			smaller=this.resizeSection(section, (-1)*delta);
			infoS=smaller.getInfo();
			gradS=infoS-info;
		}
		
		
		if(gradL>=gradS || direction>0){
			//increase the section
			if(gradL>0){
				System.out.println("increasing..");
				return this.optimiseSection(larger, 1);
				
			}else{
				//optimal (largest gradient is negative)
				return section;
			}
		}else {
			//shrink the section
			if(gradS>0){
				System.out.println("shrinking..");
				return this.optimiseSection(smaller, -1);
			}else{
				//optimal (largest gradient is negative)
				return section;
			}

		}

		
	}//end optimise section
	
	private RGBHolder resizeSection(RGBHolder section, int delta){
		
		RGBHolder resSection = new RGBHolder();

		double [][] tempR = new double[section.getHeight()+delta][section.getWidth()+delta];
		double [][] tempG = new double[section.getHeight()+delta][section.getWidth()+delta];
		double [][] tempB = new double[section.getHeight()+delta][section.getWidth()+delta];
		double [][] tempA = new double[section.getHeight()+delta][section.getWidth()+delta];
		
		//calculate new coordinates
		int offsetH = section.getTly()- (int) (delta/2);
		int offsetW = section.getTlx()-(int) (delta/2);	
		
		
		//check for negative coordinates first
		if (offsetH<0) 	offsetH=0;
		if (offsetW<0)	offsetW=0;
		
		//check if resized picture goes out of margins 
		double cornerB, cornerC;
		cornerB=offsetW+section.getWidth()+delta;
		cornerC=offsetH+section.getHeight()+delta;
		
		int dmezzi= (int)(delta/2);
		if (image.getWidth()-cornerB<0) 	offsetW=offsetW-(int)(delta/2); //remove other d/2, expand in one direction only
		if (image.getHeight()-cornerC<0)	offsetH=offsetH-(int)(delta/2);
		
		
		for (int h=0; h<section.getHeight()+delta; h++){
			for (int w=0; w<section.getWidth()+delta; w++){
								
				tempR[h][w]= image.getRedMatrix()[offsetH+h][offsetW+w];
				tempG[h][w]= image.getGreenMatrix()[offsetH+h][offsetW+w];
				tempB[h][w]= image.getBlueMatrix()[offsetH+h][offsetW+w];
				tempA[h][w]= image.getAlphaMatrix()[offsetH+h][offsetW+w];

			}	//end height
		}//end width
		
		resSection.setRedMatrix(tempR);
		resSection.setGreenMatrix(tempG);
		resSection.setBlueMatrix(tempB);
		resSection.setAlphaMax();
		
		resSection.setTly(offsetH);
		resSection.setTlx(offsetW);
		
		tempR=null;
		tempG=null;
		tempB=null;
		tempA=null;
		
		return resSection;
		
	}
	
	public void debugprint(String path) throws IOException{
		
	
		
		
	}
	
	//normalization
	
	public void localNormalisation() {
		
		//standardized values in each section

		for (int r=0; r<sections.length; r++){
			for (int c=0; c<sections[0].length; c++){			
				sections[r][c]=this.standardizeRGB(sections[r][c]);		
			}//end col
		}//end rows
		
		this.stitchAndUpdate();
		
		System.out.println("Normalised!");
		
	}//end localstd
	
	private RGBHolder standardizeRGB(RGBHolder img){
		
		//standard normalisation for each RGB component
		
		RGBHolder output = new RGBHolder();
		output.setRedMatrix(this.standardiseMatrix(img.getRedMatrix()));
		output.setGreenMatrix(this.standardiseMatrix(img.getGreenMatrix()));
		output.setBlueMatrix(this.standardiseMatrix(img.getBlueMatrix()));
		output.setAlphaMatrix(img.getAlphaMatrix());
		return output;
		
	}//end standardizeRGB	

	private double[][] standardiseMatrix(double [][] matrix){
		
		//calculate z variable
		double avg = this.averageMatrix(matrix);
		double stddev = this.stdDevMatrix(matrix);
		
		double [] [] zmatrix = new double [matrix.length][matrix[0].length];
		
		for(int j=0; j<matrix.length;j++){
			for(int i=0;i<matrix[0].length;i++){			
				zmatrix[j][i]= (matrix[j][i]-avg)/stddev;	
			}//i
		}//j
		

		return zmatrix;
		
	}//end standardize

	private void stitchAndUpdate() {
		
		//stitches sections together and updates the original image
		
		int n =this.sections.length; // matrices per side (it's a square)
		
		int subh, subw, offsetW, offsetH;
		subh= sections[0][0].getHeight();
		subw = sections[0][0].getWidth();
		
		//final image components
		double [][] redPixels = new double [subh*n][subw*n];
		double [][] greenPixels = new double [subh*n][subw*n];
		double [][] bluePixels = new double [subh*n][subw*n];
		
		offsetH=0;
		offsetW=0;
	
		
		// stitching all sections together
		for (int r=0; r<n; r++){
			for (int c=0; c<n; c++){
				for (int h=0; h<subh; h++){
					for (int w=0; w<subw; w++){
						
						double [][] RMatrix=sections[r][c].getRedMatrix();
						double [][] GMatrix=sections[r][c].getGreenMatrix();
						double [][] BMatrix=sections[r][c].getBlueMatrix();
						
						//place section in the final image
						redPixels[h+offsetH][w+offsetW]= RMatrix [h][w];
						greenPixels[h+offsetH][w+offsetW]= GMatrix [h][w];
						bluePixels[h+offsetH][w+offsetW]= BMatrix [h][w];
						
						
						offsetH=subh*r;
						offsetW=subw*c;
				
					}//width
				}//height 
			}//columns
		}//rows
		
		//save a
		
		
		//update image
		
		
		//save alpha component 
		double [][] alphabkp = this.image.getAlphaMatrix();
		
		//update image
		this.image=null;
		this.image  = new RGBHolder();
		
		this.image .setRedMatrix(redPixels);
		this.image .setGreenMatrix(greenPixels);
		this.image .setBlueMatrix(bluePixels);
		this.image .setAlphaMatrix(alphabkp);
		
		redPixels=null;
		greenPixels=null;
		bluePixels=null;
		
		
	}

	// Matrix functions
	
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
	
	private double[] findMax(double [][] matrix, int excludedElements[][]){
		
		//returns [maxValue,r,c] ... row/column  where the max value was found in the original matrix
		// excluded elements is a list of pairs of coordinates that 
		double [] max = new double [3];
		
		//initialise
		max[0]=0;
		max[1]=-1;
		max[2]=-1;
		
		
		//for each element in the matrix
		for(int r=0; r<matrix.length;r++){
			for(int c=0;c<matrix[0].length;c++){			
				
				//found new max
				if(matrix[r][c]>max[0]) {
					
					boolean excluded=false;
					
					//check if any exclusion is defined
					if(excludedElements!=null) {
						
						//check if coordinates are excluded
						for(int i=0; i< excludedElements.length; i++) {
							if( excludedElements[i][0]==r && excludedElements[i][1]==c ) excluded=true; 	
						}
					}// end if exclusion
					
					if(!excluded) {
						max[0]=matrix[r][c];
						max[1]=r;
						max[2]=c;
					}//end if
					
				}//if found new max
			}//for columns
		}//for rows
		
		return max;
	}//find max end
	
	private double findMin(double [][] matrix){
		//used in resizing to 0-255
		
		double min=matrix[0][0];
		
		//calculate z variable
		for(int j=0; j<matrix.length;j++){
			for(int i=0;i<matrix[0].length;i++){			
				if(matrix[j][i]<min) min=matrix[j][i];
			}//i
		}//j
		
		return min;
	}//find max end
	
	
	//debugging: used to display the standardized image (resizes to 0-255 and stitches sections together)

	public RGBHolder getVisualSTDImage() {
		
		System.out.println("DEBUG: Preparing visual STD image....");

		
		int n =this.sections.length; // matrices per side (it's a square)
		
		int subh, subw, offsetW, offsetH;
		subh= sections[0][0].getHeight();
		subw = sections[0][0].getWidth();
		
		//final image
		double [][] redPixels = new double [subh*n][subw*n];
		double [][] greenPixels = new double [subh*n][subw*n];
		double [][] bluePixels = new double [subh*n][subw*n];
		
		offsetH=0;
		offsetW=0;
	

		
		// stitching all sections together
		for (int r=0; r<n; r++){
			for (int c=0; c<n; c++){
				
				//resize standardized  matrices to 0-255
				double [][] RMatrix=this.resizeToRGB(sections[r][c].getRedMatrix());
				double [][] GMatrix=this.resizeToRGB(sections[r][c].getGreenMatrix());
				double [][] BMatrix=this.resizeToRGB(sections[r][c].getBlueMatrix());
				
				for (int h=0; h<subh; h++){
					for (int w=0; w<subw; w++){
						
						
						//place section in the final image
						redPixels[h+offsetH][w+offsetW]= RMatrix [h][w];
						greenPixels[h+offsetH][w+offsetW]= GMatrix [h][w];
						bluePixels[h+offsetH][w+offsetW]= BMatrix [h][w];
						
						
						offsetH=subh*r;
						offsetW=subw*c;
				
					}//width
				}//height 
			}//columns
		}//rows
		
		RGBHolder imgout = new RGBHolder();
		
		imgout.setRedMatrix(redPixels);
		imgout.setGreenMatrix(greenPixels);
		imgout.setBlueMatrix(bluePixels);
		imgout.setAlphaMatrix(image.getAlphaMatrix());
		
		
		redPixels=null;
		greenPixels=null;
		bluePixels=null;
		
		return imgout;
	
	}
	
	private double [][] resizeToRGB(double[][] zmatrix){
		
			
		//resize z variable to 0-255
		double max=this.findMax(zmatrix,null)[0];
		double min=this.findMin(zmatrix);
		

		double[][] RGBmatrix = new double [zmatrix.length][zmatrix[0].length];
		
		for(int j=0; j<RGBmatrix.length;j++){
			for(int i=0;i<RGBmatrix[0].length;i++){			
				RGBmatrix[j][i]=(int) ((Math.abs(zmatrix[j][i])/(max-min))*255);
			}//i
		}//j
		
		return RGBmatrix;
		
	}// end resizeToRGB
	
	public RGBHolder getsubMatrix(int c, int r) {
		
		return sections[c][r];

	}
	
	
}//end class
