package ProbablisticCounting;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import java.util.zip.CRC32;

public class PC {
	
	final static int MAX_BITMAP_SIZE = 512;
	String inputFile = "C:\\sem 1\\ITM\\Project\\FLowTraffic.txt";
	//String outputFile = "C:\\sem 1\\ITM\\Project\\Results.txt";
	Double fractionOfZeroes= 0.0;
	String line = "";
	int[] b = new int[MAX_BITMAP_SIZE];
	String sourceIP = "";
	String destIP = "";
	Map<String, HashSet<String>> flowList = new HashMap<String, HashSet<String>>();
	int[] actualCardinality = new int [1000];
	int[] estimatedCardinality = new int [1000];
	int hashIndex = 0;
		
	
	PC(){
		
		try{
			
				/* Initialize bitmap to zero*/			
				initialize_bitmap();
				
				buildFlowArrayList();
				/*Build bitmap with incoming source and destination IPs */
				buildBitMap();
				
		}
		catch(Exception e){
				System.out.println("Exception occured : "+e);
				e.printStackTrace();
		}
	}
	
	public void buildBitMap() {
				
		int flowNum = 0;
		
				for (String flow: flowList.keySet()){
					
					System.out.println(flow + " " + flowList.get(flow));
					System.out.println("total flows" + flow.length());
					
					if(flowNum < 1000){
					actualCardinality[flowNum] = flowList.get(flow).size();
					}
					else{
						break;
					}
					System.out.println("n = "+ actualCardinality[flowNum]);
					
					for (String eachFlow : flowList.get(flow)) {
				       
				//	hashIndex = ((eachFlow).hashCode() & 0x7fffffff) % MAX_BITMAP_SIZE;
				//	b[hashIndex] = 1;
					
				/*	CRC32 crc = new CRC32();
					long m_crc32;
					crc.update(eachFlow.getBytes());
					m_crc32 = (int)(crc.getValue() % MAX_BITMAP_SIZE); */
					
					try{
					int m_crc32;
					//crc.update(eachFlow.getBytes());
					m_crc32 = (int) ((getUUID(flow+eachFlow) & 0x7fffffff) % 512);
					b[(int) m_crc32] = 1;
					System.out.println(eachFlow + " " + (int) m_crc32); 
					}
					catch(Exception e){
						
					}
					
					}	
					
					fractionOfZeroes =  countZeroes()/ (double) MAX_BITMAP_SIZE;
					estimatedCardinality[flowNum] = (int)(-1 * MAX_BITMAP_SIZE * Math.log(fractionOfZeroes));
					initialize_bitmap();
					
					System.out.println("ncap = "+ estimatedCardinality[flowNum]);
					
					flowNum++;
				}
	}		     
		   	     		    
	static int getUUID(String name) throws NoSuchAlgorithmException {
	    SecureRandom srA = SecureRandom.getInstance("SHA1PRNG");
	    srA.setSeed(name.getBytes());
	    return new Integer(srA.nextInt());
	}
	
	/*** ONLINE OPERATION ***/
	public void buildFlowArrayList() throws IOException{
		
		
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		
		while((line = br.readLine()) != null) {
		 
			/* Split each line of the file on " "	*/
			String[] columns = line.split(" ");
		 
			/* Assign first column of the file to source IP */
			sourceIP = columns[0];
		 
			/* Assign Destination IP */
			for(int k=1; k<columns.length;k++){				 
				if(!(columns[k].equalsIgnoreCase(""))){
					destIP = columns[k];
					break;
				}
			}
			
			if(!flowList.containsKey(sourceIP)){
				HashSet<String> newFlow = new HashSet<String>();
				newFlow.add(destIP);
				flowList.put(sourceIP, newFlow);
			}
			
			else{
				
				if(flowList.get(sourceIP).contains(destIP)){
					continue;
				}
				
				else{
					flowList.get(sourceIP).add(destIP);
				}
			}
		
	}
		
		br.close();
	}
	
	public void initialize_bitmap(){
		
		for(int i=0; i<512; i++){
			b[i] = 0; // Initialize the bitmap with 0			
		}
	}
	
	public int flowListSize(ArrayList<List<String>> flowList){
		
		int size = 0;
		
		/* Calculate size of the flow list at any point*/
		for(int i = 0; i<flowList.size(); i++){
			for(int j = 1; j<flowList.get(i).size(); j++){
				size++;
			}
		}
		
		return size;
	}
	
	/*** OFFLINE OPERATION ***/
	public int countZeroes(){
		
		int noOfZeroes = 0;
		
		/* Calculate number of zeroes in the bitmap*/
		for(int i=0 ; i<MAX_BITMAP_SIZE ; i++){
			 if (b[i] == 0){
				 noOfZeroes++;
			 }
		}
		
		return noOfZeroes;
		//fractionOfZeroes = noOfZeroes/(double) MAX_BITMAP_SIZE;
		//estimatedCardinality[flowNum] = (int)(-1 * MAX_BITMAP_SIZE * Math.log(fractionOfZeroes));
	}
	
	/* Create Line Graph*/
	public XYDataset createData() {
		int count1=0;
	    XYSeriesCollection result = new XYSeriesCollection();
	    XYSeries series = new XYSeries(count1);
	      
	    System.out.println("FLowlist Size = "+flowList.size() + " actual cardinality  : " + actualCardinality.length + " n cap : "+ estimatedCardinality.length);
	    
	    /* Calculate estimated and actual cardinality for each flow */
	   	for (int flowNo =0; flowNo < 1000 ; flowNo++) {
		    	    int x = actualCardinality[flowNo];
			        int y = estimatedCardinality[flowNo];
			        series.add(x, y);
		}
	    result.addSeries(series);
	 	return result;
	}
	
	public XYDataset createData1() {
		int count1=0;
	    XYSeriesCollection result = new XYSeriesCollection();
	    XYSeries series = new XYSeries(count1);
	      
	    for (int flowNo =0; flowNo < 1000 ; flowNo++) {
	    	double x = flowNo;
	        int y = Math.abs(estimatedCardinality[flowNo] - actualCardinality[flowNo]);
	        series.add(x, y);
}
	    
	 /*   for (int flowNo =0; flowNo < gd.NO_OF_FLOWS; flowNo++) {
		    	    double x = flowNo;
			        double y = Math.abs(gd.F[flowNo].cardinality - gd.F[flowNo].numberOfElements);
			        series.add(x, y);
		}*/
	    result.addSeries(series);
	 	return result;
	} 
	
	/*public XYDataset createData1() {
		int count1=0;
	    XYSeriesCollection result = new XYSeriesCollection();
	    XYSeries series = new XYSeries(count1);
	    
	    for (int flowNo =0; flowNo < flowList.size() ; flowNo++) {
    	 
	        int x = actualCardinality[flowNo];
	        double t = actualCardinality[flowNo]/MAX_BITMAP_SIZE;
	        double y = (Math.pow(2.71828,t)-(t)-1)/(2*x);
	        series.add(x, y);
	    }
	      
	   	
	 	return result;
	}*/
	
	
	public static void main (String[] x){
			
			PC pc = new PC();
				
			/* Create Chart*/
			JFreeChart chart = ChartFactory.createXYLineChart(
		            "Probabilistic Counting", // chart title
		            "Actual Cardinality", // x axis label
		            "Estimated Cardinality", // y axis label
		            pc.createData(), //  
		            PlotOrientation.VERTICAL,
		            true, // include legend
		            true, // tooltips
		            false // urls
		            );
		 
			/* Plot Graph */
			final XYPlot plot = chart.getXYPlot( );
		    XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
		    renderer.setSeriesPaint(0, Color.RED);
		    renderer.setSeriesStroke(0, new BasicStroke( 2.0f ) );
		    plot.setRenderer( renderer );
			
		    
			/* Create and display a frame */
			ChartFrame frame = new ChartFrame("PC-1", chart);
			frame.pack();
			frame.setVisible(true);
			
			
			/*JFreeChart chart2 = ChartFactory.createXYLineChart(
		            "Probabilistic Counting", // chart title
		            "actual cardinality", // x axis label
		            "load factor", // y axis label
		            pc.createData1(), //  
		            PlotOrientation.VERTICAL,
		            true, // include legend
		            true, // tooltips
		            false // urls
		            );
		  //ChartPanel chartPanel = new ChartPanel( chart );
	      //chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
			final XYPlot plot1 = chart.getXYPlot( );
		      XYLineAndShapeRenderer renderer1 = new XYLineAndShapeRenderer( );
		      renderer1.setSeriesPaint(0, Color.YELLOW);
		      renderer1.setSeriesStroke(0, new BasicStroke( 2.0f ) );
		      plot1.setRenderer( renderer1 );
		      
			// create and display a frame...
			ChartFrame frame1 = new ChartFrame("PC-2", chart2);
			frame1.pack();
			frame1.setVisible(true); 
			
			final XYZPlot plot2 = chart.getXYZPlot( );*/
		}
	}

	

