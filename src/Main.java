import java.io.IOException;




public class Main {

	public static void main(String[] args) throws IOException {
		
	
		final int size = 160*120;
		final String dirpath= "E:\\Desktop\\car\\";

		RGBHolder img=new RGBHolder();
		img.setImageFromFile(dirpath+"images.jpg");
		
		VisualLayer vl2=new VisualLayer(img);
		
		System.out.println("test begins: ");

		vl2.setResolution(10);
		
		vl2.getTopNfeatures(20, 160, 120,dirpath+"temp\\");

		

	}//end main


}//end class
