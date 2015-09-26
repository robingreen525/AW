# vim: set filetype=r tabstop=2 shiftwidth=2 expandtab :

#  To install required packages:
#     1) Open R with installation privileges.
#     2) > source("http://bioconductor.org/biocLite.R")
#     3) > biocLite(c("flowCore","flowMeans","flowViz"))
#     $) > install.packages("EMD")

flustr <- function(file=NULL, fcs=NULL, live.colors,
                   beads=TRUE,
                   bead.gate=list(green=c(1e5,Inf),orange=c(1e5,Inf),
                                  cyan=c(1e5,Inf)),
                   bead.dilution=11.2, beads.per.ul=8.3e3,
                   all.bright=4, all.dim=3.0, 
                   fsc.sig=0.4, dead.sig=0, cluster.sig=1,
                   initial.cluster.min=4,
                   initial.cluster.max=10,
                   save.debug=FALSE,
                   output=NULL,
                   result.path=".",
                   sep="\t", plot.type="png", 
                   max.events=NULL, ...)
{
  close.devices()

  if (!is.null(output) && sink.number() == 0) 
    sink(paste(result.path,"/",output,sep=""), append=TRUE)

  if (is.null(file) && is.null(fcs)) {
    stop(paste("\n  FLUSTR ERROR:",
               "Need either a flowFrame or a path to an .fcs file."))
  }

  if (plot.type != "png" && plot.type != "pdf") {
    stop(paste("\n  FLUSTR ERROR:", 
               "Don't recognize plot type '", plot.type, "'!"))
  }

  if (missing(live.colors)) {
    stop("'live.colors' is missing!\n\n")
  }


  if (!require(flowCore,  quietly=TRUE, warn.conflicts=FALSE))
    stop("FLUSTR ERROR: can't find package 'flowCore'!")
  if (!require(flowMeans, quietly=TRUE, warn.conflicts=FALSE))
    stop("FLUSTR ERROR: can't find package 'flowMeans'!")
  if (!require(flowViz,   quietly=TRUE, warn.conflicts=FALSE))
    stop("FLUSTR ERROR: can't find package 'flowViz'!")
  if (!require(EMD,       quietly=TRUE, warn.conflicts=FALSE))
    stop("FLUSTR ERROR: can't find package 'EMD'!")

  source("/home/nodice/Documents/Code/R/utilities/logicleaxis.r")

  beads.per.ml <- beads.per.ul * 1000
  MIN_CLUSTER_POP <- 1000
  PLOT_EXT  <- paste(".",plot.type,sep="")

  channel.list        <- c(
                           "AmCyan-A",
                           "L3-1",

                           "FITC-A",
                           "GFP-A",
                           "BluFL1",

                           "PerCP-A",
                           "BluFL2",

                           "PE-A",
                           "L2-3",

                           "PE-TxRed-A",
                           "PE-Texas Red-A",
                           "L2-4",

                           "APC-A",
                           "L2-1",

                           "FSC-A",
                           "FSC",
                          
                           "SSC-A",
                           "SSC",

                           "Time",
                           "TIME"
                           )
  names(channel.list) <- c(
                           rep("cyan",2),

                           rep("green",3),

                           rep("yellow",2),

                           rep("orange",2),

                           rep("red",3),

                           rep("dead",2),

                           rep("fsc",2),

                           rep("ssc",2),

                           rep("time",2))
  unknown.colors <- !live.colors %in% names(channel.list)
  if (any(unknown.colors)) {
    cat("FLUSTR ERROR: Don't recognize one or more colors:", 
        live.colors[unknown.colors],
        "\nAvailable colors:", unique(names(channel.list)),"\n\n")
    stop()
  }

  all <- get.data(file,fcs)
  if (!is.null(max.events)) {
    all <- if (nrow(all) > max.events) 
             all[round(runif(max.events,min=0,max=nrow(all))),] 
           else 
             all
  }

  file.name  <- sub("(.*)\\/(.*)\\.fcs", "\\2", keyword(all)$FILENAME, perl=TRUE)
  plot.name  <- file.name
  debug.path <- paste(result.path,"/",file.name,"_cluster_data.tab",sep="")
  Robj.path  <- paste(result.path,"/",file.name,"_result.RData",sep="")
  if (identical(save.debug,TRUE)) {
    cat(paste("Run started at ", format(Sys.time(),"%H:%M:%S"), "\n", 
        sep=""), file=debug.path)
  }


  requested.colors <- names(channel.list) %in% live.colors
  bead.colors      <- names(channel.list) %in% c("green","orange","cyan")
  dead.colors      <- names(channel.list) %in% c("dead")
  scatter          <- names(channel.list) %in% c("fsc","ssc")

  scatter.channels <- pick.channels(channel.list[scatter],colnames(all))
  bead.channels <- pick.channels(channel.list[bead.colors], colnames(all))
  live.channels <- pick.channels(channel.list[requested.colors],
                                 colnames(all))
  dead.channel  <- pick.channels(channel.list[dead.colors],colnames(all))
  cluster.channels <- c(live.channels, dead.channel)
  nchans <- length(live.channels)

  categories <- make.categories(live.channels)

  cat("\nProcessing",file.name,"...\n")
  beads.and.not <- list(beads=data.frame(), not.beads=all)
  if (beads) beads.and.not <- find.beads(dat=all, 
                                         bead.gate=bead.gate,
                                         chans=bead.channels)

  processed <- preprocess(dat=beads.and.not$not.beads,
                          chans=c(scatter.channels,cluster.channels),
                          save.debug=save.debug, debug.path=debug.path,
                          sep=sep)

  warns <- check.processed(dat=processed$tf.data, 
                           chans=c(scatter.channels["fsc"],dead.channel))

  plot.base       <- paste(result.path,"/",plot.name,sep="")
  superclust.plot <- paste(plot.base,PLOT_EXT,sep="")
  unmerged.plot   <- paste(plot.base,"_unmerged",PLOT_EXT,sep="")
  fsc.dead.plot   <- paste(plot.base,"_fsc-dead",PLOT_EXT,sep="")

#x11()
  open.plot(fsc.dead.plot,plot.type)
  par(mar=c(4.7,5,3,1.5), cex=2, cex.axis=0.7, cex.lab=1.5, mex=0.6)

  initial.pops <- list()
  initial.chans <- c(scatter.channels["fsc"],dead.channel)
  if (warns$no.dead && warns$no.low.fsc) {
    cat("All cells are considered live\n")

    plot(processed$tf.data, initial.chans, smooth=FALSE,
         ylim=range(exprs(processed$tf.data[,dead.channel])),
         main=file.name, pch=".", axes=FALSE)
    initial.pops$cluster.pop <- processed$tf.data
  } else {
    cat("Initial clustering...\n")
    dead.dat  <- exprs(processed$tf.data[,dead.channel])
    first.cut <- cluster.flowMeans(dat=processed$tf.data, 
                                   fname=file.name,
                                   min.clusters=initial.cluster.min,
                                   max.clusters=initial.cluster.max,
                                   maxn.start=15,
                                   max.attempts=15, max.streak=5,
                                   chans=initial.chans,
                                   addNoise=FALSE, Standardize=FALSE, 
                                   nstart=100)
    if (is.null(first.cut)) {
      stop("FLUSTR ERROR: initial cluster result is NULL!\n")
      close.devices()
    }

    first.cut.ordered <- order.clusters(dat=processed$tf.data, 
                                        cluster.res=first.cut,
                                        chans=initial.chans)

    minima <- find.minima(processed$tf.data, initial.chans)

    cluster.info <- label.initial(cluster.data=first.cut.ordered,
                                  minima=minima,
                                  fsc.sig=fsc.sig, dead.sig=dead.sig)

    initial.pops <- assign.initial(all.data=processed$tf.data,
                                   cluster.res=first.cut,
                                   cluster.info=cluster.info)

    plot.initial.clusters(all.data=processed$tf.data,
                          cluster.res=first.cut,
                          cluster.info=first.cut.ordered,
                          boundaries=cluster.info$boundaries,
                          chans=initial.chans,
                          plot.name=file.name)
  }
  y.params <- subset(processed$tf.params, channel %in% dead.channel)
  logicle.axis(2, params=y.params, las=2)
  axis(1,at=axTicks(1),labels=parse(text=paste("10^",axTicks(1))))
  box()

  dev.off()

  cluster.res  <- NULL
  cluster.data <- NULL
  if (nrow(initial.pops$cluster.pop) > MIN_CLUSTER_POP) {
    cat("Clustering live cells...\n")
    cluster.res <- cluster.flowMeans(dat=initial.pops$cluster.pop, 
                                     fname=file.name,
                                     min.clusters=15, max.clusters=100,
                                     maxn.start=c(100,25,1),
                                     max.attempts=3, max.streak=51,
                                     chans=live.channels,
                                     addNoise=TRUE, Standardize=FALSE, 
                                     nstart=10,
                                     NumC=50)
    if (is.null(cluster.res)) {
      stop("FLUSTR ERROR: live cluster result is NULL!\n")
      close.devices()
    }

    open.plot(unmerged.plot, plot.type)
    par(mar=c(4.7,5,3,1.5), cex=2, cex.axis=1.5, cex.lab=1.5, mex=0.6)
    plot(initial.pops$cluster.pop,cluster.res,
         c(live.channels), main=file.name, pch=".")
    dev.off()

    cluster.data <- order.clusters(dat=initial.pops$cluster.pop, 
                                   cluster.res=cluster.res,
                                   chans=live.channels)
    #print(cluster.data)
    cluster.data <- name.fmclusters(dat=cluster.data, 
                                    significant=cluster.sig,
                                    bright=all.bright, dim=all.dim,
                                    save.debug=save.debug, 
                                    debug.path=debug.path,
                                    sep=sep)

    open.plot(superclust.plot, plot.type)
    opar <- NULL
    opar <- par(mar=c(4.2,4.2,3,1), cex=3, mex=0.6)
    if (plot.type=="pdf") {
      opar <- par(mar=c(4.2,4.2,3,1), cex=3, mex=0.6)
    } else {
      nplots <- choose(nchans,2) + 1 # Additional plot is dead vs fsc.
      ncols  <- ceiling(sqrt(nplots))
      nrows  <- ceiling(nplots/ncols)
      opar <- par(mar=c(4.2,4.2,3,1), cex=3, mex=0.6,
                  mfrow=c(nrows,ncols), mex=0.6)
    }

    plot.live.dead(processed=processed$tf.data,
                   chans=initial.chans,
                   pops=initial.pops,
                   tf.params=processed$tf.params)

    plot.fmclusters(dat=initial.pops$cluster.pop, clust.res=cluster.res, 
                    clust.dat=cluster.data,
                    tf.params=processed$tf.params,
                    chans=live.channels)
    mtext(file.name,line=-1.6,outer=TRUE)
    dev.off()
    par <- opar
  } else {
    cat("WARNING: Only", nrow(initial.pops$cluster.pop), 
        "cells found...will not attempt clustering.\n")
  }

  res <- make.result(file.name=file.name, 
                     cells=nrow(initial.pops$cluster.pop), 
                     low.fsc=nrow(initial.pops$low.fsc.pop), 
                     dead=nrow(initial.pops$dead.pop), 
                     beads=nrow(beads.and.not$beads),
                     fluor=cluster.data,
                     bead.dilution=bead.dilution, 
                     beads.per.ml=beads.per.ml)

  if (save.debug) {
    cat("\n", file=debug.path, append=TRUE)
    write.table(data.frame(res),file=debug.path,sep=sep,row.names=FALSE, 
                append=TRUE)

    cat("\n", file=debug.path, append=TRUE)

    cat(paste("Run completed at ", format(Sys.time(),"%H:%M:%S"),"\n",
        sep=""), file=debug.path, append=TRUE)
  }
  cat("DONE!\n")
  save(res, file=Robj.path)
  if (!is.null(output)) sink()
  res
}

assign.initial <- function(all.data, cluster.res, cluster.info) {
    dead.pops    <- NULL
    cluster.pops <- NULL
    low.fsc.pops <- NULL
    for (i in 1:length(cluster.info$assignments)) {
      tags <- cluster.info$assignments[[i]]
      if (is.null(tags))
        cluster.pops <- c(cluster.pops, i)
      else if ("dead" %in% tags)
        dead.pops <- c(dead.pops, i)
      else if ("low.fsc" %in% tags )
        low.fsc.pops <- c(low.fsc.pops, i)
    }
    cluster.pop <- all.data[cluster.res@Label %in% cluster.pops,]
    dead.pop    <- all.data[cluster.res@Label %in% dead.pops,]
    low.fsc.pop <- all.data[cluster.res@Label %in% low.fsc.pops,]

    list(cluster.pop=cluster.pop, dead.pop=dead.pop, low.fsc.pop=low.fsc.pop)
}


plot.initial.clusters <- function(all.data, cluster.res, cluster.info,
                                  boundaries, chans, plot.name)
{
  plot(all.data, cluster.res, chans, main=plot.name, pch=".",axes=FALSE)
  abline(v=boundaries$fsc.ct, lwd=2)
  abline(v=boundaries$fsc.plus, lty=2, lwd=2)
  abline(v=boundaries$fsc.minus, lty=2, lwd=2)
  abline(h=boundaries$dead.ct, lwd=2)
  abline(h=boundaries$dead.plus, lty=2, lwd=2)
  abline(h=boundaries$dead.minus, lty=2, lwd=2)

  for (lab in split(cluster.info,cluster.info$label)) {
    points(lab[1,]$median,lab[2,]$median, pch="+",col="purple",cex=3)
  }
}

open.plot <- function(name, type) {
  if (type=="pdf") {
    pdf(name, width=10, height=10, useDingbats=FALSE)
  } else {
    bitmap(name, width=11.5, height=10, units="in", res=300)
  }
}

pick.channels <- function(arg.chans, data.chans) {
  seen <- ""
  for (cn in arg.chans) {
    if (any(cn %in% seen)) {
      stop ("FLUSTR ERROR: Duplicate channels!")
      close.devices()
    }
    seen <- c(seen,cn)
  }

  channels <- c()
  unames <- unique(names(arg.chans))
  for (name in unames) {
    if (!any(arg.chans[names(arg.chans) %in% name] %in% data.chans)) {
      cat("FLUSTR ERROR: User channels not found in fcs file!\n",
                 "  Requested channels: '", arg.chans , "'\n",
                 "  Available channels: '", data.chans, "'\n")
      stop()
      close.devices()
    } else {
      channels <- 
        c(channels,
          intersect(arg.chans[names(arg.chans) %in% name], data.chans))
    }
  }
  names(channels) <- unames
  channels
}

check.processed <- function(dat,chans) {
  exdat <- exprs(dat[,chans])
  warns <- list(no.dead    = FALSE,
                no.low.fsc = FALSE,
                no.live    = FALSE)

  max.dead     <- max(exdat[,chans["dead"]])
  min.dead.val <- 1
  if (max.dead < min.dead.val) {
    cat(paste("WARNING: MAX", chans["dead"],
              "value is",format(max.dead,digits=3),
              "...not considering anything dead\n"))
    warns$no.dead <- TRUE
  }

  frac.low.fsc <- sum(exdat[,chans["fsc"]]<4.5)/length(exdat[,chans["fsc"]])
  min.frac.low.fsc <- 0.001
  if (frac.low.fsc < min.frac.low.fsc) {
    cat(paste("WARNING: Only", format(100*frac.low.fsc, digits=3), 
              "% is low in fsc...not considering anything low fsc\n"))
    warns$no.low.fsc <- TRUE
  }
  warns
}

plot.live.dead <- function(processed, chans, tf.params, pops) {
  processed.dat <- exprs(processed)
  fsc.range <- range(processed.dat[,chans["fsc"]])
  dead.range <- range(processed.dat[,chans["dead"]])
  plot(processed, chans, col="white", smooth=FALSE,
       xlim=fsc.range, ylim=dead.range, axes=FALSE)

  y.params <- subset(tf.params, channel %in% chans[2])
  axis(1,at=axTicks(1),labels=parse(text=paste("10^",axTicks(1))))
  logicle.axis(2, params=y.params, las=2)
  box()

  par(new=TRUE)
  if (nrow(pops$cluster.pop)>0) {
    plot(pops$cluster.pop, chans, pch=".", cex=2, smooth=FALSE, 
         xlim=fsc.range, ylim=dead.range,
         col="purple", ann=FALSE, axes=FALSE)
    try(contour(pops$cluster.pop, chans, add=TRUE))
    par(new=TRUE)
  }
  if (nrow(pops$dead.pop) > 0) {
    plot(pops$dead.pop, chans, pch=".", cex=2, smooth=FALSE, 
         xlim=fsc.range, ylim=dead.range,
         col="black", ann=FALSE, axes=FALSE)
    try(contour(pops$dead.pop, chans, col="orange", add=TRUE))
    par(new=TRUE)
  }
  if (nrow(pops$low.fsc.pop) > 0) {
    plot(pops$low.fsc.pop, chans, pch=".", cex=2, smooth=FALSE, 
         xlim=fsc.range, ylim=dead.range,
         col="tan", ann=FALSE, axes=FALSE)
    try(contour(pops$low.fsc.pop, chans, add=TRUE))
  }
  par(new=FALSE)
}

plot.fmclusters <- function(dat, clust.res, clust.dat, tf.params, chans) {
  cols <-list(
              'red'    = "red",
              'green'  = "green",
              'orange' = "orange",
              'yellow' = "yellow",
              'cyan'   = "blue",
              'dead' = "black",
              all.bright = "magenta",
              all.dim    = "darkgray",
              moribund   = "pink",

              'green&orange' = "brown4",
              'cyan&green'   = "cyan",
              'green&yellow' = "yellow2",
              'green&red'    = "brown1",

              'cyan&orange'   = "purple4",
              'orange&yellow' = "darkorange",
              'orange&red'    = "darkgoldenrod",

              'cyan&yellow' = "darkgreen",
              'cyan&red'    = "purple",

              'red&yellow' = "goldenrod"
             )
  pchs <-list(
              'red'      = ".",
              'green'    = ".",
              'orange'   = ".",
              'yellow'   = ".",
              'cyan'     = ".",
              'dead'     = ".",
              all.bright = ".",
              all.dim    = ".",
              moribund   = ".",

              'green&orange' = ".",
              'cyan&green'   = ".",
              'green&yellow' = ".",
              'green&red'    = ".",

              'cyan&orange'   = ".",
              'orange&yellow' = ".",
              'orange&red'    = ".",

              'cyan&yellow' = ".",
              'cyan&red'    = ".",

              'red&yellow' = "."
             )
      
  sizes <- aggregate(clust.dat$n,list(name=clust.dat$name),sum)
  sizes <- sizes[order(sizes$x, decreasing=TRUE),]

  for (i in 1:(length(chans)-1)) {
    for (j in (i+1):length(chans)) {
      xrange <- range(exprs(dat[,chans[i]]))
      yrange <- range(exprs(dat[,chans[j]]))
      plot(dat,c(chans[i],chans[j]),col="white", cex=3, smooth=FALSE,
           axes=FALSE)

      x.params <- subset(tf.params, channel %in% chans[i])
      y.params <- subset(tf.params, channel %in% chans[j])

      logicle.axis(1, params=x.params)
      logicle.axis(2, params=y.params, las=2)
      box()

      par(new=TRUE)
      mbids <- data.frame()
      
      for (chan in sizes$name) {
        by.name <- subset(clust.dat, name==chan)
        nme <- as.character(unique(by.name$name))
        labels <- unique(by.name[by.name$name==nme,]$label)

        if (grepl("moribund",nme)) {
          id <- sub('.*?(\\d+)','\\1',nme)
          x.med <- subset(clust.dat,label %in% labels & 
                   channel %in% names(chans[i]))$median
          y.med <- subset(clust.dat,label %in% labels & 
                   channel %in% names(chans[j]))$median
          mbids <- rbind(mbids,data.frame(id=id,x.med=x.med,y.med=y.med))
          nme <- "moribund"
        }
        plot(dat[clust.res@Label %in% labels,], 
             c(chans[i],chans[j]), col=cols[[nme]], pch=pchs[[nme]],
             xlim=xrange, ylim=yrange,
             smooth=FALSE, axes=FALSE, ann=FALSE, cex=3)
        par(new=TRUE)
      }
      if (nrow(mbids)>0) {
        for (m in 1:nrow(mbids)) {
          text(x=mbids[m,]$x.med, y=mbids[m,]$y.med,
               labels=mbids[m,]$id, cex=2)
          par(new=TRUE)
        }
      }
      par(new=FALSE)
    }
  }
}

name.fmclusters <- function(dat, significant, bright, dim, save.debug,
                            debug.path, sep)
{
  dat <- within(dat, {
                      ctplus  <- dat$median + significant*dat$mad
                      ctminus <- dat$median - significant*dat$mad
                     })
  dat <- dat[order(dat$label, dat$median, decreasing=TRUE),]
  clust.names <- NULL
  moribunds <- 1
  for (label in split(dat,dat$label)) {
    nme <- namer(grp=label,ct="median", bright=bright, dim=dim)
    if (identical(nme,"moribund")) {
      nme <- paste(nme,".",moribunds,sep="")
      moribunds <- moribunds + 1
    }
    clust.names <- c(clust.names, nme)
  }
  dat <- 
    data.frame(dat,
               name=rev(rep(clust.names,each=length(unique(dat$channel)))))
#print(dat)
  if (save.debug) {
    write.table(dat, file=debug.path, sep=sep, row.names=FALSE, append=TRUE)
  }
  dat
}

find.minima <- function(dat,chans) {

  fsc.gate <- matrix(c(log10(1e5), log10(2.5e5)),
                     dimnames=list(c("min","max"),chans["fsc"]))
  minima.filter <- rectangleGate(.gate=fsc.gate)
  filtered <- Subset(dat, minima.filter)

  dead.dat         <- exprs(filtered[,chans["dead"]])
  trimmed.dead.dat <- dead.dat[dead.dat>quantile(dead.dat,0.001) & 
                               dead.dat<quantile(dead.dat,0.990)]
  dead.dens <- density(trimmed.dead.dat,adjust=2.0)
  dead.ext  <- extrema(dead.dens$y)
  dead.minimum  <- 
    if(is.null(dead.ext$minindex)) max(dead.dens$x)
    else dead.dens$x[dead.ext$minindex[length(dead.ext$minindex)]]

  fsc.dat  <- exprs(dat[,chans["fsc"]])
  fsc.dens <- density(fsc.dat,adjust=4)
  fsc.ext  <- extrema(fsc.dens$y)
  fsc.minimum  <- 
    if (is.null(fsc.ext$minindex)) max(fsc.dens$x)
    else fsc.dens$x[fsc.ext$minindex[length(fsc.ext$minindex)]]

  list(fsc=fsc.minimum, dead=dead.minimum)
}

label.initial <- function(cluster.data, minima, fsc.sig, dead.sig) {
  fsc.dat   <- subset(cluster.data, channel=="fsc")
  fsc.ct    <- mean(fsc.dat$median)
  fsc.error <- fsc.sig*sqrt(sum(fsc.dat$mad^2))
  fsc.plus  <- fsc.ct + fsc.error
  fsc.minus <- fsc.ct - fsc.error


  dead.dat   <- subset(cluster.data, channel=="dead")
  dead.ct    <- minima$dead
  dead.error <- dead.sig*sqrt(sum(dead.dat$mad^2))
  dead.plus  <- dead.ct + dead.error
  dead.minus <- dead.ct - dead.error

  boundaries <- data.frame(fsc.ct=fsc.ct, fsc.plus=fsc.plus,
                           fsc.minus=fsc.minus, 
                           dead.ct=dead.ct,
                           dead.plus=dead.plus, dead.minus=dead.minus)

  cluster.assignments <- vector("list",length(unique(cluster.data$label)))
  for (cluster in split(cluster.data,cluster.data$label)) {
    label <- unique(cluster$label)
    if (cluster[cluster$channel %in% "fsc",]$median < fsc.minus) {
      cluster.assignments[[label]] <- 
        c(cluster.assignments[[label]], "low.fsc")
    }
    if (cluster[cluster$channel %in% "dead",]$median > dead.plus) {
      cluster.assignments[[label]] <- 
        c(cluster.assignments[[label]], "dead")
    }
  }
  list(assignments=cluster.assignments, boundaries=boundaries)
}

find.and.plot.initial.pops <- function(dat, fsc.sig, dead.sig, chans, 
                                       nclusters, ct)
{

  fsc.dat  <- dat[dat$channel=="fsc",]
  dead.dat <- dat[dat$channel=="dead",]

  fsc.ct    <- mean(fsc.dat$median)
  fsc.error <- fsc.sig*sqrt(sum(fsc.dat$mad^2))
  fsc.plus  <- fsc.ct + fsc.error
  fsc.minus <- fsc.ct - fsc.error

  dead.ct    <- mean(dead.dat$median) #w.mean(dead.dat$median,dead.dat$n)
  dead.ct    <- dead.minimum
  dead.error <- dead.sig*sqrt(sum(dead.dat$mad^2))
  dead.plus  <- dead.ct + dead.error
  dead.minus <- dead.ct - dead.error

  abline(v=fsc.ct, lwd=2)
  abline(v=fsc.plus, lty=2, lwd=2)
  abline(v=fsc.minus, lty=2, lwd=2)

  abline(h=dead.ct, lwd=2)
  abline(h=dead.plus, lty=2, lwd=2)
  abline(h=dead.minus, lty=2, lwd=2)

  for (lab in split(dat,dat$label)) {
    points(lab[1,]$median,lab[2,]$median, pch="+",col="purple",cex=3)
  }
}


w.mean <- function(x,w) {
  return(sum(x*(w/sum(w))))
}

preprocess <- function(dat, chans, save.debug, debug.path, sep) {
  spill <- keyword(dat)$SPILL
  if (is.null(spill))
    cat("\nNo spillover matrix found for this data\n")
  else
    dat <- compensate(x=dat,spillover=spill)

  cat("  Picking transformation parameters...\n")
  processed <- transform.data(dat, chans=chans, save.debug, debug.path, 
                              sep=sep)
  processed
}

order.clusters <- function(dat, cluster.res, chans) {
  nlabs  <- length(unique(cluster.res@Label))
  nchans <- length(chans)
  res <- data.frame()
  for (lab in 1:nlabs) {
    for (cn in 1:nchans) {
      chan <- chans[cn]
      cluster.dat <- exprs(dat[,chan])[cluster.res@Label==lab,]
      med  <- median(cluster.dat)
      mad.  <- mad(cluster.dat)
      n    <- nrow(dat[cluster.res@Label==lab,])
      res  <- rbind(res, data.frame(label=lab, channel=names(chan), 
                                    median=med, mad=mad., n=n))
    }
  }

  nms <- data.frame()
  for (cn in names(chans)) {
    ss <- res[res$channel %in% cn,]
    nms <- rbind(nms, data.frame(ss[order(ss$median, decreasing=TRUE),],
                      rank=1:nrow(ss)))
  } 
  nms
}

find.beads <- function(dat, bead.gate, chans) {
  mat <- matrix(unlist(bead.gate), nrow=2,
                dimnames=list(c("min","max"),
                              chans[names(bead.gate)]))
  bead.filter <- rectangleGate(filterId="beads", .gate=mat)
  not.beads <- Subset(dat, !bead.filter)
  beads     <- Subset(dat, bead.filter)
  list(not.beads=not.beads, beads=beads)
}

make.result <- function(file.name, cells, low.fsc, beads, dead, fluor,
                        bead.dilution, beads.per.ml)
{
  res <- NULL
  categories <- list()

  if (is.null(fluor)) {
    categories <- data.frame(skipped=1)
  } else {
    for (nme in unique(fluor$name)) {
      categories[[nme]] <- sum(unique(fluor[fluor$name==nme,]$n))
    }
    categories <- data.frame(categories,check.names=FALSE)
  }

  categories <- data.frame(categories[sort(names(categories))])
  total <- ifelse(is.null(fluor), sum(low.fsc,cells), sum(categories))

  if (!is.null(dead)) total <- total + dead

  res$count <- data.frame(file=file.name,
                          beads=beads,
                          cells=cells,
                          dead=dead,
                          low.fsc=low.fsc,
                          categories,
                          check.names=FALSE)

  res$ratio <- data.frame(file=file.name, 
                          low.fsc=low.fsc/total,
                          cells=cells/total,
                          dead=dead/total,
                          categories/cells,
                          check.names=FALSE)

  res$density <- data.frame(file=file.name,
                            res$count[2:ncol(res$count)]/
                            (beads/beads.per.ml*bead.dilution),
                            check.names=FALSE,
                            stringsAsFactors=FALSE)
#  print(res)
  res
}

namer <- function(grp,ct,bright,dim) {
  if (nrow(grp) == 0) {
    stop("FLUSTR ERROR: group data is empty!")
    close.devices()
  }

  # Trivial case of one channel.
  if (nrow(grp) == 1) { return(as.character(grp[1,]$channel)) } 

  # Deal with clusters that are all bright or all dim.
  if (all(grp[[ct]] < dim)) {
    return("all.dim")
  } else if (all(grp[[ct]] > bright)) {
    return("all.bright")
  } else if ( all(grp[1:2,][[ct]] > bright) ) {
    return(double.name(grp[1,]$channel,grp[2,]$channel))

  # Significantly bright in the top channel.
  } else if ( grp[1,]$ctminus > grp[2,]$ctplus && grp[1,]$median > dim) {
    return(as.character(grp[1,]$channel))

  # Not significantly different than second-brightest channel..
  } else if ( grp[1,]$ctminus < grp[2,]$ctplus ) {
    if ( mean(grp[1:2,][[ct]]) < dim ) { return("all.dim") }
    else if ( nrow(grp) == 2 ) { return("moribund") }
    else if ( grp[2,]$ctminus > grp[3,]$ctplus ) {
    # ... but second brighter than third
      if (mean(grp[1:2,][[ct]]) > bright) {
        return(double.name(grp[1,]$channel,grp[2,]$channel))
      } else { return("moribund") }
    } else { return("moribund") }
  } else {
    return("uncategoriezed")
  }
}

double.name <- function(n1,n2) {
  names <- sort(c(as.character(n1),as.character(n2)))
  paste(as.character(names[1]),"&",as.character(names[2]),sep="")
}

get.data <- function(file, fcs) {
  if (is.null(file) && !(is.null(fcs))) {
    all <- fcs
  } else if (is.null(fcs) && !(is.null(file))) {
    all <- read.FCS(file,min.limit=NULL)
  } else { 
    stop("\n\tNeed a file name or a flowFrame!\n")
    close.devices()   
  }
}

make.categories <- function(channels) {
  categories <- list()
  for (i in 1:length(channels)) {
    for (j in 1:length(channels)) {
      if (i != j) {
        chan1 <- channels[i]; chan2 <- channels[j]
        categories[[chan1]] <- 0
        categories[[double.name(chan1,chan2)]] <- 0
        categories[[double.name(chan2,chan1)]] <- 0
      }
    }
  }
  categories[["moribund"]] <- 0
  categories[["all.bright"]] <- 0
  categories[["all.dim"]] <- 0
  return(categories)
}

transform.data <- function(dat, chans, save.debug, debug.path, sep) {
  if (nrow(exprs(dat[,chans["fsc"]]))==0)
    stop("FLUSTR ERROR: No fsc data found in this flowFrame!")

  transf     <- list()
  all.params <- list()

  for (cn in chans) {
    transf[[cn]] <- 
      if (cn %in% chans[c("fsc","ssc")]) {
        shiftLogTransform(cn)
      } else {
        params <- data.frame(channel=cn,logicle.params(dat[,cn]),
                             check.names=FALSE)
        all.params <- rbind(all.params,params)
        logicleTransform(cn, t=params$t, w=params$w, m=params$m)
      }
  }
  print(all.params)

  if (save.debug) {
    write.table(all.params, file=debug.path, sep=sep, 
                row.names=FALSE, append=TRUE)
  }
  tf <- transformList(chans, transf)

  transDat <- transform(dat, tf)
  res <- list(tf.data=transDat, tf.params=all.params)
  res
}

logicle.params <- function(dat) {
  ndat  <- nrow(dat)
  exdat <- exprs(dat)
#  exdat <- exdat[exdat>-1000]

  min.val <- min(exdat)
  max.val <- max(exdat)
  lt.zero <- exdat[exdat<0]
  n.lt.zero <- length(lt.zero)

  m <- 5.5 
  t <- 2^18
  r <- quantile(lt.zero, 0.05)
  w <- abs((m-log10(t/abs(r)))/2)
  p <- uniroot(function(p) -w + 2 * p * log10(p)/(p+1), 
               c(.Machine$double.eps,10000))$root

  data.frame(lt.zero=n.lt.zero, w=w, t=t, m=m, p=p)
}

splitEndsTransform <- function(transformId, high.sd, low.sd) {
  t = new("transform",
      .Data = function(x) {
        lt.med <- x[x<=median(x)]
        gt.med <- x[x>median(x)]
        low.cutoff  <- mean(lt.med)-low.sd*sd(lt.med)
        high.cutoff <- mean(gt.med)+high.sd*sd(gt.med)
        lt.med[lt.med <= low.cutoff]  <- low.cutoff
        gt.med[gt.med > high.cutoff] <- high.cutoff
        x <- c(lt.med,gt.med)
        x
      })
  t@transformationId = transformId
  t
}

shiftLogTransform <- function(transformId) {
  t = new("transform",
      .Data= function(x) {
        mx <- abs(min(x)) + 1
        if (min(x) <= 0) x <- x + mx
        x[x<1e3] <- 1e3
        x <- log10(x)
        x
      })
  t@transformationId = transformId
  t
}

loopLogicleTransform <-
function (transformationId = "defaultLogicleTransform", w = 0, 
    t = 262144, m = 5.5, a = 0, tol = .Machine$double.eps^0.8, 
    maxit = as.integer(5000)) 
{
    if (t <= 0 || m <= 0) 
        stop(" t and m should be greater than zero")
    if (w < 0) 
        stop(" w should be greater than zero")
    p <- if (w == 0) {
      1
    } else {
      uniroot(function(p) -w + 2 * p * log10(p)/(p + 1),
              c(.Machine$double.eps, 10000))$root
    }
    k <- new("transform", 
             .Data = function(x) { 
                       res <- rep(-1,length(x))
                       for (i in 1:length(x)) {
                         res[i] <- .Call("logicle_transform", 
                                         x[i], m, w, p, t, a, tol, maxit)
                         }
                       res
                     })
    k@transformationId <- transformationId
    k
}


cluster.flowMeans <- function(dat, fname, min.clusters, max.clusters,
                              max.attempts, max.streak, 
                              maxn.start, chans, ...)
{
  if (nrow(dat) == 0) {
    stop("Data set is empty")
    close.devices()
  }
  best.guess <- NULL
  gt.max <- rep(1000,length=max.attempts)
  addNoise <- match.call(expand.dots=TRUE)$addNoise

  maxn <- if(length(maxn.start) > 1) 
            maxn.start
          else
            maxn.start:(maxn.start+max.attempts)

  found.max.plus.one <- FALSE
  
  max.found <- mpo <- max.run <- 0
  attempt <- 1
  longest.run <- 1
  nlabs <- last.size <- 0
  tries <- vector("list", max.attempts)

  while (nlabs < min.clusters || nlabs > max.clusters)
  {
    if (attempt > max.attempts) break

    cat("attempt",attempt,"\n")

    cat("  trying MaxN =", maxn[attempt], "...")

    tries[[attempt]] <- try(flowMeans(dat, c(chans), 
                            MaxN=maxn[attempt], ...))

    if (class(tries[[attempt]]) == "try-error") {
      if (identical(addNoise,FALSE)) {
        cat("attempt threw error, trying with noise...")
        tries[[attempt]] <- try(flowMeans(dat, c(chans),
                                MaxN=maxn[attempt],
                                addNoise=TRUE,
                                Standardize=match.call()$Standardize,
                                nstart=match.call()$nstart,
                                NumC=match.call()$NumC))
        if (class(tries[[attempt]]) == "try-error") { 
          cat("still failed, skipping.\n\n")
          attempt <- attempt+1
          next
        }
      } else { 
        cat("attempt threw error, skipping.\n\n")
        attempt <- attempt+1
        next
      }
    }

#    plot(dat, tries[[attempt]], c(chans), pch=".", cex=3, main=fname)
    current.guess <- tries[[attempt]]

    nlabs <- length(unique(tries[[attempt]]@Label))
    cat(" found",nlabs,"clusters.\n")

    if (nlabs > max.clusters) {
      gt.max[attempt] <- nlabs
    }

    if (nlabs > max.found) {
      max.found <- nlabs
      max.attempt <- attempt
    }
    cat("  max.found:",max.found,"max.attempt:",max.attempt,"\n\n")

    if (nlabs == last.size && nlabs > 2) 
      longest.run <- longest.run+1
    else
      longest.run <- 1

    if (longest.run == floor(max.streak) && max.attempts >= 10) {
      cat("Streak of",max.streak,"reached with",nlabs,"clusters.\n\n")
      best.guess <- tries[[attempt]]
      attempt <- attempt+1
      break
    } else if (attempt >= max.attempts) {
      cat("Couldn't find cluster size between",min.clusters,
          "and",max.clusters,"...")
      if (min(gt.max) > max.clusters && min(gt.max) < 1000) {
        cat("choosing the closest minimum of",gt.max[attempt],
            "clusters found at attempt",attempt, ".\n\n")
        attempt <- which.min(gt.max)
        best.guess <- tries[[attempt]]
      } else {
        cat("choosing maximum cluster size found at attempt",
            max.attempt,".\n\n")
        attempt <- max.attempt
        best.guess <- tries[[max.attempt]]
      }
      attempt <- attempt+1
      break
    } else {
      last.size  <- nlabs
      best.guess <- current.guess
    }
    attempt <- attempt+1
  }

  cat("picked MaxN =",maxn[attempt-1],"\n\n")
  best.guess
}

close.devices <- function() {
  for (i in dev.list()) dev.off()
}
