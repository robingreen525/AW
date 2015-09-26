class CreateBacterialPlasmids < ActiveRecord::Migration
  def self.up
    create_table :bacterial_plasmids do |t|
      t.integer :number,         :null => false
      t.string :background
      t.string :plasmid_name
      t.string :plasmid_sequence
      t.string :notes
      t.date :freeze_date
      t.integer :lock_version, :default => 0

      t.timestamps
    end
  end

  def self.down
    drop_table :bacterial_plasmids
  end
end
