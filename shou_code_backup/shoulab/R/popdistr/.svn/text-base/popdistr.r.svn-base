# vim: set filetype=r tabstop=2 shiftwidth=2 expandtab :

popdistr <- function(runs=1,N0=1,max.pop=10,max.time=2000,
                     growth.herit=0,
                     growth.distr="fixed", growth.params=list(value=2),
                     lag.herit=0,lag.distr="fixed",lag.params=list(value=0),
                     min.growth,max.growth, 
                     min.lag=0, max.lag=10,
                     transf=function(x) {x},
                     plots=TRUE, progress=TRUE, seed=as.integer(Sys.time()),
                     ...)
{
  source("~/Documents/Code/R/popdistr/lib_distr.r")
  source("~/Documents/Code/R/utilities/logaxis.r")
  if (N0 >= max.pop) stop("\nN0 >= max.pop\n")
  set.seed(seed)

  Call <- match.call(expand.dots=TRUE)
  dots <- names(list(...))
  dots <- dots[!is.element(dots,c("upper","lower"))]

  divr <- pick.dist(growth.distr)
  lagr <- pick.dist(lag.distr)

  check.params(divr,growth.params)
  check.params(lagr,lag.params)

  obs.growth.rates <- vector(mode="numeric",length=runs)
  largest.pop  <- N0
  longest.time <- 0
  res <- vector("list",runs)
  for (run in 1:runs) {
    if (identical(progress,TRUE)) {
      cat("Starting run ", run, " of ", runs, " ")
      flush.console()
    }

    simtime <- 0
    N <- N0

    # Initialize initial population members
    saved.divs <- last.divs <- div.times <- 
      transf(get.trands(divr,n=N0,growth.params,min.growth, max.growth))
    saved.lags <- lag.times <- 
      transf(get.trands(lagr,n=N0,lag.params,min.lag,max.lag))

    if (any(is.infinite(c(div.times,lag.times))))
      stop("Infinite values in initial population!")


    #  Choose a timestep that is 1% of the minimum  of lag time and 
    #  growth rate to reduce numerical error.
    dt <- signif(0.01 * min(c(div.times,
                              ifelse(lag.times>0,lag.times,div.times))),1)

    runtime <- runpop <- vector("numeric",length=max.pop)
    idx <- 1
    while( simtime < max.time && N < max.pop ) {
      if (identical(progress,TRUE)) {
        if ( identical(all.equal(simtime%%10,0,dt),TRUE) ) { 
          cat(".")
          flush.console()
        }
      }

      # determine who is done with lag time
      growers <- which(lag.times<simtime)

      # determine who is done with a cell cycle
      parents <- which(div.times[growers]<simtime)
      if (length(parents)>0) {

        # determine who will inherit, based on binomial probability.
        dh <- rep(0, length=length(parents))
        if (growth.herit > 0) {
          dh <- rbinom(n=length(parents),size=1,prob=growth.herit)
        }
        herit <- which(dh==1)
        no.herit <- which(dh==0)

        # assign division times based on inheritance
        new.divs <- rep(0,length=length(parents))
        new.divs[herit] <- last.divs[parents[herit]]
        new.divs[no.herit] <- 
          transf(get.trands(divr, n=length(no.herit),
                            growth.params,min.growth,max.growth))

        # all parents get new division times
        new.parent.divs <- 
          transf(get.trands(divr,n=length(parents),
                            growth.params,min.growth,max.growth))
        last.divs <- c(last.divs,new.parent.divs)
        div.times[parents] <- new.parent.divs+simtime
        saved.divs <- c(saved.divs,new.parent.divs)

        # add new cell division times
        div.times  <- c(div.times,new.divs+simtime)
        saved.divs <- c(saved.divs,new.divs)

        # add zero lag times for new cells
        lag.times <- c(lag.times,rep(0,length(parents)))
      } 
      N <- length(div.times)
      runtime[idx] <- simtime
      runpop[idx]  <- N

      simtime <- simtime + dt
      idx <- idx + 1
    }
    res[[run]]$growth <- data.frame(time=c(0,runtime[runtime>0]),
                                    pop=runpop[runpop>0])

    if (N > largest.pop) largest.pop <- N
    if (simtime > longest.time) longest.time <- simtime

    res[[run]]$divs <- saved.divs
    res[[run]]$lags <- saved.lags

    if (identical(plots,TRUE) && runs == 1) {
      x11()
      plot(pop ~ time, data=res[[run]]$growth, log="y",
           main=paste("Run",run,"Growth"), ...)
    }
    if (identical(progress,TRUE)) {
      cat(" finished!\n")
      flush.console()
    }
  }
  if (identical(plots,TRUE)) {
    x11()
    plot(2, xlim=c(0,longest.time), ylim=c(N0,largest.pop), 
         type="n", log="y", axes=FALSE,
         xlab="Time", ylab="Population", main="Growth of All Runs", ...)
    axis(1); log.axis(2)
    cols <- rainbow(runs)
    for (i in 1:runs) {
      lines(pop ~ time, data=res[[i]]$growth,col=cols[i])
    }
    box()
  }
  invisible(res)
}
