class YeastStrain < ActiveRecord::Base
  validates_numericality_of :number
  validates_uniqueness_of :number
  validates_presence_of :number, :mating_type

  MAT_TYPES = [
    ["a", "a"],
    ["\xce\xb1"  , "\xce\xb1"],
    ["a/\xce\xb1", "a/\xce\xb1"],
    ["unknown"   , "unknown"]
  ]
end
