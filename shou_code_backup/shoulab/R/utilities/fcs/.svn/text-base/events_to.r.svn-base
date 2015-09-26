# :vim filtype="R" :

events.to <- function(path,time.name,times) {
#  Calculates the number of events that have passed from 0 to 'times'
# miliseconds.  'times' can be a vector of values.
#
# 'path' is the path to the folder containing the fcs files.
# 'time.name' is the name of the time channel used in the fcs files.
#
#
# Requires the package "flowCore", which can be obtained by opening R with
# installation privledges and running:
#   source("http://bioconductor.org/biocLite.R")
#   biocLite("flowCore")
# 
#
# See comments for additional instructions.

  require("flowCore")

  dat <- read.flowSet(path=path,pattern=".fcs")

# Change the values in quotes to the names of the channels you want to gate
# beads on, and the numeric range to the (min,max) values for that channel.
  bead.gate <- rectangleGate("L3-1"=c(1e4,Inf),"L2-4"=c(1e4,Inf))

  beads     <- Subset(dat,bead.gate)
  not.beads <- Subset(dat,!bead.gate)

  res <- rbind(get.events(beads,times,time.name),
               get.events(not.beads,times,time.name))
  print(res)
}

get.events <- function(dat,times,time.name) {
  df <- data.frame()
  for (idx in 1:length(dat)) {
    file.name  <- sub(".*/(.*)\\.fcs", "\\1", keyword(dat[[idx]])$FIL,
                      perl=TRUE)
    for (tm in times) {
      df <- rbind(df, data.frame(file=file.name,
                  type=as.character(substitute(dat)),
                  time=tm,
                  events=(sum(exprs(dat[[idx]][,time.name])<tm))))
    }
  }
  df
}
