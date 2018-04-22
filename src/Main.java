import java.io.IOException;




public class Main {

	public static void main(String[] args) throws IOException {
		
		final String dirpath= "E:\\Desktop\\car2\\";

		RGBHolder img=new RGBHolder();
		
		System.out.println("Reading:"+dirpath+"car.jpg");
		img.setImageFromFile(dirpath+"car.jpg");
		
		if(img.getHeight()!=0){
			
			System.out.println("Calculating standard values... ");	
			VisualLayer vl1=new VisualLayer(img);
			vl1.setResolution(10);
			vl1.localNormalisation();
			RGBHolder std=vl1.visualizer();
			std.printOnFile(dirpath+"standard.jpg");
			
			System.out.println("Extracting 15 features... ");	
			VisualLayer vl2=new VisualLayer(img);		
			vl2.setResolution(10);
			vl2.getTopNfeatures(15, 50, 50,dirpath+"features\\");
			
			System.out.println("Printing inverted image... ");	
			img.invert();
			img.printOnFile(dirpath+"inverted.jpg");
			
		}//end if
		
		System.out.println("end test");
		

	}//end main


}//end class
