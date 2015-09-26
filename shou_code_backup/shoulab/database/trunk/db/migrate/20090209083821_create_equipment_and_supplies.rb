class CreateEquipmentAndSupplies < ActiveRecord::Migration
  def self.up
    create_table :equipment_and_supplies do |t|
      t.string :description, :null => false
      t.string :company,     :null => false
      t.string :cat_num,     :null => false
      t.decimal :price, :precision => 8, :scale => 2, :default => 0
      t.string :unit
      t.text :notes
      t.integer :lock_version, :default => 0

      t.timestamps
    end
  end

  def self.down
    drop_table :equipment_and_supplies
  end
end
