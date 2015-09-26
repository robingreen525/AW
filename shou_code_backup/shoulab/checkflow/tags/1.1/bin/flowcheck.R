# Call with: 'R --vanilla --slave < flowcheck.R --args in_folder out_folder bead_conc dilutions

require(flowCore, warn.conflicts=FALSE)

in.path   <- commandArgs()[5]
out.path  <- commandArgs()[6]
bead.conc <- as.numeric(commandArgs()[7])
dilutions <- sort(as.numeric(strsplit(commandArgs()[8],",",fixed=TRUE)[[1]]))
dil.conc  <- bead.conc/dilutions

in.parts  <- strsplit(in.path,"/",fixed=TRUE)[[1]]
in.folder <- in.parts[length(in.parts)]

#in.path   <- '/mnt/fred/shougroup/FACS/checkflow/wshou_CYRToPro_006'
#out.path <- '/var/www/data/checkflow/test'

#str(in.path)
#str(out.path)
#str(bead.conc)
#str(dilutions)
#str(dil.conc)

dil.names <- sub("\\.","p",dilutions)
files <- list.files(in.path)

if (length(dil.names) != length(files)) {
  stop("# dilutions != # files")
}


beads <- NULL
for (i in 1:length(dil.names)) {
  m <- paste('^',dil.names[i],'\\.fcs',sep="")
  beads <- c(beads, read.FCS(paste(in.path,files[grep(m,files,perl=TRUE)],sep="/")))
}

rows <- ifelse(length(beads)%%2 == 0,length(beads)%/%2,length(beads)%/%2+1)

png(paste(out.path,"bead_slopes.png",sep="/"),width=640, height=960)
par(mfrow=c(rows,2),mex=0.5,cex=1.5)

fits <- NULL
for (i in 1:length(beads)) {
  set <- beads[[i]]
  dat <- exprs(set)
  real.time <- dat[,"Time"] * 0.01
  f <- I(1:nrow(dat)) ~ real.time
  l <- lm(f)
  rsq <- round(summary(l)$r.squared,digits=5) 

  plot(f, ann=FALSE)
  mtext(side=1, line=2.5, text="Time (s)",cex=1.5)
  mtext(side=2, line=2.5, text="Events",cex=1.5)
  title(bquote(.(dil.conc[i])~"beads/"*mu*l))
  text(x=0,y=nrow(dat)/1.1, labels=(paste("R = ",rsq,sep="")),pos=4,cex=1)

  abline(l,col="red")

  fits <- rbind(fits, cbind(conc=dil.conc[i], slope=coef(l)[2], inter=coef(l)[1]))
}
dev.off()
#print(fits)

png(paste(out.path,"flow_rate.png",sep="/"))
par(cex=2, lwd=2, cex.axis=0.7, mar=c(3,3,3,1)+0.5)

f2<- fits[,"slope"] ~ fits[,"conc"] + 0
fits.lm <- lm(f2)
flow.rate <- coef(fits.lm)[1]

label  <- bquote("Flow rate"==.(format(flow.rate, digits=5))~mu*l/sec)
label2 <- bquote(R^2==.(format(rsq,digits=5)))

plot(f2, xlim=range(0,max(fits[,"conc"])),ylim=range(0,max(fits[,"slope"])),ann=FALSE)
abline(fits.lm,col="red")

mtext(side=1,line=2.2,text=expression("Concentration ("*mu*l^-1*")"),cex=1.5)
mtext(side=2,line=2.5,text=expression("Event Rate ("*sec^-1*")"),cex=1.5)
text(x=0,y=max(fits[,"slope"]/1.1), labels=label,pos=4,cex=0.8)
text(x=0,y=max(fits[,"slope"]/1.3), labels=label2,pos=4,cex=0.8)
title(paste("Beads in\n",in.folder))

dev.off()

