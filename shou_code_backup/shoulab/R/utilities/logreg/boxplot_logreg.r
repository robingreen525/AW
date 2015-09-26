# : vim filetype=r :
boxplot.logreg <- function(dat, f, box.lims=c(0.025,0.5,0.975), 
                           x.pos, colors, xlab=f, draw="box", ...)
{
  library(logistf)
  library(gdata)
  source("~/Documents/Code/R/utilities/expand_df.r")
  source("~/Documents/Code/R/utilities/logaxis.r")
  source("~/Documents/Code/R/utilities/logreg/solve_logreg.r")

  ord <- levels(as.factor(dat[[f]]))
  if (missing(colors)) colors <- rep("black", length(ord))

  boxes <- NULL
  dat.exp <- expand.df(dat,c("viable","inviable"))
  for (name in ord) {
    type <- subset(dat.exp, dat.exp[[f]] %in% name)
    fit <- logistf(case~log(cells.per.ml), data=type)
    x <- solve.logreg(fit, box.lims)
    boxes <- rbind(boxes, data.frame(type=unique(type[f]),
                                     low.error=x[1], low=x[2], med=x[3],
                                     hi=x[4], hi.error=x[5]))
  }
  if (missing(x.pos))  x.pos <- 1:nrow(boxes)

  pad <- 0.5
  plot(range(x.pos), range(boxes$low,boxes$hi), type="n", log="y",
       xlim=c(x.pos[1]-pad,x.pos[length(x.pos)]+pad),
       axes=FALSE, ann=FALSE, ...)

  axis(1,at=x.pos, labels=boxes[[f]], lwd.ticks=par()$lwd,
       cex.lab=0.7)
  log.axis(2,las=2,lwd.ticks=par()$lwd, major.cex=1.5)

  mtext(side=1,text=xlab, line=3.5,
        cex=par()$cex.lab*par()$cex)
  mtext(side=2,text="Initial co-culture density (cells/ml)", line=3.5,
        cex=par()$cex.lab*par()$cex)
  box()

  width <- 1
  space <- 0.1
  for (i in 1:nrow(boxes)) {
    lbord <- (x.pos[i]-width/2)+space
    rbord <- (x.pos[i]+width/2)-space
    if (draw=="box") {
      rect(xleft=lbord, xright=rbord,
           ybottom=boxes[i,]$low, ytop=boxes[i,]$hi,
           col="white", border=colors[i])
      segments(x0=lbord, x1=rbord, y0=boxes[i,]$med, col=colors[i])
#    segments(x0=i, y0=boxes[i,]$low.error, y1=boxes[i,]$low)
#segments(x0=i, y0=boxes[i,]$hi, y1=boxes[i,]$hi.error)
    } else {
      points(x=x.pos[i],y=boxes[i,]$med, col=colors[i], cex=1.5)
      arrows(x.pos[i], boxes[i,]$low,
             x.pos[i], boxes[i,]$hi, col=colors[i],
             length=0.1, code=3, angle=90)
    }
  }
}

