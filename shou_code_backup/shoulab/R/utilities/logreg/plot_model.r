# :vim filetype=r :
plot.model <- function(dat, model, f, jit=NULL, type="curve",
                       cols=palette(),
                       ltys,
                       leg, leg.title=NULL, leg.size=1,
                       leg.pos="topleft", show.legend=TRUE, ...)
{
# Takes data fitted using glmer and plots it, splitting the data according
# to the factor 'f'.

  library(lme4)

  dat <- droplevels(dat)
  dat <- within(dat, via.freq <- viable/(viable+inviable))
  jittered <- within(dat, {
                     via.freq <- jitter(via.freq,amount=jit)
                     #cells.per.ml <- jitter(cells.per.ml,amount=jit)
                       })
  ref <- levels(dat[[f]])[1]
  n.factors  <- length(levels(dat[[f]]))
  if (missing(ltys)) ltys <- rep(1,n.factors)

  if (class(model)=="mer") {
    coefs <- lme4::fixef(model)
    std.error <- sqrt(diag(vcov(model)))
    ci.upper  <- coefs+2*std.error
    ci.lower  <- coefs-2*std.error
  } else if (class(model)=="logistf") {
    coefs <- coef(model)
    ci.upper <- model$ci.upper
    ci.lower <- model$ci.lower
  } else if (any(class(model)=="glm")) {
    coefs <- coef(model)
    errs  <- confint(model)
    ci.upper <- errs[,2]
    ci.lower <- errs[,1]
  }
  inter <- coefs["(Intercept)"]
  lcpm  <- coefs["log(cells.per.ml)"]

  effect   <- coefs[grep(f,names(coefs),value=TRUE)]
  ci.upper <- ci.upper[grep(f,names(ci.upper),value=TRUE)]
  ci.lower <- ci.lower[grep(f,names(ci.lower),value=TRUE)]

  others <- grep(paste("\\(Intercept\\)|log\\(cells.per.ml\\)|",f,sep=""),
                 names(coefs), value=TRUE, invert=TRUE)

  if (type=="curve") {
    plot(via.freq~cells.per.ml, data=jittered, type="n",axes=FALSE,
         xlab="Initial co-culture density (cells/ml)", 
         ylab="",
         log="x", cex=1.5, ...)
    log.axis(1,major.cex=1.5, lwd.ticks=par()$lwd)
    axis(2, lwd.ticks=par()$lwd, las=2)
    mtext(side=2,text="Fraction viable", line=3.7, 
          cex=par()$cex*par()$cex.lab)
    box()

    n <- 1
    for (type in split(jittered,jittered[[f]])) {
      points(via.freq~cells.per.ml, data=type, col=cols[n], pch=n, cex=1.5)
      n <- n+1
    }
    n <- 1
    curve(1/(1+exp(-lcpm*log(x)-inter)), add=TRUE,
          col=cols[n], lty=ltys[n])
    n <- n+1
    for (e in effect) {
      curve(1/(1+exp(-(lcpm*log(x)+e)-inter)), add=TRUE,
            col=cols[n], lty=ltys[n])
      n <- n+1
    }
    n <- n-1
    if (show.legend) {
      fs <- if (missing(f) && missing(leg)) 
              1:n 
            else if (!missing(leg)) 
              leg
            else 
              as.character(levels(as.factor(dat[[f]])))
      legend(leg.pos, title=leg.title, legend=fs, col=cols[1:n],
             pch=1:n, bg="white", cex=leg.size)
    }
    list(colors=cols[1:n], pch=1:n)
  } else if (type=="beta") {
    x.vals <- 1:length(effect)
    x.labs <- if (missing(leg)) gsub(f,"",names(effect)) else leg
    plot(range(x.vals),range(0,ci.lower,ci.upper),type="n",
         ylab=paste("ln(odds ratio) of viability vs ",ref,sep=""),
         xlab=f, axes=FALSE,...)
    axis(1,at=x.vals,labels=x.labs,lwd.ticks=par()$lwd,cex.axis=1.2)
    axis(2,lwd.ticks=par()$lwd, las=2)
    box()
    points(x.vals,effect,cex=1.5)
    arrows(x.vals,ci.lower,x.vals,ci.upper,code=3,angle=90,length=0.1)
    abline(h=0,lty=2)
  }
}
