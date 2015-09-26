bootstrap.p <- function(m0, m1, seed, nsim=1000) {
  library(lme4)

  if (!missing(seed)) set.seed(seed)

  # Following Pinheiro and Bates, 2000; sec 2.4. :
  #   1) Generate "fake" data based on the simpler (null) model.
  #   2) Use this data to calculate the log likelihood for the null and 
  #      more complicated models.
  #   3) Return the likelihood ratio test (LRT) statistic.
  pboot <- function(m0,m1) {
    s  <- simulate(m0)
    L0 <- logLik(refit(m0,s))
    L1 <- logLik(refit(m1,s))
    2*(L1-L0)
  }

  simdev <- replicate(nsim,pboot(m0,m1))
  obsdev <- c(2*(logLik(m1)-logLik(m0)))
  mean(simdev>obsdev)
}
