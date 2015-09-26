require 'test_helper'

class OligosControllerTest < ActionController::TestCase
  def test_should_get_index
    get :index
    assert_response :success
    assert_not_nil assigns(:oligos)
  end

  def test_should_get_new
    get :new
    assert_response :success
  end

  def test_should_create_oligo
    assert_difference('Oligo.count') do
      post :create, :oligo => { }
    end

    assert_redirected_to oligo_path(assigns(:oligo))
  end

  def test_should_show_oligo
    get :show, :id => oligos(:one).id
    assert_response :success
  end

  def test_should_get_edit
    get :edit, :id => oligos(:one).id
    assert_response :success
  end

  def test_should_update_oligo
    put :update, :id => oligos(:one).id, :oligo => { }
    assert_redirected_to oligo_path(assigns(:oligo))
  end

  def test_should_destroy_oligo
    assert_difference('Oligo.count', -1) do
      delete :destroy, :id => oligos(:one).id
    end

    assert_redirected_to oligos_path
  end
end
