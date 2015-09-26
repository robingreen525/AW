class AdminController < ApplicationController

  def login
    session[:user_id] = nil
    if request.post?
      user = User.authenticate(params[:user_name], params[:password])
      if user
        session[:user_id] = user.id
        flash[:notice] = "Welcome #{user.first_name}"
        redirect_to :action => :index
      else
        flash.now[:notice] = "Invalid user/password combination"
      end
    end
  end

  def logout
    session[:user_id] = nil
    flash[:notice] = 'Logged out'
    redirect_to :action => :login
  end

  def index
  end

end
