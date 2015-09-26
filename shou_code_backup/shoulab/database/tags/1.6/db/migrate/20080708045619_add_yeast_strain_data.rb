class AddYeastStrainData < ActiveRecord::Migration
  def self.up
    YeastStrain.delete_all
    YeastStrain.create( :number      => "500",
                        :alias       => "RJD 360",
                        :background  => "W303",
                        :mating_type => "a",
                        :genotype    => "can1-100 ade2-1 his3-11,-15 leu2-3,-112 trp1-1 ura3-1",
                        :freeze_date => "2002-02-01",
                        :notes       => "from B. Fuller")
    YeastStrain.create( :number      => "501",
                        :alias       => "RJD361",
                        :background  => "W303",
                        :mating_type => "\xce\xb1",
                        :genotype    => "can1-100 ade2-1 his3-11,-15 leu2-3,-112 trp1-1 ura3-1",
                        :freeze_date => "2002-02-01",
                        :notes       => "from B. Fuller")
    YeastStrain.create( :number      => "502",
                        :alias       => "RJD1285",
                        :background  => "W303",
                        :mating_type => "a",
                        :genotype    => "sir2::LEU2 pep4::TRP1 can1-100 ade2-1 his3-11,-15 leu2-3,-112 trp1-1 ura3-1",
                        :freeze_date => "2002-02-01",
                        :notes       => "")
    YeastStrain.create( :number      => "503",
                        :alias       => "RJD1348",
                        :background  => "W303",
                        :mating_type => "\xce\xb1",
                        :genotype    => "Cdc14-GFP::his5+, bar1::hisG, can1-100 ade2-1 his3-11,-15 leu2-3,-112 trp1-1 ura3-1",
                        :freeze_date => "2002-02-01",
                        :notes       => "Dual specificity protein phosphatase (PTPase) that performs a function late in the cell cycle ")
    YeastStrain.create( :number      => "504",
                        :alias       => "RJD1519",
                        :background  => "W303",
                        :mating_type => "a",
                        :genotype    => "Vma2-GFP::his5+, bar1::LEU2, pep4::TRP1, can1-100 ade2-1 his3-11,-15 leu2-3,-112 trp1-1 ura3-1",
                        :freeze_date => "2002-02-01",
                        :notes       => "Vacuolar H(+)-ATPase (V-ATPase) regulatory subunit (subunit B) involved in nucleotide binding")
    YeastStrain.create( :number      => "505",
                        :alias       => "RJD1526/AFS499",
                        :background  => "W303",
                        :mating_type => "unknown",
                        :genotype    => "TUB1-GFP::URA3, ? can1-100 ade2-1 his3-11,-15 leu2-3,-112 trp1-1 ura3-1",
                        :freeze_date => "2002-02-01",
                        :notes       => "Tubulin alpha-1 chain")
    YeastStrain.create( :number      => "506",
                        :alias       => "RJD1801",
                        :background  => "W303",
                        :mating_type => "a",
                        :genotype    => "Cim5-GFP::HIS3, pep4::TRP1, bar1::LEU2, can1-100 ade2-1 his3-11,-15 leu2-3,-112 trp1-1 ura3-1",
                        :freeze_date => "2002-02-01",
                        :notes       => "26S proteasome complex, ATPase component ")
    YeastStrain.create( :number      => "507",
                        :alias       => "RJD1802",
                        :background  => "W303",
                        :mating_type => "a",
                        :genotype    => "Pre6-GFP::HIS3, pep4::TRP1, bar1::LEU2, can1-100 ade2-1 his3-11,-15 leu2-3,-112 trp1-1 ura3-1",
                        :freeze_date => "2002-02-01",
                        :notes       => "proteasome core catalytic complex subunit ")
    YeastStrain.create( :number      => "508",
                        :alias       => "RJD1803",
                        :background  => "W303",
                        :mating_type => "a",
                        :genotype    => "Pre6-GFP::HIS3, pep4::TRP1, bar1::LEU2, can1-100 ade2-1 his3-11,-15 leu2-3,-112 trp1-1 ura3-1",
                        :freeze_date => "2002-02-01",
                        :notes       => "proteasome core catalytic complex subunit ")
    YeastStrain.create( :number      => "509",
                        :alias       => "RJD1805",
                        :background  => "W303",
                        :mating_type => "a",
                        :genotype    => "Rpn11-GFP::HIS3, pep4::TRP1, bar1::LEU2, can1-100 ade2-1 his3-11,-15 leu2-3,-112 trp1-1 ura3-1",
                        :freeze_date => "2002-02-01",
                        :notes       => "19S regulatory particle of the proteasome subunit")
    YeastStrain.create( :number      => "510",
                        :alias       => "RJD1824",
                        :background  => "W303",
                        :mating_type => "a",
                        :genotype    => "Rpn12-GFP::HIS3, pep4::TRP1, bar1::LEU2, can1-100 ade2-1 his3-11,-15 leu2-3,-112 trp1-1 ura3-1",
                        :freeze_date => "2002-02-01",
                        :notes       => "26S proteasome complex, Non-ATPase component quite a few white colonies in the original streak-out plate")
  end
  def self.down
    YeastStrain.delete_all
  end
end
