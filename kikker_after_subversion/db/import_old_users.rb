require 'yaml'
require 'rubygems'
require_gem 'activerecord'

settings = YAML::load_file("config/database.yml")
ActiveRecord::Base.establish_connection(settings['development'])


class User < ActiveRecord::Base
  has_many :taste_entries,:dependent => true
end

class TasteEntry < ActiveRecord::Base
  has_many :user_keywords,:dependent => true,:order => "tfidf_value desc"
  belongs_to :user
end

class UserKeyword < ActiveRecord::Base
  belongs_to :taste_entry
end

filename1 = "./db/userentry.csv"
file_in = open(filename1,"r")
file_in.each{|line|
  tmp_arr = line.split(",")
  new_user = User.new()
  new_user.nickname = tmp_arr[0]
  new_user.password = tmp_arr[1]
  new_user.name = ""
  new_user.mail_address = ""
  new_user.age = 0
  new_user.regist_at = Time.now()
  new_user.save()
  new_user.taste_entries << TasteEntry.new()
  new_user.save()
}
file_in.close()

filename2 = "./db/user_keyword_table.csv"
file_in = open(filename2,"r")
file_in.each{|line|
  tmp_arr = line.split(",")
  target_user = User.find_by_nickname(tmp_arr[0])
  if target_user
    target_user.taste_entries[0].user_keywords.create(:keyword => tmp_arr[1],:tfidf_value => tmp_arr[2].to_f)
  end
}
file_in.close()

