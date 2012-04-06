Files in this directory and recursive
will be copied to the embedded Solr home at startup if the solr home directory is missing.

What files are copied is hard coded in Application.java.

WARNING! Do not change solrconfig.xml/dataDir,
the text content is a search key that will change to correct path when copied.