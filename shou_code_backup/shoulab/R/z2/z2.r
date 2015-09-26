# vim: set filetype=r tabstop=2 expandtab shiftwidth=2 :

count.z2 <- function(dat, d.range=c(range(dat$diameter))) {
  min.d <- min(d.range)
  max.d <- max(d.range)

  gated <- subset(dat, diameter>min.d & diameter<max.d)

  ag <- aggregate(gated$events, by=list(name=gated$name), sum)
  names(ag)[2] <- "events"
  ag <- data.frame(ag, min.diameter=min.d, max.diameter=max.d)
  ag
}

open.z2 <- function(path='.') {
  z2.extension <- ".#Z2"
  files <- list.files(path, pattern=z2.extension)
  names <- gsub(z2.extension,"",files)
  paths <- paste(path,files,sep="/")
  res <- NULL 
  for (i in 1:length(paths)) {
    res <- rbind(res,data.frame(name=names[i],read.z2(paths[i])))
  }
  res
}

read.z2 <- function(file) {
  lines <- readLines(file)

  bin.start <- NULL
  bin.end   <- NULL
  count.start <- NULL
  count.end <- NULL
  l <- 0
  while (!is.na(lines[l<-l+1])) {
    line <- lines[l]
    # [#Bindiam] starts bin diameter
    if (grepl('\\[#Bindiam\\]',line)) bin.start <- l+1
    if (grepl('\\[Binunits\\]',line)) bin.end <- l-1
    if (grepl('\\[#Binheight\\]',line)) count.start <- l+1
    if (grepl('\\[end\\]',line)) count.end <- l-1
  }

  bins <- as.numeric(lines[bin.start:bin.end])
  counts <- as.numeric(lines[count.start:count.end])
  data.frame(diameter=bins,events=counts)
}
