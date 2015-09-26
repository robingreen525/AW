#!/usr/bin/perl

# This program converts the output of FileMaker Pro tab-delim exports
# for plasmids into sql syntax.

use warnings;
use strict;

while (<>) {
  chomp;
  s/\013/ /g;
  my @line = split /\t/;
  my @parts = ();
  for my $chunk (@line) {
    chomp;
    if ($chunk =~ /^"(.*)"$/) {
      $chunk = $1;
    }
    $chunk =~ s/('|")/\\$1/g;
    #print "'$chunk'\n";
    push @parts, $chunk if $chunk;
  }
  my ($number, $descrip, $source, $name, $res, $strain) = @parts;
  map { $$_ =~ s/(^\s+)|(\s+$)//g } (\$number, \$descrip, \$source, \$name, \$res, \$strain);
  print "INSERT INTO bacterial_plasmids (number,description,source,plasmid_name,drug_resistance,background)\n";
  print "VALUES (";
  print $number.',\''.$descrip.'\',\''.$source.'\',\''.$name.'\',\''.$res.'\',\''.$strain.'\');'."\n";
  print "\n";
}
