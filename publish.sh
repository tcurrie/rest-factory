#!/bin/bash

if [[ $TRAVIS_PULL_REQUEST == "false" && $TRAVIS_TAG == "" ]]; then
    git config --global user.email "builds@travis-ci.com"
    git config --global user.name "Travis CI"
    git tag $VERSION -a -m "Generated tag from TravisCI for build $TRAVIS_BUILD_NUMBER"
    git push origin --tags
    openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in $DEPLOY/pubring.gpg.enc -out $DEPLOY/pubring.gpg -d
    openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in $DEPLOY/secring.gpg.enc -out $DEPLOY/secring.gpg -d
    mvn deploy --settings $DEPLOY/settings.xml -DskipTests=true -Prelease
    exit $?
fi

