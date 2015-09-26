args <- commandArgs(trailingOnly=TRUE)
loc <- args[1]
source(file.path(loc,"plot_cnv.r"))
plot.cnv(
         anc.path=unlist(strsplit(args[2],",")),
         strain.paths=unlist(strsplit(args[3],",")),
         window=as.numeric(args[4]), 
         every=as.numeric(args[5]), 
         ylim=c(-2.5,2.5))
