compile.positions <- function(project.folder) {
  # Compiles the output of of the 'results/compiled' folder by position.
  require(flowCore,  quietly=TRUE, warn.conflicts=FALSE)

  dat.location <- paste(project.folder,"results","compiled",sep="/")
  output.path  <- paste(dat.location,"by_position",sep="/")
  if (!file.exists(output.path)) dir.create(output.path)

  files <- list.files(dat.location,full.names=TRUE)
  ignore.cols <- c("location","selection")

  ROWS <- LETTERS[1:5]
  COLS <- paste("0",1:2,sep="")
  plat.names <- apply(expand.grid(ROWS,COLS),1,paste,collapse="")
  res <- data.frame()
  for (platform in plat.names) {
    plat.df    <- data.frame()
    plat.files <- files[grep(platform,files)]
    if (length(plat.files) > 0) {
      for (plat.file in plat.files) {
        location <- sub(".*[A-Z][0-9][0-9]([a-z]).*","\\1",plat.file)
        new.df   <- data.frame(read.delim(plat.file), location=location)
        plat.df  <- rbind(plat.df, new.df)
      }
      flowframe.data <- plat.df[!names(plat.df) %in% ignore.cols]
      spill <- diag(ncol(flowframe.data))
      rownames(spill) <- colnames(spill) <- names(flowframe.data)

      ff <- flowFrame(as.matrix(flowframe.data))
      #description(ff) <- build.description(ff,spill)

      write.FCS(ff, paste(output.path,"/",platform,".fcs",sep=""))
      write.table(plat.df,
                  paste(output.path,"/",platform,".txt",sep=""),
                  row.names=FALSE,sep="\t")
    }
  }
}

build.description <- function(ff,spill) {
  disc <- list()
  for (col in 1:ncol(ff)) {
    p.range <- paste("$P",col,"R",sep="")
    disc[substitute(p.range)] = 
      as.character(ceiling(parameters(ff)$range[col]))
  }
  disc
}
