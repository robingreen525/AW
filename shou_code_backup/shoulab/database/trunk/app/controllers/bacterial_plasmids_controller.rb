class BacterialPlasmidsController < ApplicationController
  # GET /bacterial_plasmids
  # GET /bacterial_plasmids.xml
  def index
    @bacterial_plasmids = BacterialPlasmid.find(:all, :order => :number)

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
    @bacterial_plasmid.number = get_new_number()

    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @bacterial_plasmid }
    end
  end

  # GET /bacterial_plasmids/1/edit
  def edit
    @bacterial_plasmid = BacterialPlasmid.find(params[:id])
  end

  def copy
    old = BacterialPlasmid.find(params[:id])
    @bacterial_plasmid = BacterialPlasmid.new(old.attributes)
    @bacterial_plasmid.number = get_new_number()
  end

  # POST /bacterial_plasmids
  # POST /bacterial_plasmids.xml
  def create
    upload = params[:bacterial_plasmid][:plasmid_sequence]
    params[:bacterial_plasmid][:plasmid_sequence] = upload_file(upload) if !upload.nil?
    @bacterial_plasmid = BacterialPlasmid.new(params[:bacterial_plasmid])

    respond_to do |format|
      if @bacterial_plasmid.save
        flash[:notice] = 'BacterialPlasmid was successfully created.'
        format.html { redirect_to(:action => :index) }
        format.xml  { render :xml => @bacterial_plasmid, 
                             :status => :created, 
                             :location => @bacterial_plasmid }
      else
        format.html { render :action => "new" }
        format.xml  { render :xml => @bacterial_plasmid.errors,
                             :status => :unprocessable_entity }
      end
    end
  end

  # PUT /bacterial_plasmids/1
  # PUT /bacterial_plasmids/1.xml
  def update
    @bacterial_plasmid = BacterialPlasmid.find(params[:id])

    upload = params[:bacterial_plasmid][:plasmid_sequence]
    params[:bacterial_plasmid][:plasmid_sequence] = upload_file(upload)

    respond_to do |format|
      if @bacterial_plasmid.update_attributes(params[:bacterial_plasmid])
        flash[:notice] = 'BacterialPlasmid was successfully updated.'
        format.html { redirect_to(:action => :index) }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @bacterial_plasmid.errors, 
                             :status => :unprocessable_entity }
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

private
  def get_new_number
      return BacterialPlasmid.find(:first, :order => "number DESC").number + 1
  end

  def upload_file(file_obj)
    return if file_obj.nil?
    seq_path = 
      Rails.root.join('/var/www/data/sequences', file_obj.original_filename)
    File.open(seq_path,'w') do |file|
      file.write(file_obj.read)
    end
    return file_obj.original_filename
  end
