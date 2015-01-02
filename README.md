scala-measures
==============
Small experiment with execution time measurement design in Scala.

If it interests you, you may take a look at the demo app for usage example. 

Here is a quick example of the core features:

    // this method is called for each collected time 
    implicit val reporter: ReportSample = yourTimeReportingHandler 
    
    // that name represents the series of execution time samples you're going to collect 
    val seriesName = "Your Action"
    
    // and here is how you hook your method 'yourAction' to the time sampler
    yourAction() withTimeSampleAs seriesName
  
Enjoy...
