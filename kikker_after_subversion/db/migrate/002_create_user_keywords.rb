class CreateUserKeywords < ActiveRecord::Migration
  def self.up
    create_table :user_keywords, :options => 'DEFAULT CHARSET=utf8' do |t|
      t.column :taste_entry_id, :integer, :null => false
      t.column :keyword, :string, :null => false
      t.column :tfidf_value,:float, :null => false
    end
    add_index :user_keywords, :id
    add_index :user_keywords, :keyword
  end

  def self.down
    drop_table :user_keywords
  end
end
