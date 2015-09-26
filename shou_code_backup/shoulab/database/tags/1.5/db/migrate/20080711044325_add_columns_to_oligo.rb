class AddColumnsToOligo < ActiveRecord::Migration
  def self.up
    add_column :oligos, :direction, :string
    add_column :oligos, :plasmid, :string
    add_column :oligos, :target, :string
  end

  def self.down
    remove_column :oligos, :target
    remove_column :oligos, :plasmid
    remove_column :oligos, :direction
  end
end
