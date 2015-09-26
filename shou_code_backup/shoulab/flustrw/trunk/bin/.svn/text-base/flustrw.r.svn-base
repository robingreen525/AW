args <- commandArgs()
data.location   <- args[5]
result.location <- args[6]
timestamp       <- args[7]
beads           <- args[8]
bead.dilution   <- as.numeric(args[9])
beads.per.ul    <- as.numeric(args[10])
live.colors     <- strsplit(args[11],",",fixed=TRUE)[[1]]
fsc.sig         <- as.numeric(args[12])
cluster.sig     <- as.numeric(args[13])
all.bright      <- as.numeric(args[14])
all.dim         <- as.numeric(args[15])

source("/var/www/projects/flustrw/bin/flustr_runner.r")
flustr.runner(data.location=data.location, result.location=result.location,
              timestamp=timestamp, beads=beads,
              bead.dilution=bead.dilution,
              beads.per.ul=beads.per.ul,
              live.colors=live.colors,
              fsc.sig=fsc.sig,
              dead.sig=0,
              cluster.sig=cluster.sig,
              all.bright=all.bright,
              all.dim=all.dim
             )
