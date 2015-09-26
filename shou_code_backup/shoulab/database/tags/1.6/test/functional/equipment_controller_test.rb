require 'test_helper'

class EquipmentControllerTest < ActionController::TestCase
  def test_should_get_index
    get :index
    assert_response :success
    assert_not_nil assigns(:equipment)
  end

  def test_should_get_new
    get :new
    assert_response :success
  end

  def test_should_create_equipment
    assert_difference('Equipment.count') do
      post :create, :equipment => { }
    end

    assert_redirected_to equipment_path(assigns(:equipment))
  end

  def test_should_show_equipment
    get :show, :id => equipment(:one).id
    assert_response :success
  end

  def test_should_get_edit
    get :edit, :id => equipment(:one).id
    assert_response :success
  end

  def test_should_update_equipment
    put :update, :id => equipment(:one).id, :equipment => { }
    assert_redirected_to equipment_path(assigns(:equipment))
  end

  def test_should_destroy_equipment
    assert_difference('Equipment.count', -1) do
      delete :destroy, :id => equipment(:one).id
    end

    assert_redirected_to equipment_path
  end
end
