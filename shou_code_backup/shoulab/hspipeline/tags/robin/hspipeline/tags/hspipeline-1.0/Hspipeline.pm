package Hspipeline;
use strict; use warnings; 
our $VERSION = 1.00;
our @ISA = qw/Exporter/;
our @EXPORT = qw/generate_paths scan_genome get_name get_parent/;

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
  my $parent = $1;
  chop $parent;
  return $parent;
}

sub get_name {
  my $path = shift;
  chop $path if $path =~ /\/$/;
  $path =~ /^.*\/(.*)$/;
  return $1;
}

sub scan_genome {
  my $p = shift;
  my $file = $p->{file};
  my $print = $p->{print};

  my %aves = ();
  my $current = "";
  my $first = 1;
  my $delta = 0;
  my $mean  = 0;
  my $M2    = 0;
  my $n     = 0;
  my $chrom = "";

  open my $f, '<', $file or die "Cannot open $file: $!";
  while (<$f>) {
    # Line is chrom pos ref depth seq qual
    my @line  = split;
    $chrom    = $line[0];
    my $pos   = $line[1];
    my $site_depth = $line[3];
    if ($current eq "") {
      $current = $chrom;
      print STDERR "Processing '$chrom'...";
    } elsif ($chrom eq $current) {
      print STDERR "." if $pos % 10000 == 0;
    } else {
      print STDERR "done!\n";
      store_data(\%aves,$current,$mean,$M2,$n);

      $delta = 0;
      $mean  = 0;
      $M2    = 0;
      $n     = 0;
      print STDERR "Processing '$chrom'...";
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
  }
  print STDERR "done!\n";
  store_data(\%aves,$chrom,$mean,$M2,$n);

  close $f;

  if ($print) {
    my $outfile = namer($file,'depthstats.txt');

    open my $dh, "> $outfile" 
      or die "Couldn't open '$outfile' for writing:$!";

    print $dh "# File '$file'\n";
    print $dh "chromosome\tlength\tmean\tsd\n";
    for my $chrom (keys %aves) {
      printf $dh "$chrom\t%d\t%.2f\t%.2f\n", 
                  $aves{$chrom}{len}, 
                  $aves{$chrom}{ave},
                  $aves{$chrom}{sd};
    }
    print $dh "\n";
    close $dh;
  }

  return \%aves;
}
sub store_data {
  my ($aves,$chrom,$mean,$M2,$n) = @_;
  $aves->{$chrom}{ave} = $mean;
  $aves->{$chrom}{sd}  = sqrt($M2/($n-1));
  $aves->{$chrom}{len} = $n;
}

sub namer {
  my $file_path = shift;
  my $suffix    = shift;

  my $folder_path = get_parent($file_path);
  my $name = get_name($folder_path);

  return $folder_path.'/'.$name.'_'.$suffix;
}
1;
