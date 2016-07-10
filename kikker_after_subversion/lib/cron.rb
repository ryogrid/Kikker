module Cron
  def Cron.create_crawl_hatebookmarked
    RailsCron.create(
      :command => "Cron.crawl_hatebookmarked",
      :start => Time.now,
      :every => 24.hours)
  end

  def Cron.crawl_hatebookmarked
      users = User.find(:all)
      users.each{|user|
        if user.user_preference.hatebu_name
          urls = Crawler.crawlHatebuUser(user.user_preference.hatebu_name)
          urls.each{|url|
            Util.updateTaste(Kikker::DOC_TYPE_HATENA,user.id,url)
          }
        end
      }
      Crawler.crawlHatebuUser(user_name)
  end
end
