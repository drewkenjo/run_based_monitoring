import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
// import ROOTFitter

def grtl = new GraphErrors('CVT negative tracks per trigger')
grtl.setTitle("CVT negative track multiplicity per trigger")
grtl.setTitleY("CVT negative track multiplicity per trigger")
grtl.setTitleX("run number")


TDirectory out = new TDirectory()

for(arg in args) {
  TDirectory dir = new TDirectory()
  dir.readFile(arg)

  def name = arg.split('/')[-1]
  def m = name =~ /\d\d\d\d/
  def run = m[0].toInteger()


    // def h2 = dir.getObject('/elec/H_trig_vz_mom_S'+(it+1))
    // def h1 = h2.projectionY()
    def h1 = dir.getObject('/cvt/hnegtrks_rat')
    h1.setTitle("CVT Negative Tracks/ trigger");
		h1.setTitleX("CVT Negative Tracks/ trigger");

    grtl.addPoint(run, h1.getMean(), 0, 0)
    // grtl[it].addPoint(run, f1.getParameter(1), 0, 0)
    out.mkdir('/'+run)
    out.cd('/'+run)
    out.addDataSet(h1)
    // out.addDataSet(f1)
}


out.mkdir('/timelines')
out.cd('/timelines')
grtl.each{ out.addDataSet(it) }
out.writeFile('CVT_neg_tracks_ratio.hipo')
