data.location   <- commandArgs()[5]
result.location <- commandArgs()[6]
timestamp       <- commandArgs()[7]
beads           <- commandArgs()[8]
bead.dilution   <- as.numeric(commandArgs()[9])
beads.per.ml    <- as.numeric(commandArgs()[10])

source("/var/www/projects/flustrw/bin/flustr_runner.r")
flustr.runner(data.location=data.location, result.location=result.location,
              timestamp=timestamp, beads=beads,
              bead.dilution=bead.dilution,
              beads.per.ml=beads.per.ml)
