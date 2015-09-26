class CreateYeastStrains < ActiveRecord::Migration
  def self.up
    create_table :yeast_strains do |t|
      t.integer :number,       :null => false
      t.string :alias
      t.string :mating_type,   :null => false
      t.string :genotype,      :null => false
      t.text :notes
      t.date :freeze_date
      t.string :background
      t.integer :lock_version, :default => 0

      t.timestamps
    end
  end

  def self.down
    drop_table :yeast_strains
  end
end
