plotgen <- function(data.location="../data/compiled",
                    plot.location="../data/fig",
                    save.plots=FALSE,
                    plate.data.file, tube.data.file, 
                    sample.cols, rows,
                    plot.data=c("od","dsred","venus","cfp"))
{
  # By default, uses the most recent data compiled by datagen.
  if (missing(plate.data.file)) {
    plate.data.file <- 
      rev(list.files(data.location, pattern="all_data",full.names=TRUE))[1]
  }
  if (missing(tube.data.file)) {
    tube.data.file <- 
      rev(list.files(data.location, pattern="tubes",full.names=TRUE))[1]
  }
  comp.date <- gsub("^.*?(\\d+_\\d+).*$", "\\1", plate.data.file, perl=TRUE)
  plot.dir  <- paste(plot.location,"/",comp.date,"_plots",sep="")
  if (save.plots && !file.exists(plot.dir)) dir.create(plot.dir)

  plate.data <- get.sample.data(plate.data.file, sample.cols, rows)
#  tube.data  <- read.delim(tube.data.file)

  for (i in dev.list()) dev.off()

  x11()
  set.par()
  plot.ct(plate.data, sample.cols, rows, "od")

  x11()
  set.par()
  plot.ct(plate.data, sample.cols, rows, c("dsred","venus","cfp"))

#  x11()
#  set.par()
#  plot.ct(tube.data, sample.cols, rows, "od")
}

plot.ct <- function(s.data, s.cols, rows, reads) {
  fluors <- c("dsred", "venus", "cfp")

  if (!any(reads %in% c("od", fluors))) 
    stop("Don't recognize one of those reads")

  source("~/Documents/Code/R/utilities/logaxis.r")

  is.od    <- any(grepl("od", reads))
  x.lab <- "Time (hrs)"
  y.lab <- ifelse(is.od, 
                  expression(Cumulative~OD[600]), 
                  "Fluorescence (AU)")
  ordinate <- paste("cum.", reads, sep="")

  od.clrs    <- c("black","blue","purple","orange","cyan",
                  "green","gold","gray")
  fluor.clrs <- list(cum.dsred="magenta", cum.venus="darkgreen",
                     cum.cfp="blue")
  clrs <- if(is.od) { od.clrs } else { fluor.clrs }
  

  plot(range(s.data$elapsed.hrs), range(s.data[ordinate]),
       log="y", type="n", axes=FALSE, ann=FALSE)
  axis(1); log.axis(2, las=2); box()
  mtext(1, line=2.3, text=x.lab, cex=2)
  mtext(2, line=2.7, text=y.lab, cex=2)

  cnt <- 1
  for (column in s.cols) {
    condition.wells <- paste(rep(rows,each=length(s.cols)), 
                                 column,sep="")
    condition.dat   <- subset(s.data, well %in% condition.wells, 
                              drop=TRUE)
    pcnt <- 1
    for (well in split(condition.dat, condition.dat$well, drop=TRUE)) {
      for (read in ordinate) {
        points(well$elapsed.hrs, well[[read]], type="o",
               col=ifelse(is.od, cnt, fluor.clrs[[read]]),
               pch=pcnt, lty=ifelse(is.od, 1, cnt))
        if(is.od) pcnt <- pcnt + 1
      }
      if(!is.od) pcnt <- pcnt + 1
    }
    cnt <- cnt+1
  }
  if (is.od) {
    legend("bottomright", legend=c("column",s.cols, "row", rows),
           lty=c(-1, rep(1,length(s.cols)),  -1, rep(-1,length(rows))),
           col=c(-1, clrs[1:length(s.cols)], -1, 
                 rep("black",length(rows))), 
           pch=c(-1, rep(-1,length(s.cols)), -1, 1:length(rows)), 
           cex=0.5)
  } else {
    legend("bottomright", legend=c("column",s.cols,"row",rows),
           lty=c(-1,1:length(s.cols),-1,rep(-1,length(rows))), 
           col=c(ifelse(is.od,clrs[1:length(s.cols)],"black"),
                 rep("black",length(rows))), 
           pch=c(-1,rep(-1,length(s.cols)),-1,1:length(rows)), 
           cex=0.5)
  }

}

set.par <- function() {
  par(mar=c(4,4,1,1),mex=0.8,cex=2,lwd=3)
}

get.sample.data <- function(file, cols, rows) {
  dat <- read.delim(file)
  sample.wells <- paste(rep(rows,each=length(cols)), cols,sep="")
  subset(dat, well %in% sample.wells)
}
