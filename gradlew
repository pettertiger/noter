#!/usr/bin/env bash

#
# Copyright 2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ] ; do
    ls -ld "$PRG"
    link=$( ls -l "$PRG" | awk '{print $NF}' )
    case $link in
        /*) PRG="$link" ;;
        *) PRG=$( cd -P "$( dirname "$PRG" )" && pwd ) / "$link" ;;
    esac
done
SAVED="$( cd -P "$( dirname "$PRG" )" && pwd )"
APP_HOME=$( cd "$( dirname "$SAVED" )" && pwd )
APP_HOME=$( cd -P "$APP_HOME" && pwd )
APP_HOME=$( cd -P "$APP_HOME" && pwd )
cd "$APP_HOME" || exit

APP_NAME="Gradle"
APP_JAR="gradle-wrapper.jar"

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD=maximum

warn () {
    echo "$*"
} >&2

die () {
    echo
    echo "$*"
    echo
    exit 1
} >&2

# OS specific support (must be 'true' or 'false').
cygwin=false
msys=false
darwin=false
nonstop=false
case "$( uname )" in
  CYGWIN* )
    cygwin=true
    ;;
  Darwin* )
    darwin=true
    ;;
  MSYS* | MINGW* )
    msys=true
    ;;
  NONSTOP* )
    nonstop=true
    ;;
esac

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD=java
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
fi

# Increase the maximum file descriptors if we can.
if ! "$cygwin" && ! "$msys" ; then
    case $MAX_FD in
      /*)
        MAX_FD_LIMIT=$( ulimit -H -n )
        if [ $MAX_FD_LIMIT != 'unlimited' ] ; then
            MAX_FD=$MAX_FD_LIMIT
        fi
        ;;
    esac
    ulimit -n $MAX_FD
    if [ $? -eq 1 ] ; then
        warn "Could not set maximum file descriptor limit: $MAX_FD"
    fi
fi

# Escape application args
save () {
    for i do printf %s\\n "$i" | sed "s/'/'\\\\''/g;1s/^/'/;\$s/\$/' \\\\/" ; done
    echo " "
}
APP_ARGS=$( save "$@" )

# Collect all arguments for the java command, stacking in reverse order:
#   * DEFAULT_JVM_OPTS
#   * JAVA_OPTS
#   * GRADLE_OPTS
#   * -classpath
#   * -D...sysprop settings
#   * user and project args
set -- \
        "-Dorg.gradle.appname=$APP_BASE_NAME" \
        -classpath "$CLASSPATH" \
        org.gradle.wrapper.GradleWrapperMain \
        "$APP_ARGS"

# Stop when "xargs" has processed all arguments
if ! "$cygwin" && ! "$msys" ; then
    case $MAX_FD in
      /*)
        MAX_FD_LIMIT=$( ulimit -H -n )
        if [ $MAX_FD_LIMIT != 'unlimited' ] ; then
            MAX_FD=$MAX_FD_LIMIT
        fi
        ;;
    esac
    ulimit -n $MAX_FD
    if [ $? -eq 1 ] ; then
        warn "Could not set maximum file descriptor limit: $MAX_FD"
    fi
fi

# Use eval to handle embedded quotes.
eval '"$JAVACMD"' "$DEFAULT_JVM_OPTS" "$JAVA_OPTS" "$GRADLE_OPTS" -classpath '"$CLASSPATH"' org.gradle.wrapper.GradleWrapperMain "$APP_ARGS"
