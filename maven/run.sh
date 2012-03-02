#!/usr/bin/env bash

export MAVEN_OPTS="-d32 -Xmx1G -server"

mkdir logs >> /dev/null
cd server

cp src/main/resources/datahamstern.properties.server src/main/resources/datahamstern.properties

# Setup the ctl-c handler
RETRY=1
handle_ctl_c()
{
        RETRY=0
}
trap "handle_ctl_c" 2

while [ "$RETRY" == "1" ]; do
  export currentLog="`date +"%s"`.log"
#  echo > ../logs/$currentLog
#  rm ../current.log
#  ln -s ../logs/$currentLog ../current.log
  mvn -Djetty.port=8081 -Ddatahamstern.homePath=../ jetty:run >> ../logs/$currentLog
done
