class CreateOligos < ActiveRecord::Migration
  def self.up
    create_table :oligos do |t|
      t.integer :number,   :null => false
      t.string  :sequence,  :null => false
      t.integer :length,   :null => false
      t.decimal :GC, :null => false, :precision => 8, :scale => 2
      t.decimal :Tm,  :null => false, :precision => 8, :scale => 2

      t.timestamps
    end
  end

  def self.down
    drop_table :oligos
  end
end
