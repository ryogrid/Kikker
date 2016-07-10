class AddSomeAboutHatebuToPreference < ActiveRecord::Migration
  def self.up
    add_column :user_preferences, :hatebu_name, :string
    add_column :user_preferences, :is_use_hatebu_learn, :boolean
  end

  def self.down
    remove_column :user_preferences, :hatebu_name
    remove_column :user_preferences, :is_use_hatebu_learn
  end
end
