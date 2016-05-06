#!/bin/bash

if [[ $TRAVIS_PULL_REQUEST == "false" ]]; then
    mvn deploy --settings $DEPLOY/settings.xml -DperformRelease=true -DskipTests=true
    exit $?
fi

