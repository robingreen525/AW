require 'test_helper'

class BacterialPlasmidsControllerTest < ActionController::TestCase
  def test_should_get_index
    get :index
    assert_response :success
    assert_not_nil assigns(:bacterial_plasmids)
  end

  def test_should_get_new
    get :new
    assert_response :success
  end

  def test_should_create_bacterial_plasmid
    assert_difference('BacterialPlasmid.count') do
      post :create, :bacterial_plasmid => { }
    end

    assert_redirected_to bacterial_plasmid_path(assigns(:bacterial_plasmid))
  end

  def test_should_show_bacterial_plasmid
    get :show, :id => bacterial_plasmids(:one).id
    assert_response :success
  end

  def test_should_get_edit
    get :edit, :id => bacterial_plasmids(:one).id
    assert_response :success
  end

  def test_should_update_bacterial_plasmid
    put :update, :id => bacterial_plasmids(:one).id, :bacterial_plasmid => { }
    assert_redirected_to bacterial_plasmid_path(assigns(:bacterial_plasmid))
  end

  def test_should_destroy_bacterial_plasmid
    assert_difference('BacterialPlasmid.count', -1) do
      delete :destroy, :id => bacterial_plasmids(:one).id
    end

    assert_redirected_to bacterial_plasmids_path
  end
end
