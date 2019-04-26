import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
import org.jlab.groot.data.H1F
import org.jlab.groot.group.DataGroup;
import org.jlab.groot.math.F1D;
import org.jlab.groot.fitter.DataFitter;
import org.jlab.groot.graphics.EmbeddedCanvas;

def grtl = new GraphErrors('CTOF_Edep')
grtl.setTitle("CTOF Edep, #pi^-")
grtl.setTitleY("CTOF Edep, #pi^- (MeV)")
grtl.setTitleX("run number")

TDirectory out = new TDirectory()

for(arg in args) {
  TDirectory dir = new TDirectory()
  dir.readFile(arg)

  def name = arg.split('/')[-1]
  def m = name =~ /\d\d\d\d/
  def run = m[0].toInteger()
  def h1 = dir.getObject('/ctof/H_CTOF_edep_pim')
  def f1 = new F1D("fit:"+h1.getName(),"[amp]*landau(x,[mean],[sigma])+exp(-[p1]*x)", 0, 50.0);
  f1.setParameter(0,0.0);
  f1.setParameter(1,0.0);
  f1.setParameter(2,1.0);
  f1.setParameter(3,0.0);
  f1.setOptStat(1111111);
  f1.setLineWidth(2);

  initLandauFitPar(h1, f1);
  DataFitter.fit(f1,h1,"LRQ");

  //grtl[it].addPoint(run, h1.getDataX(h1.getMaximumBin()), 0, 0)
  grtl.addPoint(run, f1.getParameter(1), 0, f1.getParameter(2))
  // grtl[it].addPoint(run, h1.getMean(), 0, 0)
  out.addDataSet(h1)
  out.addDataSet(f1)
}


out.mkdir('/timelines')
out.cd('/timelines')
grtl.each{ out.addDataSet(it) }
grtl2.each{ out.addDataSet(it) }
out.writeFile('CTOF_edep_pim.hipo')

private void initLandauFitPar(H1F hcharge, F1D fcharge) {
        double hAmp  = hcharge.getBinContent(hcharge.getMaximumBin());
        double hMean = hcharge.getAxis().getBinCenter(hcharge.getMaximumBin());
        double hRMS  = hcharge.getRMS(); //ns
        fcharge.setRange(fcharge.getRange().getMin(), hMean*2.0);
        fcharge.setParameter(0, hAmp);
        fcharge.setParLimits(0, 0.5*hAmp, 1.5*hAmp);
        fcharge.setParameter(1, hMean);
        fcharge.setParLimits(1, 0.8*hMean, 1.2*hMean);//Changed from 5-30
        fcharge.setParameter(2, 0.3);//Changed from 2
        fcharge.setParLimits(2, 0.1, 1);//Changed from 0.5-10
}
