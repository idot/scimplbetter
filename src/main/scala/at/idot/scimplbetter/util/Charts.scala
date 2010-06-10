package at.idot.scimplbetter.util

import org.jfree.data.category.CategoryDataset
import org.jfree.data.category.DefaultCategoryDataset
import scala.collection.immutable.TreeMap
import org.jfree.chart.ChartFactory
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.renderer.category.BarRenderer
import java.awt.Color
import java.awt.GradientPaint
import org.jfree.chart.{ChartFactory,ChartUtilities,JFreeChart}
import java.awt.image.BufferedImage
import javax.swing.ImageIcon
import javax.swing.JLabel
import org.jfree.chart.renderer.category.StandardBarPainter
import org.jfree.ui.StandardGradientPaintTransformer
import org.jfree.ui.GradientPaintTransformType
import java.io.ByteArrayOutputStream
import java.io.BufferedOutputStream

object ChartSomethingByCounts {
	val dim = 300
	
	def dummy: Array[Byte] = {
		val default = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_RGB)
		ChartUtilities.encodeAsPNG(default)
	}
	
}

case class ChartSomethingByCounts[T](something: Seq[T],f: T => String, maxPlot: Int ) {
	
	def getChart(): Array[Byte] = {
	
	val counted = TreeMap[Int,String](){Ordering.fromLessThan((i:Int,o:Int) => i > o )} ++ something.groupBy(f).map(t => (t._2.size, t._1))
	val dataset = new DefaultCategoryDataset()
	for(count <- counted take maxPlot){
		dataset.addValue(count._1, "", count._2)
	}
	val chart = ChartFactory.createBarChart(
             "",         // chart title
             "",               // domain axis label
             "",                  // range axis label
             dataset,                  // data
             PlotOrientation.HORIZONTAL, // orientation
             false,                     // include legend
             false,                     // tooltips?
             false                     // URLs?
         )
 
     chart.setBackgroundPaint(Color.white)
     val plot = chart.getCategoryPlot()
     plot.setBackgroundPaint(Color.white)
     plot.setDomainGridlinePaint(Color.white)
     plot.setRangeGridlinePaint(Color.white)
     plot.setOutlineVisible(false)
     val rangeAxis = plot.getRangeAxis().asInstanceOf[NumberAxis]
     rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits())
  
     val renderer = plot.getRenderer().asInstanceOf[BarRenderer]
     renderer.setBarPainter(new StandardBarPainter())
     renderer.setShadowVisible(false)
	 renderer.setDrawBarOutline(false)
	 renderer.setGradientPaintTransformer( new StandardGradientPaintTransformer( GradientPaintTransformType.HORIZONTAL))
	 val to = for(i <- 1 to 3 ) yield new java.util.Random().nextInt(2)
     val gradient = new GradientPaint(
             0.0f, 0.0f, new Color(0, 0, 0, 0.0f), 
             0.0f, 0.0f, new Color(to(0),to(1),to(2),1f)
     )
	 renderer.setSeriesPaint(0, gradient)
     val image = chart.createBufferedImage(ChartSomethingByCounts.dim,ChartSomethingByCounts.dim)
    // val out = new ByteArrayOutputStream()
   //  ChartUtilities.writeBufferedImageAsJPEG(out, 1.0f,image)
    // out.toByteArray
     ChartUtilities.encodeAsPNG(image)
	}
	
}


case class ChartPointsOfUsers {
	
	var creator = new UserRowCreator()
	val userRows = creator.createUserRows
	creator.assignLeading(userRows)  
	
	
	
	
}



object doChart {
	
    def main(ars: Array[String]){
    	val charto = new ChartSomethingByCounts(genList(), (s:String) => s.toString, 10)
		val wr = new java.io.FileOutputStream("testFile.png")
    	wr.write(charto.getChart)
    	wr.close
    	val frame = new org.jfree.ui.ApplicationFrame("test")

    	frame.setContentPane(new JLabel(new ImageIcon(charto.getChart)))
    	frame.pack()
    	frame.setVisible(true)
    
    }
    
    def genList(): Seq[String] = {
    	for(i <- 1 until 100) yield {
    		val ind = new java.util.Random().nextInt(100)
    		ind.toString + "name"
    	}
    }
    
}
