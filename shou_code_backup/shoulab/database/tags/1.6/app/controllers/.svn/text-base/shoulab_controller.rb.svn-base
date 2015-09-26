class ShoulabController < ApplicationController
  def authorize
  end

  def index
  end

  def yeast
    @yeast_strains = YeastStrain.find(:all, :order => :number)
  end

  def bacteria
    @bacterial_plasmids = BacterialPlasmid.find(:all, :order => :number)
  end

  def oligos
    @oligos = Oligo.find(:all, :order => :number)
  end

  def equipment
    @equipment_and_supplies = EquipmentAndSupply.find(:all, :order => :id)
  end

  def search
    sql = to_sql(params[:table], params)
    if params[:table] == 'yeast_strains'
      logger.info "Yeast table"
      @yeast_strains = YeastStrain.find_by_sql(sql)
    elsif params[:table] == 'equipment'
      logger.info "Equipment table"
      @equipment = EquipmentAndSupply.find_by_sql(sql)
    end
  end

  def to_sql(table, ps)
    cleaned = clean(table, ps)
    statement = "SELECT * FROM `#{table}`" 
    return statement unless cleaned.length > 0
    statement << " WHERE "
    conditions = []
    cleaned.each do |field, terms|
      terms.split.each do |term|
        conditions.push "(UCASE(#{table}.#{field}) LIKE '%#{term.upcase}%')"
      end
    end
    statement << '(' << conditions.join(" AND ") << ')'
  end

  def clean(table, ps)
    clean = {}
    ps.each do |key,value|
      if table =~ /yeast/
        next unless key.to_s =~ /number|alias|background|mating_type|genotype|freeze_date|notes/ and value.length > 0
      elsif table =~ /equipment/
        next unless key.to_s =~ /description|company|cat_num|price|unit|notes/ and value.length > 0
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
