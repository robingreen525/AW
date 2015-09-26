dec.to.logicle <- function(x,tf.obj) {
  if (missing(tf.obj))
    stop("Need transformation object to calculate logicle scale!")

  # For "logicle_transform" function.
  require(flowCore)

  params <- summary(tf.obj)
  ff <- flowFrame(matrix(x,dimnames=list(NULL,"x")))
  tl <- transformList("x",
          logicleTransform(t=params$t, a=params$a, w=params$w, m=params$m))
  exprs(transform(ff,tl))
}
