#!/usr/bin/perl

use warnings;
use strict;

while (<>) {
  chomp;
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
  my ($number, $description, $company, $notes, $cat_num, $pu) = @parts;
  #my ($number, $description, $company, $notes, $cat_num, $pu) = split /\t/;
  my ($price, $unit) = pprice($pu);
  map { $$_ =~ s/(^\s+)|(\s+$)//g } (\$description, \$company, \$notes, \$cat_num, \$price, \$unit);
  my $fcount += grep{ length($_) > 0 } ($description, $company, $notes, $cat_num, $price, $unit);
  #print $fcount;
  next unless $fcount > 0;

  print "Equipment.create( :description => ",'"'.$description.'"',",","\n",
        "                  :company     => ",'"'.$company.'"' ,",","\n",
        "                  :cat_num     => ",'"'.$cat_num.'"'    ,",","\n",
        "                  :price       => ",'"'.$price.'"'   ,",","\n",
        "                  :unit        => ",'"'.$unit.'"'  ,",","\n",
        "                  :notes       => ",'"'.$notes.'"' ,")","\n";
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


