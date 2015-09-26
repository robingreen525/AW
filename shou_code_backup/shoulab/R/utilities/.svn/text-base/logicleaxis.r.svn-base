logicle.axis <- function(side, tf.obj, type="logicle", ...) {
  source("/home/nodice/Documents/Code/R/utilities/dec_to_logicle.r")

  limits <- c()
  if (side==1 || side==3) {
    limits <- par("usr")[1:2]
  } else {
    limits <- par("usr")[3:4]
  }

  tics <- logicle.tics(tf.obj, limits)

  axis(side,at=tics$major.tics, labels=parse(text=tics$major.labels), ...)
  opar <- par(tcl=-0.25)
  axis(side,at=tics$minor.tics, labels=rep(" ",length(tics$minor.tics)))
  par(opar)
}

logicle.tics <- function(tf.obj, lims) {
  vals     <- invLogicle(tf.obj, lims)
  min.dec  <- floor(log10(abs(min(vals))))
  min.dec  <- ifelse(min(vals)<0, -min.dec, min.dec)
  max.dec  <- ceiling(log10(abs(max(vals))))
  dec.exps <- abs(min.dec:max.dec)
  maj.tics <- make.vals(dec.exps)
  min.tics <- make.min.tics(maj.tics$values)

  logicle.maj <- dec.to.logicle(maj.tics$values, tf.obj) 
  logicle.min <- dec.to.logicle(min.tics, tf.obj)

  list(major.tics=logicle.maj, major.labels=maj.tics$labels,
       minor.tics=logicle.min)
}

make.min.tics <- function(maj) {
  maj <- maj[-c(1,length(maj))]
  maj[maj==0] <- 1
  min <- c()
  for (i in 1:9)
    min <- c(min, maj*i)

  min <- sort(min)
  min <- min[!min %in% maj]
  min
}

make.vals <- function(exps) {
  vals <- c()
  labs <- c()
  zero <- FALSE
  for (ex in exps) {
    if (ex == 0) {
      vals <- c(vals,0)
      labs <- c(labs,"0")
      zero <- TRUE
    } else if (zero) {
      vals <- c(vals,10^ex)
      labs <- c(labs,paste("10^",ex))
    } else {
      vals <- c(vals,-10^ex)
      labs <- c(labs,paste("-10^",ex))
    }
  }
  if (length(labs)>7) {
    labs <- gsub('-?10\\^ 1',"phantom(0)",labs)
  }
  list(labels=labs,values=vals)
}

invLogicle <- function(tf.obj, ticks) {
  ff <- flowFrame(matrix(ticks,dimnames=list(NULL,"ticks")))
  tl <- transformList("ticks", inverseLogicleTransform("id",trans=tf.obj))
  exprs(transform(ff,tl))
}

