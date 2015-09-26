# This file is auto-generated from the current state of the database. Instead of editing this file, 
# please use the migrations feature of Active Record to incrementally modify your database, and
# then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your database schema. If you need
# to create the application database on another system, you should be using db:schema:load, not running
# all the migrations from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended to check this file into your version control system.

ActiveRecord::Schema.define(:version => 20090107081116) do

  create_table "bacterial_plasmids", :force => true do |t|
    t.integer  "number",           :limit => 11,                :null => false
    t.string   "background"
    t.string   "plasmid_name"
    t.string   "plasmid_sequence"
    t.string   "notes"
    t.date     "freeze_date"
    t.integer  "lock_version",     :limit => 11, :default => 0
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "drug_resistance"
  end

  create_table "oligos", :force => true do |t|
    t.integer  "number",     :limit => 11,                                               :null => false
    t.string   "sequence",                                               :default => "", :null => false
    t.integer  "length",     :limit => 11,                                               :null => false
    t.decimal  "GC",                       :precision => 8, :scale => 2,                 :null => false
    t.decimal  "Tm",                       :precision => 8, :scale => 2,                 :null => false
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "direction"
    t.string   "plasmid"
    t.string   "target"
  end

  create_table "sessions", :force => true do |t|
    t.string   "session_id", :default => "", :null => false
    t.text     "data"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "sessions", ["session_id"], :name => "index_sessions_on_session_id"
  add_index "sessions", ["updated_at"], :name => "index_sessions_on_updated_at"

  create_table "users", :force => true do |t|
    t.string   "user_name"
    t.string   "first_name"
    t.string   "last_name"
    t.string   "email"
    t.string   "hashed_password"
    t.string   "salt"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "yeast_strains", :force => true do |t|
    t.integer  "number",       :limit => 11,                 :null => false
    t.string   "alias"
    t.string   "mating_type",                :default => "", :null => false
    t.string   "genotype",                   :default => "", :null => false
    t.text     "notes"
    t.date     "freeze_date"
    t.string   "background"
    t.integer  "lock_version", :limit => 11, :default => 0
    t.datetime "created_at"
    t.datetime "updated_at"
  end

end
