require 'test_helper'

class EquipmentAndSuppliesControllerTest < ActionController::TestCase
  def test_should_get_index
    get :index
    assert_response :success
    assert_not_nil assigns(:equipment_and_supplies)
  end

  def test_should_get_new
    get :new
    assert_response :success
  end

  def test_should_create_equipment_and_supply
    assert_difference('EquipmentAndSupply.count') do
      post :create, :equipment_and_supply => { }
    end

    assert_redirected_to equipment_and_supply_path(assigns(:equipment_and_supply))
  end

  def test_should_show_equipment_and_supply
    get :show, :id => equipment_and_supplies(:one).id
    assert_response :success
  end

  def test_should_get_edit
    get :edit, :id => equipment_and_supplies(:one).id
    assert_response :success
  end

  def test_should_update_equipment_and_supply
    put :update, :id => equipment_and_supplies(:one).id, :equipment_and_supply => { }
    assert_redirected_to equipment_and_supply_path(assigns(:equipment_and_supply))
  end

  def test_should_destroy_equipment_and_supply
    assert_difference('EquipmentAndSupply.count', -1) do
      delete :destroy, :id => equipment_and_supplies(:one).id
    end

    assert_redirected_to equipment_and_supplies_path
  end
end
