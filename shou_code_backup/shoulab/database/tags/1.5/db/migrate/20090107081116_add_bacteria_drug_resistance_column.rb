class AddBacteriaDrugResistanceColumn < ActiveRecord::Migration
  def self.up
    add_column :bacterial_plasmids, :drug_resistance, :string
  end

  def self.down
    remove_column :bacterial_plasmids, :drug_resistance
  end
end
