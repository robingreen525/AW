splitjoin <- function(df, factors, fun) {
  do.call("rbind",lapply(split(df,df[factors]), fun))
}
