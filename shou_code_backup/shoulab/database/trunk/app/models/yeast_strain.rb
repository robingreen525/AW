class YeastStrain < ActiveRecord::Base
  validates_presence_of :number, :mating_type, :initials
  validates_uniqueness_of :number
  validates_numericality_of :number
  validates_format_of :initials, :with => /[A-Za-z]/

  MAT_TYPES = [
    ["All", ""],
    ["a"         , "a"],
    ["\xce\xb1"  , "\xce\xb1"],
    ["a/\xce\xb1", "a/\xce\xb1"],
    ["unknown"   , "unknown"]
  ]
end
