class OligosController < ApplicationController
  # GET /oligos
  # GET /oligos.xml
  def index
    @oligos = Oligo.find(:all, :order => :number)

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @oligos }
    end
  end

  # GET /oligos/new
  # GET /oligos/new.xml
  def new
    @oligo = Oligo.new
    last_oligo = Oligo.find(:first, :order => "number DESC")
    if last_oligo
      @oligo.number = last_oligo.number + 1
    else
      @oligo.number = 1
    end

    respond_to do |format|
      format.html #index.html.erb
      format.xml  { render :xml => @oligo }
    end
  end

  # GET /oligos/1/edit
  def edit
    @oligo = Oligo.find(params[:id])
  end

  def copy
    old           = Oligo.find(params[:id])
    @oligo        = Oligo.new(old.attributes)
    @oligo.number = get_new_number()
  end

  # POST /oligos
  # POST /oligos.xml
  def create
    @oligo = Oligo.new(params[:oligo])
    Oligo.calculate(@oligo)

    respond_to do |format|
      if @oligo.save
        flash[:notice] = 'Oligo was successfully created.'
        format.html { redirect_to :action => :index }
        format.xml  { render :xml => @oligo, :status => :created, :location => @oligo }
      else
        format.html { render :action => "new" }
        format.xml  { render :xml => @oligo.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /oligos/1
  # PUT /oligos/1.xml
  def update
    @oligo = Oligo.find(params[:id])
    @oligo.attributes = params[:oligo]
    Oligo.calculate(@oligo)

    logger.info @oligo.attributes

    respond_to do |format|
      if @oligo.update_attributes(@oligo.attributes)
        flash[:notice] = 'Oligo was successfully updated.'
        format.html { redirect_to :action => :index }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @oligo.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /oligos/1
  # DELETE /oligos/1.xml
  def destroy
    @oligo = Oligo.find(params[:id])
    @oligo.destroy

    respond_to do |format|
      format.html { redirect_to(oligos_url) }
      format.xml  { head :ok }
    end
  end

private
  def get_new_number
      return Oligo.find(:first, :order => "number DESC").number + 1
  end
end
