plot.abif <- function(dat,trim=4) {
  dat <- dat[trim:(nrow(dat)-trim),]

  cols <- list(A="green",C="blue",G="black",T="red")

  plot(c(1,nrow(dat)), range(dat[2:5]),type="n")

  lmins <- list()
  for (i in 2:ncol(dat)) {
    color <- cols[[names(dat)[i]]]
    lmins[[i-1]] <- as.vector(localMaxima(-dat[,i]))
    points(dat[,i], col=color,type="o")
  }
  names(lmins) <- names(dat)[2:ncol(dat)]
  legend("topright",legend=names(cols),
         lty=1, col=unlist(cols))
  
  get.areas(dat, lmins)
}

get.areas <- function(dat,lmins) {
  limits <- list(A=c(1:2),G=c(1:3), T=c(2:3))
  abline(v=lmins[["A"]][1:2],col="green")
  abline(v=lmins[["G"]][1:3],col="black")
  abline(v=lmins[["T"]][2:3],col="red")

  a.area  <- sum(dat$A[lmins$A[1]:lmins$A[2]])
  g1.area <- sum(dat$G[lmins$G[1]:lmins$G[2]])

  t.area  <- sum(dat$T[lmins$T[2]:lmins$T[3]])
  g2.area <- sum(dat$G[lmins$G[2]:lmins$G[3]])

  ratios <- data.frame(a.ratio=g1.area/a.area, t.ratio=g2.area/t.area)
  ratios
}


# taken from 
#http://stackoverflow.com/questions/6836409/
#       finding-local-maxima-and-minima-in-r
localMaxima <- function(x) {
  # Use -Inf instead if x is numeric (non-integer)
  y <- diff(c(-.Machine$integer.max, x)) > 0L
  rle(y)$lengths
  y <- cumsum(rle(y)$lengths)
  y <- y[seq.int(1L, length(y), 2L)]
  if (x[[1]] == x[[2]]) {
    y <- y[-1]
  }
  y
}
