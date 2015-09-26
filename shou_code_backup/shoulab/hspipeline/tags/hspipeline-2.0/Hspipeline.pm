package Hspipeline;
use strict; use warnings; 
our $VERSION = 1.00;
our @ISA = qw/Exporter/;
our @EXPORT = qw/generate_paths scan_genome get_name get_parent namer/;

sub generate_paths {
  my ($p) = @_;
  my $proj    = $p->{proj};
  my $pattern = $p->{pattern};
  my $concat  = $p->{concat};
  my %genomes = ();

  # A project path contains folders with strain names.  Each strain folder
  # contains its sequence as a .fastq.gz file.
  opendir(my $dh, $proj);
  for my $folder (readdir $dh) {
    next if $folder =~ /\.+/;
    my $strain_folder = $proj.'/'.$folder;
    my $cat_name = $strain_folder.'/'.$folder;
    die "Folder '$strain_folder' doesn't exist!" unless -e $strain_folder;
    if (-d $strain_folder) {
      opendir(my $dh2, $strain_folder);
      push my @dir, map {$strain_folder.'/'.$_} grep /$pattern/, 
                    readdir $dh2;
      closedir $dh2;

      next unless scalar @dir > 0;

      my @cats = grep /cat/, @dir;
      
      if ($concat) {
        # Uncompress, concatenate, and recompress fastq files.
        if (scalar @cats < 2) {
          @cats = ();
          print "concatenating fastq files...";
          foreach my $r ("R1","R2") {
            my $search = $r.'_\d+';
            my $files = join " ", sort grep /$search/, @dir;
            my $cat_name = $strain_folder.'/'.$folder.'_'.$r.'_cat.fastq.gz';
         
            my $cat = "gunzip -c $files | cat - | gzip > $cat_name";
            system($cat) == 0 or die "Concatenation/compression failed!: $!";
            push @cats, $cat_name;
          }
        }
        @dir = @cats;
      }
      ($genomes{$folder}) = \@dir;
    } else {
      die "'$strain_folder' is not a folder!";
    }
  }
  close $dh;

  # Remove entries with undefined values.
  while (my ($key, $val) = each %genomes) {
    delete $genomes{$key} if scalar @$val == 0;
  }
  return \%genomes;
}

sub get_parent {
    my $path = shift;
    $path =~ /^(.*\/).*$/;
  if (defined $1) {
      my $parent = $1;
      chop $parent;
      return $parent;
  } else {
      return '.';
  }
}

sub get_name {
  my $path = shift;
  chop $path if $path =~ /\/$/;
  $path =~ /^.*\/(.*)$/;
  defined $1 ? return $1 : return $path;
}

sub scan_genome {
  my $p = shift;
  my $file   = $p->{file};
  my $depth_thresh = $p->{max_depth};
  my $print  = $p->{print};

  my %aves;
  my $current = "";
  my $chrom = "";

  my $first = 1;

  my $n     = 0;
  my $delta = 0;
  my $mean  = 0;
  my $M2    = 0;
  my $max_depth = 0;

  my $n_thresh     = 0;
  my $delta_thresh = 0;
  my $mean_thresh  = 0;
  my $M2_thresh    = 0;
  my $max_depth_thresh = 0;

  my $pos = 0;
  my $last_pos = $pos;

  open my $f, '<', $file or die "Cannot open $file: $!";
  while (<$f>) {
    # Line is chrom pos ref depth seq qual
    my @line  = split;
    $chrom = $line[0];
    $pos   = $line[1];
    my $site_depth = $line[3];
    if ($current eq "") {
      $current = $chrom;
      print STDERR "[scan_genome] Processing '$chrom'...";
    } elsif ($chrom eq $current) {
      print STDERR "." if $pos % 10000 == 0;
    } elsif ($chrom ne $current) {
      print STDERR "done!\n";
      store_data({data=>\%aves,
                  chrom=>$current,

                  n=>$n,
                  mean=>$mean,
                  M2=>$M2,
                  max_depth=>$max_depth,
                  last_pos=>$last_pos,

                  n_thresh=>$n_thresh,
                  mean_thresh=>$mean_thresh,
                  M2_thresh=>$M2_thresh,
                  max_depth_thresh=>$max_depth_thresh,
                 });

      $n = 0;
      $delta = 0;
      $mean  = 0;
      $M2    = 0;
      $max_depth = 0;

      $n_thresh     = 0;
      $delta_thresh = 0;
      $mean_thresh  = 0;
      $M2_thresh    = 0;
      $max_depth_thresh = 0;

      print STDERR "[scan_genome] Processing '$chrom'...";
      $current = $chrom;
    }
    # Calculate running mean and sd according to Knuth (1998). 
    # Found on 
    # http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance
    $n++;
    $delta = $site_depth - $mean;
    $mean += $delta/$n;
    $M2   += $delta*($site_depth-$mean);

    # Coverage distribution.
    $aves{$chrom}{depth}{$pos} = $site_depth;
    $max_depth = $site_depth if $max_depth < $site_depth;

    if ($site_depth<$depth_thresh && $chrom ne 'chrM') {
        $n_thresh++;
        $delta_thresh = $site_depth - $mean_thresh;
        $mean_thresh += $delta_thresh/$n_thresh;
        $M2_thresh   += $delta_thresh*($site_depth-$mean_thresh);
        $max_depth_thresh = $site_depth if $max_depth_thresh < $site_depth;
    }

    $last_pos = $pos;
  }
  print STDERR "done!\n";
  store_data({data=>\%aves,
              chrom=>$chrom,

              n=>$n,
              mean=>$mean,
              M2=>$M2,
              max_depth=>$max_depth,
              last_pos=>$last_pos,

              n_thresh=>$n_thresh,
              mean_thresh=>$mean_thresh,
              M2_thresh=>$M2_thresh,
              max_depth_thresh=>$max_depth_thresh,
            });

  close $f;

  if ($print) {
    my $outfile = namer($file,'depthstats.txt');

    open my $dh, "> $outfile" 
      or die "Couldn't open '$outfile' for writing:$!";

    print $dh "# File '$file'\n";
    print $dh "chromosome\tlast_position\tsites_w_coverage\tmean\tsd\tsites_w_coverage_depth<$depth_thresh\tmean_depth<$depth_thresh\tsd_depth<$depth_thresh\n";
    for my $chrom (keys %aves) {
      printf $dh "$chrom\t%d\t%d\t%.2f\t%.2f\t%d\t%.2f\t%.2f\n", 
                  $aves{$chrom}{last_pos},
                  $aves{$chrom}{sites_w_cov}, 
                  $aves{$chrom}{ave},
                  $aves{$chrom}{sd},
                  $aves{$chrom}{sites_w_cov_thresh}, 
                  $aves{$chrom}{ave_thresh},
                  $aves{$chrom}{sd_thresh};
    }
    print $dh "\n";
    close $dh;
  }

  return \%aves;
}
sub store_data {
  my $p = shift;
  my $data  = $p->{data};
  my $chrom = $p->{chrom};

  $data->{$chrom}{ave} = $p->{mean};
  $data->{$chrom}{sd}  = sqrt($p->{M2}/($p->{n}-1));
  $data->{$chrom}{sites_w_cov} = $p->{n};
  $data->{$chrom}{max_depth} = $p->{max_depth};
  $data->{$chrom}{last_pos} = $p->{last_pos};

  $data->{$chrom}{ave_thresh} = $p->{mean_thresh};
  $data->{$chrom}{sd_thresh}  = sqrt($p->{M2_thresh}/($p->{n_thresh}-1));
  $data->{$chrom}{sites_w_cov_thresh} = $p->{n_thresh};
  $data->{$chrom}{max_depth_thresh} = $p->{max_depth_thresh};
}

sub namer {
  my $file_path = shift;
  my $suffix    = shift;

  my $folder_path = get_parent($file_path);

  my $name;
  if ($folder_path eq '.') {
      $name = $file_path;
  } else {
      $name = get_name($folder_path);
  }

  return $folder_path.'/'.$name.'_'.$suffix;
}
1;
