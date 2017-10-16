#!/bin/bash

cd output/
sort -o sortedPostings.txt -k 1,1 -k 2n,2 tempPostings.txt