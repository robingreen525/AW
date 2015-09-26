log.axis <-
function (side, limits, exp.format=TRUE,
          grid=TRUE, grid.lty="solid", grid.col="lightgray",
          grid.lwd=par()$cex.axis,
          major=TRUE, minor=TRUE, label.minor=FALSE,
          minor.cex=0.75, major.cex=par()$cex.axis, ...)
{

# \name{log.axis}

# \title{Add nice log-10 axis tics and labels to a plot}

# \description{Function to add a nice log base-10 axis
# to a plot, with major/minor tics and optional gridlines.
# See \code{log.tics} for details on generating tic positions.}

if (!side %in% 1:4)
   stop("Side must be 1, 2, 3, or 4.")

if ((side == 1 || side == 3) & !par("xlog"))
    stop("x-axis must be on a log scale")
if ((side == 2 || side == 4) & !par("ylog"))
    stop("y-axis must be on a log scale")

if (missing(limits)) {
   if (side == 1 || side == 3)
      limits <- par("usr")[1:2]
   else
      limits <- par("usr")[3:4]
   limits <- 10^limits      
}

# Shrink limits by a buffer
# :: Currently we just plot the lines if they fit inside the
# plot area;  probably better to err on giving more axis info to
# your audience, since log axes can be a bit confusing
#if ((side == 1 || side == 3) & par("xaxs") == "r")
#   limits <- c(limits[1]+0.04*diff(limits),limits[2]-0.04*diff(limits))
#if ((side == 2 || side == 4) & par("yaxs") == "r")
#   limits <- c(limits[1]+0.04*diff(limits),limits[2]-0.04*diff(limits))


tics <- log.tics(limits, exact10=FALSE)
maj <- tics$major.tics
#maj <- maj[maj < limits[2] & maj > limits[1]]
min <- tics$minor.tics
#min <- min[min < limits[2] & min > limits[1]]
# Remove minor tics coinciding with major tics
min <- min[!min %in% maj]
# skip over minor tics by value of `minor.skip', default=1
#min <- min[seq(length(min),1,by=-minor.skip)]

min.exp <- log10(min)
maj.exp <- log10(maj)

# draw major tics
if (major) {
   par(tcl=-0.5)
   if (exp.format)
     axis(side,maj,labels=parse(text=paste("10^",maj.exp)), cex.axis=major.cex,...)
   else
     axis(side,maj,labels=formatC(maj,format="fg"), cex.axis=major.cex,...)
   # draw grid lines at major tic positions if desired
   if (grid){
      if (side == 1 || side == 3)
        abline(v=maj, col=grid.col, lty=grid.lty, lwd=grid.lwd)
      else
        abline(h=maj, col=grid.col, lty=grid.lty, lwd=grid.lwd)
   }
}

# draw minor tics
if (minor) {
   par(tcl=-0.25)
   if (label.minor) { # Draw labels at minor tic positions?
      if (exp.format)
        axis(side, min, labels=parse(text=paste("10^",min.exp)), cex.axis=minor.cex, ...)
      else
        axis(side, min, labels=formatC(min, format="fg"), cex.axis=minor.cex, ...)
   } else 
      axis(side, min, labels=F, ...)
   # draw grid lines at minor tic positions if desired
   if (grid) {
      if (side == 1 || side == 3)
         abline(v=min, col=grid.col, lty=grid.lty, lwd=grid.lwd)
      else
         abline(h=min, col=grid.col, lty=grid.lty, lwd=grid.lwd)
   }
   # reset tic length
   par(tcl=-0.5)
}

}

log.tics<-
function (x, exact10=TRUE)
{

# \name{log.tics}

# \title{Calculate nice tics for log axes}

# \description{Find nice log base-10 axis tics.
# Values in x less than 0 are removed.
# Can set axis tics to exact multiples of
# ten, e.g., 0.1, 1, 10, if \code{exact10},
# or nearest 10^i increment, e.g., 0.2 or 0.3 (for i = -1), otherwise.}

# \arguments{\item{x} a vector of data or data range for which tics will be created}

# \value{a list containing the input data range (from x), the calculated tic range,
# and the calculated major and minor tic values.}

# UPDATE:  tolerance for getting close to an exact10 tic.  See lpplot for
#         more.....

#  UPDATE:  Fixed exact10 calculation for limits equal to exactly 0.1/10
#                          --NK 13-Mar-04

# ------------------

x<-as.numeric(x)
xlim <- range(x, na.rm=TRUE)

if (any(x <= 0)) {
   warning("Some x data <= zero. Setting to NA.")
   x[x <=0] <- NA
   ok <- complete.cases(x)
   x<-x[ok]
   xlim <- range(x, na.rm=T)
}

x2 <- ifelse(xlim[2]<=0.1,trunc(log10(xlim[2])-0.0001),trunc(log10(xlim[2])+0.9999))
x1 <- ifelse(xlim[1]>=10,trunc(log10(xlim[1])),trunc(log10(xlim[1])-0.9999))
if (!exact10) {
   xlim[1] <- 10^x1*trunc(xlim[1]/10^x1)
   xlim[2] <- 10^(x2-1)*ceiling(xlim[2]/10^(x2-1))
} else {
   xlim <- c(10^x1,10^x2)
}
tics.min <- c()
tics.maj <- c()
if (10^x1 >= xlim[1])
   tics.maj <- as.numeric(formatC(10^x1,format="fg"))
for (i in x1:(x2-1)) {
    if (10^(i+1) <= xlim[2])
       tics.maj <- c(tics.maj, as.numeric(formatC(10^(i+1),format="fg")))
    f <- ifelse(i==x1, xlim[1] / 10^x1, 1)
    e <- ifelse(i==(x2-1), xlim[2], 10^(i+1)-10^i)
    tics.min <- c(tics.min, seq(f*10^i, e, by=10^i))
}

list(true.range=range(x), tic.range=range(tics.min),
     minor.tics=tics.min, major.tics=tics.maj)

}
