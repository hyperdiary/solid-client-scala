docker run --rm -v ~/Solid:/data -v ~/solid-config:/config  -p 3000:3000 -it solidproject/community-server:latest -c /config/my-config.json