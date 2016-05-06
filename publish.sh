#!/bin/bash

if [[ $TRAVIS_PULL_REQUEST == "false" ]]; then
    mvn deploy --settings $DEPLOY/settings.xml -DskipTests=true -Prelease
    exit $?
fi

