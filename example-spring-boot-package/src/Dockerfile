FROM registry.sensetime.com/docker.io/openjdk:8-alpine
MAINTAINER zhouyuyang<11393923@qq.com>
VOLUME /tmp
WORKDIR /usr/local/example-spring-boot-package
COPY ./builder-start.sh ./
RUN chmod 755 builder-start.sh \
&& sed -i "s/\r//" builder-start.sh
ENTRYPOINT ["/bin/sh", "-c", "./builder-start.sh"]