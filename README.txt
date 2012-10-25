Fabric3 Samples

I. Introduction

The samples are intended to demonstrate capabilities of the Fabric3 runtime:

* Starter Applications - These applications demonstrate how to develop basic distributed applications using Fabric3. It is recommended users begin with
  these applications.

* Feature Applications - These applications demonstrate using specific Fabric3 features.

* BigBank Loan Application - BigBank showcases advanced features of the Fabric3 runtime. It is intended to provide a complete, real-world application
  that demonstrates SCA and Fabric3 best-practices.

* Policy Applications - Demonstrate how to create and apply custom policies in Fabric3.

II. Prerequisites
 
* JDK 6.0 or later

* Maven 3.0.4 or later to build the samples. Maven can be downloaded from http://maven.apache.org/download.html.

III. Installation

* To build the samples, execute the following command from the top level directory:
	
	mvn clean install

* The samples also contain an automated build for assembling a single-VM runtime as well as a set of runtimes that form a multi-clustered domain.
  To automatically download and build the runtimes, execute the following command from the /servers directory:
	
	mvn clean install

IV. Documentation

* The samples documentation can be found at http://docs.fabric3.org/display/fabric3/Getting+Started. 

V. Reporting Issues

* If you experience a problem or would like to suggest improvements, send a note to the user list (http://xircles.codehaus.org/projects/fabric3/lists)
  or file a JIRA issue (http://jira.codehaus.org/browse/FABRICTHREE). 




 
   


