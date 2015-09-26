plot.cnv <- function(dat.file, size=1000, min.del.size=1, sds=1.5, 
                     strain, ...)
{
  suppressPackageStartupMessages(library(R.utils,quietly=TRUE,
                                         warn.conflicts=FALSE))
  graphics.off()
  path   <- dirname(dat.file)
  if (missing(strain)) strain <- basename(path)

  cat("Loading ",dat.file,"...\n",sep="")
  dat    <- read.delim(dat.file)

  cnv  <- data.frame()
  dels <- data.frame()
  for (chrom.dat in split(dat,dat$chrom)) {
    ord <- chrom.dat[order(chrom.dat$pos),]
    windowed <- sliding.window(ord, size)
    current.chrom <- chrom.dat$chrom[1]
    cat("Plotting ",as.character(current.chrom),"...\n",sep="")

    possible <- seq(1:max(chrom.dat$pos))
    miss     <- setdiff(possible,chrom.dat$pos)
    nmiss    <- length(miss)

    png(file.path(path,paste(current.chrom,".png",sep="")),w=15,h=10,
        units="in", res=120)
    set.par()
    plot(log2.ratio~I(pos/1000), data=windowed, main=ord$current.chrom, 
         ylab="log2(evo/anc)", xlab="position (kb)", 
         type="n",axes=FALSE,...)
    axis(1,lwd.ticks=par()$lwd)
    axis(2,lwd.ticks=par()$lwd,las=2)
    box()

    lines(log2.ratio~I(pos/1000), data=windowed)
    points(log2.ratio~I(pos/1000), data=windowed,
           col=ifelse(sds.away>sds,"red","black"))
    points(I(miss/1000),rep(0,nmiss),col="orange",pch=4)

    cnv  <- rbind(cnv,  subset(windowed,sds.away>sds))
    dels <- rbind(dels, get.dels(current.chrom,miss,min.del.size))
    dev.off()
  }
  names(dels)[1] <- "#chrom"
  write.table(cnv,file.path(path,paste(strain,"_cnv_locations.txt",sep="")),
              sep="\t",row.names=FALSE, quote=FALSE)
  write.table(dels,file.path(path,paste(strain,"_deletion_locations.txt",
                                        sep="")),
              sep="\t",row.names=FALSE, quote=FALSE)
  gc()
}

get.dels <- function(chrom,miss,min.del.size) {
  if (length(miss)==0) return()
  intervals <- seqToIntervals(miss)
  ints <- data.frame(chrom=chrom,intervals)
  ints <- within(ints, length <- (to-from)+1)
  ints <- subset(ints,length>=min.del.size)
  ints
}

sliding.window <- function(dat,size=100) {
  grand.mean <- mean(dat$log2.ratio)
  grand.sd   <- sd(dat$log2.ratio)

  log2.ratio.means <- NULL
  pos.means   <- NULL
  sds.away    <- NULL

  beg <- 1
  en  <- size
  while(en < max(dat$pos)) {
    r.mean   <- mean(dat$log2.ratio[beg:en])-grand.mean
    pos.mean <- mean(dat$pos[beg:en])
    log2.ratio.means  <- c(log2.ratio.means,r.mean)
    pos.means    <- c(pos.means,pos.mean)
    sds.away     <- c(sds.away,abs(r.mean)/grand.sd)
    beg <- beg+size
    en  <- en+size
  }
  data.frame(chrom=dat$chrom[1], pos=pos.means,log2.ratio=log2.ratio.means,sds.away=sds.away)
}

set.par <- function() {
  par(mar=c(5,5.3,1.5,1), 
      mex=0.8,
      lwd=4.0,
      cex=2, 
      cex.axis=1.5,
      cex.lab=1.5,
      font=2,
      font.axis=2,
      font.lab=2,
      font.main=2,
      font.sub=2)
  palette(c("black","orange","green","steelblue","purple","red","grey","brown","red","violet","salmon","goldenrod","cyan","darkgreen"))
}
