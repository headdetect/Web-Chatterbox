Chatterbox
==============

##Installs

- [MySQL server] (http://www.sitepoint.com/how-to-install-mysql/)
- [Play Framework] (http://www.playframework.org/documentation/2.0.4/Installing)
- [Intellij (Optional)] (http://www.jetbrains.com/idea/)


##Running

### Starting App

- [Play instructions] (http://www.playframework.org/documentation/2.0.4/NewApplication)

### Debugging with Intellij

- Start play application using `play debug`
- In Intellij, click the debug button. Debug Configurations window should pop up.
- Add new `Remote` configuration.
- Set port to `9999`, Module to `Web-Chatterbox`
- Leave everything else at default.
- Save and run.


##Contributing

- All code must be formatted using my exported format. Import format in eclipse using [formatEclipse.xml](Web-Chatterbox/blob/master/app/eclipseFormat.xml)
- All classes must include sectioned comments that are found in [format.txt](Web-Chatterbox/blob/master/app/format.txt)