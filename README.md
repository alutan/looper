<!--
       Copyright 2017 IBM Corp All Rights Reserved

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
-->

The *looper* microservice runs a dozen calls to *portfolio* REST APIs in a loop.  You can use this for
performance and load testing.  It calls the *portfolio* service via the **mpRestClient** from
**MicroProfile**, and builds and passes the **JWT** that it requires.

Note it deliberately does not cause any changes in loyalty level, since **Twitter** will disable your
account if you blast in too many tweets per second.  It also doesn't talk to **Watson**, since you only
get 2500 free calls per month before it starts charging you per call.

It responds to a `GET ?count={count}` REST request, where you pass in the count of how many iterations
to run.  If you omit the **count** query param, it assumes 1 iteration.

For example, if you hit the `http://<hostname>:9080/looper?count=5` URL, it would run 5 iterations.  It
returns the output from the various calls (various collections of JSON) as `text/plain`.

Note that the call doesn't return until all the iterations are complete.  So you might be waiting a
long time in a browser (or curl) if you request a high count.  To address that, there's also a
command-line client that will also get installed to the pod running the looper servlet.  Just
`kubectl exec` into the pod, and then run `loopctl.sh`, passing it parameters as explained when you
run it with no parameters.  It will run a specified number of iterations, on a specified number of
parallel threads.  You will see output from every iteration, with timings.  Or you can build the
CLI client locally and run it from your laptop, passing the *node port* or *ingress* URL of where
the *looper* servlet is running.

 ### Prerequisites for OCP Deployment
 This project requires three secrets: `jwt`, and `urls`.
 
 ### Build and Deploy to OCP
To build `looper` clone this repo and run:
```
cd templates

oc create -f looper-liberty-projects.yaml

oc create -f looper-liberty-deploy.yaml -n looper-liberty-dev
oc create -f looper-liberty-deploy.yaml -n looper-liberty-stage
oc create -f looper-liberty-deploy.yaml -n looper-liberty-prod

oc new-app looper-liberty-deploy -n looper-liberty-dev
oc new-app looper-liberty-deploy -n looper-liberty-stage
oc new-app looper-liberty-deploy -n looper-liberty-prod

oc create -f looper-liberty-build.yaml -n looper-liberty-build

oc new-app looper-liberty-build -n looper-liberty-build

```
