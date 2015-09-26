#  To install required packages:
#     1) Open R with installation privileges.
#     2) > source("http://bioconductor.org/biocLite.R")
#     3) > biocLite(c("flowCore","flowMeans","flowViz"))
flustr.runner <- function(data.location, result.location=data.location,
                          results=NULL, timestamp=NULL, beads=TRUE,
                          bead.dilution=11.2, beads.per.ul=8.3e3, 
                          sep="\t", ...)
{
  require(flowCore, quietly=TRUE, warn.conflicts=FALSE)

  # Change if in a non-standard location.
#   source("/var/www/projects/flustrw/bin/flustr.r")
    source("~/Documents/Code/R/flustr/flustr.r")

  beads.per.ml <- beads.per.ul * 1000

  for (folder in 1:length(data.location)) {
    if (!identical(file.info(data.location[folder])$isdir,TRUE)) next

    cat("In folder ", data.location[folder], 
        " (", folder, " of ",length(data.location), ")...\n", sep="") 

    fcs.files <- 
      list.files(path=data.location[folder],pattern="*.fcs",full.names=TRUE)

    # For Diva software, the last part of the default file name is 
    # zero-padded, so we sort the files on that.
    fcs.files <- fcs.files[order(sub("^.*([A-Z][0-9]+)\\.fcs$", "\\1", 
                                 fcs.files))]

    nFiles <- length(fcs.files)

    if (nFiles == 0) next

    timestamp <- ifelse(is.null(timestamp),
                        format(Sys.time(),"%Y%m%d_%H%M%S"), timestamp)

    result.folder <- paste(timestamp,"-flustr_results",sep="")

    result.path   <- paste(result.location[folder],"/",result.folder,sep="")
    count.path    <- paste(result.path,"/","counts.tab",sep="")
    intermed.path <- paste(result.path,"/","count_res_part.RData",sep="")

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
      count.res[[i]] <- res$count
      save(count.res, file=intermed.path)
    }
    if (all(unlist(lapply(count.res,is.null)))) {
      stop("FLUSTR.RUNNER ERROR: No files were processed!\n\n")
    }

    not.fluor <- c("file","beads","dead","cells","low.fsc")

    categories <- add.categories(categories,count.res)
    not.fluor.categories <- categories[names(categories) %in% not.fluor]
    fluor.categories     <- categories[!names(categories) %in% not.fluor]
    categories <- c(not.fluor.categories,
                    fluor.categories[order(names(fluor.categories))])
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
    totals       <- c("cells","dead","low.fsc")
    total.ratios <- t(apply(final.counts[names(final.counts) %in% totals],
                            1, function(x) x/sum(x)))
    live.ratios  <- 
      final.counts[!names(final.counts) %in% not.fluor]/final.counts$cells

    final.res <- data.frame(final.counts, total.ratios, live.ratios,
                            check.names=FALSE,
                            stringsAsFactors=FALSE)
    if (beads) {
      mls <- final.counts$beads / beads.per.ml * bead.dilution
      final.abs.count <- final.counts[2:ncol(final.counts)]/mls
      final.res <- data.frame(final.res, final.abs.count,
                              check.names=FALSE, stringsAsFactors=FALSE)
    }

    write.table(final.res,file=count.path,sep=sep,row.names=FALSE)
  }
}

add.categories <- function(cats,dat) {
  for (i in 1:length(dat)) {
    for (nme in names(dat[[i]])) {
      if (is.null(cats[[nme]]))
        cats[[nme]] <- 1
    }
  }
  cats
}
