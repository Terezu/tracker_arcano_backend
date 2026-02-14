#!/bin/sh
# Simple redirect to mvnw.cmd for Windows compatibility
exec "$(dirname "$0")/mvnw.cmd" "$@"
