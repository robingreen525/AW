# vim: set filetype=r tabstop=2 shiftwidth=2 expandtab :

death.comp <- function(A0=1e4, B0=A0, stop.pop=0.1*min(A0,B0),dra=-0.3,
                       drb=1.02*dra, noise=0.05, t.start=0, t.end=100,
                       t.step=1, runs=1, ...)
{
# Death comp - Models death rates of two populations side-by-side.
#   inputs
#     A0 - initial size of population A.
#     B0 - initial size of population B.
#     dra - death rate of population A.
#     drb - death rate of population B.
#     noise - percent error to add to each data point.
#     time - the time interval for the comparison.
  require(MASS,quiet=TRUE)

  time <- seq(t.start,t.end,by=t.step)
  obj <- list()
  for (i in 1:runs) {
    res.a <- A0*exp(rnorm(n=length(time),m=dra,sd=abs(dra)*noise)*time)
    res.b <- B0*exp(rnorm(n=length(time),m=drb,sd=abs(drb)*noise)*time)
    res.a <- res.a[res.a>=stop.pop]
    res.b <- res.b[res.b>=stop.pop]
    shorter <- ifelse(length(res.a)>length(res.b),length(res.b),length(res.a))
    short.a <- res.a[1:shorter]
    short.b <- res.b[1:shorter]
    short.time <- time[1:shorter]
    res <- data.frame(time=short.time,A=short.a,B=short.b)

    fits  <- list()
    fits$a <- nls(short.a ~ A0*exp(a*short.time), start=list(A0=A0,a=dra))
    fits$b <- nls(short.b ~ B0*exp(b*short.time), start=list(B0=B0,b=drb))
    confints <- list()
    confints$a <- confint(parm="a",profile(fits$a))[1:2]
    confints$b <- confint(parm="b",profile(fits$b))[1:2]

    obj[[i]] <- list(A0=A0, B0=B0, stop.pop=stop.pop, 
                     dra=dra, drb=drb, noise=noise,
                     t.start=t.start,t.end=t.end,t.step=t.step,
                     result=res, fits=fits, confints=confints)
  }
  if (identical(runs,1)) {
    plot(range(time[1:(max(length(res.a),length(res.b)))]),c(min(res.a,res.b),max(A0,B0)),xlab="Time",ylab="Population Size",type="n",log="y",...)
    points(res.a ~ time[1:length(res.a)],col="green")
    points(res.b ~ time[1:length(res.b)],col="blue")
  }
  obj
}


summarize.death.comp <- function(dat) {
  a.rate <- dat[[1]]$dra
  b.rate <- dat[[1]]$drb
  
  obs.rates <- NULL
  confints <- NULL
  overlaps <- 0
  for (i in 1:length(dat)) {
    obs.rates <- rbind(obs.rates,
                      data.frame(a=coef(dat[[i]]$fits$a)["a"],
                                 b=coef(dat[[i]]$fits$b)["b"])
                      )
    confints <- rbind(confints,
                      data.frame(a.min=dat[[i]]$confints$a[1],
                                 a.max=dat[[i]]$confints$a[2],
                                 b.min=dat[[i]]$confints$b[1],
                                 b.max=dat[[i]]$confints$b[2]
                                )
                     )
  }
  overlaps <- sum(as.numeric(confints$a.min < confints$b.max))

  xs <- 1:length(obs.rates$a)
  plot(obs.rates$a,ylim=range(confints))
  points(obs.rates$b,col="green")
  arrows(xs,confints$a.min,xs,confints$a.max,code=3,length=0.1,angle=90)
  arrows(xs,confints$b.min,xs,confints$b.max,code=3,length=0.1,angle=90,col="green")
  abline(h=a.rate)
  abline(h=b.rate,col="green")

  t.start <- dat[[1]]$t.start
  t.end   <- length(dat[[1]]$result$time) # returns the time actually used.
  t.step  <- dat[[1]]$t.step
  res <- data.frame(t.start=t.start, t.end=t.end, t.step=t.step,
                    stop.pop=dat[[1]]$stop.pop,
                    A0=dat[[1]]$A0,B0=dat[[1]]$B0,
                    dra=dat[[1]]$dra,drb=dat[[1]]$drb,
                    noise=dat[[1]]$noise, runs=length(dat),
                    overlap=overlaps/length(dat))
  res
}


