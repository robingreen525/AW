package Hspipeline;
use strict; use warnings; 
our $VERSION = 1.00;
our @ISA = qw/Exporter/;
our @EXPORT = qw/index_reference usage generate_paths scan_genome get_name get_parent find_cnv compare_to_anc/;

sub index_reference {
  my $ref_path = shift;
  my $parent = get_parent($ref_path);
  my $index = "bwa index -a is ".$ref_path;

  # Look for files ending in '.bwt', which indicates indexing has been
  # performed.
  opendir(my $dh, $parent);
  unless (grep /bwt$/, readdir($dh)) {
    print "Indexing genome...\n";
    system($index) == 0 or die "bwa Indexing failed: $!";
  } else {
    print "Looks like sequence has been indexed, continuing...\n";
  }
  close $dh;
}

sub find_cnv {
  my $p = shift;
  my $anc_path  = $p->{anc_path};
  my $all_paths = $p->{all_paths};
  my $R_path    = $p->{R_path};
  my $window    = $p->{window};

  my $pileup_ext = "_sorted.pileup";
  my $cnv_ext    = $pileup_ext."_cnvfind.txt";

  my $evo_paths = get_evo_paths($all_paths, $anc_path);
  my $evo_pileups = join " ", map { $_.'/'.get_name($_).$pileup_ext }
                    @$evo_paths;
  my $anc_pileup  = $anc_path.'/'.get_name($anc_path).$pileup_ext;

  my $cnvfind = "cnvfind $anc_path $anc_pileup $evo_pileups";

  print "Finding CNV...\n";
  system($cnvfind)==0 or die "cnvfind failed: $!";

  my $R = "R --vanilla --slave < $R_path/call_plot_cnv.r";
  for my $evo_path (@$evo_paths) {
    my $evo_name = get_name($evo_path);
    my $evo_cnv = $evo_path.'/'.$evo_name.$cnv_ext;
    my $plot_cnv = "$R --args $R_path $evo_cnv $window $evo_name";

    print "Plotting CNV data for $evo_cnv...\n";
    system($plot_cnv)==0 or die "plotting on $evo_cnv failed: $!";
  }
}

sub get_evo_paths {
  my $path_hash = shift;
  my $anc_path  = shift;
  my @paths;

  for my $path (values %$path_hash) {
    next if get_parent($path->[0]) eq $anc_path;
    push @paths, get_parent(@$path);
  }

  return \@paths;
}

sub compare_to_anc {
  my $p = shift;
  my $anc_path     = $p->{anc_path};
  my $strain       = $p->{strain};
  my $project_path = $p->{proj};
  my $file_suffix  = $p->{file};
  my $vcf_tol      = $p->{vcf_tol};

  my $anc_name = get_name($anc_path);
  return if $strain =~ /$anc_name/;

  my $anc = $anc_path.'/'.$anc_name.$file_suffix;
  my $evo = $project_path.'/'.$strain.'/'.$strain.$file_suffix;

  # Find SNPs unique to the evolved strain.
  print "Finding entries in ".$strain.$file_suffix." that are not in ancestral strain...\n";
  my $vcfunique = "vcfunique -d$vcf_tol $anc $evo";
  system($vcfunique) == 0 or die "vcfunique failed: $!";
  print "done!\n";
}

sub usage {
  die(qq/
    Usage: hspipeline -e [end type] -t [number of threads] -p <path to project folder> -r <path to reference genome> -a <path to ancestor folder> -R <path to R files>
      -e end type -- Either 's' or 'p' for single- or paired-end data, respectively.
      -t number of threads -- The number of processors to use (for computers with multiple processors).
      -p project folder -- A folder containing folders named after strains.  These folders contain the sequencing output from Illumina.
      -r reference genome -- The concatenated reference genome in fasta format.
      -a ancestor folder -- The folder that contains (or will contain) the ancestor data.
      -R Path to directory with R files.
      \n/);
}

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
  my $file = shift;
  my %aves = ();
  my $current = "";
  my $first = 1;
  my $delta = 0;
  my $mean  = 0;
  my $M2    = 0;
  my $n     = 0;
  my $chrom = "";
  open my $f, '<', $file;
  while (<$f>) {
    # Line is chrom pos ref depth seq qual
    my @line = split;
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
      $aves{$current}{ave} = $mean;
      $aves{$current}{sd}  = sqrt $M2/($n-1);
      $aves{$current}{len} = $n;

      $delta = 0;
      $mean  = 0;
      $M2    = 0;
      $n     = 0;
      print STDERR "Processing '$chrom'...";
      $current = $chrom;
    }
    # Calculate running mean and sd, according to Knuth (1998).
    $n++;
    $delta = $site_depth - $mean;
    $mean += $delta/$n;
    $M2    = $M2 + $delta*($site_depth-$mean);

    # Coverage distribution.
    $aves{$chrom}{depth}{$pos} = $site_depth;
  }
  print STDERR "done!\n";
  $aves{$chrom}{ave} = $mean;
  $aves{$chrom}{sd}  = sqrt $M2/($n-1);
  $aves{$chrom}{len} = $n;

  close $f;
  return \%aves;
}
1;
