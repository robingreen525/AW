get.trands <- function(func, n, params, min, max) {
  rands <- numeric(n)

  if (identical(func,"fixed")) { 
    val <- as.vector(unlist(params))
    if (val > max || val < min) 
      stop("Fixed value outside of min/max range!")
    rands <- rep(val,n)
  } else {
    formals(func) <- c(list(n=n),params)
    rands <- func()

    out.of.bounds <- which(rands < min | rands > max)
    n.out <- length(out.of.bounds)
    while (n.out > 0) {
      formals(func) <- c(list(n=n.out),params)
      rands[out.of.bounds] <- func()
      out.of.bounds <- which(rands < min | rands > max)
      n.out <- length(out.of.bounds)
    }
  }
  rands
}


rbimodal <- function(n, p1, mean1, sd1, mean2, sd2) {
  rands <- numeric(n)
  pops <- rbinom(n=n, prob=p1, size=1) 
  rands[which(pops==1)] <- 
    rnorm(n=length(which(pops==1)), mean=mean1, sd=sd1)
  rands[which(pops==0)] <- 
    rnorm(n=length(which(pops==0)), mean=mean2, sd=sd2)

  rands
}

check.params <- function(distr, params) {
  Call <- match.call()

  if ( missing(params) ) {
    stop( paste("Need parameters for '",Call$distr,"'.") )
  }

  if ( identical(distr,"fixed") ) {
    if ( length(params) != 1 )
      stop( "'fixed' distribution only takes one parameter." )
    else 
      return()
  } else {
    pnames <- names(params)
    m <- match(pnames,names(formals(distr)))
    if ( any(is.na(m)) ) {
      wrong <- paste(pnames[which(is.na(m))])
      message <- ifelse(length(wrong)==1, " is not a parameter for ", 
                        " are not parameters for ")
      stop(paste("'",wrong,"'", message, "'",Call$distr,"'.", sep=""))
    }
  }
}

pick.dist <- function(choice) {
  distr <- switch(tolower(choice), fixed="fixed", beta=rbeta, 
                   cauchy=rcauchy, `chi-squared`=rchisq, exponential=rexp, 
                   f=rf, gamma=rgamma, geometric=rgeom, 
                   `log-normal` = rlnorm, lognormal=rlnorm, 
                   logistic= rlogis, `negative binomial`=rnbinom, 
                   normal=rnorm, poisson=rpois, t=mydt, uniform=runif, 
                  weibull=rweibull, bimodal=rbimodal, NULL)


  if ( is.null(distr) ) {
    stop("\nCurrently supported distributions:\n\t",
         paste("fixed","tbeta", "cauchy", "chi-squared", "exponential", 
               "f", "gamma", "geometric", "log-normal", "logistic", 
               "negative binomial", "normal", "poisson", "t", 
               "uniform", "weibull", "bimodal", sep="\n\t"))
  } else {
    distr
  }
}

rtdist <- function(n=10, distr="normal", params=list(mean=2, sd=0.1),
                   min=1, max=100) 
{
  distr <- pick.dist(distr)
  check.params(distr, params)
  get.rands(n=n, func=distr, params=params, min=min, max=max)
}
