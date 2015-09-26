class AddColumnsToBacterialPlasmids < ActiveRecord::Migration
  def self.up
    add_column :bacterial_plasmids, :alias, :string
    add_column :bacterial_plasmids, :source, :string
    add_column :bacterial_plasmids, :description, :text
  end

  def self.down
    remove_column :bacterial_plasmids, :alias, :string
    remove_column :bacterial_plasmids, :source, :string
    remove_column :bacterial_plasmids, :description, :text
  end
end
