#!/usr/bin/env bash

mkdir input
mkdir output

cd input/

#download files
wget https://commoncrawl.s3.amazonaws.com/crawl-data/CC-MAIN-2017-39/segments/1505818685129.23/wet/CC-MAIN-20170919112242-20170919132242-00{000..100}.warc.wet.gz
#decompress
gunzip *.gz