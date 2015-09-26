plater <- function( initial.vol=NULL, ex.v, ex.vf, ex.d, plating.vol, runs, all.data=FALSE, verbose=FALSE )
{
  UL_IN_ML <- 1000
  total <- runs * length(ex.d)

  initial.vol.ml <- NULL
  initial.vol.ml <- if( !is.null(initial.vol) ) initial.vol/UL_IN_ML

  ex.v.ml <- ex.v/UL_IN_ML
  ex.vf.ml <- ex.vf/UL_IN_ML
  plating.vol.ml <- plating.vol/UL_IN_ML
  n.samples <- 2 + length(ex.v)
  n.dils    <- length(ex.v)

  obs.densities <- matrix(0, nrow=runs, ncol=n.samples)
  ex.densities  <- matrix(cumprod(c(ex.d, ex.v / ex.vf, 1)), nrow=runs, ncol=n.samples, byrow=TRUE)

  error      <- matrix(0, nrow=runs, ncol=n.samples)


  step <- 1
  if (!is.null(initial.vol.ml)) {

    # Choose a pipette to take out sample.
    p <- choose.pipette(initial.vol.ml)
    v <- take.volume(initial.vol.ml, p, runs, ex.d)
    obs.densities[,step] <- v[,"a.cells"]/initial.vol.ml

    if (identical(verbose, TRUE)) {
      cat( "\n\nStep", step, ": Initial sample without dilution.\n\n", sep="" )
      cat( "  Expecting to remove", initial.vol.ml, "ml containing ", ex.d, "cells/ml.\n" )
      cat( "  Chose a P", p[1], " to remove ", initial.vol.ml, " ml (", p[2]*100, "% error) and removed ",
           v[,"a.vol"], " ml containing ", v[,"a.cells"]*v[,"a.vol"], " cells (", 
           format(obs.densities[,step], scientific=TRUE), " cells/ml).\n", sep="" )
    }
  } else {
    obs.densities[,step] <- ex.d

    if (identical(verbose, TRUE) ) {
      cat( "\n\nStep", step, ": Start with a flask containing", ex.d, "cells/ml.\n\n" )
    }
  }
  step <- step+1

  for (i in 1:n.dils) {

    # Choose a pipette to take out sample.
    p <- choose.pipette(ex.v.ml[i])
    P <- choose.pipette(ex.vf.ml[i])

    samp <- obs.densities[,step-1]

    v <- take.volume(ex.v.ml[i], p, runs, samp)
    V <- take.volume(ex.vf.ml[i]-ex.v.ml[i], P, runs)
    VT <- v[,"a.vol"]+V[,"a.vol"]

    obs.densities[,step] <- v[,"a.cells"]/VT

    if (identical(verbose, TRUE)) {
      cat( "\nStep ", step, " : Dilution ", i, " -- Dilute ", ex.v.ml[i], " ml into ", ex.vf.ml[i], " ml\n", sep="" )
      cat( "  Chose a P", p[1], " to take ", ex.v.ml[i], "ml (", p[2]*100, "% error).\n" )
      cat( "  Actually took ", v[,"a.vol"], "ml containing", v[,"a.cells"]*v[,"a.vol"], "cells.\n" )
      cat( "  Chose a P", P[1], " to take ", ex.vf.ml[i]-ex.v.ml[i], "ml (", P[2]*100, "% error).\n" )
      cat( "  Actually took ", V[,"a.vol"], "ml.\n" )
      cat( "  Expected", ex.densities[step], "cells/ml." )
      cat( "  Got", obs.densities[,step], "cells/ml.\n\n" )
    }
    
    step <- step + 1
  }

  # Choose a pipette to plate with.
  p    <- choose.pipette(plating.vol.ml)
  samp <- obs.densities[,step-1]
  v    <- take.volume(plating.vol.ml, p, runs, samp)

  obs.densities[,step] <- v[,"a.cells"]/plating.vol.ml
  error <- abs(obs.densities - ex.densities) / ex.densities
  plated <- as.vector(t(obs.densities[,ncol(obs.densities)]*plating.vol.ml))

  if (identical(verbose, TRUE)) {
    cat( "Step", step, ": Plating...\n", sep="" )
    cat( "  Expecting to remove", plating.vol.ml, "ml containing ", ex.densities[step], "cells/ml.\n" )
    cat( "  Chose a P", p[1], " to remove ", plating.vol.ml, " ml (", p[2]*100, "% error) and removed ",
         v[,"a.vol"], " ml containing ", v[,"a.cells"], " cells (", 
         format(obs.densities[,step], scientific=TRUE), " cells/ml).\n", sep="" )
    cat( "  Expecting", ex.densities[step]*plating.vol.ml, " colonies but will see ", plated, "colonies.\n\n" )
  }
  if (identical(all.data, TRUE)) {
    return( list( exp=ex.densities, obs=obs.densities, error=error, plated=plated ) )
  } else {
    return( cbind( plated=plated, error=as.vector(error[,ncol(error)]) ) )
  }
}

take.volume <- function( vol, pipette, n, cells=NULL ) {

  #return( cbind(a.vol=vol, a.cells=cells*vol) )

  vol.w.error <- rnorm( n, vol, pipette[2]*vol )

  cells.w.error <- NULL
  if (!is.null(cells)) {
    cells.w.error <- rpois(n, cells*vol.w.error)
    return( cbind(a.vol=vol.w.error, a.cells=cells.w.error) )
  } else {
    return( cbind(a.vol=vol.w.error) )
  }

}


choose.pipette <- function(vol) {
  vol <- vol*1000
  pipette <- NULL
  error   <- NULL
  if (vol > 0 & vol < 10) {
    pipette <- 10
    if ( vol < 1 ) error <- 5
    if ( vol > 1 & vol < 5 ) error  <- 2.5
    if ( vol > 5 & vol < 10 ) error <- 1 
  } else if (vol >= 10 & vol < 20) {
    pipette <- 20
    error   <- 1
  } else if (vol >= 20 & vol < 200) {
    pipette <- 200
    error   <- ifelse( vol < 100, 1, 0.8 )
  } else if (vol >= 200 & vol <= 1000) {
    pipette <- 1000
    error   <- ifelse( vol < 500, 3, 0.8 )
  } else {
    stop(paste("Don't have pippette for volume",vol))
  }

  c(pipette, error/100)
}

run.sim <- function(start.density=c(1e5,1e6), v=c(100,10,100), vf=c(1000,1000), runs=1e4 ) 
{
  if ( length(v)-1 != length(vf) ) {
    stop("Check starting parameters.\n")
  }
  n.plots <- 2
  par(mfrow=c(length(start.density), n.plots))

  res <- NULL
  k <- 1
  for (i in start.density) {
    res <- c(res, list(plate.sim(ex.v=v, ex.vf=vf, ex.d=i, runs=runs)))
    #x11()
    hist(res[[k]]$obs, breaks=seq(min(res[[k]]$obs), max(res[[k]]$obs)),
         main=bquote(.(i)~"cells/ml"), xlab="Observed CFU/ml")
    #x11()
    #hist(res[[k]]$error, main=bquote(.(i)~"cells/ml"))
    plot(density(res[[k]]$error*100), main=bquote(.(i)~"cells/ml"), xlab="% Error")
    k <- k+1
  }
  res
}
fit.gaussian <- function() {
}

pem <- function( cfu, k.sd=2, initial.volume=NULL, sample.volumes, dilution.volumes, plating.volume, 
                 runs, step.size=NULL, search.range=NULL )
{
  # pem - Plating expectation maximization.
  #
  #   cfu - A vector of colony forming units (CFU) seen on one or more plates.
  #
  #   sample.volumes - A vector of volumes (in ul) sampled in the dilution process.
  #                    This includes the initial amount taken out of the experimental
  #                    flask, which is *not* diluted.
  #
  #   dilution.volumes - A vector of *final* volumes (in ul) used in the dilution process.
  #
  #   plating.volume   - The final volume plated (in ul).
  #
  #   runs - The number of times the simulation should be run for each initial density.
  #
  #   k.sd - The initial guess of cell density is obtained by multiplying the mean cfu/plate by
  #          the overall dilution factor (u).  The range to search is 
  #
  #                          u - u*k.sd/sqrt(u) to u + u*k.sd/sqrt(u)
  #
  #          which is two times the k.sd'th standard deviation from the mean, assuming a Poisson
  #          distribution.
  #
  #   step.size - The step size through the range as a percent of that range. If NULL, defaults
  #               to a size that will give 50 steps.
  #
  # Given CFU and dilution information, pem makes an initial guess of the original density
  # by finding the mean original density after applying the dilutions to each CFU.

  UL_IN_ML = 1000

  guess <- prod(dilution.volumes/sample.volumes) * mean(cfu) / (plating.volume/UL_IN_ML)

  

  min.d <- NULL
  max.d <- NULL
  if (is.null(search.range)) {
    min.d   <- guess - guess*k.sd/sqrt(mean(cfu))
    max.d   <- guess + guess*k.sd/sqrt(mean(cfu))
  } else {
    min.d <- min(search.range)
    max.d <- max(search.range)
  }
  search.range <- max.d - min.d

  step <- NULL
  if (is.null(step.size)) {
    step <- search.range / 50
  } else {
    step <- search.range * step.size/100
  }
  total.steps <- floor(search.range/step+1)


  cat( "Initial guess is", guess, "cfu/ml.\n" )
  cat( "Will try values from ", min.d, " to ", max.d, " in increments of ", step,
       " (", total.steps, " steps).\n", sep="" )

  probs <- array(0, dim=c(total.steps, 2), dimnames=list(NULL,c("initial.density", "p")))
  #probs <- NULL
  idx <- 1
  for ( i in seq(min.d, max.d, step) ) {
    res <- plater( initial.vol=initial.volume, ex.v=sample.volumes, ex.vf=dilution.volumes, ex.d=i,
                   plating.vol=plating.volume, runs=runs )

    p <- NULL
    for (j in cfu) {
      p <- prod( p, sum(res[,1] == j)/runs )
    }
    p <- p*factorial(length(cfu))
    #probs <- rbind(probs, cbind(initial.density=i, prob=p))
    probs[idx,] <- c(i, p)
    idx <- idx+1
  }
  #print(probs)

  plot( probs[,2] ~ probs[,1], type="o", xlab="Starting Density (cells/ml)", ylab="Probability")
  gaus.fit <- fit.gaussian(probs)
  list( gfit=gaus.fit, probs=probs )
}

fit.gaussian <- function(p) {

  fac <- 0.2
  peak <- p[order(p[,"p"],decreasing=TRUE),][1,]

  A.guess <- peak["p"]
  u.guess <- peak["initial.density"]
  sd.guess <-abs(p[order((p[,"p"] - fac*A.guess)^2)][1] - peak["initial.density"])

  nfit <- nls( p[,"p"] ~ A*exp((-(p[,"initial.density"]-u)^2)/(2*sd^2)),
               start=list(A=A.guess, u=u.guess, sd=sd.guess),
               control=list(warnOnly=TRUE) )
 lines( coef(nfit)[1] * exp((-(p[,"initial.density"]-coef(nfit)[2])^2)/(2*coef(nfit)[3]^2)) ~ p[,"initial.density"], col="red" )
 nfit
}

