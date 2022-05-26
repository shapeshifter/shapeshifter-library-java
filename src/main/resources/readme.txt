Java code can be generated form the xsd using JAXB xjc tool.  This is included with java 8 jre, but not from java 9 onwards.

Example command line to generated classes:

/usr/lib/jvm/java-8-openjdk-amd64/bin/xjc UFTP-agr.xsd
/usr/lib/jvm/java-8-openjdk-amd64/bin/xjc UFTP-dso.xsd
/usr/lib/jvm/java-8-openjdk-amd64/bin/xjc UFTP-cro.xsd
