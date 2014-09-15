from dgageot/java8
maintainer Ludovic Borie <l.borie@free.fr>

# Install Git
run apt-get install -y git

# Clone project
run git clone https://github.com/GDG-Lille/twitter2pubsub.git

# Download most of maven dependencies
run cd twitter2pubsub && mvn dependency:go-offline

# Build
run cd twitter2pubsub && mvn verify dependency:copy-dependencies

workdir twitter2pubsub

cmd ["java", "-jar", "target/twitter2pubsub.jar", "#JenniferLawrence"]