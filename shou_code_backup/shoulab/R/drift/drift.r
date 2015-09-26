wright.fisher <- function(N=100, p=0.5, gens=50, runs=1) {
  initial.pop <- sample(c(rep(0,N*p),rep(1,N*(1-p))))
  res <- data.frame()
  for (run in 1:runs) {
    pop <- initial.pop
    res <- rbind(res, data.frame(run=run,t=0,
                                 freq=length(pop[pop==1])/length(pop)))
    for (i in 1:gens) {
      pop <- sample(x=pop, size=N, replace=TRUE)
      res <- rbind(res, data.frame(run=run,t=i,
                                   freq=length(pop[pop==1])/length(pop)))
    }
  }
  res
}

moran <- function(N=100, p=0.5, gens=50, runs=1) {
  pop <- sample(c(rep(0,N*p),rep(1,N*(1-p))))
  freq <-length(pop[pop==1])/length(pop) 
  res <- data.frame()
  for (i in 0:(gens-1)) {
    for (j in 0:(N-1)) {
      #cat("pop: ",pop,"\n")
      #cat("freq: ",freq,"\n")
      pop[sample(1:N,size=1)] <- rbinom(n=1,size=1,prob=freq)
      freq <-length(pop[pop==1])/length(pop) 
      #cat("new freq: ",freq,"\n")
      res <- rbind(res, data.frame(t=(i*N+j)/N,freq=freq))
    }
  }
  res
}

mean.time <- function(N, p, fix=TRUE, haploid=TRUE) {
  ploidy <- ifelse(haploid,2,4)
  if (fix)
    -ploidy*N*(1/p)*(1-p)*log(1-p)
  else
    -ploidy*N*(1/p)*p*log(p)
}
var.time <- function(N, p, fix=TRUE, haploid=TRUE) {
  ploidy <- ifelse(haploid,16,32)
  P <- if (fix)
        integrate(function(x) log(x)/(1-x),lower=p,upper=1)$value
       else
        integrate(function(x) log(x)/x,lower=0,upper=p)$value

  if (fix)
    ploidy*N*(log(p)*log(1-p) - P + (1-p)*(1/p)*log(1-p))
  else
    ploidy*N*(p/(1-p)*log(p) - P)
}

var.freq <- function(N, p0, t, haploid=TRUE) {
  ploid <- ifelse(haploid,1,2)
  p0*(1-p0)*(1- (1-(1/(ploid*N)))^t )
}

pop.size <- function(mean.time,p, fix=TRUE, haploid=TRUE) {
  ploidy <- ifelse(haploid,2,4)
  N <- if (fix)
        -mean.time / (ploidy*(1/p)*(1-p)*log(1-p))
       else
        -mean.time / (ploidy*(1/p)*p*log(p))

  var <- var.time(N=N,p=p,fix=fix,haploid=haploid)
  list(N=N,var=var)
}
