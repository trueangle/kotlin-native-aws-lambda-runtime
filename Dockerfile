FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && \
    apt-get install -y openjdk-17-jdk curl libcurl4-openssl-dev git unzip && \
    apt-get clean

WORKDIR /sample