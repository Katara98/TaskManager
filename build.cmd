javac -cp libs/log4j-1.2.17.jar -d classes -sourcepath ./sources sources/ua/edu/sumdu/j2se/volyk/tasks/*.java ^
sources/ua/edu/sumdu/j2se/volyk/tasks/models/*.java ^
sources/ua/edu/sumdu/j2se/volyk/tasks/views/*.java ^
sources/ua/edu/sumdu/j2se/volyk/tasks/controllers/*.java

jar cvfm program.jar manifest.mf -C classes/ . -C res/ .
pause