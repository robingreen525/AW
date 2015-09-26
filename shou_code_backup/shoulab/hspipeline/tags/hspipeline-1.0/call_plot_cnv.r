args <- commandArgs(trailingOnly=TRUE)
loc <- args[1]
source(file.path(loc,"plot_cnv.r"))
plot.cnv(dat=args[2], size=as.numeric(args[3]), strain=args[4], 
         ylim=c(-2.5,2.5))
