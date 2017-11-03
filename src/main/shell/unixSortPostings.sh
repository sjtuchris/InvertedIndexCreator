#!/bin/bash

cd output/
#for f in Postings_*.txt ;
#do sort -t$'\t' -k 1,1 -k 2n,2 -o $f < $f ;
#done
cat Postings_*.txt > sortedPostings.txt
sort -S 10G -t$'\t' -k 1,1 -k 2n,2 -o sortedPostings.txt < sortedPostings.txt
rm *_*.txt
#sort -o sortedPostings.txt -k 1,1 -k 2n,2 tempPostings.txt