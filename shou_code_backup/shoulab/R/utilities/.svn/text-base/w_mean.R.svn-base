w.mean <- function(xi,sigma) {
  var      <- sigma^2
  wm.var   <- 1/sum(1/var,na.rm=TRUE)
  print(wm.var)
  wm       <- sum(xi/var,na.rm=TRUE)*wm.var
  wm.conf  <- 2*sqrt(wm.var)
  data.frame(w.mean=wm, wm.var=wm.var, wm.conf=wm.conf)
}
