#  To install required packages:
#     1) Open R with installation privileges.
#     2) > source("http://bioconductor.org/biocLite.R")
#     3) > biocLite(c("flowCore","flowClust","flowViz"))
flustr.runner <- function(data.location,result.location=data.location,
                          beads=TRUE, bead.dilution=10, beads.per.ml=7.8e6,
                          ...)
{
  require(flowCore)

  # Change as appropriate.
  source("~/Documents/Code/R/flustr/flustr.r")

  for (run in 1:length(data.location)) {
    if (!identical(file.info(data.location[run])$isdir,TRUE)) next

    cat("In folder ", data.location[run], 
        " (", run, " of ",length(data.location), ")...\n", sep="") 

    fcs.files <- 
      list.files(path=data.location[run],pattern="*.fcs",full.names=TRUE)
    nFiles <- length(fcs.files)
    if (nFiles == 0) next

    result.folder <- 
      paste(format(Sys.time(),"%Y%d%m_%H%M%S"),"-flustr_results",sep="")

    result.path <- paste(result.location[run],"/",result.folder,sep="")
    count.path  <- paste(result.path,"/","counts.csv",sep="")
    intermed.path  <- paste(result.path,"/","intermed.csv",sep="")
    dir.create(result.path)

    count.res <- vector("list",nFiles)
    categories <- list()
    for (i in 1:nFiles) {
      res <- flustr(file=fcs.files[[i]], 
                    save.plot=TRUE, save.debug=TRUE,
                    data.path=result.path, ...)
      write.table(data.frame(res),file=intermed.path,sep=";",
                  row.names=FALSE,append=TRUE)
      count.res[[i]] <- res$count
      categories <- add.categories(categories,count.res[[i]])
    }

    final.counts <- list()
    for (i in 1:nFiles) {
      info <- count.res[[i]]
      for (ca in names(categories)) {
        val <- info[[ca]] 
        if (is.null(val)) 
          val <- 0
        else 
          val <- ifelse(is.factor(val), as.character(val), val)
        if (is.null(final.counts[[ca]])) {
          final.counts[[ca]] <- val
        } else {
          final.counts[[ca]] <- c(final.counts[[ca]],val)
        }
      }
    }

    final.counts <- data.frame(final.counts)
    final.ratios <- final.counts[2:ncol(final.counts)]
    final.ratios <- final.counts[2:ncol(final.counts)]/final.counts$cells

    final.res <- data.frame(final.counts, final.ratios,
                            check.names=FALSE,
                            stringsAsFactors=FALSE)
    if (beads) {
      mls <- final.counts$beads / beads.per.ml * bead.dilution
      final.abs.count <- final.counts[2:ncol(final.counts)]/mls
      final.res <- data.frame(final.res, final.abs.count,
                              check.names=FALSE, stringsAsFactors=FALSE)
    }
    print(final.res)
    write.table(final.res,file=count.path,sep=";",row.names=FALSE)
  }
}

add.categories <- function(cats,dat) {
  for (nme in names(dat)) {
    if (is.null(cats[[nme]]))
      cats[[nme]] <- 1
  }
  cats
}
