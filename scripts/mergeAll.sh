#!/bin/sh

git checkout ice-cream-sandwich
git merge master

git checkout beta
git merge master

git checkout beta-ics
git merge beta
git merge ice-cream-sandwich

git checkout ics-catalogs
git merge ice-cream-sandwich

git checkout ics-catalogs-reorder
git merge ics-catalogs

git checkout position-2
git merge master

git checkout master
