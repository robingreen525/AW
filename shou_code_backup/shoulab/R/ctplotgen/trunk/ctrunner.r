ctrunner <- function(data.location, sample.cols, blank.cols, rows) {
  source("../scripts/plotgen.r"); source("../scripts/datagen.r")

  datagen(data.location=data.location,
          sample.cols=sample.cols,blank.cols=blank.cols, rows=rows)

  plotgen(data.location=paste(data.location,"/compiled",sep=""),
          sample.cols=sample.cols, rows=rows)
}
