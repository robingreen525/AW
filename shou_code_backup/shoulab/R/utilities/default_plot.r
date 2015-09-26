default.plot <- function(w=15,h=10, device="x11",name="") {
    plot.name <- paste(name,".",device,sep="")
    if (!grepl("lattice", device)) {
        dev <- get(device)
        if (device=="x11") {
            dev(width=w,height=h)
        } else if (device=="pdf") {
            dev(file=plot.name, width=w,height=h, useDingbats=FALSE)
        } else if (device=="png") {
            dev(file=plot.name, width=w,height=h, units="in", res=120)
        } else if (device=="svg") {
            dev(file=plot.name, width=w,height=h)
        }
        par(mar=c(5,5.3,1.5,1), 
            family="NimbusSan",
            mex=0.8,
            lwd=4.0,
            cex=2, 
            cex.axis=1.5,
            cex.lab=1.5,
            font=2,
            font.axis=2,
            font.lab=2,
            font.main=2,
            font.sub=2)
        palette(c("black","orange","green","steelblue","purple","grey","cyan","salmon","goldenrod","darkgreen"))

    } else if (grepl("lattice",device)) {
        dev <- NULL

        if (grepl("x11", device)) {
            dev <- "x11"
        } else if (grepl("pdf", device)) {
            dev <- "pdf"
        } else if (grepl("png", device)) {
            dev <- "png"
        } else if (grepl("svg", device)) {
            dev <- "svg"
        }

        plot.name <- paste(name,".",dev,sep="")

        lattice.theme <- list(
                              #axis.line=list(lwd=3),
                              par.xlab.text=list(cex=2,font=2),
                              par.ylab.text=list(cex=2,font=2),
                              axis.text=list(cex=2,font=2),
                              plot.symbol=list(cex=2,font=2),
                              superpose.symbol=list(cex=rep(2,7)),
                              superpose.line=list(lwd=rep(3,7))
                              )
        if (dev == "x11") {
            trellis.device(dev, color=FALSE, width=w, height=h)
        } else {
            trellis.device(dev, color=FALSE, width=w, height=h, file=plot.name)
        }
        trellis.par.set(lattice.theme)
        lattice.theme
    }
}
