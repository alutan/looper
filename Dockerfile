#       Copyright 2017-2019 IBM Corp All Rights Reserved

#   Licensed under the Apache License, Version 2.0 (the "License");
#   you may not use this file except in compliance with the License.
#   You may obtain a copy of the License at

#       http://www.apache.org/licenses/LICENSE-2.0

#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.

FROM ibmcom/websphere-liberty:kernel-ubi-min
USER root

ARG SSL=false
ARG MP_MONITORING=false
ARG HTTP_ENDPOINT=false

COPY ./server.xml /config/server.xml
COPY ./jvm.options /config/jvm.options
COPY ./server/target/server-1.0-SNAPSHOT.war /config/apps/looper.war
COPY ./client/target/client-1.0-SNAPSHOT.jar /loopctl.jar
COPY ./key.jks /config/resources/security/key.jks
COPY ./validationKeystore.jks /config/resources/security/validationKeystore.jks
COPY ./keystore.xml /config/configDropins/defaults/keystore.xml
COPY ./client/loopctl.sh /loopctl.sh

COPY ./src/main/liberty/config /config/
COPY ./target/portfolio-1.0-SNAPSHOT.war /config/apps/Portfolio.war
RUN chown -R 1001.0 /loopctl.jar /loopctl.sh /config /opt/ibm/wlp/usr/servers/defaultServer /opt/ibm/wlp/usr/shared/resources && chmod -R g+rw /config /opt/ibm/wlp/usr/servers/defaultServer  /opt/ibm/wlp/usr/shared/resources

EXPOSE 9080

USER 1001

RUN installUtility install --acceptLicense microprofile-3.0 monitor-1.0 appsecurity-2.0 jwtsso-1.0
