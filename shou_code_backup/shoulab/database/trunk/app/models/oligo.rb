class Oligo < ActiveRecord::Base

  validates_presence_of :number, :sequence, :direction, :target, :initials
  validates_numericality_of :number
  validates_uniqueness_of :number, :sequence
  validates_format_of :sequence,
                      :with => /^[AGCT]+$/i,
                      :message => "is invalid"
  validates_format_of :sequence,
                      :with => /[agct]+/,
                      :message => "needs at least one base of homologous sequence (lowercase)"
  validates_format_of :direction,
                      :with => /^[FR]$/,
                      :message => "must be 'F' or 'R'"
  validates_format_of :initials, :with => /[A-Za-z]/

  DIRECTION = [
    ['Select Direction', ''],
    ['F', 'F'],
    ['R', 'R'],
    ['Random','Random']
  ]

  def upcase_direction
    self.direction.upcase!
  end

  protected

  def self.calculate(oligo)
    gc_count = oligo.sequence.count("gc").to_f

    oligo.length = oligo.sequence.count("atgc")
    oligo.gc     = 100* gc_count/oligo.length.to_f
    oligo.tm     = 59.4 + 41*gc_count/oligo.length.to_f - 500/oligo.length.to_f + 2
  end

end
