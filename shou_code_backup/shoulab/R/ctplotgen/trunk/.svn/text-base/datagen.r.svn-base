datagen <- function(data.location="../data", result.location=data.location,
                    sample.cols, blank.cols, rows, 
                    reads=c("od","dsred","venus","cfp"),
                    save=TRUE)
{
  files <- list.files(data.location, pattern=".txt",full.names=TRUE)
  result.dir <- paste(data.location,"/","compiled",sep="")
  if (!file.exists(result.dir)) dir.create(result.dir)
  now.date <- paste(format(Sys.time(),"%Y%d%m_%H%M%S"),sep="")
  concat.data.path <- paste(data.location,"/",result.dir,"/",now.date,
                            "_all_data.txt", sep="")

  dat <- calc.elapsed(calc.dilutions(concatenate(files), 
                                     reads,
                                     sample.cols,blank.cols,rows))
  if (save) { 
    write.table(dat, concat.data.path, row.names=FALSE,sep="\t") 
  }
  dat
}

calc.dilutions <- function(dat, reads, samples, blanks, rows) {
  blank.wells  <- paste(rep(rows,each=length(blanks)), blanks, sep="")
  sample.wells <- paste(rep(rows,each=length(samples)), samples, sep="")

  subtracted <- subtract.bg(dat, reads, blank.wells, sample.wells)
  bs.reads   <- paste("bs.",reads,sep="")

  afters  <- subset(subtracted,when=="after")
  befores <- subset(subtracted,when=="before")
  dilutions <- ifelse(befores$bs.od > 0, befores$bs.od/afters$bs.od, 0)

  w.dilutions <- data.frame( well= afters$well,
                             timepoint = afters$timepoint,
                             timestamp = afters$timestamp,
                             before=befores[reads],
                             after=afters[reads],
                             before=befores[bs.reads],
                             after=afters[bs.reads],
                             dilution=dilutions)

  cum.names <- paste("cum.",bs.reads,sep="")
  cum.dils  <- subset(w.dilutions, timepoint==0)$dilution
  prev.dils <- cum.dils
  for (tp in 1:max(w.dilutions$timepoint)) {
    dils <- subset(w.dilutions, timepoint==tp)$dilution
    cum.dils <- c(cum.dils, dils*prev.dils)
    prev.dils <- dils
  }

  cum <- w.dilutions[paste("after.",bs.reads,sep="")] * cum.dils
  names(cum) <- paste("cum.",reads,sep="")

  w.dilutions <- data.frame(w.dilutions, cum.dilution=cum.dils, cum)
  w.dilutions
}

calc.elapsed <- function(dat) {
  start.time <- unique(subset(dat, timepoint==0)$timestamp)
  elapsed <- NULL
  for (tp in split(dat,dat$timepoint)) {
    elapsed  <- c(elapsed, as.numeric(difftime(tp$timestamp,
                                               start.time,
                                               units="hours")))
  }
  w.elapsed <-data.frame(dat, elapsed.hrs=elapsed)
  w.elapsed
}

subtract.bg <- function(dat, reads, blank.wells, sample.wells) {
  subtracted <- data.frame()

  tp  <- split(dat, list(timepoint=dat$timepoint,when=dat$when))
  subtracted <- 
    unsplit(lapply(tp, function(x) {
                mean.blanks <- mean(x[x$well %in% blank.wells,reads])
                subtracted  <- x[reads] - rep(mean.blanks, each=nrow(x))
                                   }),
      list(dat$timepoint, dat$when))

  names(subtracted) <- paste("bs.",reads,sep="")

  return(data.frame(dat,subtracted))
}

concatenate <- function(files) {
  nFiles <- length(files)
  concat <- data.frame()
  timepoint <- -1 
  for (i in 1:nFiles) {
    if (files[i] %in% "tube") next
    dat <- read.delim(files[i], na.strings="#N/A")

    when <- NULL
    if (grepl("after",files[i])) {
      when <- rep("after", times=nrow(dat))
      timepoint <- timepoint + 1
    } else {
      when <- rep("before", times=nrow(dat))
    }
    concat <- rbind(concat, data.frame(dat, timepoint=timepoint, when=when))
  }
  concat
}


