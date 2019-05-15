import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
import org.jlab.groot.data.H1F
import org.jlab.groot.group.DataGroup;
import org.jlab.groot.math.F1D;
import org.jlab.groot.fitter.DataFitter;
import org.jlab.groot.graphics.EmbeddedCanvas;

def grtl = (1..6).collect{
  sec_num = it
  def gr = new GraphErrors('sec'+sec_num)
  gr.setTitle("DC residuals (peak value) per sector")
  gr.setTitleY("DC residuals (peak value) per sector (cm)")
  gr.setTitleX("run number")
  return gr
}

TDirectory out = new TDirectory()

for(arg in args) {
  TDirectory dir = new TDirectory()
  dir.readFile(arg)

  def name = arg.split('/')[-1]
  def m = name =~ /\d\d\d\d/
  def run = m[0].toInteger()

  out.mkdir('/'+run)
  out.cd('/'+run)

  (0..<6).each{
    sec_num = (it+1)
    def h21 = dir.getObject(String.format('/dc/DC_residuals_trkDoca_%d_1',sec_num))
    def h11 = h21.projectionY()
    def h22 = dir.getObject(String.format('/dc/DC_residuals_trkDoca_%d_2',sec_num))
    def h12 = h22.projectionY()
    def h23 = dir.getObject(String.format('/dc/DC_residuals_trkDoca_%d_3',sec_num))
    def h13 = h23.projectionY()
    def h24 = dir.getObject(String.format('/dc/DC_residuals_trkDoca_%d_4',sec_num))
    def h14 = h24.projectionY()
    def h25 = dir.getObject(String.format('/dc/DC_residuals_trkDoca_%d_5',sec_num))
    def h15 = h25.projectionY()
    def h26 = dir.getObject(String.format('/dc/DC_residuals_trkDoca_%d_6',sec_num))
    def h16 = h26.projectionY()
    h11.add(h12)
    h11.add(h13)
    h11.add(h14)
    h11.add(h15)
    h11.add(h16)
    h11.setName("sec"+sec_num)
    h11.setTitle("DC residuals per sector")
    h11.setTitleX("DC residuals per sector (cm)")

    // def f1 = ROOTFitter.fit(h1)
    def f1 = new F1D("fit:"+h11.getName(), "[amp]*gaus(x,[mean],[sigma])", -0.2, 0.2);
    f1.setLineWidth(2);
    f1.setOptStat("1111");
    initTimeGaussFitPar(f1,h11);
    DataFitter.fit(f1,h11,"LQ");

    //grtl[it].addPoint(run, h1.getDataX(h1.getMaximumBin()), 0, 0)
    // grtl[it].addPoint(run, h11.getMean(), 0, 0)
    grtl[it].addPoint(run, f1.getParameter(1), 0, 0)
    out.addDataSet(h11)
    out.addDataSet(f1)
  }
}


out.mkdir('/timelines')
out.cd('/timelines')
grtl.each{ out.addDataSet(it) }
out.writeFile('dc_residuals_sec.hipo')

private void initTimeGaussFitPar(F1D f1, H1F h1) {
        double hAmp  = h1.getBinContent(h1.getMaximumBin());
        double hMean = h1.getAxis().getBinCenter(h1.getMaximumBin());
        double hRMS  = h1.getRMS(); //ns
        // double rangeMin = (hMean - (3*hRMS));
        // double rangeMax = (hMean + (3*hRMS));
        // double pm = hRMS;
        // f1.setRange(rangeMin, rangeMax);
        f1.setParameter(0, hAmp);
        // f1.setParLimits(0, hAmp*0.8, hAmp*1.2);
        f1.setParameter(1, hMean);
        // f1.setParLimits(1, hMean-pm, hMean+(pm));
        f1.setParameter(2, hRMS);
        // f1.setParLimits(2, 0.1*hRMS, 0.8*hRMS);
}