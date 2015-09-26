class YeastStrainsController < ApplicationController
  # GET /yeast_strains
  # GET /yeast_strains.xml
  def index
    @yeast_strains = YeastStrain.find(:all, :order => "number")

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @yeast_strains }
    end
  end

  # GET /yeast_strains/new
  # GET /yeast_strains/new.xml
  def new
    @yeast_strain = YeastStrain.new
    @yeast_strain.number = 
      YeastStrain.find(:first, :order => "number DESC").number + 1

    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @yeast_strain }
    end
  end

  # GET /yeast_strains/1/edit
  def edit
    @yeast_strain = YeastStrain.find(params[:id])
  end

  # POST /yeast_strains
  # POST /yeast_strains.xml
  def create
    @yeast_strain = YeastStrain.new(params[:yeast_strain])

    respond_to do |format|
      if @yeast_strain.save
        flash[:notice] = 'Yeast Strain was successfully entered.'
        format.html { redirect_to(:action => :index) }
        format.xml  { render :xml => @yeast_strain, :status => :created, :location => @yeast_strain }
      else
        format.html { render :action => "new" }
        format.xml  { render :xml => @yeast_strain.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /yeast_strains/1
  # PUT /yeast_strains/1.xml
  def update
    @yeast_strain = YeastStrain.find(params[:id])

    respond_to do |format|
      if @yeast_strain.update_attributes(params[:yeast_strain])
        flash[:notice] = 'Yeast Strain was successfully updated.'
        format.html { redirect_to(:action => :index) }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @yeast_strain.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /yeast_strains/1
  # DELETE /yeast_strains/1.xml
  def destroy
    @yeast_strain = YeastStrain.find(params[:id])
    @yeast_strain.destroy

    respond_to do |format|
      format.html { redirect_to(yeast_strains_url) }
      format.xml  { head :ok }
    end
  end
end
