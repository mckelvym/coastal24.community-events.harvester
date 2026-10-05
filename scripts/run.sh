#!/bin/bash

# Source version from version.sh
source "$(dirname "$0")/version.sh"
echo "docker run $IMAGE:${VERSION}"
docker pull $IMAGE:$VERSION
docker run --rm --name=coastal24.community-events-harvester \
    -v $(pwd)/../logs:/logs \
    -v $(pwd)/../../coastal24.community-events.rss:/data \
    $IMAGE:$VERSION \
    /data/events.xml
