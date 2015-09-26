# : vim filetype=r :
expand.df <- function(dat, names=c("viable","inviable")) {
  expanded <- vector("list", sum(dat[names[1]],dat[names[2]]))
  cur <- 1
  for (i in 1:nrow(dat)) {
    part <- dat[i,][!names(dat) %in% names]
    success <- dat[i,][[names[[1]]]]
    failure <- dat[i,][[names[[2]]]]
    if (success > 0) {
      for (j in 1:success) {
        expanded[[cur <- cur+1]] <- data.frame(case=1, part)
      }
    }
    if (failure > 0) {
      for (k in 1:failure) {
        expanded[[cur <- cur+1]] <- data.frame(case=0, part)
      }
    }
  }
  expanded <- do.call("rbind",expanded)
  expanded
}
