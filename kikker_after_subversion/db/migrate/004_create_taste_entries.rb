class CreateTasteEntries < ActiveRecord::Migration
  def self.up
    create_table :taste_entries, :options => 'DEFAULT CHARSET=utf8' do |t|
      t.column :user_id, :integer, :null => false
    end
    add_index :taste_entries, :id
    add_index :taste_entries, :user_id
  end

  def self.down
    drop_table :taste_entries
  end
end
