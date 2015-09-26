require 'test_helper'

class YeastStrainsControllerTest < ActionController::TestCase
  def test_should_get_index
    get :index
    assert_response :success
    assert_not_nil assigns(:yeast_strains)
  end

  def test_should_get_new
    get :new
    assert_response :success
  end

  def test_should_create_yeast_strain
    assert_difference('YeastStrain.count') do
      post :create, :yeast_strain => { }
    end

    assert_redirected_to yeast_strain_path(assigns(:yeast_strain))
  end

  def test_should_show_yeast_strain
    get :show, :id => yeast_strains(:one).id
    assert_response :success
  end

  def test_should_get_edit
    get :edit, :id => yeast_strains(:one).id
    assert_response :success
  end

  def test_should_update_yeast_strain
    put :update, :id => yeast_strains(:one).id, :yeast_strain => { }
    assert_redirected_to yeast_strain_path(assigns(:yeast_strain))
  end

  def test_should_destroy_yeast_strain
    assert_difference('YeastStrain.count', -1) do
      delete :destroy, :id => yeast_strains(:one).id
    end

    assert_redirected_to yeast_strains_path
  end
end
