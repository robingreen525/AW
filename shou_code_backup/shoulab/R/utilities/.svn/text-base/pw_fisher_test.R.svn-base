pairwise.fisher.test <- function(a,b,names,data,p.adjust.method="bonferroni") {
    if (missing(a) | missing(b) | missing(names))
        stop("Missing some parameters.")

    Calls <- match.call()
    a <- as.character(Calls$a)
    b <- as.character(Calls$b)
    names <- as.character(Calls$names)


    METHOD <- "Fisher's exact test"
    DNAME <- deparse(substitute(data))

    compare.levels <- function(i, j) {
        mat <- matrix(c(data[,a][c(i,j)],data[,b][c(i,j)]),ncol=2)
        fisher.test(mat)$p.value
    }

    level.names <- unique(as.character(data[,names]))
    pval <- pairwise.table(compare.levels,level.names,p.adjust.method)
    ans <- list(method=METHOD, data.name=DNAME, p.value=pval,
                p.adjust.method=p.adjust.method)
    class(ans) <-"pairwise.htest"
    ans
}
