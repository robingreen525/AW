solve.logreg <- function(fit, p=c(0.025, 0.5, 0.975)) {
  alpha <- 0; beta  <- 0
  lower <- 0; upper <- 0
  idx <- 1
  coefs <- coef(fit)
  if (length(coefs==2)) {
    alpha <- coefs[1]
    idx <- 2
  }
  beta  <- coefs[idx]
  lower <- fit$ci.lower[idx]
  upper <- fit$ci.upper[idx]

  vals <- function(p,a,b) (log(p/(1-p))-a) / b
  res <- exp(c(vals(p[1],alpha,upper),
               vals(p,alpha,beta),
               vals(p[3],alpha,lower)))

  names(res) <- c("lower", p[1:3], "upper")
  res
}
