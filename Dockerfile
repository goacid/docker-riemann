FROM openjdk:8

RUN apt-get -y update
RUN wget https://github.com/riemann/riemann/releases/download/0.2.14/riemann_0.2.14_all.deb && dpkg -i riemann_0.2.14_all.deb
RUN apt-get install -y build-essential ruby ruby-dev zlib1g-dev

# Install Riemann dashboard
RUN gem install riemann-client riemann-tools riemann-dash
RUN apt-get install -y python-pip
RUN pip install riemann-client

# Add riemann configuration and script files
#ADD ./config/riemann.config /etc/riemann/riemann.config
ADD ./config/riemann-dash-config.rb /etc/riemann/riemann-dash-config.rb
ADD ./config/ws_config.json /etc/riemann/ws_config.json
ADD ./scripts/start.sh /usr/local/bin/start.sh

#cleaning
RUN apt-get clean -y
# Expose ports
EXPOSE 4567
EXPOSE 5555/tcp
EXPOSE 5555/udp
EXPOSE 5556

# Start services
CMD ["start.sh"]
