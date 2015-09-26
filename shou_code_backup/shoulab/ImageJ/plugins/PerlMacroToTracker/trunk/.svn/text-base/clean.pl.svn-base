#!/usr/bin/perl

#usage: cat the results.txt file into this program

system( "mkdir -p ./results" );
system( "mkdir -p ./results/frames" );

#This is intended to facilitate "is this the first line in the file" as it must be processed differently.
$first = 1;
#Reading from standard in because I am lazy and using STDIN allows for commandline pipeing.
while ($line = <STDIN>) {

    #Clean the '\n' off of the line
    chomp($line);
    #Split the clean line into constituant data.
    @pieces = split("\t",$line);

    #This is a sweet hack. We have the benifit of data lines being 12 elements long.
    #If a line is not 12 long then it isn't detected particle data.
    if ( $#pieces  == 12) {
	
	#If this is our first line then, we know it is our column headers.
	#I think the code looks cleaner when using strings as indexes.
	#(Hopefully easier to modify) - problems will arrise if the column names change
        if ( $first == 1 ) {

	    @header = @pieces; #This is simple to make things "read" better.

	    #I wanted to print out the line so the user had some idea as to what I detected in the file.
            print $line."\n";

	    #Setting up the map from "strings" to #integers# (indexes)
            $j = 0;
            foreach $piece ( @pieces ) {
                $map{$piece} = $j;
                $j++;
            }
	    
	    ######
	    #I decided to create a text file that a user could reference when curious about column ordering.
	    $to_file = "Y\tX\tCirc.\tArea";
	    foreach $piece ( @pieces ) {
                if ( $piece ne "Y" && $piece ne "X" && $piece ne "Circ." && $piece ne "Area" ) {
                    $to_file = $to_file."\t".$piece;
                }
            }
	    system( "echo ".$to_file." > ./results/frame_columns.txt" );
	    ######

	    #No longer the column header line.
            $first = 0;
	    
	    #When we have finished setting up the indexing we will be placing the rest of
	    #the data into it's appropriate frame file.
        } else {
            $slice = $pieces[$map{"slice"}];

	    #The ImageJ plug in wants the frames indexes to start at zero.
            $out = $slice - 1;

	    #I am making a new hash that checks to see if the file has already been created.
	    #This allows me to create new files seeded with a header
            if ( $seen{$slice} == 0 ) {
                $seen{$slice} = 1;
		#This puts the "Frame #Slice#" header into the file
                system( "echo \"frame $out\" > ./results/frames/frame_$out" );
            }

	    #Here we are creating the current particles line of output.
	    #The ordering of the X Y and m1 m2 is very important to the ImageJ plugin.
	    #So here I have reordered them as is requests.
            $to_file = $pieces[$map{"Y"}]."\t".$pieces[$map{"X"}];
	    $to_file = $to_file."\t".$pieces[$map{"Circ."}]."\t".$pieces[$map{"Area"}];

	    #This is just a clunky way of printing out the rest of the cells data.
	    foreach $piece ( @header ) {
		if ( $piece ne "Y" && $piece ne "X" && $piece ne "Circ." && $piece ne "Area" ) {
		    $to_file = $to_file."\t".$pieces[$map{$piece}];
		}
	    }
	    #Final step is intended to append the most recent particle data to its file.
            system( "echo \"$to_file\" >> ./results/frames/frame_$out" );
        }
    }
}
