module KikkerHelper
SHINOBI_TOP = "072197900"
SHINOBI_KEYWORD = "072197901"
SHINOBI_SUGGEST = "072197902"
SHINOBI_REGIST = "0721979003"
SHINOBI_OTHER = "0721979004"

BUNNER_TYPE_HATEBU = 0
BUNNER_TYPE_NEWS = 1
BUNNER_TYPE_OTHER = 2
BUNNER_TYPE_YOUTUBE = 3

ARRANGE_KEYWORD_COUNT = 10
USER_TASTE_LIMIT = 200 #実際の計算に使うユーザのベクトル長

CEEK_NEWS_NATIONAL = "社会";
CEEK_NEWS_POLITICS = "政治"
CEEK_NEWS_WORLD = "国際"
CEEK_NEWS_CHINA = "中国・朝鮮"
CEEK_NEWS_BUSINESS = "ビジネス"
CEEK_NEWS_IT = "電脳"
CEEK_NEWS_SPORTS = "スポーツ"
CEEK_NEWS_ENTERTAINMENT = "エンターテイメント"
CEEK_NEWS_SCIENCE = "サイエンス"
CEEK_NEWS_OBITUARIES = "訃報・人事"
CEEK_NEWS_LOCAL = "地方・地域"
CEEK_NEWS_ETC = "その他"

ALIGN_KEYWORD_LIMIT = 10
ALIGN_USER_KEYWORDS = 50
ALIGN_YOUTUBE_HTML_LIMIT = 30
ALIGN_EACH_NEWS_HTML_LIMIT = 10
ALIGN_HATEBU_HTML_LIMIT = 50
ALIGN_HATEBU_RSS_LIMIT = 50
ALIGN_YOUTUBE_RSS_LIMIT = 30
ALIGN_NEWS_RSS_LIMIT = 30

NOTICE = YAML::load_file("#{RAILS_ROOT}/db/notice.yaml").freeze

def get_notice()
  result = "<div id=\"notice\">"
  
  if NOTICE.instance_of?(Array) && (NOTICE.length > 0)
    NOTICE.each{|line|
      result += line + '<br>'
    }
    return result + "</div>"
  else
    return nil
  end
end

def get_notice_rss_description()
  if NOTICE.instance_of?(Array) && (NOTICE.length > 0)
    result = ""
    NOTICE.each{|line|
      result += line + '<br>'
    }
    return result
  else
    return nil
  end
end

end
