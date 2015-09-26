# : vim filetype=r :
ci.logreg <- function(dat, f, p=0.5, x.pos, colors, xlab=f, ...) {
  library(logistf)
  library(gdata)
  source("~/Documents/Code/R/utilities/expand_df.r")
  source("~/Documents/Code/R/utilities/logaxis.r")
  source("~/Documents/Code/R/utilities/logreg/solve_logreg.r")


  res <- data.frame()
  dat.exp <- expand.df(dat,c("viable","inviable"))
  for (type in drop.levels(split(dat.exp, dat.exp[[f]]))) {
    fit <- logistf(case~log(cells.per.ml), data=type)

    coefs <- coef(fit)
    idx <- 1
    if (length(coefs==2)) {
      alpha <- coefs[1]
      idx <- 2
    }
    beta  <- as.numeric(coefs[idx])

    # Because of inverse relationship between beta and fraction viable
    upper <- as.numeric(fit$ci.lower[idx])
    lower <- as.numeric(fit$ci.upper[idx])


    
    vals <- function(p,a,b) (log(p/(1-p))-a) / b
    ci <- data.frame(t(exp(vals(p,alpha,c(lower,beta,upper)))))
    names(ci) <- c("lower", "beta", "upper")
    res <- rbind(res, data.frame(f=unique(type[[f]]), ci))
  }
  names(res)[1] <- f
  res


  if (missing(x.pos)) x.pos <- 1:nrow(res)
  if (missing(colors)) colors <- rep("black", nrow(res))

  plot(range(x.pos), range(res[-1]), type="n", log="y", 
       axes=FALSE, ann=FALSE)
  axis(1, lwd.ticks=par()$lwd)
  log.axis(2, las=2, lwd.ticks=par()$lwd, major.cex=par()$cex.axis)
  mtext(side=1,text=xlab, line=3.5,
        cex=par()$cex.lab*par()$cex)
  mtext(side=2,text="Initial co-culture density (cells/ml)", line=3.5,
        cex=par()$cex.lab*par()$cex)
  box()
  points(res$beta~x.pos, cex=1.5, col=colors)
  arrows(x.pos,res$lower,x.pos,res$upper,
         col=colors, code=3,length=0.2,angle=90)
}
