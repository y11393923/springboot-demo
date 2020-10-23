#!/bin/sh
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
IMAGE_NAME=example-spring-boot-package
CONTAINER_NAME=example-spring-boot-package
if [ -z ${1} ]; then
   echo "^ Please enter docker or k8s to build ^"
   exit
fi
if [ ${1} = 'docker' ]; then
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
elif [ ${1} = 'k8s' ]; then
    docker build -t ${IMAGE_NAME}:1.0 .
    echo "^ Dockerfile successfully built ^"
    echo "^ Start k8s build ^"
    sed -i "s| imageName| ${IMAGE_NAME}| g" deploy.yml
    sed -i "s| logPath| ${DIR}| g" deploy.yml
    sed -i "s| libPath| ${DIR}| g" deploy.yml
    sed -i "s| configPath| ${DIR}| g" deploy.yml
    kubectl apply -f deploy.yml
    if [ $? -eq 0 ]; then
      echo "^ k8s successfully built ^"
      rm -rf ${DIR}/builder-start.sh ${DIR}/Dockerfile ${DIR}/builder.sh
    fi
else
    echo "^ Please enter docker or k8s to build ^"
fi
