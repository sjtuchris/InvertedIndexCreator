# InvertedIndexCreator

See Introduction.pdf for more details.

Overview:

This generator has two main functions: generating inverted index list using
Common Crawl data set and executing query search using inverted index list. The
development environment is OS X or Linux

Instructions:

Pre-requisites: Java 7, mongoDB, node, maven. MongoDB should be started
before running this program.

First, go to directory of the package, build the project using Maven, in terminal:

	cd path/to/your/InvertedIndexCreator
	mvn install

Then, prepare the dataset using shell “initialize.sh”, which will download and
decompress the common crawl data automatically. In terminal:

	mvn exec:java

Input E, and after it finishes, you will see the common crawl data in the “input”
directory.

To start generating inverted index list, again, in terminal:

	mvn exec:java

Type A
It will take some time to generate this index list. We will talk about run speed
details in other section.
After it finishes, you will see three files: lexicon.txt, pageUrlTable.txt and
invertedIndexList.txt.
Here we come to the query processing part. In terminal:

	mvn exec:java

Type B

Now it’s time to query, go to “frontEnd” directory, in terminal:

	cd frontEnd
	npm install
	npm run dev

Type in the words you would like to search, the rule is that “&” represents
conjunctive search and “@” means disjunctive search. Click search button or
press enter.

You can also just type http://localhost:8888/query?dog&cat&bird in your browser
to get the result directly.