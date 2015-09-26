dec.to.logicle <- function(x,params) {
  if (missing(params))
    stop("Need parameters to calculate logicle scale!")
  res <- rep(-1,length(x))

  # For "logicle_transform" function.
  require(flowCore)

  #  Have to use for loops because of a bug that gives incorrect results 
  # if 'x' is a vector.
  # Args MUST be in this order, even though they are named.
  for (i in 1:length(x)) {
    res[i] <- .Call("logicle_transform",
                    x=x[i],
                    m=params$m,
                    w=params$w,
                    p=params$p,
                    t=params$t,
                    a=0,
                    tol=.Machine$double.eps^0.8,
                    maxit=as.integer(5000)
                   )

  }
  res
}
