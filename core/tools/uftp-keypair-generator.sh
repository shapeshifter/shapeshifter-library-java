#!/bin/bash

mvn install -U -f ../.. --projects :shapeshifter-api,:shapeshifter-core --am -q
mvn exec:java -f ../.. --projects :shapeshifter-core -Dexec.mainClass="org.lfenergy.shapeshifter.core.tools.UftpKeyPairTool" -q