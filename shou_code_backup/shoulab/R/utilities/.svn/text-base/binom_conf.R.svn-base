# vim: set filetype=r tabstop=4 expandtab shiftwidth=4 :

binom.conf <- function(x, size, conf.level=0.95, step=1E-4, start=0,
                       stop=1, plot=FALSE, ...) {
# Given 'x' events in 'size' observations, determines the 95% confidence 
# interval for the probability observing 'x'.

    obs.p <- x/size
    alpha <- 1-conf.level
    lb <- alpha/2
    ub <- 1-alpha/2
    nms <- c(paste(as.character(100*alpha/2),"%",sep=""),
             paste(as.character(100*(1-alpha/2)),"%",sep=""))

    tries <- seq(start,stop,by=step) # Generate all probabilities to test.

    # pbinom returns the probability of observing x or less events.
    # Pr(K<=x).
    pr <- 1-pbinom(q=x, size=size, prob=tries)

    ci <- c(tries[max(which(pr<lb))],tries[min(which(pr>ub))])

    if (identical(plot,TRUE)) {
        par(mfrow=c(1,2),mar=c(4,4,2,1),cex=2,lwd=3,mex=1)
        plot(dbinom(x=x, size=size, prob=tries)~tries,xlab=expression(p),
             ylab=expression(P(p==hat(p))),...)
        draw(obs.p,ci,alpha)

        plot(pr ~ tries,xlab=expression(p),ylab=expression(P(p<hat(p))),...)
        draw(obs.p,ci,alpha)
    }

    if (length(ci) == 1) ci <- c(0,ci)
    names(ci) <- nms

    ans <- list(obs.p=obs.p,confint=ci)

    ans
}
draw <- function(phat,cint,a) {
    abline(v=phat,lty=1)
    abline(v=cint[1],lty=2)
    abline(v=cint[2],lty=2)
    mtext(3, at=phat, text=expression(hat(p)),cex=par()$cex)
    mtext(3, at=cint[1], text=paste(100*a/2,"%",sep=""),cex=par()$cex)
    mtext(3, at=cint[2], text=paste(100*(1-a/2),"%",sep=""),cex=par()$cex)
}
