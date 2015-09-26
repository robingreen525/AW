args <- commandArgs(trailingOnly=TRUE)
loc <- args[1]
source(file.path(loc,"plot_cnv.r"))
plot.cnv(
         anc.path=unlist(strsplit(args[2],",")),
         strain.paths=unlist(strsplit(args[3],",")),
         smooth.window=as.numeric(args[4]), 
         plot.every=as.numeric(args[5]))
