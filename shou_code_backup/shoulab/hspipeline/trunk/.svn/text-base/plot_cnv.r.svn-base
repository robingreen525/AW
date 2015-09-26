plot.cnv <- function(anc.path, strain.paths, smooth.window=50, 
                     plot.every=10, min.del.size=1, sds=3)
{
    graphics.off()
    device <- "png"
    cnv.ext    <- "_cnvfind.txt"
    depthstats.ext <- "_depthstats.txt"
    cnv.folder <- "cnv_plots"
    cov.folder <- "coverage_plots"

    anc.cov.plot.path <- file.path(anc.path,cov.folder)
    anc.name <- basename(anc.path)

    if (!file.exists(anc.cov.plot.path)) dir.create(anc.cov.plot.path)

    chrom.order <- paste("chr",c("I","II","III","IV","V","VI","VII",
                                 "VIII","IX","X","XI","XII","XIII",
                                 "XIV","XV","XVI","M"), sep="")

    get.x.ticks <- function(x) {
        max.pos <- max(x$pos)
        seq(1,max.pos+max.pos/10,by=max.pos/10)
    }

    first <- TRUE
    xpos <- 0
    for (strain.path in strain.paths) {
        strain.cov.plot.path <- file.path(strain.path,cov.folder)
        strain.cnv.plot.path <- file.path(strain.path,cnv.folder)
        if (!file.exists(strain.cov.plot.path))
            dir.create(strain.cov.plot.path)
        if (!file.exists(strain.cnv.plot.path))
            dir.create(strain.cnv.plot.path)

        strain.name <- basename(strain.path)
        data.file <- file.path(strain.path, 
                               paste(strain.name,cnv.ext,sep=""))

        cat("[plot.cnv] Loading ",data.file,"...",sep="")
        dat <- read.delim(data.file,
                          # reduces load time ~ 1.6x.
                          colClasses=c("character",    
                                       rep("integer",3),
                                       rep("numeric",4)),
                          # small further reduction.
                          nrows=1.3e7, 
                         )
        cat("done!\n\n")

        not.in.evo.data    <- subset(dat,log2.chrom.ratio==-999)
        not.in.anc.data    <- subset(dat,log2.chrom.ratio==999)
        not.in.either.data <- subset(dat,is.na(log2.chrom.ratio))

        in.both.data <- 
            na.omit(subset(dat, !(log2.chrom.ratio %in% c(-999,999))))

        dat <- NULL

        # Output tables
        cnv      <- data.frame()
        dels     <- data.frame()
        anc.dels <- data.frame()


        setup.device(device, file.path(strain.cnv.plot.path,
                                       "genome_coverage_ratio.png"))
        setup.genome.cnv.plot(name=strain.name)

        depthstats.plot.name <- "genome_coverage.png"
        cat("[plot.cnv] Strain '", strain.name, "'\n",sep="")

        dat.chrom <- unique(in.both.data$chrom)
        if (!all(chrom.order %in% dat.chrom)) {
            chrom.order <- dat.chrom
        }

        for (current.chrom in chrom.order) {
            #if (current.chrom != "chrXI") next
            cat("[plot.cnv] Processing '",current.chrom,"'...\n",sep="")

            chrom.data <- subset(in.both.data,chrom==current.chrom)

            not.in.evo.pos <- 
                subset(not.in.evo.data,chrom==current.chrom)$pos
            not.in.anc.pos <-
                subset(not.in.anc.data,chrom==current.chrom)$pos
            not.in.either.pos <- subset(not.in.either.data,
                                        chrom==current.chrom)$pos

            chrom.windowed  <- sliding.window(chrom.data,smooth.window)

            chrom.reduced <- reduce(chrom.windowed,plot.every)
            chrom.reduced <- within(chrom.reduced, sds.away <-
                abs(log2.chrom.ratio-mean(log2.chrom.ratio))/
                sd(log2.chrom.ratio))

            chrom.x.ticks <- get.x.ticks(chrom.reduced)

            genome.reduced <- reduce(chrom.windowed,plot.every*100)

            chrom.data     <- NULL
            chrom.windowed <- NULL

            plot.name <- paste(current.chrom,".png",sep="")

            cat("[plot.cnv] Plotting coverage ratio...", sep="")
            setup.device(device, file.path(strain.cnv.plot.path,plot.name))
            cnv.plot(ratio.data=chrom.reduced,
                     x.ticks=chrom.x.ticks,
                     not.in.evo=not.in.evo.pos,
                     not.in.anc=not.in.anc.pos,
                     not.in.either=not.in.either.pos,
                     sds)
            dev.off()
            cat("done!\n")

            not.in.evo <- NULL
            not.in.anc <- NULL
            not.in.either <- NULL


            cat("[plot.cnv] Plotting coverage...",sep="")
            setup.device(device, file.path(strain.cov.plot.path,plot.name))
            coverage.plot(cov.data=chrom.reduced,column="evo.cov",
                          x.ticks=chrom.x.ticks, strain=strain.name)
            dev.off()
            cat("done!\n")

            if (current.chrom != "chrM") {
                cat("[plot.cnv] Adding to genome coverage ratio plot...",
                    sep="")
                xpos <- genome.cnv.plot(genome.reduced, xpos)
                cat("done!\n")
            }

            cnv  <- rbind(cnv,  subset(chrom.reduced,sds.away>sds))
            dels <- rbind(dels, get.dels(current.chrom,not.in.evo.pos,
                                         min.del.size))
            if (first) {
                cat("[plot.cnv] Plotting coverage of ",anc.name,"...",
                    sep="")
                setup.device(device, 
                             file.path(anc.cov.plot.path,plot.name))
                coverage.plot(cov.data=chrom.reduced, column="anc.cov",
                              x.ticks=chrom.x.ticks, 
                              strain=anc.name)
                dev.off()
                cat("done!\n")

                anc.dels <- rbind(anc.dels, get.dels(current.chrom, 
                                                     not.in.anc.pos, 
                                                     min.del.size))
            }
            cat("\n")
        } # end chrom loop.
        dev.off()

        names(dels)[1] <- "#chrom"
        table.write(cnv,strain.path,strain.name,"_cnv_locations.txt")
        table.write(dels,strain.path,strain.name,"_deletion_locations.txt")

        if (first) {
            names(anc.dels)[1] <- "#chrom"
            table.write(anc.dels,anc.path,anc.name,
                        "_deletion_locations.txt")

            cat("[plot.cnv] Plotting ",anc.name," depth stats...", sep="")
            anc.depthstats.plot.path <- 
                file.path(anc.cov.plot.path, depthstats.plot.name)

            setup.device(device, anc.depthstats.plot.path)
            plot.depthstats(file.path(anc.path,
                                      paste(anc.name,depthstats.ext,
                                            sep="")),
                            chrom.order)

            dev.off()
            cat("done!\n")
            first <- FALSE
        }

        cat("[plot.cnv] Plotting genome depth stats...",sep="")
        setup.device(device, file.path(strain.cov.plot.path,
                                       depthstats.plot.name))
        plot.depthstats(file.path(strain.path,
                                  paste(strain.name,depthstats.ext,
                                        sep="")),
                        chrom.order)
        dev.off()
        cat("done!\n")
        gc()

        cat("\n")
        xpos <- 0
    } # end strain loop
    cat("\n")
}

plot.depthstats <- function(file.location,chr.order) {
    chr.order <- chr.order[chr.order!="chrM"]

    name <- basename(file.location)
    mean.col <- 7
    sd.col   <- 8
    depth.dat <- read.delim(file.location,comment.char="#", as.is=1)
    depth.dat <- droplevels(subset(depth.dat, chromosome != "chrM"))
    depth.dat <- depth.dat[match(chr.order,depth.dat$chromosome),]
    depth.dat$chromosome <- factor(depth.dat$chromosome,
                                   levels=depth.dat$chromosome)

    x.las <- 1
    chroms <- if (all(grepl("chr", depth.dat$chromosome))) {
        gsub("chr","",depth.dat$chromosome)
    } else {
        x.las <- 2
        gsub("[A-Za-z_]", "", depth.dat$chromosome)
    }

    minus.sd <- depth.dat[[7]]-depth.dat[[8]]
    plus.sd  <- depth.dat[[7]]+depth.dat[[8]]

    xs <- (1:nrow(depth.dat))
    x.range <- range(xs)
    y.range <- range(c(minus.sd,plus.sd))
    fake.y <- c(y.range, rep(y.range[1], length(depth.dat$chromosome)-2))

    plot(depth.dat$chromosome, fake.y, type="n", 
         main=name, border="white", axes=FALSE, ann=FALSE)
    axis(1, at=1:length(chroms), labels=chroms, lwd.ticks=par()$lwd,
         cex.axis=0.9, las=x.las)
    axis(2,lwd.ticks=par()$lwd, las=2)
    mtext("mean coverage",side=2, line=3.8, cex=par()$cex*1.5)
    mtext("chromosome",side=1, line=3, cex=par()$cex*1.5)
    box()
    points(depth.dat$chromosome, depth.dat[[7]],pch=22,cex=3)
    arrows(xs,minus.sd,xs,plus.sd,code=3,angle=90,length=0.1)
}

setup.device <- function(dev,path) {
    if (dev=="x11") {
        #x11(); return()
        x11(w=15,h=10)
    } else {
        png(path, w=15,h=10, units="in", res=120)
         
    }
    set.par()
}

coverage.plot <- function(cov.data, column, x.ticks, strain) {
    coverage <- cov.data[[column]]

    plot(range(x.ticks),range(coverage),
         type="n",ann=FALSE,axes=FALSE)
    title(main=paste(strain,": ",cov.data$chrom[1],sep=""))

    axis(1,at=x.ticks,labels=round(x.ticks/1000,0),
         lwd=par()$lwd,lwd.ticks=par()$lwd,
         cex.axis=ifelse(max(x.ticks)<1e6,1.2,1))

    axis(2,las=2,lwd=par()$lwd,lwd.ticks=par()$lwd,cex.axis=1.2)

    mtext("Position (Kb)",side=1,line=3,cex=par()$cex*1.5)
    mtext("Read depth",side=2, line=3.8, cex=par()$cex*1.5)

    box()

    points(cov.data$pos,coverage, type="h",
           col=ifelse(coverage<5,"magenta","black"))
}

cnv.plot <- function(ratio.data, x.ticks, not.in.evo, not.in.anc,
                     not.in.either, sds, ...)
{
    plot(range(x.ticks),range(ratio.data$log2.chrom.ratio),
         type="n", axes=FALSE,
         main=ratio.data$chrom[1],
         ylab="log2(evo/anc)", xlab="position (Kb)", ...)

    axis(1,at=x.ticks,labels=round(x.ticks/1000,0),
         lwd.ticks=par()$lwd,
         cex.axis=ifelse(max(x.ticks)<1e6,1.2,1))
    axis(2,lwd.ticks=par()$lwd,las=2,cex.axis=1)
    box()

    lines(log2.chrom.ratio~pos, data=ratio.data)
    points(log2.chrom.ratio~pos, data=ratio.data,
           col=ifelse(sds.away>sds,"red","black"))

    points(not.in.evo,rep(0.1,length(not.in.evo)),col="orange",pch=4)
    points(not.in.anc,rep(-0.1,length(not.in.anc)),col="purple",pch=4)
    points(not.in.either,rep(0,length(not.in.either)),col="cyan",pch=4)

    abline(h=0,col="green",lty=3)
}

setup.genome.cnv.plot <- function(name, yrange=c(-2.5,2.5))
{
    plot(c(1,12e6),yrange,
         type="n", axes=FALSE, xaxs="i", main=name,
         ylab="log2(evo/anc)", xlab="")

    axis(2,lwd.ticks=par()$lwd,las=2)
    box()
}

genome.cnv.plot <- function(ratio.data,x.start) {
    x.end <- max(ratio.data$pos)+x.start
    chrom <- gsub("chr","",ratio.data$chrom[1])

    abline(v=x.end,col="blue",lwd=par()$lwd/2)
    lines(log2.genome.ratio~I(pos+x.start), data=ratio.data)
    mtext(chrom, side=1, line=1, at=(x.start+x.end)/2, cex=2)
    mtext("chromosome",side=1, line=3, cex=par()$cex*1.5)

    x.end
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

reduce <- function(dat.frame, every) {
    return(dat.frame[seq.int(1L,nrow(dat.frame),as.integer(every)),])
}

sliding.window <- function(dat, size=50) {

  beg <- count <- 1
  en  <- size

  row.est  <- ceiling(nrow(dat)/size)
  dat.mat  <- as.matrix(dat[-1])
  windowed <- matrix(ncol=7,nrow=row.est)
  while(en < nrow(dat)) {
      windowed[count,] <- .colMeans(dat.mat[beg:en,],m=size,n=7)
      beg <- beg+size
      en  <- en+size
      count <- count+1
  }
  res <- na.omit(data.frame(chrom=rep(dat$chrom[1],nrow(windowed)),
                            windowed))

  names(res)[-1] <- names(dat[-1])
  res
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
    pfx <- "/media/Data/hspipeline_test"
    anc.path <- file.path(pfx,"cc/Sample_WY1335")
    strain.paths <- 
        paste(pfx,c("cc/CC45RL1"),sep="/")
    plot.cnv(anc.path=anc.path,
             strain.paths=unlist(strsplit(strain.paths,",")),
             smooth.window=50,
             plot.every=10,)
}

