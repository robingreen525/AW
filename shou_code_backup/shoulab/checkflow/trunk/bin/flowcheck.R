# Call with: 'R --vanilla --slave < flowcheck.R --args in_folder in_files out_folder bead_conc dilutions

require(flowCore, quietly=TRUE, warn.conflicts=FALSE)

in.path   <- commandArgs()[5]
in.files  <- strsplit(commandArgs()[6],",",fixed=TRUE)[[1]]
out.path  <- commandArgs()[7]
bead.conc <- as.numeric(commandArgs()[8])
dilutions <- sort(as.numeric(strsplit(commandArgs()[9],",",fixed=TRUE)[[1]]),decreasing=TRUE)
fsc.gate  <- as.numeric(strsplit(commandArgs()[10],",",fixed=TRUE)[[1]])
ssc.gate  <- as.numeric(strsplit(commandArgs()[11],",",fixed=TRUE)[[1]])
fitc.gate <- as.numeric(strsplit(commandArgs()[12],",",fixed=TRUE)[[1]])
dil.conc  <- bead.conc/dilutions

in.parts  <- strsplit(in.path,"/",fixed=TRUE)[[1]]
in.folder <- in.parts[length(in.parts)]

debug <- FALSE
if (identical(debug, TRUE)) {
#### Debugging paths ######
  in.path  <- '/mnt/fred/shougroup/FACS/checkflow/test'
  out.path <- '/var/www/data/checkflow/test'
  in.files <- c("A11.fcs","A12.fcs","B11.fcs","B12.fcs")
  bead.conc <- 1780
  dilutions <- c(25,10,5,2.5)
  dil.conc <- bead.conc / dilutions
  fitc.gate <- c(-Inf, Inf)
  fsc.gate <- c(-Inf, Inf)
  ssc.gate <- c(-Inf, Inf)
}

#str(in.path)
#str(out.path)
#str(bead.conc)
#str(dilutions)
#str(dil.conc)

bead.filter <- rectangleGate(filterId = "Beads",
                             "FSC-A" = c(fsc.gate[1],fsc.gate[2]),
                             "SSC-A" = c(ssc.gate[1],ssc.gate[2]),
                             "FITC-A" = c(fitc.gate[1],fitc.gate[2]))

beads <- NULL
for (i in 1:length(in.files)) {
  beads <- c(beads, Subset(read.FCS(paste(in.path,in.files[i],sep="/"),column.pattern="((FS|SS)C)|FITC|Time"), bead.filter))
}

if (length(beads) != length(in.files))
  stop("# Dilutions != # Files")

rows <- ifelse(length(beads)%%2 == 0,length(beads)%/%2,length(beads)%/%2+1)

png(paste(out.path,"bead_slopes.png",sep="/"),width=680, height=960)
par(mfrow=c(rows,2),mex=0.5,cex=1.5)

fits <- NULL
for (i in 1:length(beads)) {
  set <- beads[[i]]
  dat <- exprs(set)
  real.time <- dat[,"Time"] * 0.01
  f <- I(1:nrow(dat)) ~ real.time
  l <- lm(f)
  rsq <- summary(l)$r.squared 
  se <- summary(l)$coefficients[2,2]
  r <- coef(l)[2]

  plot(f, ann=FALSE)
  mtext(side=1, line=2.5, text="Time (s)",cex=1.5)
  mtext(side=2, line=2.5, text="Events",cex=1.5)
  title(bquote(.(dil.conc[i])~"beads/"*mu*l))
  text(x=0,y=nrow(dat)/1.1, labels=(bquote(rate==.(format(r,digits=3))~events~sec^-1)),pos=4,cex=1)
  text(x=0,y=nrow(dat)/1.2, labels=(bquote(R^2==.(format(rsq,digits=5)))),pos=4,cex=1)
  text(x=0,y=nrow(dat)/1.3, labels=(bquote(2%*%SE==.(format(2*se,digits=3)))),pos=4,cex=1)

  abline(l,col="red")

  fits <- rbind(fits, cbind(conc=dil.conc[i], 
                            slope=coef(l)[2], 
                            se=summary(l)$coefficients[2,"Std. Error"], 
                            inter=coef(l)[1]))
}
dev.off()
print(fits)

png(paste(out.path,"flow_rate.png",sep="/"))
par(cex=2, lwd=2, cex.axis=0.7, mar=c(3,3,3,1)+0.5)

f2        <- fits[,"slope"] ~ fits[,"conc"] - 1
fits.lm   <- lm(f2, weights=1/fits[,"se"]^2)
print(summary(fits.lm))


flow.rate <- coef(fits.lm)[1]
flow.rsq  <- summary(fits.lm)$r.squared
flow.se   <- summary(fits.lm)$coefficients[2]

label  <- bquote("Flow rate"==.(format(flow.rate, digits=5))~mu*l/sec)
label2 <- bquote(R^2==.(format(flow.rsq,digits=5)))
label3 <- bquote("95% CI"==.(format(2*flow.se,digits=4)))

plot(f2, xlim=range(0,max(fits[,"conc"])),ylim=range(0,max(fits[,"slope"])),ann=FALSE)

arrows(fits[,"conc"], fits[,"slope"]-2*fits[,"se"],
       fits[,"conc"], fits[,"slope"]+2*fits[,"se"],
       length=0.01, code=3, angle=90)

abline(fits.lm,col="red")

mtext(side=1,line=2.2,text=expression("Concentration ("*mu*l^-1*")"),cex=1.5)
mtext(side=2,line=2.5,text=expression("Event Rate ("*sec^-1*")"),cex=1.5)
text(x=0,y=max(fits[,"slope"]/1.1),  labels=label ,pos=4,cex=0.8)
text(x=0,y=max(fits[,"slope"]/1.2), labels=label3,pos=4,cex=0.8)
text(x=0,y=max(fits[,"slope"]/1.32),  labels=label2,pos=4,cex=0.8)
title(paste("Beads in Folder\n","'",in.folder,"'",sep=""))

dev.off()

