source("~/Documents/Code/R/utilities/default_plot.r")

metapop.process <- function(path) {
    if (is.na(file.info(path)$size)) {
        cat("\nFile", path, "is not a file.\n")
        return()
    }
    if (file.info(path)$size == 0) {
        cat("\nFile", path, "had 0 size.\n")
        return()
    }

    info <- as.character((grep("^#", readLines(path), value=TRUE)))
    info <- substring(info,2)

    rows <- as.numeric(gsub("rows: (\\d+).*", "\\1", info, perl=TRUE))
    cols <- as.numeric(gsub(".*cols: (\\d+).*", "\\1", info, perl=TRUE))
    locations <- rows*cols

    dat <- read.delim(path, comment.char="#", stringsAsFactors=FALSE)

    dat <- within(dat, hours <- timestep/60)

    dat.summary <- aggregate(dat[grep("coop|cheat", names(dat))],
                             list(timestep=dat$timestep,
                                  hours=dat$hours), 
                             function(x) sum(x)/locations)
    binaries <- c("coops.extinct", "cheats.extinct", "all.extinct",
                  "env.changed")
    for (n in binaries) {
        dat.summary[n] <- unique(dat[n])
    }

    dat.summary <- within(dat.summary, coop.ratio <- coops/cheats)
    dat.summary <- within(dat.summary, coop.freq <- coops/(coops+cheats))
    list(info=info, data=dat, summary=dat.summary)
}

metapop.plot <- function(dat, xrange, yrange, plot.name,
                         device="x11",  pad=1, add=FALSE, ...)
{
    # Plots output with all timepoints in one file.

    extinct <- function(dat, cutoff=1) {
        dat <- within(dat, {coop.switch   <- FALSE
                            cheat.switch  <- FALSE
                            coop.extinct  <- FALSE
                            cheat.extinct <- FALSE}
        )
        for (i in 1:nrow(dat)) {
            if (dat[i,]$coops < cutoff)  {
                dat[i,]$coops <- cutoff
                dat[i,]$coop.extinct  <- TRUE
            }
            if (dat[i,]$cheats < cutoff) {
                dat[i,]$cheats <- cutoff
                dat[i,]$cheat.extinct <- TRUE
            }
        }

        for (i in 2:nrow(dat)) {
            if ((dat[i,]$coop.extinct && !dat[i-1,]$coop.extinct) ||
                (!dat[i,]$coop.extinct && dat[i-1,]$coop.extinct))
            {
                dat[i,]$coop.switch <- TRUE 
            }

            if ((dat[i,]$cheat.extinct && !dat[i-1,]$cheat.extinct) ||
                (!dat[i,]$cheat.extinct && dat[i-1,]$cheat.extinct))
            {
                dat[i,]$cheat.switch <- TRUE 
            }
        }
        dat
    }

    means <- extinct(dat$summary)

    if (missing(xrange)) xrange <- range(means["timestep"],na.rm=TRUE)
    if (missing(yrange)) yrange <- range(means[c("coops","cheats")]+pad,
                                         maxes[c("coops","cheats")]+pad,
                                         1e7,na.rm=TRUE)

    plot.width <- 10
    plot.height <-10 

    # Plot means
    default.plot(w=plot.width, h=plot.height, device,
                 paste(plot.name,"_cells",sep=""))
    plot(xrange, yrange, type="n", log="y", axes=FALSE, 
         xlab="Time (hrs)", ylab="", ...)

    title(main=basename(plot.name),cex.main=0.5)
    mtext(side=2, text="Cells", line=3.5, cex=3)
    axis(1,lwd=par()$lwd, cex.axis=1)
    log.axis(2,lwd=par()$lwd, las=2)
    box(lwd=par()$lwd)

    # Plot frequency
    default.plot(w=plot.width, h=plot.height, device,
                 paste(plot.name,"_freq",sep=""))
    plot(xrange, c(0,1), type="n", axes=FALSE, 
         xlab="Time (hrs)", ylab="", ...)

    title(main=basename(plot.name),cex.main=0.5)
    mtext(side=2, text="Coop. frequency", line=3.5, cex=3)
    axis(1,lwd=par()$lwd, cex.axis=1)
    axis(2,lwd=par()$lwd, las=2)
    box(lwd=par()$lwd)

    lines(coop.freq~timestep, data=means, col="black", lwd=4)

    # Switch to cell plot.
    dev.set(dev.next())

    lines(coops~timestep, data=means, subset=!coop.extinct, col="red", lwd=3)
    points(coops~timestep, data=means, subset=coop.switch, col="red")
    lines(cheats~timestep, data=means, subset=!cheat.extinct, col="blue",
          lwd=3)
    points(cheats~timestep, data=means, subset=cheat.switch, col="blue")

    gc()
    if (device != "x11") graphics.off()
}

metapop.plotsurface <- function(dat, device="lattice.x11", save.to, plot.name, 
                                type="level", show.info=TRUE, ...)
{
    library(lattice)
    graphics.off()

    if (missing(plot.name) && device == "lattice.x11") {
        plot.name <- ""
    }


    default.plot(paste("lattice.",device,sep=""), w=15, h=9,
                 name=file.path(save.to,plot.name))

    surf <- NULL
    if (type=="level") {
        z <- dat$coop.freq.mean
        z[z==0] <- NA
        z[z<1e-3] <- 2e-3
        z <- log10(z)

        y <- dat$max.death.rate/max(dat$max.death.rate)

        key.seq <- log10(c(1e-3, sqrt(10)*1e-3, 1e-2, sqrt(10)*1e-2, 1e-1,
                           0.5, 0.6, 0.7, 0.8, 0.9, 1))
        key.label <- log10(c(1e-3, sqrt(10)*1e-3, 1e-2, sqrt(10)*1e-2, 
                             1e-1, 0.25, 0.5, 0.75, 1))
        key.label.text <-
            c(parse(text=paste("10^", key.label[key.label<log10(0.25)])),
                                      10^key.label[key.label>=log10(0.25)])
        key.label.text[1] <- expression(phantom()<=10^{-3})

        x.at <- unique(dat$migration.rate)
        x.label <- parse(text=paste("10^", log10(x.at)))

        y.at <- rev(unique(y))
        y.label <- parse(text=paste("10^", log10(y.at)))

        level.panel <- function(x,y,z, ...) {
            panel.levelplot(x=x,y=y,z=z,...)

            n.cheats.extinct <- dat$cheats.extinct
            total.extinct <- dat$cheats.extinct+dat$coops.extinct
            cheat.extinct.text <- ifelse(n.cheats.extinct>0,
                                         paste(n.cheats.extinct, "/",
                                               total.extinct, sep=""), "")
            if (show.info) {
                panel.text(x=x,y=y, labels=cheat.extinct.text,
                           col="cyan", cex=1.8)
                panel.text(x=x,y=y,
                           labels=paste("\n\nn=",dat$runs.used,sep=""),
                           col="black", cex=1.5)
            }
            panel.abline(v=log10(x.at)-0.5, h=log10(y.at)-0.5,
                         col="black", lwd=trellis.par.get()$axis.line$lwd, ...)
        }

        surf <- levelplot(z~migration.rate*y,
                          data=dat,
                          ylab="Max death rate / Max growth rate",
                          main=plot.name,
                          at=key.seq,
                          colorkey=list(labels=list(at=key.label,
                                                    labels=key.label.text)),
                          col.regions=colorRampPalette(c("blue",
                                                         "purple","red")),
                          panel=level.panel,
                          scale=list(x=list(at=x.at, label=x.label, log=10),
                                     y=list(at=y.at, label=y.label, log=10))
                         )
    } else {
        wireframe.panel <- function(x,y,z,...) {
            panel.wireframe(x,y,z,...)
            panel.cloud(x,y,z=ifelse(dat$coops.extinct |
                                     dat$all.extinct, 0, NA),
                        col=ifelse(dat$all.extinct,"black",
                            ifelse(dat$coops.extinct & !dat$cheats.extinct,
                                   "red", "white")), 
                        cex=3.5, ...)
        }
        surf <- wireframe(coop.freq~migration.rate * max.death.rate,
                          data=dat,
                          panel=wireframe.panel,
                          main=plot.name,
                          aspect=c(1,0.3),
                          zlim=c(0,1),
                          scale=list(arrows=FALSE,
                                     x=list(log=10),
                                     y=list(log=10, rot=50),
                                     cex=1.5, lwd=2, tck=c(0.5,1.5,1.5),
                                     distance=c(1,10,3)),
                          screen=list(z=15,x=-60,y=0),
                          drape=TRUE,
                          par.box=list(lwd=2),
                          panel.aspect=0.7)
    }

    print(surf)
    if (device != "lattice.x11") dev.off()
    #surf
}

metapop.summarize.runs <- function(folder, data.ext="tab", last=5,
                                   run.match="", ...)
{
    graphics.off()
    res.names <- c("type","initial.occupancy", "mutant.freq", "u",
                   "env.change.rate", "migration.rate",
                   "max.death.rate", "coop.freq.mean", "coop.freq.sd",
                   "runs.used", "timepoints.used", "last", "coops.extinct",
                   "cheats.extinct", "all.extinct")


    conditions <- list.files(folder, full.names=TRUE)
    conditions <- subset(conditions, grepl("^.*ar_",conditions) &
                         file.info(conditions)$isdir)
    name.match <- paste("^.*/(.*)\\.",data.ext,sep="")

    res <- data.frame(matrix(0, nrow=length(conditions), 
                                ncol=length(res.names)))
    names(res) <- res.names
    n.conditions <- length(conditions)
    for (i in seq_along(conditions)) {
        cat("\rProcessing ", i, "/", n.conditions, sep="")

        matched <- sub(".*ar_(.*?)_.*u=(.*?)_.*mutr=(.*?)_.*mr=(.*?)_.*dr=-(.*?)_.*env=(.*?)_.*hrs=(.*?)_.*occ=(.*?)_.*", "\\1,\\2,\\3,\\4,\\5,\\6,\\7,\\8",
                       conditions[i], perl=TRUE)

        params <- strsplit(x=matched, split=",")[[1]]
        res[i,]$type              <- params[1]
        res[i,]$u                 <- as.numeric(params[2])
        res[i,]$mutant.freq       <- as.numeric(params[3])
        res[i,]$migration.rate    <- as.numeric(params[4])
        res[i,]$max.death.rate    <- as.numeric(params[5])
        res[i,]$env.change.rate   <- as.numeric(params[6])
        res[i,]$initial.occupancy <- as.numeric(params[8])

        max.hrs <- as.numeric(params[7])

        if (any(is.na(res[i,]))) {
            cat("\nFound NA in", conditions[i], "\n")
            browser()
        }

        runs   <- list.files(conditions[i], full.names=TRUE, pattern=run.match)
        runs   <- subset(runs, file.info(runs)$isdir)
        n.runs <- length(runs)

        dat.files <- list.files(runs, full.names=TRUE)
        dats <- data.frame(matrix(0, nrow=length(dat.files), ncol=14))
        names(dats) <- names(metapop.process(dat.files[1])$summary)
        used <- 0
        for (j in seq_along(runs)) {
            run     <- list.files(runs[j], full.names=TRUE)
            n.files <- length(run)

            all.timepoints <- as.numeric(sub(name.match,"\\1", run, perl=TRUE))
            run.ordered <- run[order(all.timepoints)]

            if (n.files == 1) {
                cat("\nonly one file found in folder", runs[j], "\n")
                next
            }

            # either the run isn't finished, or something has gone extinct.
            if (max(all.timepoints) < max.hrs*60) {
                dat <- tryCatch(metapop.process(run.ordered[n.files])$summary,
                                error=function(e) browser())

                # add to data if something is extinct.
                if (dat$coops.extinct == 1 || dat$cheats.extinct == 1) {
                    dats[used+1,] <- dat
                    used <- used + 1
                } else {
                    next
                }
            } else {
                for.analysis <- tail(run.ordered, last)
                for (k in seq_along(for.analysis)) {
                    dats[used+1,] <- metapop.process(for.analysis[k])$summary
                    used <- used + 1
                }
            }
        }
        dats <- dats[1:used,]
        dats <- dats[order(dats$timestep),]

        coops.extinct  <- sum(dats$coops.extinct)
        cheats.extinct <- sum(dats$cheats.extinct)
        all.extinct    <- sum(dats$all.extinct)

        mean.freq <- mean(dats$coop.freq)
        sd.freq   <- sd(dats$coop.freq)

        last.timepoint <- tail(dats, n.runs)
        all.coops.extinct  <- all(last.timepoint$coops.extinct == 1)
        all.cheats.extinct <- all(last.timepoint$cheats.extinct == 1)
        all.all.extinct    <- all(last.timepoint$all.extinct == 1)
        if (all.coops.extinct || all.cheats.extinct) {
            sd.freq <- 0
            if (all.all.extinct || all.coops.extinct) {
                mean.freq   <- 0
            }
            if (all.cheats.extinct) {
                mean.freq <- 1
            }
        }

        res[i,]$coop.freq.mean  <- mean.freq
        res[i,]$runs.used       <- n.runs
        res[i,]$timepoints.used <- used
        res[i,]$coops.extinct   <- coops.extinct
        res[i,]$cheats.extinct  <- cheats.extinct
        res[i,]$all.extinct     <- all.extinct
        res[i,]$coop.freq.sd    <- sd.freq
        res[i,]$last            <- last

        #if (max(all.timepoints) == max.hrs*60) browser()
    }
    cat("\n")
    res
}


metapop.surface <- function(folder, data.ext="tab", subfolder=1, ...)
{
    files <- list.files(folder,full.names=TRUE)
    name.match <- paste("^.*/(.*)\\.",data.ext,sep="")
    file.names <- sub(name.match,"\\1",files,perl=TRUE)

    res.names <-c("migration.rate","max.death.rate","coop.freq")

    res <- data.frame(matrix(0, nrow=length(files), ncol=3))
    names(res) <- res.names
    for  (i in seq_along(files)) {
        res[i,]$migration.rate <- 
            60*as.numeric(sub(".*mr=(.*)_dr.*","\\1", files[i], perl=TRUE))
        res[i,]$max.death.rate <- 
            60*as.numeric(sub(".*dr=-(.*)_max-pop.*","\\1",
                              files[i], perl=TRUE))

        dat <- metapop.process(files[i][subfolder])$summary

        freq.range <- (nrow(dat)-100):nrow(dat)
        res[i,]$coop.freq <- mean(dat[freq.range,"coop.freq"])
    }
    res
}

metapop.plotfolder <- function(folder, save.path, run.pattern,
                               device="svg", ...)
{
    if (missing(save.path)) {
        save.folder <- ifelse(grepl("^/", folder), substring(folder,2), folder)
        save.path <- file.path("fig", save.folder)
    }
    if (!file.exists(save.path)) dir.create(save.path, recursive=TRUE)

    folders <- list.files(folder, full.names=TRUE, pattern="ar_")
    runs <- if (missing(run.pattern)) {
        lapply(folders, function (x) list.files(x, full.names=TRUE)[1])
    } else {
        list.files(folders, full.names=TRUE, pattern=run.pattern)
    }

    lapply(runs, function(x) metapop.timepoints(x, device=device,
                                                save.path=save.path))
}

metapop.timepoints <- function(folder, timepoint.range, row.col,
                               data.ext="tab", device="x11", save.path)
{
    graphics.off()
    if (is.na(folder)) stop("Folder is NA!")
    # Plot data that is represented by one file per time step.
    save.path <- save.to(data.folder=folder, save.path)
    files <- get.files(folder, data.ext)
    f <- files$files
    timepoints <- files$timepoints

    rc <- ifelse(missing(row.col), "", row.col)

    passed.xlim <- eval(match.call()$xlim)
    hrs <- if (is.null(passed.xlim)) {
               timepoints/60.0
            } else {
                seq(passed.xlim[1],passed.xlim[2])
    }

    log.hrs <- hrs+1

    ave.cell.lab <- "Ave. cells/location"
    freq.lab     <- "Coop. freq"
    setup.plot <- function(y.lab, plot.name, log.x) {
        x.lab <- "Time (10^3 x hrs)"
        log.x.lab <- "Time (hrs)"
        title(main=basename(plot.name),cex.main=0.3)
        mtext(side=2, text=y.lab, line=3.6, cex=3)
        if (log.x) {
            mtext(side=1, text=log.x.lab, line=3.3, cex=3)
            log.axis(1,lwd=par()$lwd, cex.axis=1.2)
        } else {
            mtext(side=1, text=x.lab, line=3.3, cex=3)
            axis(1,lwd=par()$lwd, at=pretty(hrs),
                 labels=pretty(hrs)/1000, cex.axis=1.2)
        }
        if (y.lab==ave.cell.lab)
            log.axis(2,lwd=par()$lwd, las=2, cex.axis=1.2)
        else
            axis(2,lwd=par()$lwd, las=2, cex.axis=1.2)
        box(lwd=par()$lwd)
    }

    plot.width <- 10
    plot.height <-10 

    # frequency plot
    plot.name <- paste(basename(dirname(folder)),"_freq",sep="")
    default.plot(w=plot.width, h=plot.height, device,
                 file.path(save.path, plot.name))
    plot(range(hrs), c(0,1), log="", type="n", axes=FALSE, ann=FALSE)
    setup.plot(freq.lab, plot.name, FALSE)

    # cell plot
    plot.name <- paste(basename(dirname(folder)),"_cells",sep="")
    default.plot(w=plot.width, h=plot.height, device,
                 file.path(save.path,plot.name))
    plot(range(hrs), c(1,1e7), type="n", log="y", axes=FALSE, ann=FALSE)
    setup.plot(ave.cell.lab, plot.name, FALSE)

    # log x frequency plot
    plot.name <- paste(basename(dirname(folder)),"_freq_logx",sep="")
    default.plot(w=plot.width, h=plot.height, device,
                 file.path(save.path,plot.name))
    plot(range(log.hrs), c(0,1), log="x", type="n", axes=FALSE, ann=FALSE)
    setup.plot(freq.lab, plot.name, TRUE)

    # log x cell plot
    plot.name <- paste(basename(dirname(folder)),"_cells_logx",sep="")
    default.plot(w=plot.width, h=plot.height, device,
                 file.path(save.path,plot.name))
    plot(range(log.hrs), c(1,1e7), type="n", log="xy", axes=FALSE, ann=FALSE)
    setup.plot(ave.cell.lab, plot.name, TRUE)


    # start with frequency plot.
    dev.set(dev.next())

    env.lwd <- 2
    opar <- par(lwd=2)
    coop.alive <- TRUE
    cheat.alive <- TRUE
    for (i in 2:(length(f))) {

        f.current <- f[i]
        f.prev    <- f[i-1]
        current <- NULL
        prev <- NULL
        current.all <- metapop.process(f.current)

        if (is.null(current.all$summary)) {
            cat("\n[timepoints] File", f.current, "has no data. Skipping.\n")
            i <- i+1
            next
        }


        prev.all <- metapop.process(f.prev)


        if (missing(row.col)) {
            prev <- prev.all$summary
            current <- current.all$summary
        } else {
            prev <- subset(prev.all$dat, row.col==rc)
            current <- subset(current.all$dat, row.col==rc)
        }

        paired <- rbind(prev,current)
        paired <- within(paired, coop.freq <- coops/(coops+cheats))

        times <- unique(paired$hours)
        log.times <- times+1

        for (tms in list(times,log.times)) {
            if (current$all.extinct) {
                paired[2,"coop.freq"] <- 0
                points(0~hours, data=current, pch=4, col="black")
            }
            lines(tms, paired$coop.freq, col="black", lwd=3)
            if (current$env.changed) {
                abline(v=current$hours, lwd=env.lwd)
            }
            dev.set(dev.next())

            lowest.y <- 10^(par()$usr[3])
            if (current$all.extinct) {
                paired[2,c("coops","cheats")] <- rep(lowest.y,2)
                points(lowest.y~hours, data=current, pch=4, col="black")
            } else if (current$coops.extinct &! current$cheats.extinct) { 
                if (coop.alive) {
                    paired[2,"coops"] <- lowest.y 
                    points(lowest.y~hours, data=current, pch=4, col="red")
                }
            } else if (current$cheats.extinct &! current$coops.extinct) { 
                if (cheat.alive) {
                    paired[2,"cheats"] <- lowest.y 
                    points(lowest.y~hours, data=current, pch=4, col="blue")
                }
            }
            lines(tms, paired$coops, col="red", lwd=3)
            lines(tms, paired$cheats, col="blue", lwd=3)
            if (current$env.changed) {
                abline(v=current$timestep, lwd=env.lwd)
            }
            dev.set(dev.next())
            gc()
        }

        if (current$all.extinct) {
            break
        } else if (current$coops.extinct &! current$cheats.extinct) {
            coop.alive <- FALSE
        } else if (current$cheats.extinct &! current$coops.extinct) {
            cheat.alive <- FALSE
        }
    }
    if (device!="x11") graphics.off()
}

metapop.makeheatmaps <- function(folder, first=0, last, data.ext="tab",
                                 device="jpeg", pop.size, ...) {
    graphics.off()
    save.path <- save.to(data.folder=folder, ...)
    files <- get.files(folder, data.ext)$files
    total <- length(files)
    if (missing(last)) last <- total
    for (i in (first+1):last) {
        cat("\rtime point ", i, "/", total, sep="")
        metapop.heatmap(dat=metapop.process(files[i]), max.pop=1e7,
                        save.path=save.path, pop.size=pop.size, device=device)
    }
    cat("\n")
}

metapop.heatmap <- function(dat, max.pop, save.path, pop.size=FALSE, 
                            device="jpeg", ...)
{
    if (missing(save.path)) save.path <- "."
    info <- dat$info
    dat <- dat$dat
    rows <- max(dat$row)
    cols <- max(dat$col)
    ext <- if (device != "x11") paste(".",device,sep="")
    to.screen <- if (device=="x11") TRUE else FALSE
    #device <- get(device)

    shift <- 0.5
    small.shift <- shift/10

    if (missing(max.pop)) max.pop <- max(dat[c("coops","cheats")])
    max.shades <- round(log10(max.pop),1)*10
    max.alphas <- max.shades*10

    #coop.cols  <- rgb(red=seq(0,1,length.out=max.shades),
    #                  blue=0,green=0,alpha=0.3)
    #cheat.cols <- rgb(red=0,blue=seq(0,1,length.out=max.shades),green=0,
    #                  alpha=0.3)

    #all.cols <- rainbow(n=max.shades, s=1, start=0/6, end=4/6)
    all.colors <- colorRampPalette(c("red","purple","blue"))(max.shades)

    background.grid <- function(r,c,s,color) {
        for (i in 1:r) {
            for (j in 1:c) {
                rect(i-shift,j-shift,i+shift,j+shift,col=color, border="gray")
                mtext(side=1, text=j, at=j, line=0, cex=1.5)
                mtext(side=2, text=i, at=i, line=0, las=2, cex=1.5)
            }
        }
    }

    pick.color <- function(cheat.per, dens) {
        color <- rgb2hsv(col2rgb(all.colors[cheat.per]))
        return(hsv(h=color["h",], v=color["v",], s=dens))
    }

    scale.to <- function(mi, ma, val) {
        ((ma-mi)*(val)) + mi
    }

    scale.pop <- function(pop) {
        if (pop==0)
            return(0)
        else if (pop==1)
            return(1)
        else if (pop>1)
            return(ceiling(log10(pop))*10)
    }

    format.print <- function(pop.size) {
        if (pop.size < 1000) {
            return(sprintf("%.4f", pop.size))
        } else {
            return(sprintf("%.2e", pop.size))
        }
    }






    if (to.screen) {
        get(device)
    } else {
        cat(info, file=file.path(save.path,"info.txt"))
    }

    timestep  <- unique(dat$timestep)*60
    coop.mat  <- matrix(0,nrow=rows,ncol=cols)
    cheat.mat <- matrix(0,nrow=rows,ncol=cols)

    if (!to.screen) {
        default.plot(w=10,h=10,device=device, file.path(save.path, timestep))
        #device(w=10, h=10,
               #units="in", res=120,
        #       filename=file.path(save.path, paste(timestep,ext,sep="")))
    }
    par(oma=c(1,1,1,1))
    par(lwd=2, font=1, family="sans")
    nf <- layout(matrix(c(1,2),2,1,byrow=TRUE),
                 widths=c(par()$cin[2],par()$cin[2]),
                 heights=c(par()$cin[1]*0.1,par()$cin[1]*0.9))

    opar <- par(mar=c(1,1,1,1))

    # plots color gradient at top
    plot(range(0,max.shades),c(0,1),type="n", ann=FALSE, axes=FALSE)
    for (i in 1:max.shades) {
        rect(i-shift, 0, i+shift, 1, col=all.colors[i],border=NA)
    }

    par(mar=c(1,1,1,1))
    plot(c(1,rows),c(1,cols),type="n",axes=FALSE,ann=FALSE,
         ylim=c(shift,rows+shift), xlim=c(shift,cols+shift))
    title(paste("Timestep: ", timestep, sep=""))
    left <- 1; right <- cols
    middle <- mean(c(left,right))
    mtext(side=3,at=left,  line=4.7, text="0", font=2)
    mtext(side=3,at=middle,line=4.7, text="50", font=2)
    mtext(side=3,at=right,line=4.7,text="100", adj=0, font=2)
    mtext(side=3,at=middle,line=5.7, text="Percent cheater", font=2)
    background.grid(rows,cols,shift,col="white")

    real.max.pop <- max(dat$coops + dat$cheats)
    if (real.max.pop > max.pop) max.pop <- real.max.pop

    min.saturation <- 0.1
    min.shade <- 1
    for (r in 1:nrow(dat)) {
        dat.row <- dat[r,]
        raw.coops <- dat.row$coops
        raw.cheats <- dat.row$cheats

        n.coops  <- scale.pop(raw.coops)
        n.cheats <- scale.pop(raw.cheats)
        total.pop <- n.coops + n.cheats
        dens      <- scale.to(mi=min.saturation, ma=1,
                              mean(c(n.coops,n.cheats))/max.shades)
        cheat.per <- scale.to(mi=min.shade, ma=max.shades,
                              (n.cheats/(total.pop)))

        this.color <- ifelse(total.pop==0, "#FFFFFF",
                             pick.color(cheat.per, dens))

        tryCatch(rect(dat.row$col-shift,dat.row$row-shift,
                      dat.row$col+shift,dat.row$row+shift,
                      col=this.color),
                 error=function(e) { cat("Error: ", e,"\n"); browser() } )

        if (pop.size) {
            text(x=dat.row$col, y=dat.row$row,
                 labels=paste(format.print(raw.coops),"\n",
                              format.print(raw.cheats), sep=""),
                 cex=1)
        }

    }
    if (!to.screen) dev.off() else browser()
    }

get.files <- function(folder, data.ext) {

    name.match <- paste("^.*/(.*)\\.",data.ext,sep="")

    all.f <- list.files(folder, pattern=data.ext, full.names=TRUE)
    if (length(all.f) == 0) 
        stop("No files have extension '", data.ext, "'")

    cat("Processing folder", folder, "\n")
    all.timepoints <- as.numeric(sub(name.match,"\\1", all.f, perl=TRUE))
    ord <- order(all.timepoints)
    all.f <- all.f[ord]
    all.timepoints <- all.timepoints[ord]

    return(list(files=all.f, timepoints=all.timepoints))
}

save.to <- function(data.folder, save.path) {
    if (missing(save.path)) {
        save.path <- file.path("fig", sub("^(\\.\\/)?", "", data.folder))
        if (!file.exists(save.path)) dir.create(save.path, recursive=TRUE)
    }
    return(save.path)
}

metapop.autoplot <- function(dat, save.to) {
    ss <- subset(dat, migration.rate > 0 & max.death.rate > 0 &
                 initial.occupancy > 0)

    if (!file.exists(save.to)) dir.create(save.to, recursive=TRUE)

    types <- unique(ss$type)
    initial.occs <- unique(ss$initial.occupancy)
    mutant.freqs <- unique(ss$mutant.freq)
    mutation.rates <- unique(ss$u)
    env.change.rates <- unique(ss$env.change.rate)
    migration.rates <- unique(ss$migration.rate)
    max.death.rates <- unique(ss$max.death.rate)

    for (ty in types) {
        for (occ in initial.occs) {
            for (mutf in mutant.freqs) {
                for (ec in env.change.rates) {
                    for (u.rate in mutation.rates) {
                        plot.name <- paste(ty,
                                           "_occ=",  sprintf("%.2f", occ),
                                           "_u=",    sprintf("%.2e", u.rate),
                                           "_mutr=", sprintf("%.2e", mutf),
                                           "_envr=", sprintf("%.2e", ec),
                                           sep="")
                        cat("plotting", plot.name, "\n") 
                        for.plot <- subset(ss,
                                           type==ty &
                                           u==u.rate &
                                           initial.occupancy==occ & 
                                           mutant.freq==mutf &
                                           env.change.rate==ec)

                        metapop.plotsurface(for.plot,
                                            save.to=save.to,
                                            plot.name=plot.name,
                                            device="lattice.svg")
                    }
                }
            }
        }
    }
}

