#!/usr/bin/perl

# This program converts the output of FileMaker Pro csv exports
# for yeast strains into rails syntax.

use warnings;
use strict;

while (<>) {
  chomp;
  s/\013/ /g;
  my @parts = ();
  while (/(".*?"),?/g) {
    my $chunk = $1;
    $chunk =~ s/(^\s+)|(\s+$)//g;
    $chunk =~ s/"//g;
    #print $chunk,"\n";
    
    push @parts, $chunk;
  }
  my ($number, $alias, $bg, $geno, $notes, $date) = @parts;
  (my $mat,$geno) = mat($geno);
  ($notes, $date) = date($notes,$date);
  #f $1;
  #  my $m = $2;
  #  print $mat,"\n";
  #print "'$geno'","\n";
  #print $notes,"\n";
  #print $date, "\n";
  print "YeastStrain.create( :number      => ",'"'.$number.'"',",","\n",
        "                    :alias       => ",'"'.$alias.'"' ,",","\n",
        "                    :background  => ",'"'.$bg.'"'    ,",","\n",
        "                    :mating_type => ",'"'.$mat.'"'   ,",","\n",
        "                    :genotype    => ",'"'.$geno.'"'  ,",","\n",
        "                    :freeze_date => ",'"'.$date.'"'  ,",","\n",
        "                    :notes       => ",'"'.$notes.'"' ,")","\n";
}

sub mat {
  my $g = shift;
  my $m = '';
  my $full = '';

  # Find mating type and assign to '$m'.
  $g =~ s/(\d)D/$1\\xce\\x94/g;

  if ($g =~ /(MAT\s*(alpha|a)\s*\/\s*MAT\s*(alpha|a),?)/) {
    $full = $1;
    $m    = $2.'/'.$3;
  } elsif ($g =~ /(MAT\s*(a[\/]alpha|alpha|\?|a),?)/) {
    $full = $1;
    $m = ($2) ? $2 : 'unknown';
  } else { $m = 'unknown'; }

  $m =~ s/\?/unknown/;
  $m =~ s/alpha/\\xce\\xb1/;
  $m =~ s/\s+//g;

  $g =~ s/(.*?)\s*$full\s*(.*)/$1 $2/ if $full;
  $g =~ s/(?:^\s*)|(?:\s*$)//g;

  if ($m) {
    return $m,$g;
  } else { return '', $g; }

}
sub date {
  my $n = shift;
  my $d = shift;

  my %months = (
    Jan   => '01',
    Feb   => '02',
    March => '03',
    April => '04',
    May   => '05',
    June  => '06',
    July  => '07',
    Aug   => '08',
    Sep   => '09',
    Oct   => '10',
    Nov   => '11',
    Dec   => '12',
  );
  my @months = keys %months;


  #print $d,"\n";
  if ($d =~ /(\d\d)\/(\d\d)\/(\d\d)/) {
    return $3.'-'.$1.'-'.$2;
  } else {
    for my $month (keys %months) {
      #print $d,"\n",$month,"\n";
      if ($d =~ /$month/) {
        if ($d =~ /^$month,?\s+(\d\d\d\d)$/) {
          return $n, $1.'-'.$months{$month}.'-01';
        } elsif ($d =~ /^(.*?)[,;]?\s*($month),?\s+(\d\d\d\d)/) {
          my $extra = $1;
          if (length($extra) > 1 and length($n) > 1) {
            $n = $n.'; '.$extra;
          } elsif (length($extra) > 1) {
            $n = $extra;
          }
          return $n, $3.'-'.$months{$2}.'-'.'01';
        }
      }
    }
  }
}
