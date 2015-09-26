#  To install required packages:
#     1) Open R with installation privileges.
#     2) > source("http://bioconductor.org/biocLite.R")
#     3) > biocLite(c("flowCore","flowMeans","flowViz"))
flustr.runner <- function(data.location, result.location=data.location,
                          timestamp=NULL, beads=TRUE,
                          bead.dilution=10, beads.per.ul=7.8e3, 
                          sep="\t", ...)
{
  require(flowCore, quietly=TRUE, warn.conflicts=FALSE)

  # Change if in a non-standard location.
   source("/var/www/projects/flustrw/bin/flustr.r")
#  source("./flustr.r")

   beads.per.ml <- beads.per.ul * 1000

  for (folder in 1:length(data.location)) {
    if (!identical(file.info(data.location[folder])$isdir,TRUE)) next

    cat("In folder ", data.location[folder], 
        " (", folder, " of ",length(data.location), ")...\n", sep="") 

    fcs.files <- 
      list.files(path=data.location[folder],pattern="*.fcs",full.names=TRUE)

    nFiles <- length(fcs.files)

    if (nFiles == 0) next

    timestamp <- ifelse(is.null(timestamp),
                        format(Sys.time(),"%Y%m%d_%H%M%S"), timestamp)

    result.folder <- paste(timestamp,"-flustr_results",sep="")

    result.path   <- paste(result.location[folder],"/",result.folder,sep="")
    count.path    <- paste(result.path,"/","counts.tab",sep="")
    intermed.path <- paste(result.path,"/","intermed.tab",sep="")

    if (!file.exists(result.path)) dir.create(result.path,recursive=TRUE)

    count.res <- vector("list",nFiles)
    categories <- list()
    for (i in 1:nFiles) {
      res <- try(flustr(file=fcs.files[[i]], 
                        save.plot=TRUE, save.debug=TRUE,
                        beads=beads, 
                        bead.dilution=bead.dilution,
                        beads.per.ml=beads.per.ml,
                        result.path=result.path, sep=sep, ...))
    if (class(res) == "try-error") {
      cat("\nFLUSTR.RUNNER ERROR: File '", fcs.files[[i]], 
          "' threw error, skipping.\n\n")
      next
    }
      write.table(data.frame(res),file=intermed.path,sep=sep,
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
#print(final.res)
    write.table(final.res,file=count.path,sep=sep,row.names=FALSE)
  }
}

add.categories <- function(cats,dat) {
  for (nme in names(dat)) {
    if (is.null(cats[[nme]]))
      cats[[nme]] <- 1
  }
  cats
}
