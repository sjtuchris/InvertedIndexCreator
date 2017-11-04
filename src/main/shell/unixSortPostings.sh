#!/bin/bash

cd output/

cat Postings_*.txt > sortedPostings.txt
sort -S 2G -t$'\t' -k 1,1 -k 2n,2 -o sortedPostings.txt < sortedPostings.txt
rm *_*.txt
