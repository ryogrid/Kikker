class CreateCounters < ActiveRecord::Migration
  def self.up
    create_table :counters, :options => 'DEFAULT CHARSET=utf8' do |t|
      t.column :html_count, :integer, :null => false
      t.column :rss_count, :integer, :null => false
      t.column :api_count, :integer, :null => false
      t.column :target_date,:datetime,:null => false
    end
    add_index :counters, :id
    Counter.create(:html_count => '0',:rss_count => '0',:api_count => '0',:target_date => Time.now)
  end

  def self.down
    drop_table :counters
  end
end
