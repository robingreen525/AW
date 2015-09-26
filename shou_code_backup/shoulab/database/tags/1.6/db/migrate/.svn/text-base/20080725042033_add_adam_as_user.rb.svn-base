class AddAdamAsUser < ActiveRecord::Migration
  def self.up

    self.down

    User.create( :user_name => 'nodice',
                 :first_name => 'Adam',
                 :last_name => 'Waite',
                 :email     => 'nodice@u.washington.edu',
                 :hashed_password => '9d3f9ced41f697016cc92b04b9fabf828bf6eb0e',
                 :salt => '-6172523480.968442941783914' )
  end

  def self.down
    User.delete_all
  end
end
