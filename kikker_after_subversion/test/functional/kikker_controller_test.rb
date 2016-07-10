require File.dirname(__FILE__) + '/../test_helper'
require 'kikker_controller'

# Re-raise errors caught by the controller.
class KikkerController; def rescue_action(e) raise e end; end

class KikkerControllerTest < Test::Unit::TestCase
  def setup
    @controller = KikkerController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
  end

  # Replace this with your real tests.
  def test_truth
    assert true
  end
end
