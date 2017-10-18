#!/bin/bash

cd output/
for f in Postings_*.txt ;
do sort -k 1,1 -k 2n,2 -o $f < $f ;
done
sort -n --merge Postings_*.txt -o sortedPostings.txt
rm *_*.txt
#sort -o sortedPostings.txt -k 1,1 -k 2n,2 tempPostings.txt