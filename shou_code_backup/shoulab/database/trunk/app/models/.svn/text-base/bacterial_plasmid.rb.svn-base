class BacterialPlasmid < ActiveRecord::Base
  validates_presence_of     :number, :drug_resistance, :plasmid_name, :source, :initials
  validates_uniqueness_of   :number
  validates_uniqueness_of   :plasmid_sequence, :allow_nil=>true
  validates_numericality_of :number
  validates_format_of :initials, :with => /[A-Za-z]/
end
