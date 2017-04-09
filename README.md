## Inner Structure of vcs

Every version of every file in repo is been storing as blob(contains only byte[] of its content).
Every commit has references to all added and removed files in this commit and reference to parent commit.
That means that if you want to access all files in revision, you should get around all commits in the
current branch and restore all files were added or removed (that is been using in checkout and merge for example).
Merge doesn't allow conflicts except equal content files.
VcsObject is representing in JSON structure and hash of its representation defines object's hash.
Branch is a file containing hash of corresponding commit.

## Class hierarchy

All classes were divided into 5 packages representing 5 levels of application:
- parser(gui)
- vcs command classes
- handlers for operations with basic objects such as commit, branch, working copy etc
- vcs object classes encapsulate work with vcs blobs and commits as entities been representing as vcs files
- file system and serializer/deserializer

You can read package and class documentation for more information

## Usage

build -> gradle fatJar

help -> --help

