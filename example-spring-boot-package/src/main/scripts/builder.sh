#!/bin/sh
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
IMAGE_NAME=example-spring-boot-package
CONTAINER_NAME=example-spring-boot-package
docker build -t ${IMAGE_NAME}:1.0 .
echo "^ Dockerfile successfully built ^"
docker run -d -p 18080:18080 -e TZ=Asia/Shanghai --name ${CONTAINER_NAME} \
-v /usr/share/zoneinfo:/usr/share/zoneinfo \
-v ${DIR}/config:/usr/local/example-spring-boot-package/config \
-v ${DIR}lib:/usr/local/example-spring-boot-package/lib \
-v ${DIR}/logs:/usr/local/example-spring-boot-package/logs \
${IMAGE_NAME}:1.0
if [ $? -eq 0 ]; then
  echo "^ The mirror has been started successfully ^"
  rm -rf ${DIR}/builder-start.sh ${DIR}/Dockerfile ${DIR}/builder.sh
fi