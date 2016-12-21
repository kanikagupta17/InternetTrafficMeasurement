package ProbablisticCounting;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.util.ShapeUtilities;

public class ScatterPlot extends ApplicationFrame 
{
   public ScatterPlot( String applicationTitle, String chartTitle, HashMap result )
   {
      super(applicationTitle);
            
      JPanel jpanel = createDemoPanel(chartTitle,result);
      jpanel.setPreferredSize(new Dimension(640, 480));
      setContentPane(jpanel);
  }

  public JPanel createDemoPanel(String chartTitle, HashMap result ) {

      JFreeChart jfreechart = ChartFactory.createScatterPlot(chartTitle ,
    	         "Actual Cardinality" ,
    	         "Estimated Cardinality" , createDataset(chartTitle,result), PlotOrientation.VERTICAL, true, true, false);
      Shape diamond = ShapeUtilities.createDiamond(3);

      XYPlot xyPlot = (XYPlot) jfreechart.getPlot();
      xyPlot.setDomainCrosshairVisible(true);
      xyPlot.setRangeCrosshairVisible(true);
      
      XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)xyPlot.getRenderer();
      renderer.setSeriesShape(1, diamond );
      renderer.setSeriesPaint(1,Color.BLACK);
      renderer.setSeriesShapesVisible(1, true);
      //changing the Renderer to XYDotRenderer
      //xyPlot.setRenderer(new XYDotRenderer());
      //XYDotRenderer xydotrenderer = new XYDotRenderer();
    // xyPlot.setRenderer(xydotrenderer);
    //  xydotrenderer.setSeriesShape(0, cross);

     

      return new ChartPanel(jfreechart);
  }

    

   
   private XYDataset createDataset(String chartTitle, HashMap result)
   {
      final XYSeries series = new XYSeries(chartTitle);  
      
     /*Iterator<Integer> itr2 = result.keySet().iterator();
      while (itr2.hasNext()) {
          Integer key = itr2.next();
          //i += key + map.get(key);
          series.add(key,Integer(result.get(key)));
      } 
      Map<Integer, Integer> map = new HashMap<Integer, Integer>();
      for (Map.Entry<Integer,Integer> entry : result.entrySet())
      {
    	  series.add(entry.getKey(),entry.getValue());
      }
        */       
      Iterator it = result.entrySet().iterator();
      while (it.hasNext()) {
          Map.Entry pair = (Map.Entry)it.next();
          
         // if(!(chartTitle.equals("HyperLogLog"))){
          series.add((Integer)pair.getKey(),(Integer)pair.getValue());
        //  }
          
         // else{
        	//  series.add((Integer)pair.getKey(),(Double)pair.getValue());
               
       //   }
         // System.out.println(pair.getKey() + " = " + pair.getValue());
          it.remove(); // avoids a ConcurrentModificationException
      }

      final XYSeriesCollection dataset = new XYSeriesCollection( );          
      dataset.addSeries( series );          
      
      return dataset;
   }

   public static void main( String[ ] args ) 
   {
      
   }
}