# vim: set filetype=r tabstop=2 shiftwidth=2 expandtab :

# Updated 11 June, 2010
od.correction <- function(ods, type="gensys") {
  if (identical(type,"gensys")) {
    fix <- ods>0.5
    ods[fix] <- 2.282*ods[fix]/(2.748-ods[fix])
    ods
  } else {
    stop("Don't recognize that type!")
  }
}

pw.to.gen20 <- function(ods) {
  ods / 1.065827
}

gen20.to.pw <- function(ods) {
  ods * 1.065827
}
