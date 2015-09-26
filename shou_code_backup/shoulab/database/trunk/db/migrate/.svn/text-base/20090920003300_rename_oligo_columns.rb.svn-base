class RenameOligoColumns < ActiveRecord::Migration
  def self.up
    rename_column :oligos, :GC, :gc
    rename_column :oligos, :Tm, :tm
  end

  def self.down
    rename_column :oligos, :gc, :GC
    rename_column :oligos, :tm, :Tm
  end
end
