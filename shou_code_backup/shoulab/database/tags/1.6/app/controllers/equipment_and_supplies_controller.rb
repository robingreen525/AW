class EquipmentAndSuppliesController < ApplicationController
  # GET /equipment_and_supplies
  # GET /equipment_and_supplies.xml
  def index
    @equipment_and_supplies = EquipmentAndSupply.find(:all)

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @equipment_and_supplies }
    end
  end

  # GET /equipment_and_supplies/1
  # GET /equipment_and_supplies/1.xml
  def show
    @equipment_and_supply = EquipmentAndSupply.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :xml => @equipment_and_supply }
    end
  end

  # GET /equipment_and_supplies/new
  # GET /equipment_and_supplies/new.xml
  def new
    @equipment_and_supply = EquipmentAndSupply.new

    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @equipment_and_supply }
    end
  end

  # GET /equipment_and_supplies/1/edit
  def edit
    @equipment_and_supply = EquipmentAndSupply.find(params[:id])
  end

  # POST /equipment_and_supplies
  # POST /equipment_and_supplies.xml
  def create
    @equipment_and_supply = EquipmentAndSupply.new(params[:equipment_and_supply])

    respond_to do |format|
      if @equipment_and_supply.save
        flash[:notice] = 'EquipmentAndSupply was successfully created.'
        format.html { redirect_to(@equipment_and_supply) }
        format.xml  { render :xml => @equipment_and_supply, :status => :created, :location => @equipment_and_supply }
      else
        format.html { render :action => "new" }
        format.xml  { render :xml => @equipment_and_supply.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /equipment_and_supplies/1
  # PUT /equipment_and_supplies/1.xml
  def update
    @equipment_and_supply = EquipmentAndSupply.find(params[:id])

    respond_to do |format|
      if @equipment_and_supply.update_attributes(params[:equipment_and_supply])
        flash[:notice] = 'EquipmentAndSupply was successfully updated.'
        format.html { redirect_to(@equipment_and_supply) }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @equipment_and_supply.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /equipment_and_supplies/1
  # DELETE /equipment_and_supplies/1.xml
  def destroy
    @equipment_and_supply = EquipmentAndSupply.find(params[:id])
    @equipment_and_supply.destroy

    respond_to do |format|
      format.html { redirect_to(equipment_and_supplies_url) }
      format.xml  { head :ok }
    end
  end
end
