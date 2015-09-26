read.pw <- function(file) {
  # read.pw -- Used to read in data produced by powerwave machines.
  # Expects each chunk of data to be an entire plate read at a particular
  # time ("plate" format).

  lines <- readLines(file)

  plate <- NULL
  res   <- NULL
  l <- 0
  while (!is.na(lines[l<-l+1])) {
    x <- unlist(strsplit(lines[l], "\t"))

    # A line that starts with a time indicates the data is in
    # the wrong format
    if ( grepl('^[0-9][0-9]:',x[1]) ) 
      stop("read.pw currently only supports plate format.")

    if ( length(x)>1 && any(grepl('[A-H]',x[1]) && sum(x=="")<2) ) {
      if (identical(x[1], "H")) {
        plate <- rbind( plate, as.numeric(x[2:13]) )
        rownames(plate) <- LETTERS[1:8]
        res <- c(res, list(plate))
        plate <- NULL
      } else {
        plate <- rbind( plate, as.numeric(x[2:13]) )
      }
    }
  }
  res
}

max.od <- function(dat, wells, blanks, delt=NA, time.range=NA)
{
  if (is.na(delt)) stop("Need a time step.")
  time.range <- seq(min(time.range*60/10), max(time.range*60/10))
  dat.sub <- lapply(dat, function(x) as.vector(x[time.range]))

  times <- seq(1, length(dat[time.range]))*delt/60
  ods <- ods.times(dat[time.range], wells, blanks)
  maxes <- matrix(0, nrow=length(wells$rows), ncol=length(wells$cols),
                  dimnames=list(c(wells$rows),c(wells$cols)))
  for (i in seq_along(wells$cols)) {
    for (j in seq_along(wells$rows)) {
      maxes[j,i] = max(unlist(ods[paste(wells$rows[j],wells$cols[i],sep="")]))
    }
  }
  plot.well(dat[time.range], wells, blanks, delt)

  maxes
}

plot.well <- function(dat, wells=list(rows=LETTERS[1:8],cols=1:12),
                      blanks, shift=0, timestep.min,
                      od.range, zoom=FALSE, fit=FALSE, 
                      A, r, lag=0, ...) 
{
  source("/home/ajwaite/Documents/Code/R/lag_log.R")

  par(mfrow=c(length(wells$rows), length(wells$cols)), mex=0.5)

  times <- (seq(1, length(dat))*timestep.min/60) + shift
  ods <- ods.times(dat, wells, blanks)

  if (missing(od.range)) {
    od.range <- c(0, 2)
  }
  if (missing(A) &! missing(od.range)) A <- od.range[1]/10
  fits <- NULL

  for (i in seq_along(ods)) {
    well <- ods[[i]]
    fit.range <- which(well>od.range[1] & well<od.range[2])
    well.ss <- well[fit.range]
    time.ss <- times[fit.range]

    if (identical(zoom, TRUE)) {
      plot(well.ss ~ time.ss, xlab="Time (hours)", ylab="OD", log="y", ...)
    } else {
      plot(well ~ times, xlab="Time (hours)", ylab="OD", log="y", ...)
    }

    if (missing(A) && missing(od.range)) A <- well.ss[1]
    if (identical(fit, TRUE)) {
      if (lag == 0) {
        f <- try(nls( well.ss ~ A*exp(r*time.ss),
                     start=list(A=A, r=r),
                     control=list(warnOnly=TRUE)))
      } else {
        f <- try(nls( well.ss ~ ll(A, lag, r, time.ss),
                     start=list(A=A, r=r, lag=lag),
                     control=list(warnOnly=TRUE)))
      }

      if (class(f) == "try-error") {
        browser()
      } else {
        f.od0 <- coef(f)["A"]
        f.r   <- coef(f)["r"]

        if (lag == 0) {
          lines( f.od0*exp(f.r*time.ss) ~ time.ss, col="red" )
        } else {
          f.lag <- coef(f)["lag"]
          x.lag <- seq(0, f.lag, 0.1)
          x.exp <- seq(f.lag, max(time.ss)+max(time.ss)*0.1, 0.1)
          lines(x.lag, rep(f.od0,length(x.lag)), col="red")
          lines(x.exp, f.od0*exp(f.r*(x.exp-f.lag)), col="red")
        }


        fits <- c(fits, list(f))
        names(fits)[[i]] <- names(ods)[[i]]
      }
    }
  }
  fits
}

ods.times <- function( dat, wells, blanks ) {
    row.key <- list(A=1, B=2, C=3, D=4, E=5, F=6, G=7, H=8)
    rows <- wells$rows
    cols <- wells$cols
    dat.bs <- dat
    if (!missing(blanks)) {
        dat.bs <- lapply(dat, function(x) x-mean(x[,blanks]))
    }

    nms <- paste(rep(rows, each=length(cols)), cols, sep="")
    dat.a <- array(unlist(dat.bs), dim=c(8,12,length(dat.bs)))

    p.ods <- list()
    for (r in rows) {
        rn <- row.key[[r]]
        for (k in cols) {
            p.ods <- c(p.ods, list(dat.a[rn,k,]))
        }
    }
    names(p.ods) <- nms
    p.ods
}
