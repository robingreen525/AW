class CreateOligos < ActiveRecord::Migration
  def self.up
    create_table :oligos do |t|
      t.integer :number,   :null => false
      t.string  :sequence,  :null => false
      t.string  :direction
      t.text    :target
      t.text    :notes
      t.integer :length,   :null => false
      t.decimal :GC, :null => false, :precision => 5, :scale => 3
      t.decimal :Tm,  :null => false, :precision => 5, :scale => 3
      t.string  :plasmid
      t.integer :lock_version, :default => 0

      t.timestamps
    end
  end

  def self.down
    drop_table :oligos
  end
end
