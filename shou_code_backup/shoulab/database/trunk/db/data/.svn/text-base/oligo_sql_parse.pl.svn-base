#!/usr/bin/perl

# This program converts the output of FileMaker Pro tab-delim exports
# for oligos into sql syntax.

use warnings;
use strict;

while (<>) {
  #print;
  chomp;
  s/\013/ /g;
  s/('|")/\\$1/g;
  my ($number, $seq, $target, $info) = split /\t/;
  my ($length, $gc, $tm) = calc_oligo($seq);

  print "INSERT INTO oligos (number,sequence,length,gc,tm,target)\n";
  print "VALUES (";
  print $number.',\''.$seq.'\','.$length.','.$gc.','.$tm.',\''.$target.'\');'."\n";
  print "\n";
}

sub calc_oligo {
  my $s = shift;
  my $len = length($s);
  my $gc = 0;
  $gc++ while $s =~ /[gc]/g;

  my $tm = 59.4 + 41*$gc/$len - 500/$len + 2;
  #print join "\t", ($len,$gc,$tm);
  #print "\n";

  return $len, $gc/$len*100, $tm;
}
