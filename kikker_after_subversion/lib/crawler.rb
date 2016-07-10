require 'net/http'

class Crawler

def Crawler.crawl()
    users = User.find(:all)
    users.each{|user|
      if user.user_preference&&user.user_preference.hatebu_name
        urls = Crawler.crawlHatebuUser(user.user_preference.hatebu_name)
        p urls
        urls.each{|url|
          Util.updateTaste("hatebu",user.id,url[0])
        }
      end
    }
end

#はてブの個人ページを1ページだけ取得し、その中に含まれるブックマークされているページのURLの配列を返す
def Crawler.crawlHatebuUser(user_name)
  doc = getAUserPage(user_name)
  return doc.scan(/<dt class="bookmark"><a href="(.*)" class="/)
end

private

#1ページだけリクエスト
def Crawler.getAUserPage(user_name)
  Net::HTTP.version_1_2
  return Net::HTTP.get 'b.hatena.ne.jp', '/' + user_name + '/'
end

end