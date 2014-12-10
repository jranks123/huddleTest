Introduction
========
Your task is to develop the route calculation logic at the heart of a basic Sat Nav system.
The Sat Nav system is to guide a driver around a network of one way streets in a small town. 
The road junctions are inspiringly named A to E. The map data provides you with the distance 
between these junctions, as well as the direction of travel that is permitted along that street. 
For example AB4 represents a road from A to B (but not from B to A), with a length of 4.

You will need to provide code that calculates:

1. The length of a given route
2. The number of possible routes between two points (that meet some conditions)
3. The shortest route between two points.

Input
======
The program gets the towns and distances from a file called input.text.  


This shoud be in the format

		[town][town][Distance]
		[town][town][Distance]

etc

eg.

		AB1
		ER4
		EA2


Test Input
==========
The tests are input to the program via a file called tests.text. The provided test have already been added but to add your ownsimply append a test onto a new line of tests.text with the following format:  

Format of route length test:  

    routeLength [route]

Where:  
.[route] is a string in the format "[town][town][town]...[town]"
eg routeLength ABCA


Format of get number of routes test:

      getNumberOfRoutes [Test name]  [operator] [number] [Start Town] [End Town]

Where:  
.[Test Name] is a string containing the test type, either "JunctionTest" or "WeightTest"  
.[Number] is an integer that signifies the limiting number for the test  
.[Start Town] is a a string containing the start town  
.[End Town] is a a string containing the end town  
.[Operator] is a string containing the operator that will limit the results, either "equals" to print the number of routes   
that match the limiting number exactly or "upto" to print the number of routes that are no greater that the limiting number.  
eg getNumberOfRoutes WeightTest upto 30 A B  



Format of get shortest route test: 

      getShortestRoute [Start Town] [End Town]

Where:  
.[Start Town] is a a string containing the start town
.[End Town] is a a string containing the end town
eg getShortestRoute A B


INSTRUCTIONS  
=====  
Download the git repository from https://github.com/jranks123/huddleTest
Navigate to the huddleTest-master folder.    

In the command line enter: java huddleMapProblem input.text tests.text


