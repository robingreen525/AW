fitness.plot <- function(path, with.fit=TRUE, ...) {
  dat <- read.delim(path)
  dat <- within(dat, coop.ratio <- coops/cheats)
  dat[dat$coop.ratio == 0,]$coop.ratio <- NA
  browser()

  plot(coop.ratio~step, data=dat, type="n", log="y", axes=FALSE,
       xlab="Time (hrs)", ylab="", ...)
  mtext(side=2, text="Coop:Cheat", line=3.5, cex=3)
  axis(1,lwd=par()$lwd, lwd.ticks=par()$lwd)
  log.axis(2,lwd=par()$lwd, lwd.ticks=par()$lwd, las=2)
  box(lwd=par()$lwd)

  for (i in split(dat, dat$row.col)) lines(coop.ratio~step, data=i,lwd=5)

  fit <- try(nls(coop.ratio~A*exp(a*step), data=dat,
             start=list(A=1, a=-0.015)))
  fit.lm <- lm(log(coop.ratio)~step, data=dat, weights=1/coop.ratio)

  if (class(fit) != "try-error") {
    print(summary(fit))
    if (with.fit) {
      curve(coef(fit)["A"]*exp(coef(fit)["a"]*x), add=TRUE, col="orange")
    }
  } else {
    print(summary(fit.lm))
    if (with.fit) {
      curve(exp(coef(fit.lm)[2]*x + coef(fit.lm)[1]), 
            add=TRUE, col="green", lwd=1)
    }
  }

  invisible(list(dat=dat, fit.nls=fit, fit.lm=fit.lm))
}
