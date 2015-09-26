class ShoulabController < ApplicationController
  def authorize
  end

  def index
  end

  def yeast
    @yeast_strains = YeastStrain.find(:all, :order => :number)
  end

  def plasmids
    @bacterial_plasmids = BacterialPlasmid.find(:all, :order => :number)
  end

  def oligos
    @oligos = Oligo.find(:all, :order => :number)
  end

  def equipment
    @equipment_and_supplies = EquipmentAndSupply.find(:all, :order => :id)
  end
  
  def chemicals
    @chemicals = Chemical.find(:all, :order => :name)
  end

  def search
    sql = to_sql(params[:table], params)
    if params[:table] == 'yeast_strains'
      logger.info "Yeast table"
      @yeast_strains = YeastStrain.find_by_sql(sql).sort_by { |y| y.number }
      render :action => "yeast"
    elsif params[:table] == 'equipment_and_supplies'
      logger.info "Equipment table"
      @equipment_and_supplies = EquipmentAndSupply.find_by_sql(sql)
      render :action => "equipment"
    elsif params[:table] == 'chemicals'
      logger.info "Chemical table"
      @chemicals = Chemical.find_by_sql(sql)
      render :action => "chemicals"
    else
      flash[:notice] = "Don't recognize table #{params[:table]}"
    end
  end

private
  def to_sql(table, ps)
    cleaned = clean(table, ps)
    statement = "SELECT * FROM `#{table}`" 
    return statement unless cleaned.length > 0
    statement << " WHERE "
    conditions = []
    cleaned.each do |field, terms|
      terms.split.each do |term|
        neg = []
        if term.split(//)[0] == '-'
          if term.length > 1
            neg = 'NOT'
            term.gsub!('-','')
          else next
          end
        end
        conditions.push "(UCASE(#{table}.#{field}) #{neg} LIKE '%#{term.upcase}%')"
      end
    end
    statement << '(' << conditions.join(" AND ") << ')'
  end

  def clean(table, ps)
    clean = {}
    ps.each do |key,value|
      if value.length > 0
        if table =~ /yeast/
          next unless key.to_s =~ /number|alias|background|mating_type|genotype|freeze_date|notes/
        elsif table =~ /equipment/
          next unless key.to_s =~ /description|company|cat_num|price|unit|notes/
        elsif table =~ /chemical/
          next unless key.to_s =~ /name|storage_loc|storage_temp|company|cat_num|price|unit|notes/
        end
      else
        next
      end
      if value =~ /([*"'\;])/
        flash[:notice] = "Illegal characters in '#{key}' field (`#{$1}`)!" 
        return clean.clear
      end
      clean[key] = if value =~ /%/
        value.gsub(/%/,'\%')
        else
          value
        end
      end
      return clean
    end
end
