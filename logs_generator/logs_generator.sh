#!/bin/bash
if [ "$1" == "log4j" ] ;then
	if [ -f log4j/Log4jSocketAppenderExample.class ]; then	
		sudo rm -rf log4j/Log4jSocketAppenderExample.class
	fi
	sudo javac -cp log4j/*:.  log4j/Log4jSocketAppenderExample.java
	if [ -f log4j/Log4jSocketAppenderExample.class ]; then
			sudo chmod 755 log4j/Log4jSocketAppenderExample.class	
			echo "Start to run log4j appender to send logs - Press Ctrl + C to stop ."
			sudo java -cp log4j/*:. log4j.Log4jSocketAppenderExample
	else 
			echo "Can not run log4j appender to send logs !"
	fi
else
	if [ "$1" == "socket" ] ;then
		echo "Start to to send logs via socket - Press Ctrl + C to stop ."
		sudo ruby socket/socket.rb
	else
		echo "Incorrect arguments : Argument must be 'socket' or 'log4j'"
		echo "Example : /logs_generator.sh log4j"
	fi
fi
