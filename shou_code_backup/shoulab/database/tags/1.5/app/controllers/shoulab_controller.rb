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

  def search
    terms = parse(params)
    sql = to_sql(terms)
    logger.warn(terms)
    @yeast_strains = YeastStrain.find_by_sql(sql)
    #@yeast_strains = YeastStrain.search("ade lys his")
  end

  def to_sql(p)
    table = 'yeast_strains'
    start = "SELECT * FROM `#{table}` WHERE "
    final = ''
    ts = []
    conditions = []
    p.each do |field, terms|
      ts = terms.split
      ts.each do |term|
        conditions.push "(UCASE(#{table}.#{field}) LIKE '%#{term.upcase}%')"
      end
    end
    final << start << '(' << conditions.join(" AND ") << ')'
  end
    
  def parse(ps)
    clean = {}
    ps.each do |key,value|
      next unless 
        key.to_s =~ /number|alias|background|mating_type|genotype|freeze_date|notes/ and 
        value.length > 0
    clean[key] = if value =~ /%/
      value.gsub(/%/,'\%') 
      else
        value
      end
    end
    return clean
  end

end
