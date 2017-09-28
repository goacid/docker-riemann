# Serve HTTP traffic on this port
set  :port, 4567

# Answer queries sent to this IP address
set  :bind, "0.0.0.0"

# Custom riemann layout
config[:ws_config] = '/etc/riemann/ws_config.json'
