class Oligo < ActiveRecord::Base

  before_validation :upcase_direction

  validates_presence_of :number, :sequence, :direction, :target
  validates_numericality_of :number
  validates_uniqueness_of :number, :sequence
  validates_format_of :sequence,
                      :with => /^[AGCT]+$/i,
                      :message => "is invalid"
  validates_format_of :direction,
                      :with => /^[FR]$/,
                      :message => "must be 'F' or 'R'"


  def upcase_direction
    self.direction.upcase!
  end

  protected

  def self.calculate(oligo)
    oligo.length = oligo.sequence.length
    oligo.GC     = (oligo.sequence.count("GC").to_f / oligo.length.to_f) * 100
    oligo.Tm     = 59.4 + 41*(oligo.GC/100)/oligo.length - 500/oligo.length + 2
  end

end
