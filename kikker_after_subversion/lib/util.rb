class Util

#ユーザの趣向を更新する
#categoryはhatebu,news,youtubeのどれか
def Util.updateTaste(category,user_id,url)
  user = User.find_by_id(user_id)
  if user
    begin
      rpc_result = Kikker.getTasteByURL(Util.categoryStrToDocType(category),url)
    rescue
      return #例外が発生したら抜けてしまう(現状、DBに入っていないURLを与えられると例外が返却されるみたい)
    end
    begin
      taste_entry_id = user.taste_entries[0].id
    rescue
    end
    #一つずつすでにあるか確認しつつ、追加、もしくは更新する
    rpc_result["tags"].each{|each_tag|
          already_exist = UserKeyword.find(:first,:conditions => ["taste_entry_id = ? and keyword = ?",taste_entry_id,each_tag[0]])
          if already_exist
            already_exist.tfidf_value += each_tag[1]
            already_exist.save()
          else
            UserKeyword.create(:taste_entry_id => taste_entry_id,:keyword => each_tag[0],:tfidf_value => each_tag[1])
          end
    }
  end
end

def Util.categoryStrToDocType(doc_str)
  hash = {"hatebu" => 2,"news" => 3,"video" => 4}
  return hash[doc_str]
end

end