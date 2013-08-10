Fabric3 Sample Applications
============================

Introduction
----------------
The samples are intended to demonstrate capabilities of the Fabric3 runtime:

* Starter Applications - These applications demonstrate how to develop basic distributed applications using Fabric3. It is recommended users begin with
  these applications.

* Feature Applications - These applications demonstrate using specific Fabric3 features.

* BigBank Loan Application - BigBank showcases advanced features of the Fabric3 runtime. It is intended to provide a complete, real-world application
  that demonstrates SCA and Fabric3 best-practices.

* FastQuote FX Trading Application - FastQuote demonstrates how to build low-latency applications with Fabric3 and makes use of features such as ZeroMQ
pub/sub communications and Disruptor integration.

* Policy Applications - Demonstrate how to create and apply custom policies in Fabric3.

Prerequisites
----------------

* JDK 7.0 or later

* Maven 3.0.4 or later to build the samples. Maven can be downloaded from http://maven.apache.org/download.html.

Installation
----------------

* To build the samples, execute the following command from the top level directory:

	mvn clean install

* The samples also contain an automated build for assembling a single-VM runtime as well as a set of runtimes that form a multi-clustered domain.
  To automatically download and build the runtimes, execute the following command from the /servers directory:

	mvn clean install

Documentation
----------------

* The samples documentation can be found at https://fabric3.atlassian.net/wiki/display/FABRIC/Getting+Started.

Reporting Issues
----------------

* If you experience a problem or would like to suggest improvements, send a note to the user list (http://xircles.codehaus.org/projects/fabric3/lists)
  or file a JIRA issue.



