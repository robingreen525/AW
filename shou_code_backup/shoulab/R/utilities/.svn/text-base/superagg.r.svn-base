superagg <- function(dat,factors,params,funcs=c("mean","sd")) {
  # Extends aggregate() to handle multiple functions, and names resulting
  # columns appropriately.

  factor.names <- names(factors)

  if (is.null(factor.names)) warning("Factors are unnamed!")

  if (!is.list(factors)) stop("'factors' must be a list")
    
  if (!is.character(params)) stop("'params' must be a string")

  if (!all(params %in% names(dat)))
    stop("one or more params not in dataset")

  if (!all(factor.names %in% names(dat)))
    stop("one or more factors not in dataset")

  params <- sort(params)

  nfactors <- length(factors)
  nparams  <- length(params)
  agglen   <- nfactors+nparams

  fs <- which(names(dat) %in% factor.names)
  ps <- which(names(dat) %in% params)
  dat.ss <- dat[sort(names(dat[ps]))]

  res <- aggregate(dat.ss, factors,funcs[1])[1:agglen]
  for (fun in funcs[-1])  {
    res <- cbind(res, aggregate(dat.ss, factors,fun)[(nfactors+1):agglen])
  }

  names(res)[(nfactors+1):length(res)] <- 
    apply(expand.grid(params,funcs),1,paste,collapse=".")
  res
}
