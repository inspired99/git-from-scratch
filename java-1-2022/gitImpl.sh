#!/bin/bash
args=$@
./gradlew run -q --args="$args"