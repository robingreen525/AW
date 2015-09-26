class CreateChemicals < ActiveRecord::Migration
  def self.up
    create_table :chemicals do |t|
      t.string  :name,         :null => false
      t.string  :storage_loc,  :null => false
      t.integer :storage_temp, :null => false
      t.string  :company,      :null => false
      t.string  :cat_num,      :null => false
      t.decimal :price, :precision => 8, :scale => 2, :default => 0
      t.string  :unit
      t.text    :notes
      t.integer :lock_version, :default => 0

      t.timestamps
    end
  end

  def self.down
    drop_table :chemicals
  end
end
