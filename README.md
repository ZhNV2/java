## Structure of vcs

Every version of every file in repo is been storing as blob(contains only byte[] of its content).
Every commit has references to all added files in this commit and reference to parent commit.
That means that if you want to access all files in revision, you should get around all commits in the
current branch and restore all files were added (that is been using in checkout and  merge).
Merge doesn't allow conflicts except equal content files.
VcsObject is representing in JSON structure and hash of its representation defines object's hash.
Branch is a file containing hash of corresponding commit.

## Usage

java -classpath "build\classes\main;libs\gson-2.2.4.jar;libs\jcommander-1.48.jar" ru.Main ... to execute from project folder.
use --help for more details.