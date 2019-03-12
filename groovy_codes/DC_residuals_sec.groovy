import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
// import ROOTFitter

def grtl = (1..36).collect{
  sec_num = it.intdiv(6)
  sl_num = it%6
  def gr = new GraphErrors('sec'+sec_num +'sl'+sl_num)
  gr.setTitle("DC residuals (peak value) per sector per superlayer")
  gr.setTitleY("DC residuals (peak value) per sector per superlayer")
  gr.setTitleX("run number")
  return gr
}

TDirectory out = new TDirectory()

for(arg in args.drop(1)) {
  TDirectory dir = new TDirectory()
  dir.readFile(arg)

  def name = arg.split('/')[-1]
  def m = name =~ /\d\d\d\d/
  def run = m[0].toInteger()

  out.mkdir('/'+run)
  out.cd('/'+run)

  (0..<36).each{
    // def h2 = dir.getObject('/elec/H_trig_vz_mom_S'+(it+1))
    // def h1 = h2.projectionY()
    sec_num = (it+1).intdiv(6)
    sl_num = (it+1)%6
    def h1 = dir.getObject(String.format('/dc/DC_residuals_trkDoca_%d_%d',sec_num,sl_num))
    h1.setName("sec"+sec_num+"sl"+sl_num)
    h1.setTitle("DC residuals per sector per superlayer")
    h1.setTitleX("DC residuals per sector per superlayer")

    // def f1 = ROOTFitter.fit(h1)

    //grtl[it].addPoint(run, h1.getDataX(h1.getMaximumBin()), 0, 0)
    grtl[it].addPoint(run, h1.getMean()), 0, 0)
    out.addDataSet(h1)
    // out.addDataSet(f1)
  }
}


out.mkdir('/timelines')
out.cd('/timelines')
grtl.each{ out.addDataSet(it) }
out.writeFile('out_DC_residuals_sec_sl.hipo')