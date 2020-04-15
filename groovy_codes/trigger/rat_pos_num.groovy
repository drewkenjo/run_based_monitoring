import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors

data = []

for(arg in args) {
  TDirectory dir = new TDirectory()
  dir.readFile(arg)

  def name = arg.split('/')[-1]
  def m = name =~ /\d{4,5}/
  def run = m[0].toInteger()

  def h1 = dir.getObject('/trig/H_trig_sector_positive_rat')
  data.add([run:run, h1:h1])
}


TDirectory out = new TDirectory()
out.mkdir('/timelines')

(0..<6).each{ sec->
  def grtl = new GraphErrors('sec'+(sec+1))
  grtl.setTitle("Positives per trigger per sector")
  grtl.setTitleY("Positives per trigger per sector")
  grtl.setTitleX("run number")

  data.each{
    if (sec==0){
      out.mkdir('/'+it.run)
      out.cd('/'+it.run)
      out.addDataSet(it.h1)
    }
    grtl.addPoint(it.run, it.h1.getBinContent(sec),0,0)
  }
  out.cd('/timelines')
  out.addDataSet(grtl)
}

out.writeFile('rat_positive.hipo')