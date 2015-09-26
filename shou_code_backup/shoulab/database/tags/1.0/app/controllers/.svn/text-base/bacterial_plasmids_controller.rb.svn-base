class BacterialPlasmidsController < ApplicationController
  # GET /bacterial_plasmids
  # GET /bacterial_plasmids.xml
  def index
    @bacterial_plasmids = BacterialPlasmid.find(:all)

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @bacterial_plasmids }
    end
  end

  # GET /bacterial_plasmids/1
  # GET /bacterial_plasmids/1.xml
  def show
    @bacterial_plasmid = BacterialPlasmid.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :xml => @bacterial_plasmid }
    end
  end

  # GET /bacterial_plasmids/new
  # GET /bacterial_plasmids/new.xml
  def new
    @bacterial_plasmid = BacterialPlasmid.new
    @bacterial_plasmid.number =
      BacterialPlasmid.find(:first, :order => "number DESC").number

    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @bacterial_plasmid }
    end
  end

  # GET /bacterial_plasmids/1/edit
  def edit
    @bacterial_plasmid = BacterialPlasmid.find(params[:id])
  end

  # POST /bacterial_plasmids
  # POST /bacterial_plasmids.xml
  def create
    @bacterial_plasmid = BacterialPlasmid.new(params[:bacterial_plasmid])

    respond_to do |format|
      if @bacterial_plasmid.save
        flash[:notice] = 'BacterialPlasmid was successfully created.'
        format.html { redirect_to(@bacterial_plasmid) }
        format.xml  { render :xml => @bacterial_plasmid, :status => :created, :location => @bacterial_plasmid }
      else
        format.html { render :action => "new" }
        format.xml  { render :xml => @bacterial_plasmid.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /bacterial_plasmids/1
  # PUT /bacterial_plasmids/1.xml
  def update
    @bacterial_plasmid = BacterialPlasmid.find(params[:id])

    respond_to do |format|
      if @bacterial_plasmid.update_attributes(params[:bacterial_plasmid])
        flash[:notice] = 'BacterialPlasmid was successfully updated.'
        format.html { redirect_to(@bacterial_plasmid) }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @bacterial_plasmid.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /bacterial_plasmids/1
  # DELETE /bacterial_plasmids/1.xml
  def destroy
    @bacterial_plasmid = BacterialPlasmid.find(params[:id])
    @bacterial_plasmid.destroy

    respond_to do |format|
      format.html { redirect_to(bacterial_plasmids_url) }
      format.xml  { head :ok }
    end
  end
end
