import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
import org.jlab.groot.data.H1F
import org.jlab.groot.group.DataGroup;
import org.jlab.groot.math.F1D;
import org.jlab.groot.fitter.DataFitter;
import org.jlab.groot.graphics.EmbeddedCanvas;

def grtl = (1..6).collect{
  def gr = new GraphErrors('Mean, sec'+it)
  gr.setTitle("p1a t_tdc-t_fadc (mean)")
  gr.setTitleY("p1a t_tdc-t_fadc (mean) (ns)")
  gr.setTitleX("run number")
  return gr
}
def grtl2 = (1..6).collect{
  def gr = new GraphErrors('Sigma,'+it)
  gr.setTitle("p1a t_tdc-t_fadc (sigma)")
  gr.setTitleY("p1a t_tdc-t_fadc (sigma) (ns)")
  gr.setTitleX("run number")
  return gr
}

TDirectory out = new TDirectory()
TDirectory out2 = new TDirectory()

for(arg in args) {
  TDirectory dir = new TDirectory()
  dir.readFile(arg)

  def name = arg.split('/')[-1]
  def m = name =~ /\d{4,5}/
  def run = m[0].toInteger()

  out.mkdir('/'+run)
  out.cd('/'+run)
  out2.mkdir('/'+run)
  out2.cd('/'+run)

  (0..<6).each{
    def h1 = dir.getObject('/tof/p1a_tdcadc_dt_S'+(it+1))
    def f1 = new F1D("fit:"+h1.getName(), "[amp]*gaus(x,[mean],[sigma])", -1.0, 1.0);
    f1.setLineWidth(2);
    f1.setOptStat("1111");
    initTimeGaussFitPar(f1,h1);
    DataFitter.fit(f1,h1,"LQ");
    recursive_Gaussian_fitting(f1,h1)
    if (f1.getChiSquare()>500) recursive_Gaussian_fitting(f1,h1)
    //grtl[it].addPoint(run, h1.getDataX(h1.getMaximumBin()), 0, 0)
    grtl[it].addPoint(run, f1.getParameter(1), 0, 0)
    grtl2[it].addPoint(run, f1.getParameter(2), 0, 0)
    out.addDataSet(h1)
    out.addDataSet(f1)
    out2.addDataSet(h1)
    out2.addDataSet(f1)

  }
}


out.mkdir('/timelines')
out.cd('/timelines')
grtl.each{ out.addDataSet(it) }
out.writeFile('ftof_tdcadc_time_p1a_mean.hipo')

out2.mkdir('/timelines')
out2.cd('/timelines')
grtl2.each{ out2.addDataSet(it) }
out2.writeFile('ftof_tdcadc_time_p1a_sigma.hipo')

private void initTimeGaussFitPar(F1D f1, H1F h1) {
        double hAmp  = h1.getBinContent(h1.getMaximumBin());
        double hMean = h1.getAxis().getBinCenter(h1.getMaximumBin());
        double hRMS  = 1.5//h1.getRMS(); //ns
        double rangeMin = (hMean - 1.5*hRMS);
        double rangeMax = (hMean + 1.5*hRMS);
        f1.setRange(rangeMin, rangeMax);
        f1.setParameter(0, hAmp);
        //f1.setParLimits(0, hAmp*0.8, hAmp*1.2);
        f1.setParameter(1, hMean);
        // f1.setParLimits(1, hMean-1, hMean+1);
        f1.setParameter(2, hRMS);
        //f1.setParLimits(2, 0.1*hRMS, 0.8*hRMS);
        // f1.setParameter(3,0);
}

private void recursive_Gaussian_fitting(F1D f1, H1F h1){
        double rangeMin = h1.getAxis().getBinCenter(h1.getMaximumBin())-1.5*Math.abs(f1.getParameter(2))
        double rangeMax = h1.getAxis().getBinCenter(h1.getMaximumBin())+1.5*Math.abs(f1.getParameter(2))
        // limit fitting range as 2 sigma
        def f2 = new F1D("temp", "[amp]*gaus(x,[mean],[sigma])", -1.0, 1.0);
        f2=f1
        f2.setRange(rangeMin,rangeMax)
        DataFitter.fit(f2,h1,"LQ");
        if (f1.getChiSquare()>f2.getChiSquare()){
          System.out.println("Replacing fitting function")
          f1=f2
          f1.setName("fit:"+h1.getName())
        }
}
