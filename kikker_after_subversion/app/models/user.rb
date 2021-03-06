class User < ActiveRecord::Base
  attr_accessor :taste_vector
  
  has_many :taste_entries,:dependent => true
  has_one :user_preference,:dependent => true
  
  def self.login(nickname,password)
    find(:first,:conditions=> ["nickname = ? and password = ?",nickname,password])
  end
  
  def try_to_login
    User.login(self.nickname,self.password)
  end
end
