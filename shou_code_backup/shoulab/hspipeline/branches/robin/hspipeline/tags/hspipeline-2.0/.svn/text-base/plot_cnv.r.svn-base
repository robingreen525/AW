plot.cnv <- function(anc.path, strain.paths, window=500, every=100, 
                     min.del.size=1, sds=1.5, ...)
{
    graphics.off()
    cnv.ext    <- "_cnvfind.txt"
    cnv.folder <- "cnv_plots"
    cov.folder <- "coverage_plots"

    anc.cov.plot.path <- file.path(anc.path,cov.folder)
    anc.name <- basename(anc.path)

    if (!file.exists(anc.cov.plot.path)) dir.create(anc.cov.plot.path)

    first <- TRUE
    for (strain.path in strain.paths) {
        strain.cov.plot.path <- file.path(strain.path,cov.folder)
        strain.cnv.plot.path <- file.path(strain.path,cnv.folder)
        if (!file.exists(strain.cov.plot.path)) dir.create(strain.cov.plot.path)
        if (!file.exists(strain.cnv.plot.path)) dir.create(strain.cnv.plot.path)

        strain.name <- basename(strain.path)
        data.file <- file.path(strain.path, paste(strain.name,cnv.ext,sep=""))

        cat("[plot.cnv] Loading",data.file,"...",sep="")
        dat <- read.delim(data.file,
                          # reduces load time from 73 sec to 45 sec.
                          colClasses=c("factor",    
                                       rep("integer",3),
                                       rep("numeric",2)),
                          # further reduction to > 44 sec.
                          nrows=1.3e7, 
                          )
        cat("done!\n\n")

        cnv  <- data.frame()
        dels <- data.frame()
        anc.dels <- data.frame()

        for (chrom.data in split(dat,dat$chrom)) {
            current.chrom <- as.character(chrom.data$chrom[1])
            cat("[plot.cnv] Processing '",current.chrom,"'...\n",sep="")

            not.in.evo    <- subset(chrom.data,log2.ratio==-999)
            not.in.anc    <- subset(chrom.data,log2.ratio==999)
            not.in.either <- subset(chrom.data,is.na(log2.ratio))

            plottable.data <- na.omit(subset(chrom.data,
                                             !(log2.ratio %in% c(-999,999))))

            windowed <- sliding.window(plottable.data, size=window)
            windowed <- within(windowed,
                               color <- ifelse(sds.away>sds,"red","black"))

            reduced <- chrom.data[seq.int(1L,nrow(chrom.data),as.integer(every)),]
            max.pos <- max(windowed$pos)
            x.ticks <- seq(1,max.pos+max.pos/10,by=max.pos/10)



            plot.name <- paste(current.chrom,".png",sep="")

            cat("[plot.cnv] Plotting coverage...",sep="")
            setup.device("png", file.path(strain.cov.plot.path,plot.name))
            coverage.plot(cov.data=reduced[c("chrom","pos","evo.cov")],
                          x.ticks=x.ticks, strain=strain.name)
            dev.off()
            cat("done!\n")

            cat("[plot.cnv] Plotting coverage ratio...", sep="")
            setup.device("png", file.path(strain.cnv.plot.path,plot.name))
            cnv.plot(ratio.data=windowed,
                     not.in.evo=not.in.evo,
                     not.in.anc=not.in.anc,
                     not.in.either=not.in.either,
                     x.ticks=x.ticks,...)
            dev.off()
            cat("done!\n")

            cnv  <- rbind(cnv,  subset(windowed,sds.away>sds))
            dels <- rbind(dels, get.dels(current.chrom,not.in.evo$pos,
                                         min.del.size))
            if (first) {
                cat("[plot.cnv] Plotting coverage of ",anc.name,"...", sep="")
                setup.device("png", file.path(anc.cov.plot.path,plot.name))
                coverage.plot(cov.data=reduced[c("chrom","pos","anc.cov")],
                              x.ticks=x.ticks, strain=anc.name)
                dev.off()
                cat("done!\n")

                anc.dels <- rbind(dels, get.dels(current.chrom, 
                                                 not.in.anc$pos, 
                                                 min.del.size))
                names(anc.dels)[1] <- "#chrom"
                table.write(anc.dels,anc.path,anc.name,"_deletion_locations.txt")
            }
            cat("\n")
        }

        names(dels)[1] <- "#chrom"
        table.write(cnv,strain.path,strain.name,"_cnv_locations.txt")
        table.write(dels,strain.path,strain.name,"_deletion_locations.txt")

        gc()
        first <- FALSE
    }
    cat("\n\n")
}

setup.device <- function(dev,path) {
    if (dev=="x11") {
        x11()
        #x11(w=15,h=10)
    } else {
        png(path, w=15,h=10, units="in", res=120)
         
    }
    set.par()
}

coverage.plot <- function(cov.data, x.ticks, strain) {

    plot(range(x.ticks),range(cov.data[3]),
         type="n",ann=FALSE,axes=FALSE)
    title(main=paste(strain,": ",cov.data$chrom[1],sep=""))

    axis(1,at=x.ticks,labels=round(x.ticks/1000,0),
         lwd=par()$lwd,lwd.ticks=par()$lwd,
         cex.axis=ifelse(max(x.ticks)<1e6,1.2,1))

    axis(2,las=2,lwd=par()$lwd,lwd.ticks=par()$lwd,cex.axis=1.2)

    mtext("Position (Kb)",side=1,line=3,cex=par()$cex*1.5)
    mtext("Read depth",side=2, line=3.8, cex=par()$cex*1.5)

    box()

    lines(cov.data$pos,cov.data[[3]])
    points(cov.data$pos,cov.data[[3]],col=ifelse(cov.data[3]<5,"magenta","black"))
}

cnv.plot <- function(ratio.data, x.ticks, not.in.evo, not.in.anc, not.in.either,
                     ...)
{
    plot(range(x.ticks),range(ratio.data$log2.ratio),
         type="n", axes=FALSE,
         main=ratio.data$chrom[1],
         ylab="log2(evo/anc)", xlab="position (Kb)", ...)

    axis(1,at=x.ticks,labels=round(x.ticks/1000,0),
         lwd.ticks=par()$lwd,
         cex.axis=ifelse(max(x.ticks)<1e6,1.2,1))
    axis(2,lwd.ticks=par()$lwd,las=2)
    box()

    lines(log2.ratio~pos, data=ratio.data)
    points(log2.ratio~pos, data=ratio.data, col=color)

    points(not.in.evo$pos,rep(0.1,nrow(not.in.evo)),col="orange",pch=4)
    points(not.in.anc$pos,rep(-0.1,nrow(not.in.anc)),col="purple",pch=4)
    points(not.in.either$pos,rep(0,nrow(not.in.either)),col="blue",pch=4)

    abline(h=0,col="green",lty=3)
}


table.write <- function(dat,path,name,ext) {
    write.table(dat, file=file.path(path, paste(name,ext,sep="")),
                sep="\t",
                row.names=FALSE,
                quote=FALSE)
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
  na.omit(data.frame(chrom=dat$chrom[1],
                     pos=pos.means,
                     log2.ratio=log2.ratio.means,
                     sds.away=sds.away))
}

# Copied from package R.utils.
seqToIntervals <- function(idx,...) {
  idx <- as.integer(idx)
  idx <- unique(idx)
  idx <- sort(idx)
  n <- length(idx)
  if (n == 0) 
      return(res)
  d <- diff(idx)
  d <- (d > 1)
  d <- which(d)
  nbrOfIntervals <- length(d) + 1
  res <- matrix(as.integer(NA), nrow = nbrOfIntervals, ncol = 2)
  colnames(res) <- c("from", "to")
  fromValue <- idx[1]
  toValue <- fromValue - 1
  lastValue <- fromValue
  count <- 1
  for (kk in seq(along = idx)) {
      value <- idx[kk]
      if (value - lastValue > 1) {
          toValue <- lastValue
          res[count, ] <- c(fromValue, toValue)
          fromValue <- value
          count <- count + 1
      }
      lastValue <- value
  }
  if (toValue < fromValue) {
      toValue <- lastValue
      res[count, ] <- c(fromValue, toValue)
      count <- count + 1
  }
  res
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

test.plot.cnv <- function() {
    pfx <- "/media/Data/Sequences/Illumina"
    anc.path <- file.path(pfx,"cc/Sample_WY1335")
    strain.paths <- 
        paste(pfx,c("cc/Sample_CC45RS1","cc/Sample_CC38RL1"),sep="/")
    plot.cnv(anc.path=anc.path,
             strain.paths=unlist(strsplit(strain.paths,",")),
             window=500,
             every=100,
             ylim=c(-2.5,2.5))
}

