@echo off
set log_type="%1"
if %log_type% == "log4j" (
	if exist log4j\Log4jSocketAppenderExample.class (
		del log4j\Log4jSocketAppenderExample.class
	)
	javac -cp log4j\*;. log4j\Log4jSocketAppenderExample.java
	if exist log4j\Log4jSocketAppenderExample.class (
		echo Start to run log4j appender to send logs - Press Ctrl + C to stop .
		java -cp log4j\*;. log4j.Log4jSocketAppenderExample
	) else (	
		echo Can not run log4j appender to send logs !
	)
) else (
	if %log_type% == "socket" (
		echo Start to to send logs via socket - Press Ctrl + C to stop .
		ruby socket/socket.rb
	) else (
		echo Incorrect arguments ; Argument must be 'socket' or 'log4j'
		echo Example ; C;\\logs_generator.bat log4j
	)
)