class TasteEntry < ActiveRecord::Base
  has_many :user_keywords,:dependent => true,:order => "tfidf_value desc"
  belongs_to :user
end
