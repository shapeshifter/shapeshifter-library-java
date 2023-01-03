#!/bin/bash

mvn install -U -f ../../.. --projects :uftp-connector --am -q
mvn exec:java -f ../../.. --projects :uftp-connector -Dexec.mainClass="org.lfenergy.shapeshifter.connector.tools.UFTPKeyPairTool" -q