# data-integration
Extracts user behaviour metadata as JSON objects to CSV.

### To Whom It May Concern:

Hi! 

I run it from the command line with the following (after compiling, packaging, and testing through Maven):

```
java -cp target/DataIntegrationTest-1.0-SNAPSHOT.jar com.interset.DataIntegrationTask.TaskRunner src/main/resources/metadataObjects.json src/main/resources/results.csv
```

I used [Jackson](http://wiki.fasterxml.com/JacksonHome) to map JSON to POJOs and also to output metrics to the console as a JSON object. 

[Joda](http://www.joda.org/joda-time/) was used to parse dates and time zones, to format them into an ISO 8601 compliant string, and to compare times to determine the date range.

I manually programmed the writing to `results.csv` after converting the metadata into the schema. 

##### Classes:

`Filter` does the bulk of the work; it maps the JSON onto Metadata POJOS and also turns them into Record objects. 

It's got an instance of `Metrics`. Metrics is where the numbers and other information is stored and updated for eventual output. 

The `Metadata` class is the raw POJO version of the metadata objects from the JSON file. 

`Record` represents the Java object form of what will be written as CSV. The action mapping and Joda DateTime magic happen here. 

`TestRunner` houses the JUnit tests I wrote to verify the format of information in Record objects, mapping from one form to another, and metrics according to the sample metadata I made.

##### Notes:

Files are determined as unique based on their directory and filename. 