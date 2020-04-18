import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
import fitter.CTOFFitter_mass;

def data = []

for(arg in args) {
  TDirectory dir = new TDirectory()
  dir.readFile(arg)

  def name = arg.split('/')[-1]
  def m = name =~ /\d{4,5}/
  def run = m[0].toInteger()

  def h1 = dir.getObject('/ctof/H_CTOF_pos_mass')
  def f1 = CTOFFitter_mass.fit(h1)

  data.add([run:run, mean:f1.getParameter(1), sigma:f1.getParameter(2).abs(), h1:h1, f1:f1])
}

['mean', 'sigma'].each{name ->
  TDirectory out = new TDirectory()

  def grtl = new GraphErrors(name)
  grtl.setTitle("CTOF mass^2 for #pi^+ ("+name+")")
  grtl.setTitleY("CTOF mass^2 for #pi^+ ("+name+") (GeV^2)")
  grtl.setTitleX("run number")

  data.each{
    grtl.addPoint(it.run, it[name], 0, 0)
    out.mkdir('/'+it.run)
    out.cd('/'+it.run)
    out.addDataSet(it.h1)
    out.addDataSet(it.f1)
  }

  out.mkdir('/timelines')
  out.cd('/timelines')
  out.addDataSet(grtl)
  out.writeFile('ctof_m2_pip_'+name+'.hipo')
}
