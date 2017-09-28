#!/bin/bash

# start riemann services
riemann /etc/riemann/riemann.config &
riemann-dash /etc/riemann/riemann-dash-config.rb
