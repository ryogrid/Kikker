# Filters added to this controller will be run for all controllers in the application.
# Likewise, all the methods added will be available for all controllers.
class ApplicationController < ActionController::Base
helper_method :authorized?
before_filter :set_charset,:check_address
ACCESS_DENY = YAML::load_file("#{RAILS_ROOT}/db/access_deny.yml").freeze  
  
  def authorized?
    session[:user_id]
  end

protected  
  # HTTPヘッダにエンコード情報を付加
  def set_charset
    content_type = @headers["Content-Type"] || 'text/html'
    if /^text\//.match(content_type)
      @headers["Content-Type"] = "#{content_type}; charset=utf-8"
    end
  end

  #アクセス制限を行う
  def check_address
    if ACCESS_DENY.index(request.env["REMOTE_ADDR"]) != nil
      render "You can't read this site : - )"
      p "access denied from" + request["REMOTE_ADDR"]
      return false
    end
  end
private
  def authorize
    unless authorized_admin?
      #ログインした後にそこに戻れるように保存しておく
      session[:jumpto] = request.parameters
      flash[:notice] = "ログインして下さい"
      redirect_to(:action => "login")
    end
  end
  
  
  
end