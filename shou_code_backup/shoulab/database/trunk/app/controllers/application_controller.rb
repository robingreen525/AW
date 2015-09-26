# Filters added to this controller apply to all controllers in the 
# application.
# Likewise, all the methods added will be available for all controllers.

class ApplicationController < ActionController::Base
  layout "shoulab"

  before_filter :authorize, :except => :login

  session :session_key => '_shoulab_session_id'

  helper :all # include all helpers, all the time

  # See ActionController::Base for details 
  # Uncomment this to filter the contents of submitted sensitive data parameters
  # from your application log (in this case, all fields with names like "password"). 
  # filter_parameter_logging :password

    def authorize
      unless User.find_by_id(session[:user_id])
        redirect_to :controller => :admin, :action => :login
      end
    end
end
