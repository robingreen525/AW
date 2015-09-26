# vim: set filetype=r tabstop=2 shiftwidth=2 expandtab :

#  To install required packages:
#     1) Open R with installation privileges.
#     2) > source("http://bioconductor.org/biocLite.R")
#     3) > biocLite(c("flowCore","flowClust","flowViz"))

flustr <- function(file=NULL, fcs=NULL, 
                   bead.gate=list(fitc=c(1.5e5,Inf),pe=c(1.5e5,Inf)),
                   dust.gate=list(fsc=c(-Inf,3.5e4),ssc=c(-Inf,Inf)), 
                   bead.dilution=10, beads.per.ml=7.8e6,
                   all.bright=4, all.dim=2, 
                   fsc.sig=0.5, dead.sig=1, cluster.sig=1.5,
                   save.plot=FALSE, save.debug=FALSE, data.path=".", ...)
{

  if (is.null(file) && is.null(fcs)) {
    stop("\n  Need either a flowFrame or a path to an .fcs file.")
  }

  require(flowCore)
  require(flowMeans)
  require(flowViz)

  for (i in dev.list()) dev.off()

  fsc <- "FSC-A"; ssc <- "SSC-A"; 
  red <- "PE-A"; dead <- "APC-A"; cyan <- "AmCyan-A"; yellow <- "FITC-A"
  Time <- "Time"
  live.channels <- c(red,yellow,cyan)
  all.channels  <- c(live.channels,dead)
  categories    <- make.categories(live.channels)

  all <- get.data(file,fcs)
  file.name <- sub(".*/(.*)\\.fcs", "\\1", keyword(all)$FIL,perl=TRUE)
  plot.name <- file.name

  debug.path <- paste(data.path,"/",file.name,"_cluster_data.csv",sep="")
  if (identical(save.debug,TRUE)) {
    cat(paste("Run started at ", format(Sys.time(),"%H:%M:%S"), "\n", 
        sep=""), file=debug.path)
  }

  cat("\nProcessing",file.name,"...\n")
  beads.and.not <- find.beads(all, bead.gate)
  processed <- preprocess(dat=beads.and.not$not.beads,
                          chans=c(fsc,ssc,all.channels),
                          save.debug=save.debug, debug.path=debug.path)
  processed.dat <- exprs(processed)

  min.fsc <- min(exprs(processed[,c(fsc)]))
  max.fsc <- max(exprs(processed[,c(fsc)]))
  min.dead <- min(exprs(processed[,c(dead)]))
  max.dead <- max(exprs(processed[,c(dead)]))

#  plot(processed, c(fsc,dead), 
#       xlim=c(min.fsc,max.fsc), ylim=c(min.dead,max.dead),
#       main=file.name, smooth=FALSE, pch=".", cex=2, col="orange")
#  contour(processed, c(fsc,dead), add=TRUE)

  cat("Initial clustering...\n")
  first.cut <- cluster.flowMeans(dat=processed, 
                                 fname=file.name,
                                 min.clusters=4, max.clusters=11,
                                 maxn.start=20,
                                 max.attempts=50, max.streak=15,
                                 chans=c(fsc,dead),
                                 addNoise=FALSE, Standardize=FALSE, 
                                 nstart=10)

  nlabs <- length(unique(first.cut@Label))

  plot(processed,first.cut, c(fsc,dead), main=file.name, pch=".", cex=3)

  first.cut.ordered <- order.clusters(dat=processed, 
                                      cluster.res=first.cut,
                                      chans=c(fsc,dead))

  initial.pops <- find.and.plot.initial.pops(first.cut.ordered, 
                                             fsc.sig=fsc.sig,
                                             dead.sig=dead.sig,
                                             chans=list(fsc=fsc,dead=dead),
                                             nclusters=nlabs, ct=mean)
  dead.pops    <- NULL
  cluster.pops <- NULL
  low.fsc.pops <- NULL
  for (i in 1:length(initial.pops)) {
    tags <- initial.pops[[i]]
    if (is.null(tags))
      cluster.pops <- c(cluster.pops, i)
    else if ("dead" %in% tags)
      dead.pops <- c(dead.pops, i)
    else if ("low.fsc" %in% tags )
      low.fsc.pops <- c(low.fsc.pops, i)
  }

  cluster.pop <- processed[first.cut@Label %in% cluster.pops,]
  dead.pop    <- processed[first.cut@Label %in% dead.pops,]
  low.fsc.pop <- processed[first.cut@Label %in% low.fsc.pops,]


  cluster.pop <- cluster.pop
  cat("Clustering live cells...\n")

  cluster.res <- cluster.flowMeans(dat=cluster.pop, 
                                   fname=file.name,
                                   min.clusters=15, max.clusters=25,
                                   maxn.start=50,
                                   max.attempts=1, max.streak=51,
                                   chans=live.channels,
                                   addNoise=FALSE, Standardize=FALSE, 
#                                   OrthagonalResiduals=TRUE,
#                                   Update="Means",
                                   nstart=10,
                                   NumC=15)

  cluster.data <- order.clusters(dat=cluster.pop, 
                                 cluster.res=cluster.res,
                                 chans=live.channels)
  print(cluster.data)
  cluster.data <- name.fmclusters(dat=cluster.data, significant=cluster.sig,
                                  bright=all.bright, dim=all.dim,
                                  save.debug=save.debug, 
                                  debug.path=debug.path)

  x11()
  opar <-  par(mar=c(4.2,4.2,3,1), cex=3,mfrow=c(2,2),mex=0.6)
  plot(processed, c(fsc,dead), col="white", smooth=FALSE,
       xlim=c(min.fsc,max.fsc), ylim=c(min.dead,max.dead))
  par(new=TRUE)
  if (nrow(cluster.pop)>0) {
    plot(cluster.pop, c(fsc,dead) ,pch=".", cex=2, smooth=FALSE, 
         xlim=c(min.fsc,max.fsc), ylim=c(min.dead,max.dead),
        col="purple", ann=FALSE, axes=FALSE)
    contour(cluster.pop, c(fsc,dead), add=TRUE)
    par(new=TRUE)
  }
  if (nrow(dead.pop) > 0) {
    plot(dead.pop, c(fsc,dead) ,pch=".", cex=2, smooth=FALSE, 
         xlim=c(min.fsc,max.fsc), ylim=c(min.dead,max.dead),
        col="black", ann=FALSE, axes=FALSE)
    contour(dead.pop, c(fsc,dead), add=TRUE, col="orange")
    par(new=TRUE)
  }
  if (nrow(low.fsc.pop) > 0) {
    plot(low.fsc.pop, c(fsc,dead) ,pch=".", cex=2, smooth=FALSE, 
         xlim=c(min.fsc,max.fsc), ylim=c(min.dead,max.dead),
        col="tan", ann=FALSE, axes=FALSE)
    contour(low.fsc.pop, c(fsc,dead), add=TRUE)
  }
  par(new=FALSE)
  plot.fmclusters(dat=cluster.pop, clust.res=cluster.res, 
                  clust.dat=cluster.data)
  mtext(file.name,line=-1.6,outer=TRUE)
  par <- opar
  if (identical(save.plot,TRUE)) {
    savePlot(filename=paste(data.path,"/",plot.name,".png",sep=""))
    dev.off()
    savePlot(filename=paste(data.path,"/",plot.name,"_initial.png",sep=""))
    dev.off()
    savePlot(filename=paste(data.path,"/", 
                            plot.name,"_unmerged.png",sep=""))
    dev.off()
  }

  res <- make.result(file.name=file.name, cells=nrow(cluster.pop), 
                     low.fsc=nrow(low.fsc.pop), dead=nrow(dead.pop), 
                     beads=nrow(beads.and.not$beads), fluor=cluster.data,
                     bead.dilution=bead.dilution, beads.per.ml=beads.per.ml)

  if (save.debug) {
    cat("\n", file=debug.path, append=TRUE)
    write.table(data.frame(res),file=debug.path,sep=";",row.names=FALSE, 
                append=TRUE)

    cat("\n", file=debug.path, append=TRUE)

    cat(paste("Run completed at ", format(Sys.time(),"%H:%M:%S"),"\n",
        sep=""), file=debug.path, append=TRUE)
  }
  print(res)
  res
}

plot.fmclusters <- function(dat, clust.res, clust.dat) {
  cols <-list(`FITC-A`="green", `PE-A`="red", `AmCyan-A`="blue",
              `APC-A`="black",
              all.bright="gold", all.dim="gray",
              `FITC-A&PE-A`="brown4", `PE-A&FITC-A`="brown4",
              `FITC-A&AmCyan-A`="cyan", `AmCyan-A&FITC-A`="cyan",
              `PE-A&AmCyan-A`="purple", `AmCyan-A&PE-A`="purple",
              moribund="pink")
  pchs <-list(`FITC-A`='.', `PE-A`='.', `AmCyan-A`='.',`APC-A`='.',
              all.bright=".", all.dim=".",
              `FITC-A&PE-A`=".", `PE-A&FITC-A`=".",
              `FITC-A&AmCyan-A`=".", `AmCyan-A&FITC-A`=".",
              `PE-A&AmCyan-A`=".", `AmCyan-A&PE-A`=".",
              moribund=".")

  chans <- sort(as.character(unique(clust.dat$channel)))

  for (i in 1:(length(chans)-1)) {
    for (j in (i+1):length(chans)) {
      xrange <- range(exprs(dat[,chans[i]]))
      yrange <- range(exprs(dat[,chans[j]]))
      plot(dat,c(chans[i],chans[j]),col="white", cex=3, smooth=FALSE)
      par(new=TRUE)
      mbids <- data.frame()
      
      sizes <- aggregate(clust.dat$n,list(name=clust.dat$name),sum)
      sizes <- sizes[order(sizes$x, decreasing=TRUE),]
      for (chan in sizes$name) {
        by.name <- subset(clust.dat, name==chan)
        nme <- as.character(unique(by.name$name))
        labels <- unique(by.name[by.name$name==nme,]$label)

        if (grepl("moribund",nme)) {
          id <-strsplit(nme,"")[[1]][regexpr('\\d+',nme,perl=TRUE)]
          x.med <- subset(clust.dat,label %in% labels & 
                   channel %in% chans[i])$median
          y.med <- subset(clust.dat,label %in% labels & 
                   channel %in% chans[j])$median
          mbids <- rbind(mbids,data.frame(id=id,x.med=x.med,y.med=y.med))
          nme <- "moribund"
        }
        plot(dat[clust.res@Label %in% labels,], 
             c(chans[i],chans[j]), col=cols[[nme]], pch=pchs[[nme]],
             xlim=xrange, ylim=yrange,
             smooth=FALSE, axes=FALSE, ann=FALSE, cex=3)
        par(new=TRUE)
      }
      for (m in 1:nrow(mbids)) {
        text(x=mbids[m,]$x.med, y=mbids[m,]$y.med,
             labels=mbids[m,]$id, cex=1.5)
        par(new=TRUE)
      }
      par(new=FALSE)
    }
  }
}

name.fmclusters <- function(dat, significant, bright, dim, save.debug,
                            debug.path)
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
  print(dat)
  if (save.debug) {
    write.table(dat, file=debug.path, sep=";", row.names=FALSE, append=TRUE)
  }
  dat
}

find.and.plot.initial.pops <- function(dat, fsc.sig, dead.sig,
                                       chans, nclusters, ct)
{
  fsc.dat  <- dat[dat$channel==chans$fsc,]
  dead.dat <- dat[dat$channel==chans$dead,]

  fsc.ct    <- mean(fsc.dat$median)
  fsc.error <- fsc.sig*sqrt(sum(fsc.dat$mad^2))
  fsc.plus  <- fsc.ct + fsc.error
  fsc.minus <- fsc.ct - fsc.error

  dead.ct    <- mean(dead.dat$median) #w.mean(dead.dat$median,dead.dat$n)
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

  cluster.assignments <- vector("list",nclusters)
  for (cluster in split(dat,dat$label)) {
    label <- unique(cluster$label)
    if (cluster[cluster$channel %in% chans$fsc,]$median < fsc.minus) {
      cluster.assignments[[label]] <- 
        c(cluster.assignments[[label]], "low.fsc")
    }
    if (cluster[cluster$channel %in% chans$dead,]$median > dead.plus) {
      cluster.assignments[[label]] <- 
        c(cluster.assignments[[label]], "dead")
    }
  }
#  print(cluster.assignments)
  cluster.assignments
}

w.mean <- function(x,w) {
  return(sum(x*(w/sum(w))))
}

preprocess <- function(dat, chans, save.debug, debug.path) {
  spill <- keyword(dat)$SPILL
  if (is.null(spill))
    cat("\nNo spillover matrix found for this data\n")
  else
    dat <- compensate(x=dat,spillover=spill)

  cat("  Picking transformation parameters...\n")
  processed <- transform.data(dat, chans=chans, save.debug,debug.path)
  processed
}

order.clusters <- function(dat, cluster.res, chans) {
  nlabs <- length(unique(cluster.res@Label))
  res <- data.frame()
  for (lab in 1:nlabs) {
    for (cn in chans) {
      med <- median(exprs(dat[,cn])[cluster.res@Label==lab,])
      mad <- mad(exprs(dat[,cn])[cluster.res@Label==lab,])
      n   <- nrow(dat[cluster.res@Label==lab,])
      res <- rbind(res, data.frame(label=lab, channel=cn, 
                                   median=med, mad=mad, n=n))
    }
  }

  nms <- data.frame()
  for (cn in chans) {
    ss <- res[res$channel %in% cn,]
    nms <- rbind(nms, data.frame(ss[order(ss$median, decreasing=TRUE),],
                      rank=1:nrow(ss)))
  } 
  nms
}

find.beads <- function(dat, bead.gate) {
  bead.filter <- rectangleGate(filterId="beads",
                               "FITC-A" = bead.gate$fitc,
                               "PE-A"   = bead.gate$pe)
  not.beads <- Subset(dat, !bead.filter)
  beads     <- Subset(dat, bead.filter)
  list(not.beads=not.beads, beads=beads)
}

make.result <- function(file.name, cells, low.fsc, beads, dead, fluor,
                        bead.dilution, beads.per.ml)
{
  res <- NULL
  categories <- list()

  for (nme in unique(fluor$name)) {
    categories[[nme]] <- sum(unique(fluor[fluor$name==nme,]$n))
  }

  categories <- data.frame(categories,check.names=FALSE)

  total <- sum(categories)
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
#    cat("Group",unique(grp$group),"\n  ")
  if (all(grp[[ct]] < dim)) {
#      cat("1")
    return("all.dim")
  } else if (all(grp[[ct]] > bright)) {
#      cat("2")
    return("all.bright")
  } else if (grp[1,][[ct]] > bright && grp[2,][[ct]] > bright) {
#      cat("3")
    return(double.name(grp[1,]$channel,grp[2,]$channel))
  } else if ( grp[1,]$ctminus > grp[2,]$ctplus ) {
#      cat("4")
    return(as.character(grp[1,]$channel))
  } else if (grp[1,]$ctminus < grp[2,]$ctplus && 
             grp[2,]$ctminus > grp[3,]$ctplus)
  {
#      cat("5")
    if (mean(grp[c(1,2),][[ct]]) > bright) {
      return(double.name(grp[1,]$channel,grp[2,]$channel))
    } else {
      return("moribund")
    }
  } else if (grp[1,]$ctminus < grp[2,]$ctplus && 
             grp[2,]$ctminus < grp[3,]$ctplus)
  {
    if (mean(grp[[ct]]) > bright) {
#        cat("6a")
      return("all.bright")
    } else {
#        cat("6b")
      return("moribund")
    }
  } else {
#      cat("7")
    return("uncategoriezed")
  }
}

double.name <- function(n1,n2) {
  paste(as.character(n1),"&",as.character(n2),sep="")
}

get.data <- function(file, fcs) {
  if (is.null(file) && !(is.null(fcs))) {
    all <- fcs
  } else if (is.null(fcs) && !(is.null(file))) {
    all <- read.FCS(file,min.limit=NULL)
  } else { stop("\n\tNeed a file name or a flowFrame!\n") }
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

transform.data <- function(dat, chans, save.debug, debug.path) {
  if (nrow(exprs(dat$Time))==0) return(0)

  transf <- list()
  all.params <- list()

  for (cn in chans) {
    transf[[cn]] <- 
      if (identical(cn,"APC-A")) {
#        print(summary(dat))
#        tfl <- list()
#        tfl[[cn]] <- 
#          splitEndsTransform(cn, high.sd=200, low.sd=100)
#          shiftLogTransform(cn)
#        tf1 <- transformList("APC-A",tfl)
#        dat <- transform(dat, tf1)
#        print(summary(dat))

        params <- data.frame(channel=cn,logicle.params(dat[,cn]),
                             check.names=FALSE)
        all.params <- rbind(all.params,params)
        logicleTransform(cn, t=params$t, w=params$w, m=params$m)
      } else if (cn %in% c("SSC-A","FSC-A")) {
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
    write.table(all.params, file=debug.path, sep=";", 
                row.names=FALSE, append=TRUE)
  }
#  transf <- logicleTransform(cn, w=w, t=t, m=m)
  tf <- transformList(chans, transf)

  transDat <- transform(dat, tf)
  transDat
}

logicle.params <- function(dat) {
  exdat <- exprs(dat)
#  exdat <- exdat[exdat < mean(exdat)+10*sd(exdat)]
  min.val <- min(exdat)
  max.val <- max(exdat)
  lt.zero <- exdat[exdat<0]
  n.lt.zero <- length(lt.zero)

  t <- 2^18
  r <- quantile(lt.zero, 0.05)

  m <- log10(max.val)
  w <- abs((m-log10(t/abs(r)))/2)

#  sink(colnames(dat))
#  cat("colname:",colnames(dat),"\n")
#  cat("intensities < 0:\n",lt.zero,"\n")
#  cat("  min.val:", min.val, "\n")
#  cat("  lt zero: ",n.lt.zero, " (",100* n.lt.zero/nrow(exdat),"%)",
#      " p: ",p,"\n",sep="")
#  sink()
# cat("  t=",t," w=",w," m=",m,"\n")
  data.frame(lt.zero=n.lt.zero, w=w, t=t, m=m)
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

cluster.flowMeans <- function(data, fname, min.clusters, max.clusters,
                              max.attempts, max.streak, 
                              maxn.start, chans, ...)
{
  if (nrow(data) == 0) stop("Data set is empty")
  first.cut <- NULL
  gt.max <- rep(1000,length=max.attempts)

  maxn <- maxn.start:(maxn.start+max.attempts)
  found.max.plus.one <- FALSE
  
  max.found <- mpo <- max.run <- 0
  attempt <- longest.run <- 1;
  nlabs <- last.size <- 0
  tries <- vector("list", max.attempts)
  x11()
  while (nlabs < min.clusters || nlabs > max.clusters) {

    cat("attempt",attempt,"\n")
    cat("  trying MaxN =", maxn[attempt], "...")

    tries[[attempt]] <- 
      try(flowMeans(data, c(chans), MaxN=maxn[attempt], ...))
    if (class(tries[[attempt]]) == "try-error") {
      cat("attempt threw error, skipping.\n")
      attempt <- attempt+1
      next
    }

    plot(data, tries[[attempt]], c(chans), pch=".", cex=3, main=fname)
    first.cut <- tries[[attempt]]

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

    if (nlabs == last.size && nlabs != 2) 
      longest.run <- longest.run+1
    else
      longest.run <- 1

    if (longest.run == floor(max.streak) && max.attempts >= 10) {
      first.cut <- tries[[attempt]]
      attempt <- attempt+1
      cat("Streak of",max.streak,"reached with",nlabs,"clusters.\n\n")
      break
    }

    if (attempt >= max.attempts) {
      cat("Couldn't find cluster size between",min.clusters,
          "and",max.clusters,"...")
      if (min(gt.max) > max.clusters && min(gt.max) < 1000) {
        attempt <- which.min(gt.max)
        cat("choosing the closest minimum of",gt.max[attempt],
            "clusters found at attempt",attempt, ".\n\n")
        first.cut <- tries[[attempt]]
      } else {
        attempt <- max.attempt
        cat("choosing maximum cluster size found at attempt",
            attempt,".\n\n")
        first.cut <- tries[[max.attempt]]
      }
      break
    }
    last.size <- nlabs
    attempt <- attempt + 1
  }
  cat("picked MaxN =",maxn[attempt-1],"\n")
  first.cut
}
