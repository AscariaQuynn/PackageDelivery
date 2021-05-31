# PackageDelivery
App that keeps record of packages processed. Each package info consist of weight (kg), destination postal code and optionally fees related to package weight.

#### How to clone repository into Idea
- Open IntelliJ IDEA. Welcome screen appears (if not, then File -> Close Project).
- Use "Get from Version Control" and screen for filling repository URL will appear
- Insert repository URL: https://github.com/AscariaQuynn/PackageDelivery.git and proceed with "Clone"

#### How to build project in Idea
- make sure Maven window is visible, View -> Tool Windows -> Maven
- on Maven window, click on "m" icon in the middle of the top panel, window "Run Anything" will appear
- you need to run command "mvn clean install", so type it there and hit enter
- you will see in the bottom window of the Idea that maven goals are running
- it should take no more that 15-30 sec and finish with message "BUILD SUCCESS"

#### How to prepare app for running
- make sure Java is accessible via command "java" in your favorite terminal/command line, so open it and type "java" and hit enter
- if it is not working, you should install at least java 8, you can Google it of follow for example https://www3.ntu.edu.sg/home/ehchua/programming/howto/JDK_Howto.html
- open your favorite terminal/command line, navigate to location where you cloned this project, for example "C:/Projects/PackageDelivery"
- when you list files, you should see "target" folder and some other folders and files

#### Run and use the app
- the easiest way, type: java -jar target/package-delivery-1.0-SNAPSHOT.jar and hit enter
- from here, you can navigate by instructions in terminal/command line
- so, app informed you that "Option 'Packages' is required."...
- when you listed files in current folder, you can see "InitialPackages.pack" file and "Fees.fee"
- you can use those two files. app is showing you how to use them:
  - PackageDelivery usage: java -jar package-delivery-1.0-SNAPSHOT.jar --p=<FILE> [--f=<FILE>]
  - note that command in [ ] is optional and thus you can ommit it
  - so final command could be: java -jar target/package-delivery-1.0-SNAPSHOT.jar --p=InitialPackages.pack --f=Fees.fee

#### Inside the app
- so app is running, welcome text "Welcome to the PackageDelivery app!" appeared, you can start using app by typing your favorite weight and postcode
- type "15.8 12345" and hit enter, you can see that app informed you your data was added
- when invalid input is given, app will tell you about it and how to type correct one
- every minute, packages are automatically printed into the terminal/command line
- if you don't want to wait, type "print" and hit enter
- exit application with "quit" command

## App output
- while printing interim results from memory, all packages added with same post code will
be transformed into one interim result with weights added together and if Fees file was used,
then associated fees will be added to interim results

example Fees.fee file
```
weight   price
10       5.00
5        2.50
3        2.00
2        1.50
1        1.00
0.5      0.70
0.2      0.50
0        0.00
 ```
