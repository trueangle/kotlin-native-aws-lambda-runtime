FROM public.ecr.aws/lambda/provided:al2

COPY sample/build/bin/linuxX64/releaseExecutable/sample.kexe /var/runtime/bootstrap

COPY large-file.json /var/task/large-file.json
RUN chmod +r /var/task/large-file.json

COPY o.json /var/task/o.json
RUN chmod +r /var/task/o.json

# Check if RIE is in /usr/local/bin or /opt/aws
RUN if [ -f /usr/local/bin/aws-lambda-rie ] || [ -f /opt/aws/aws-lambda-rie ]; then echo "RIE is present"; else echo "RIE is not present"; fi

COPY lambda_docker_entry_point.sh /lambda_docker_entry_point.sh
RUN chmod +x /lambda_docker_entry_point.sh

ENTRYPOINT ["/lambda_docker_entry_point.sh"]
