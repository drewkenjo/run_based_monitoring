import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
import org.jlab.groot.data.H1F
import org.jlab.groot.group.DataGroup;
import org.jlab.groot.math.F1D;
import org.jlab.groot.fitter.DataFitter;
import org.jlab.groot.graphics.EmbeddedCanvas;

def grtl1 = (1..6).collect{
  sec_num = it
  def gr1 = new GraphErrors('sec'+sec_num+' sl'+1)
  gr1.setTitle("t max per sector per superlayer")
  gr1.setTitleY("t max per sector per superlayer (ns)")
  gr1.setTitleX("run number")
  return gr1
}
def grtl2 = (1..6).collect{
  sec_num = it
  def gr2 = new GraphErrors('sec'+sec_num+' sl'+2)
  gr2.setTitle("t max per sector per superlayer")
  gr2.setTitleY("t max per sector per superlayer (ns)")
  gr2.setTitleX("run number")
  return gr2
}
def grtl3 = (1..6).collect{
  sec_num = it
  def gr3 = new GraphErrors('sec'+sec_num+' sl'+3)
  gr3.setTitle("t max per sector per superlayer")
  gr3.setTitleY("t max per sector per superlayer (ns)")
  gr3.setTitleX("run number")
  return gr3
}
def grtl4 = (1..6).collect{
  sec_num = it
  def gr4 = new GraphErrors('sec'+sec_num+' sl'+4)
  gr4.setTitle("t max per sector per superlayer")
  gr4.setTitleY("t max per sector per superlayer (ns)")
  gr4.setTitleX("run number")
  return gr4
}
def grtl5 = (1..6).collect{
  sec_num = it
  def gr5 = new GraphErrors('sec'+sec_num+' sl'+5)
  gr5.setTitle("t max per sector per superlayer")
  gr5.setTitleY("t max per sector per superlayer (ns)")
  gr5.setTitleX("run number")
  return gr5
}
def grtl6 = (1..6).collect{
  sec_num = it
  def gr6 = new GraphErrors('sec'+sec_num+' sl'+6)
  gr6.setTitle("t max per sector per superlayer")
  gr6.setTitleY("t max per sector per superlayer (ns)")
  gr6.setTitleX("run number")
  return gr6
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
    def h11 = dir.getObject(String.format('/dc/DC_Time_%d_1',sec_num))
    def h12 = dir.getObject(String.format('/dc/DC_Time_%d_2',sec_num))
    def h13 = dir.getObject(String.format('/dc/DC_Time_%d_3',sec_num))
    def h14 = dir.getObject(String.format('/dc/DC_Time_%d_4',sec_num))
    def h15 = dir.getObject(String.format('/dc/DC_Time_%d_5',sec_num))
    def h16 = dir.getObject(String.format('/dc/DC_Time_%d_6',sec_num))
    h11.setName("sec"+sec_num+"sl"+1)
    h12.setName("sec"+sec_num+"sl"+2)
    h13.setName("sec"+sec_num+"sl"+3)
    h14.setName("sec"+sec_num+"sl"+4)
    h15.setName("sec"+sec_num+"sl"+5)
    h16.setName("sec"+sec_num+"sl"+6)

    // def f1 = ROOTFitter.fit(h1)
    def f11 = new F1D(String.format("Inverted_S_%d_%d",sec_num,0+1),"[p0]/(1+exp(-[p1]*(x-[p2])))",-100,1000);
    f11.setName("fit:"+h11.getName())
    f11.setLineWidth(2);
    f11.setOptStat("111111");
    initInvertedSFitPar(1,f11);
    DataFitter.fit(f11,h11,"LQ");

    def f12 = new F1D(String.format("Inverted_S_%d_%d",sec_num,1+1),"[p0]/(1+exp(-[p1]*(x-[p2])))",-100,1000);
    f12.setName("fit:"+h12.getName())
    f12.setLineWidth(2);
    f12.setOptStat("111111");
    initInvertedSFitPar(2,f12);
    DataFitter.fit(f12,h12,"LQ");

    def f13 = new F1D(String.format("Inverted_S_%d_%d",sec_num,2+1),"[p0]/(1+exp(-[p1]*(x-[p2])))",-100,1000);
    f13.setName("fit:"+h13.getName())
    f13.setLineWidth(3);
    f13.setOptStat("111111");
    initInvertedSFitPar(3,f13);
    DataFitter.fit(f13,h13,"LQ");

    def f14 = new F1D(String.format("Inverted_S_%d_%d",sec_num,3+1),"[p0]/(1+exp(-[p1]*(x-[p2])))",-100,1000);
    f14.setName("fit:"+h14.getName())
    f14.setLineWidth(3);
    f14.setOptStat("111111");
    initInvertedSFitPar(4,f14);
    DataFitter.fit(f14,h14,"LQ");

    def f15 = new F1D(String.format("Inverted_S_%d_%d",sec_num,4+1),"[p0]/(1+exp(-[p1]*(x-[p2])))",-100,1000);
    f15.setName("fit:"+h15.getName())
    f15.setLineWidth(3);
    f15.setOptStat("111111");
    initInvertedSFitPar(5,f15);
    DataFitter.fit(f15,h15,"LQ");

    def f16 = new F1D(String.format("Inverted_S_%d_%d",sec_num,5+1),"[p0]/(1+exp(-[p1]*(x-[p2])))",-100,1000);
    f16.setName("fit:"+h16.getName())
    f16.setLineWidth(3);
    f16.setOptStat("111111");
    initInvertedSFitPar(6,f16);
    DataFitter.fit(f16,h16,"LQ");

    //t_max = p2-(2/p1)
    grtl1[it].addPoint(run, f11.getParameter(2)-(2/f11.getParameter(1)), 0, 0)
    out.addDataSet(h11)
    out.addDataSet(f11)
    grtl2[it].addPoint(run, f12.getParameter(2)-(2/f12.getParameter(1)), 0, 0)
    out.addDataSet(h12)
    out.addDataSet(f12)
    grtl3[it].addPoint(run, f13.getParameter(2)-(2/f13.getParameter(1)), 0, 0)
    out.addDataSet(h13)
    out.addDataSet(f13)
    grtl4[it].addPoint(run, f14.getParameter(2)-(2/f14.getParameter(1)), 0, 0)
    out.addDataSet(h14)
    out.addDataSet(f14)
    grtl5[it].addPoint(run, f15.getParameter(2)-(2/f15.getParameter(1)), 0, 0)
    out.addDataSet(h15)
    out.addDataSet(f15)
    grtl6[it].addPoint(run, f16.getParameter(2)-(2/f16.getParameter(1)), 0, 0)
    out.addDataSet(h16)
    out.addDataSet(f16)
    // out.addDataSet(f1)
  }
}


out.mkdir('/timelines')
out.cd('/timelines')
grtl1.each{ out.addDataSet(it) }
grtl2.each{ out.addDataSet(it) }
grtl3.each{ out.addDataSet(it) }
grtl4.each{ out.addDataSet(it) }
grtl5.each{ out.addDataSet(it) }
grtl6.each{ out.addDataSet(it) }
out.writeFile('dc_tmax_sec_sl.hipo')

public void initInvertedSFitPar(int slayer, F1D function) {
  double min = 100.0;
  double max = 220.0;
  if (slayer == 1) {
    min = 100.0; max = 240.0;
    function.setParameter(1,-0.038); function.setParLimits(1,-0.01,-0.06);
    function.setParameter(2,118.0); function.setParLimits(2,100.0,150.0);
  }
  if (slayer == 2) {
    min = 120.0; max = 240.0;
    function.setParameter(1,-0.040); function.setParLimits(1,-0.01,-0.06);
    function.setParameter(2,136.0); function.setParLimits(2,100.0,200.0);
  }
  if (slayer == 3) {
    min = 200.0; max = 450.0;
    function.setParameter(1,-0.030);function.setParLimits(1,-0.01,-0.05);
    function.setParameter(2,320.0); function.setParLimits(2,200.0,500.0);
  }
  if (slayer == 4) {
    min = 200.0; max = 500.0;
    function.setParameter(1,-0.023); function.setParLimits(1,-0.01,-0.05);
    function.setParameter(2,350.0); function.setParLimits(2,200.0,500.0);
  }
  if (slayer == 5) {
    min = 400.0; max = 700.0;
    function.setParameter(1,-0.024);function.setParLimits(1,-0.01,-0.05);
    function.setParameter(2,623.0); function.setParLimits(2,500.0,700.0);
  }
  if (slayer == 6) {
    min = 480.0; max = 700.0;
    function.setParameter(1,-0.034); function.setParLimits(1,-0.01,-0.05);
    function.setParameter(2,683.0); function.setParLimits(2,500.0,750.0);
  }
  function.setRange(min,max);
  function.setLineColor(2);
  function.setLineWidth(4);
}