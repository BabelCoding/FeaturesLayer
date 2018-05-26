import java.io.IOException;




public class Main {

	public static void main(String[] args) throws IOException {
		
		final String dirpath= "C:\\Desktop\\car\\";

		RGBHolder img=new RGBHolder();
		
		System.out.println("Reading:"+dirpath+"car.jpg");
		img.setImageFromFile(dirpath+"car.jpg");
		
		if(img.getHeight()!=0){
			
			//example 0: extract top10 features with format 50x50
			IconoclasticLayer ic0=new IconoclasticLayer(img);
			ic0.setResolution(10);
			System.out.println("Extracting 10 features... ");	
			ic0.getNfeatures(10, 50, 50,dirpath+"features\\");
			
			
			// example 1: standardise each section then extract features
			IconoclasticLayer ic1=new IconoclasticLayer(img);
			ic1.setResolution(10);
			System.out.println("Calculating standard values... ");	
			ic1.localNormalisation();
			System.out.println("Extracting 15 features... ");	
			ic1.getNfeatures(15, 50, 50,dirpath+"STDfeatures\\");
			
			
			// example 2: get AVG values in a linear RGB array
			IconoclasticLayer ic2=new IconoclasticLayer(img);		
			ic2.setResolution(10);
			double [] rgbarray = ic2.getAVGValues();
			
			// example 3: invert colours and print img
			System.out.println("Printing inverted image... ");	
			img.invert();
			img.printOnFile(dirpath+"inverted.jpg");
			
		}//end if
		
		System.out.println("end test");
		

	}//end main


}//end class
