class ChemicalsController < ApplicationController
  # GET /chemicals
  # GET /chemicals.xml
  def index
    @chemicals = Chemical.find(:all, :order => :name)

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @chemicals }
    end
  end

  # GET /chemicals/new
  # GET /chemicals/new.xml
  def new
    @chemical = Chemical.new

    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @chemical }
    end
  end

  # GET /chemicals/1/edit
  def edit
    @chemical = Chemical.find(params[:id])
  end

  def copy
    old = Chemical.find(params[:id])
    @chemical = Chemical.new(old.attributes)
  end

  # POST /chemicals
  # POST /chemicals.xml
  def create
    @chemical = Chemical.new(params[:chemical])

    respond_to do |format|
      if @chemical.save
        flash[:notice] = 'Chemical was successfully entered.'
        format.html { redirect_to(:action => :index) }
        format.xml  { render :xml => @chemical, :status => :created, :location => @chemical }
      else
        format.html { render :action => "new" }
        format.xml  { render :xml => @chemical.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /chemicals/1
  # PUT /chemicals/1.xml
  def update
    @chemical = Chemical.find(params[:id])

    respond_to do |format|
      if @chemical.update_attributes(params[:chemical])
        flash[:notice] = 'Chemical was successfully updated.'
        format.html { redirect_to(:action => :index) }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @chemical.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /chemicals/1
  # DELETE /chemicals/1.xml
  def destroy
    @chemical = Chemical.find(params[:id])
    @chemical.destroy

    respond_to do |format|
      format.html { redirect_to(chemicals_url) }
      format.xml  { head :ok }
    end
  end
end
