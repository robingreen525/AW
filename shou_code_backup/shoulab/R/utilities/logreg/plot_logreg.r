# :vim filetype=r :
plot.logreg <- function(dat, f, jit=NULL, leg, cols=palette(), ltys,
                        show.legend=TRUE, method="logistf", 
                        leg.size=1, ...)
{
# This function plots logistical regression curves on binary outcome data.
# Inputs:
#  dat    -- a data frame with the following columns: 
#              viable       -- the number of viable wells.
#              inviable     -- the number of inviable wells.
#              cells.per.ml -- the cell density.
#  f      -- the column name of the factor that separates the different 
#            samples as a character string.
#  method -- Which function to use for fitting. "logistf" is better for
#            sparse datasets, but is slow. "glm" may fail to converge on
#            some datasets, but is much faster.
#
# Output  -- a list with the color names and numbers used for plotting
#            characters. 

  library(logistf)

#  Change these paths as needed.  Both functions are available on the
# honeycomb source code database.
  source("~/Documents/Code/R/utilities/expand_df.r")
  source("~/Documents/Code/R/utilities/logaxis.r")

  dat <- droplevels(dat)
  ord <- levels(as.factor(dat[[f]]))
  dat <- within(dat, via.freq <- viable/(viable+inviable))
  jittered <- within(dat, via.freq <- jitter(via.freq,amount=jit))

  if (missing(ltys)) ltys <- rep(1, length(dat[[f]]))

  plot(via.freq~cells.per.ml, data=jittered, 
       xlab="Initial coculture density (cells/ml)", ylab="Fraction viable",
       log="x", cex=1.5, axes=FALSE, type="n", ...)

  log.axis(1,major.cex=1.5, lwd.ticks=par()$lwd)
  axis(2, lwd=par()$lwd, las=2)
  box()


  n <- 1
  if (missing(f)) {
    fit <- fitter(method,dat)

    points(via.freq~cells.per.ml,data=jittered, col=cols[n], pch=n, cex=1.5)
    curve(1/(1+exp(-coef(fit)[2]*log(x)-coef(fit)[1])), add=TRUE,
          col=cols[n], lty=ltys[n])
  } else {
    for (name in ord) {
      type <- subset(dat,dat[[f]] %in% name)
      jittered.ss <- subset(jittered,jittered[[f]] %in% name)
      fit <- fitter(method,type)

      points(via.freq~cells.per.ml,data=jittered.ss, col=cols[n], 
             pch=n, cex=1.5)
      curve(1/(1+exp(-coef(fit)[2]*log(x)-coef(fit)[1])), add=TRUE, 
            col=cols[n], lty=ltys[n])
      n <- n+1
    }
    n <- n-1
  }

  if (show.legend) {
    fs <- if (missing(f) && missing(leg)) 
            1:n 
          else if (!missing(leg)) 
            leg
          else 
            as.character(levels(as.factor(dat[[f]])))
    legend("topleft", legend=fs, col=cols[1:n], lty=ltys[1:n], pch=1:n, 
           bg="white", cex=leg.size)
  }
  list(colors=cols[1:n], pch=1:n)
}

fitter <- function(meth, dat) {
  fit <- if (meth=="logistf") {
           logistf(case~log(cells.per.ml), data=expand.df(dat))
         } else if (meth=="glm") {
           glm(cbind(viable,inviable)~log(cells.per.ml), data=dat,
                 family=binomial())
         } else { stop("Don't recognize that fitting method!") }
  fit
}

