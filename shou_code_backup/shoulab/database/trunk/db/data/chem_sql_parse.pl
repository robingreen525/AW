#!/usr/bin/perl

use warnings;
use strict;

while (<>) {
  chomp;
  next if /^#/;
  s/\013/ /g;
  my @line = split /\t/;
  my @parts = ();
  for my $chunk (@line) {
    if ($chunk =~ /^"(.*)"$/) {
      $chunk = $1;
    }
    $chunk =~ s/"//g;
    $chunk =~ s/('|")/\\$1/g;
    #print "'$chunk'\n";
    push @parts, $chunk;
  }
  my ($name, $location, $storage_temp, $company, $cat_num, $price, $unit, $notes) = @parts;
  #my ($price, $unit) = pprice($p_u);
  $storage_temp =~ s/RT/25/;
  map { $$_ =~ s/(^\s+)|(\s+$)//g } (\$name, \$location, \$storage_temp, \$company, \$cat_num, \$price, \$unit, \$notes);
  my $fcount += grep{ length($_) > 0 } (\$name, \$location, \$storage_temp, \$company, \$cat_num, \$price, \$unit, \$notes);
  #print $fcount;
  next unless $fcount > 0;

  print "INSERT INTO chemicals (name,storage_loc,storage_temp,company,cat_num,price,unit,notes)\n";
  print "VALUES (";
  print "'".$name."','".$location."','".$storage_temp."','".$company."','".$cat_num."','".$price."','".$unit."','".$notes."');\n";
  print "\n";
}

sub pprice {
  my $pu = shift;

  $pu =~ /^(.*)\$((?:\d+\,)?\d+\.\d+)(.*)$/;
  if ($1 and $3) {
    return $2, join " ", ($1, $3);
  } elsif ($1 and not $3) {
    return $2, $1;
  } elsif ($3 and not $1) {
    return $2, $3;
  } else {
    return $2, '';
  }
}


