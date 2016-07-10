require 'xmlrpc/client'

#検索するドキュメントのタイプ 2=はてブ 3=CEEK.JP NEWS 4=Youtube 他=だめぽ
class Kikker
DOC_TYPE_HATENA = 2
DOC_TYPE_CEEK_NEWS = 3
DOC_TYPE_YOUTUBE = 4

  #ハッシュを返す
  #キーは,"titles","urls","eval_points","crawled_dates","categories","tags","view_counts"
  def Kikker.search(doc_type,keyword_arr,value_arr)
    client = Kikker::getClient()
    return client.call 'KikkerWebAPI.search',doc_type,keyword_arr,value_arr
  end
  
  #ハッシュを返す
  #キーは,"titles","urls","crawled_dates","categories","tags","view_counts"
  def Kikker.searchByKeyword(doc_type,keyword)
    client = Kikker::getClient()
    return client.call 'KikkerWebAPI.searchByKeyword',doc_type,keyword
  end
  
  #ハッシュを返す
  #キーは,"title","crawled_date","category","tags"
  def Kikker.getTasteByURL(doc_type,url)
    client = Kikker::getClient()
    return client.call 'KikkerWebAPI.getTasteByURL',doc_type,url
  end
  
  #ハッシュを返す
  #キーは,"titles","urls","eval_points","crawled_dates","categories","tags","view_counts"
  def Kikker.searchWithCollaborative(user_id)
    client = Kikker::getClient()
    rpc_result = client.call 'KikkerWebAPI.searchWithCollaborative',user_id
  end

private   
  def Kikker.getClient
     return XMLRPC::Client.new3(:host => 'localhost',:port => 7777,:timeout => 10000)
  end
end