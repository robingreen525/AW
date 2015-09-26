class Chemical < ActiveRecord::Base
  validates_presence_of :name, :company, :cat_num, :price, :unit, :storage_temp
  validates_numericality_of :price, :storage_temp
end
