Vagrant.configure("2") do |config|
  config.vm.box = "ubuntu/xenial64"

  config.vm.network "forwarded_port", guest: 8080, host: 9000
  config.vm.provider "virtualbox" do |vm|
     vm.memory = "1280"
  end

  # Dependencies
  config.vm.provision "shell", inline: <<-SHELL
    echo "...add repository"
    sudo add-apt-repository ppa:openjdk-r/ppa -y
    echo "...update"
    sudo apt-get update -y
    echo "...install openjdk 8"
    sudo apt-get install openjdk-8-jdk -y
    echo "...install maven"
    sudo apt-get install maven -y
    echo "finished installs!"
  SHELL

  # Spring Boot
  config.vm.provision "shell", run: 'always', inline: <<-SHELL
    cd /vagrant/
    mvn spring-boot:run
  SHELL
end