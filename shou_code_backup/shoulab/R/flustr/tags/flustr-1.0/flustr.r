# vim: set filetype=r tabstop=2 shiftwidth=2 expandtab :

#  To install required packages:
#     1) Open R with installation privileges.
#     2) > source("http://bioconductor.org/biocLite.R")
#     3) > biocLite("flowCore")
#     4) > biocLite("flowClust")
#     5) > biocLite("flowViz")

flustr <- function(file=NULL, fcs=NULL, 
                   K=4:17, B=100, B.init=50, randomStart=20, 
                   trans=0, nu.est=1,
                   bead.gate=list(fitc=c(1.5e5,Inf),pe=c(1.5e5,Inf)),
                   dust.gate=list(fsc=c(0,3.5e4),ssc=c(0,Inf)), 
                   gate.dead=TRUE, min.increase=NULL, 
                   central.tendency="median",
                   cluster.sig=1, all.bright=4, all.dim=2,
                   save.plot=FALSE, save.debug=FALSE, data.path=".", ...)
{
# Clusters populations of cells.
#   1) Removes beads and dust/debris using rectangular gating.
#   2) Uses flowClust to identify R, Y, and C cells.
#       a) Tries multiple K values and picks the one with the greatest
#           BIC score for further analysis.
#   3) Plots all fluorescent channel combinations and reports ratio of 
#      each type.

  if (is.null(file) && is.null(fcs)) {
    stop("\n  Need either a flowFrame or a path to an .fcs file.")
  }

  if (!(central.tendency %in% c("mean","median")))
    stop("\n  Not a valid indicator of central tendency.")

  require(flowCore)
  require(flowClust)
  require(flowViz)

  fsc <- "FSC-A"; ssc <- "SSC-A"; 
  red <- "PE-A"; dead <- "APC-A"; cyan <- "AmCyan-A"; yellow <- "FITC-A"
  Time <- "Time"
  live.channels <- c(red,yellow,cyan)
  all.channels  <- c(live.channels,dead)
  cluster.channels <- if(gate.dead) live.channels else all.channels
  categories    <- make.categories(live.channels)
  clust.params  <- list(K=K,bic=vector(length=length(K)),
                       B=B,B.init=B.init,randomStart=randomStart,
                       trans=trans,nu.est=nu.est)


  live.dead.filter <- kmeansFilter(filterId="live and dead",
                                   "APC-A"=c("live","dead"))
  clust.filter <- tmixFilter("all clusters", cluster.channels,
                             K=K, B=B, trans=trans, nu.est=nu.est,
                             B.init=B.init, randomStart=randomStart,
                             level=1, criterion="BIC")

  all <- get.data(file,fcs)
  file.name <- sub(".*/(.*)\\.fcs", "\\1", keyword(all)$FIL,perl=TRUE)
  plot.name <- file.name

  debug.path <- paste(data.path,"/",file.name,"_cluster_data.csv",sep="")
  cat(paste("Run started at ", format(Sys.time(),"%H:%M:%S"), "\n", sep=""),
      file=debug.path)

  cat("\nProcessing",file.name,"...\n")
  beads.and.not <- find.beads(all, bead.gate)
  processed <- preprocess(dat=beads.and.not$not.beads, chans=all.channels,
                          save.debug=save.debug, debug.path=debug.path)

  cluster.pop <- processed
  dead.cells <- NULL
  if (gate.dead) {
    live.dead <- split(processed, filter(processed,live.dead.filter))

    dust <- rectangleGate(filterId="dust",
                          "FSC-A" = dust.gate$fsc,
                          "SSC-A" = dust.gate$ssc)
    cluster.pop <- Subset(live.dead$live, !dust)
  }

  cluster.res  <- filter(cluster.pop, clust.filter)
  clust.params$bic <- criterion(cluster.res, "BIC")

  best.set <- pick.set(cluster.res, clust.params$bic, min.increase)

  clusters <- name.clusters(dat=cluster.pop,
                            clust=best.set,
                            include=cluster.channels,
                            bright=all.bright, 
                            dim=all.dim,
                            significant=cluster.sig,
                            ct=central.tendency,
                            clust.params=clust.params,
                            save.debug=save.debug,
                            debug.path=debug.path)

  opar <- par(cex=3,mfrow=c(3,3),mex=0.6,mar=c(4,4,1,1))
  if (gate.dead) {
    opar <-  par(cex=3,mfrow=c(2,2),mex=0.6,mar=c(4,4,1,1))
  }

  if (gate.dead) {
    plot(cluster.pop, c(fsc,dead), smooth=FALSE,col="purple")
    opar <- par(new=TRUE)
    plot(live.dead$dead, c(fsc,dead), smooth=FALSE,col="black", axes=FALSE,
         ann=FALSE)
  }
  par <- opar

  plot.clusters(dat=clusters, channels=cluster.channels, par=par)

  x11()
  if (gate.dead) {
    par(cex=3,mfrow=c(2,2),mex=0.6,mar=c(4,4,1,1))
  } else {
    par(cex=3,mfrow=c(3,3),mex=0.6,mar=c(4,4,1,1))
  }
  plots(best.set, cluster.pop, channels=cluster.channels, par=par)

  if (length(clust.params$bic) > 1) {
    plot(clust.params$bic ~ K,type="o",axes=FALSE, 
         xlab="k (# of clusters)", ylab="BIC")
    axis(1,at=seq(min(K),max(K))); axis(2); graphics::box()
  }
  if (identical(save.plot,TRUE)) {
    savePlot(filename=paste(data.path,"/", 
                            plot.name,"_unmerged.png",sep=""))
    dev.off()
    savePlot(filename=paste(data.path,"/",plot.name,".png",sep=""))
    dev.off()
  }

  res <- make.result(file.name=file.name, cells=cluster.pop, 
                     dead=live.dead$dead, beads=beads.and.not$beads,
                     fluor=clusters)

  print(res)
  if (save.debug) {
    cat("\n", file=debug.path, append=TRUE)
    write.table(data.frame(res),file=debug.path,sep=";",row.names=FALSE, 
                append=TRUE)

    cat("\n", file=debug.path, append=TRUE)

    cat(paste("Run completed at ", format(Sys.time(),"%H:%M:%S"),"\n",
        sep=""), file=debug.path, append=TRUE)
  }
  res
}

preprocess <- function(dat, chans, save.debug, debug.path) {
  spill <- keyword(dat)$SPILL
  if (is.null(spill))
    cat("\nNo spillover matrix found for this data\n")
  else
    dat <- compensate(x=dat,spillover=spill)

  cat("  Picking transformation parameters...\n")
  processed <- transform.data(dat, chans, save.debug,debug.path)
  processed
}

find.beads <- function(dat, bead.gate) {
  bead.filter <- rectangleGate(filterId="beads",
                               "FITC-A" = bead.gate$fitc,
                               "PE-A"   = bead.gate$pe)
  not.beads <- Subset(dat, !bead.filter)
  beads     <- Subset(dat, bead.filter)
  list(not.beads=not.beads, beads=beads)
}


plot.clusters <- function(dat, channels, par) {
  cols <-list(`FITC-A`="green", `PE-A`="red", `AmCyan-A`="blue",
              `APC-A`="black",
              all.bright="gold", all.dim="gray",
              `FITC-A&PE-A`="orange", `PE-A&FITC-A`="orange",
              `FITC-A&AmCyan-A`="cyan", `AmCyan-A&FITC-A`="cyan",
              `PE-A&AmCyan-A`="purple", `AmCyan-A&PE-A`="purple",
              moribund="pink")
  pchs <-list(`FITC-A`='.', `PE-A`='.', `AmCyan-A`='.',`APC-A`='.',
              all.bright=".", all.dim=".",
              `FITC-A&PE-A`=".", `PE-A&FITC-A`=".",
              `FITC-A&AmCyan-A`=".", `AmCyan-A&FITC-A`=".",
              `PE-A&AmCyan-A`=".", `AmCyan-A&PE-A`=".",
              moribund=".")

  for (i in seq_along(channels)) {
    for (j in i:length(channels)) {
      if (i != j) {
        c1.name <- channels[i]; c2.name <- channels[j]
#cat(c1,"vs",c2,"\n")
        c1.min <- c2.min <- 1
        c1.max <- c2.max <- 1
        for (k in seq_along(dat)) {
          exdat <- exprs(dat[[k]])
          c1 <- exdat[,c1.name]; c2 <- exdat[,c2.name]
          c1.range <- range(c1)
          c2.range <- range(c2)
#cat("c1:",c1.name,"range:",c1.range,"\n")
#          cat("c2:",c2.name,"range:",c2.range,"\n")

          if (min(c1.range) < c1.min) c1.min <- min(c1.range)
          if (min(c2.range) < c2.min) c2.min <- min(c2.range)
          if (max(c1.range) > c1.max) c1.max <- max(c1.range)
          if (max(c2.range) > c2.max) c2.max <- max(c2.range)
        }
        for (k in seq_along(dat)) {
          frame <- dat[[k]]
          name <- names(dat)[k]
          if (grepl("moribund",name)) name <- "moribund"
          plot(frame,c(c1.name,c2.name),smooth=FALSE, col=cols[[name]],
               pch=pchs[[name]],
               xlim=c(c1.min,c1.max),ylim=c(c2.min,c2.max),
               axes=FALSE,ann=FALSE,cex=3)
          par(new=TRUE)
        }
        axis(1); axis(2)
        mtext(c1.name,side=1,line=2.7,cex=0.8)
        mtext(c2.name,side=2,line=2.7,cex=0.8)
        graphics::box()
        par(new=FALSE)
      }
    }
  }
}

plots <- function(filter, all.data, channels, par) {
  for (i in seq_along(channels)) {
    for (j in i:length(channels)) {
      if (i != j) {
        ichan <- channels[i]; jchan <- channels[j]
#       cat(ichan," vs ",jchan,"\n")
        plot(filter,data=all.data, subset=c(i,j), cex=3, ellipse=FALSE,
             pch.outliers="x",cex.outliers=1)
      }
    }
  }
}

make.result <- function(file.name, cells, beads, dead, fluor)
{
  res <- NULL
  categories <- list()

  cell.count <- nrow(exprs(cells))
  bead.count <- nrow(exprs(beads))

  dead.count <- 0
  if (!is.null(dead)) dead.count <- nrow(exprs(dead))

  for (i in 1:length(fluor)) {
    chan <- names(fluor)[i]
    if (is.null(categories[[chan]])) {
      categories[[chan]] <- nrow(exprs(fluor[[i]]))
    } else {
      categories[[chan]] <- categories[[chan]] + nrow(exprs(fluor[[i]]))
    }
  }
  categories <- data.frame(categories,check.names=FALSE)

  total <- sum(categories)
  if (!is.null(dead)) total <- total+dead.count

  res$counts <- data.frame(file=file.name,
                           cells=cell.count,
                           beads=bead.count,
                           dead=dead.count,
                           categories,
                           check.names=FALSE)
  res$ratios <- data.frame(file=file.name, 
                           res$counts[2:ncol(res$counts)]/total,
                           check.names=FALSE)
#  print(res)
  res
}

name.clusters <- function(dat,clust,include,bright,dim,significant,
                          ct, save.debug,debug.path, clust.params) 
{
  res <- list()
  clusters <- split(dat,clust)
  means <- lapply(clusters, function(x) each_col(x[,include],mean))
  size  <- lapply(clusters, function(x) nrow(exprs(x$Time)))
  meds  <- lapply(clusters, function(x) each_col(x[,include],median))
  sds   <- lapply(clusters, function(x) each_col(x[,include],sd))
  nms   <- sapply(means,function(x) { names(x[which.max(x)]) })

  dat <- data.frame(row.names=FALSE)
  for (i in 1:length(means)) {
    dat <- rbind(dat, data.frame(group=i,channel=names(means[[i]]),
                                 size=size[[i]],
                                 mean=means[[i]], median=meds[[i]],
                                 sd=sds[[i]]))
  }

  dat <- dat[order(dat$group, dat[[ct]], decreasing=TRUE),]
  dat <- within(dat, {
                      ctplus   <- dat[[ct]]+significant*sd
                      ctminus  <- dat[[ct]]-significant*sd
                     })

  moribunds <- 1
  clust.names <- NULL
  for (grp in split(dat,dat$group)) {
#    cat("Group",unique(grp$group),"\n  ")
    if (all(grp[[ct]] < dim)) {
#      cat("1")
      clust.names <- c(clust.names, "all.dim")
    } else if (all(grp[[ct]] > bright)) {
#      cat("2")
      clust.names <- c(clust.names, "all.bright")
    } else if (grp[1,][[ct]] > bright && grp[2,][[ct]] > bright) {
#      cat("3")
      clust.names <- c(clust.names,
                       double.name(grp[1,]$channel,grp[2,]$channel))
    } else if ( grp[1,]$ctminus > grp[2,]$ctplus ) {
#      cat("4")
      clust.names <- c(clust.names, as.character(grp[1,]$channel))
    } else if (grp[1,]$ctminus < grp[2,]$ctplus && 
               grp[2,]$ctminus > grp[3,]$ctplus)
    {
#      cat("5")
      clust.names <- c(clust.names,
                       double.name(grp[1,]$channel,grp[2,]$channel))
    } else if (grp[1,]$ctminus < grp[2,]$ctplus && 
               grp[2,]$ctminus < grp[3,]$ctplus)
    {
      if (mean(grp[[ct]]) > bright) {
#        cat("6a")
        clust.names <- c(clust.names, "all.bright")
      } else {
#        cat("6b")
        clust.names <- c(clust.names, paste("moribund.",moribunds,sep=""))
        moribunds <- moribunds + 1
      }
    } else {
#      cat("7")
      clust.names <- c(clust.names, "uncategoriezed")
    }
#    cat("\n")
  }
  #print(clust.names)

  names(means) <- clust.names
  names(clusters) <- clust.names
  dat <- data.frame(name=rev(rep(clust.names,each=length(include))), dat)
  means <- data.frame(means,check.names=FALSE)
  clust.params <- data.frame(clust.params,check.names=FALSE)

  if (save.debug) {
    write.table(clust.params, file=debug.path, sep=";",
                row.names=FALSE, append=TRUE)
    cat("\nusing ",ct," and ",significant,"*sd to name groups.\n", sep="",
        file=debug.path, append=TRUE)
    write.table(dat, file=debug.path, sep=";",row.names=FALSE, append=TRUE)
  }
  print(dat)
  print(means)
  clusters
}

double.name <- function(n1,n2) {
  paste(as.character(n1),"&",as.character(n2),sep="")
}


pick.set <- function(result, bics, min.inc) {
  if (length(bics)==1) 
    return(result)
  else
    k <- pick.k(bics,min.inc)
  return(result[[k]])
}

pick.k <- function(bics,min.inc) {
  ratios <- NULL
  nBics <- length(bics)
  for (i in 1:(nBics-1)) {
    ratios[i] <- (bics[i]-bics[i+1])/bics[i]
  }
  gmi  <- ratios[ratios>min.inc]
  lgmi <- which(ratios == gmi[length(gmi)]) + 1


  if (is.null(min.inc))
    return(which.max(bics))
  else {
#    cat("bics: ", bics,"\n")
#    cat("ratios: ", ratios,"\n")
#    cat("last > min.inc: ",lgmi,"\n")
    return(lgmi)
  }
}

get.data <- function(file, fcs) {
  if (is.null(file) && !(is.null(fcs))) {
    all <- fcs
  } else if (is.null(fcs) && !(is.null(file))) {
    all <- read.FCS(file,min.limit=NULL)
  } else { stop("\n\tNeed a file name or a flowFrame!\n") }
}


transform.data <- function(dat, chans, save.debug, debug.path) {
  if (nrow(exprs(dat$Time))==0) return(0)

  transf <- list()
  all.params <- list()

  max.lt.zero <- 0
  w <- 1
  t <- 2^18
  for (cn in colnames(dat)) {
    if (cn %in% chans) {
      params <- data.frame(channel=cn,logicle.params(dat[,cn]),
                           check.names=FALSE)
      if (params$lt.zero > max.lt.zero) {
        max.lt.zero <- params$lt.zero
        w <- params$w
        m <- params$m
      }
      all.params[[cn]] <- params
    }
  }
  all.params.df <- do.call("rbind",all.params)
  print(all.params.df)
  for (cn in chans) {
    transf[[cn]] <- 
      if (identical(cn,"APC-A"))
        logicleTransform(cn, t=t, w=w, m=m)
      else
        logicleTransform(cn, t=t, w=all.params[[cn]]$w, 
                         m=all.params[[cn]]$m)
  }

  picked <- paste("picked   w=",w,"\n")
  if (save.debug) {
    write.table(all.params.df, file=debug.path, sep=";", 
                row.names=FALSE, append=TRUE)
    cat(picked, file=debug.path, append=TRUE)
  }

  cat(picked)

#  transf <- logicleTransform(cn, w=w, t=t, m=m)
  tf <- transformList(chans, transf)

  transDat <- transform(dat, tf)
  transDat
}

logicle.params <- function(dat) {
  exdat <- exprs(dat)
  exdat <- exdat[exdat>quantile(exdat,0.001)]

  min.val <- min(exdat)
  max.val <- max(exdat)
  lt.zero <- exdat[exdat<0]
  n.lt.zero <- length(lt.zero)

  t <- 2^18
  r <- quantile(exdat, 0.05)
  m <- log10(max.val) #quantile(exdat,0.999))
  w <- (m-log10(t/abs(r)))/2

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
