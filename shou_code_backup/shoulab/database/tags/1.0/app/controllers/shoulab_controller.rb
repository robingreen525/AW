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

end
