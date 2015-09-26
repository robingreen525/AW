plot.fluortraj <- function(x="time", y, method=sum, cutoff=10,
                           by.well=TRUE, save.plot=TRUE, coop.cols=" ",
                           skip.cols=" ", dat, ylm=NULL, ...)
{
  require(gdata)
  source("~/Documents/Code/R/utilities/logaxis.r")

  if (!identical(skip.cols," "))
    skip.cols <- paste(skip.cols,sep="",collapse="|")
  if (!identical(coop.cols," "))
    coop.cols <- paste(coop.cols,sep="",collapse="|")


  fig.folder <- "figs"
  if(!any(grepl(fig.folder,list.files()))) {
    dir.create(fig.folder)
    Sys.chmod(fig.folder,"0777")
  }

  cols <- list(CFP="blue",YFP="green",RFP="magenta")

  if (identical(by.well,TRUE)) {

    y.method  <- aggregate(dat[[y]],
                 dat[c("well","timepoint","filter")],method)[4]
    x.mean    <- aggregate(dat[[x]],
                 dat[c("well","timepoint","filter")],mean)

    daf <- drop.levels(cbind(x.mean,y.method))
    names(daf)[4:5] <- c(x,y)
  } else {
    daf <- dat
  }

  rat <- list()
  daf <- drop.levels(subset(daf, !grepl(skip.cols, well)))
  for (w in drop.levels(split(daf,daf$well))) {
    y.min <- 1e16 
    y.max <- 0
    for (f in split(w,w$filter)) {
      ff <- na.omit(remove.outliers(f,y,cutoff=cutoff))
      if (max(ff[[y]]) > y.max) y.max <- max(ff[[y]])
      if (min(ff[[y]]) < y.min) y.min <- min(ff[[y]])
    }

    well <- as.character(unique(w$well))

    pname <- paste(fig.folder,"/",well,"_",y,"_",deparse(substitute(method)),"_by_",x,sep="")
    plocname <- paste(fig.folder,"/",well,"_",y,"_",deparse(substitute(method)),"_by_",x,sep="")
    if (identical(save.plot,TRUE)) {
      pdf(paste(pname,".pdf",sep=""))
    } else {
      x11()
    }

    ratio <- NULL
    if (grepl(coop.cols,well)) {
      w <- subset(w,filter != "CFP")
    } else if (identical(by.well,TRUE)) {
      if (identical(save.plot,TRUE)) {
        pdf(paste(pname,"_ratio.pdf",sep=""))
      } else {
        x11()
      }
      ratio <- remove.outliers(w[w$filter=="RFP",],y,cutoff)[[y]] /
               remove.outliers(w[w$filter=="CFP",],y,cutoff)[[y]]

      x.well.mean <- aggregate(w[[x]],w[c("timepoint")],mean)[[2]]
      rat[[well]] <- data.frame(time=x.well.mean,RC.ratio=ratio)
      par(cex=1.5,lwd=3,mar=c(4,4.5,1,1),mex=0.6)

      plot(x.well.mean,ratio,type="n",xlab=x,ylab="RFP:CFP",axes=FALSE,...)
      axis(1); log.axis(2); box();
      points(x.well.mean,ratio,type="o")
      abline(h=1,lty=2)
      abline(h=1/5,lty=2)
      if (identical(save.plot,TRUE)) dev.off()
    }

    if (is.null(ylm)) 
      yl <- c(y.min,y.max)
    else
      yl <- ylm
    dev.set(dev.list()[1])
    par(cex=1.5,lwd=3,mar=c(4,4,1,1),mex=0.6)
    plot(w[[x]],w[[y]],type="n", ylim=yl, axes=FALSE, xlab=x,ylab=y,...)
    axis(1); log.axis(2); box();

    for (f in split(w,w$filter)) {
      filt <- as.character(unique(f$filter))
      if (nrow(f) > 0) {
        f <- remove.outliers(f,y,cutoff=cutoff)
        if (nrow(f)>0 && !(grepl(coop.cols,well) && filt=="CFP")) {
          lines(f[[x]],f[[y]],type="o",col=cols[[filt]])
        }
      }
    }
    if (identical(save.plot,TRUE)) dev.off()
  }
  if (identical(by.well,TRUE)) rat
}

remove.outliers <- function(dat,a,cutoff) {
  med <- median(dat[[a]])
  dat[dat[[a]]/med > cutoff,] <- NA
  dat
}

plot.fluortraj.all <- function(x="time",y="farea", save.plot=TRUE,dat,...)
{
  require(gdata)
  cols <- list(CFP="cyan",YFP="green",RFP="red")
  for (w in split(dat,dat$well)) {
    well <- unique(w$well)
    for (l in split(w,w$location)) {
      loc  <- unique(l$location)
      if (identical(save.plot,TRUE))
        pdf(paste("figs/",well,loc,"_",y,"_by_",x,".pdf",sep=""))
      else
        x11()
      par(cex=1.5,lwd=3,mar=c(4.2,4.2,1,1),mex=0.6)
      if (grepl("[2-4,8]",well)) {
        ss <- subset(l,filter != "CFP")
        plot(ss[[x]],ss[[y]],type="n",xlab=x,ylab=y,...)
      }
      else
        plot(l[[x]],l[[y]],type="n",xlab=x,ylab=y,axes=TRUE,...)
      for (f in split(l,l$filter)) {
        filt <- as.character(unique(f$filter))
        if (!(grepl("[2-4,8]",well) && filt=="CFP"))
          lines(f[[x]],f[[y]],type="o",col=cols[[filt]])
      }
      if (identical(save.plot,TRUE))
        dev.off()
    }
  }
}
