## Inner structure

Server and client are both non-blocking.
There are special classes (Reader and Writer) performing basic work with channels.
Reader reads data from channel and does something with at (look at update).
Writer writes data to channel.


## Usage
build -> gradle serverJar

build -> gradle clientJar

help -> --help

## UI Usage

Double click to download file.

## Apologizes

Despite of essential performance, now there are several technical issues (such as file channels).
I hope I'll fix them in 3-4 days.

## Updates

Now file channels are removed, but there is still a problem with continuous connection between server and client.
Also button exit does not work.
Hope fix it in several days.

