minority <- function(L=1, H=100, p=seq(0,1,0.001)) {

  res <- data.frame()

  for (i in p) {
    z <- 0
    for (n in 1:L) {
      x <- choose(L,n)*i^n*(1-i)^(L-n)
      y <- 0
      for (m in 0:(n-1)) {
        y <- y + choose(H,m)*i^m*(1-i)^(H-m)
      }
      z <- z + y*x
    }
    res <- rbind(res, data.frame(p=i,P=z))
  }
  plot(P ~ p,data=res,type="l")
  res
}
