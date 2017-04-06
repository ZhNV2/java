package ru.spbau;

import ru.spbau.zhidkov.ExternalFileHandler;
import ru.spbau.zhidkov.VcsFileHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Class implementing add command.
 */
public class AddCommand {

    private ExternalFileHandler externalFileHandler;
    private VcsFileHandler vcsFileHandler;

    public AddCommand(ExternalFileHandler externalFileHandler, VcsFileHandler vcsFileHandler) {
        this.externalFileHandler = externalFileHandler;
        this.vcsFileHandler = vcsFileHandler;
    }


    /**
     * Adds files to repo (adds them to temporary list of
     * files to add).
     *
     * @param fileNames list of files to add.
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void add(List<String> fileNames) throws IOException, Vcs.VcsIncorrectUsageException {
//        fileNames = fileNames.stream()
//                .map(s-> Vcs.getCurrentFolder() + File.separator + s)
//                .map(FileSystem::normalize)
//                .collect(Collectors.toList());

        for (String fileName : fileNames) {
            if (!externalFileHandler.exists(fileName)) {
                throw new FileNotFoundException(fileName);
            }
            if (externalFileHandler.isDirectory(fileName)) {
                throw new Vcs.VcsIncorrectUsageException("You may add only files");
            }
        }
        //StringBuilder stringBuilder = new StringBuilder();
        vcsFileHandler.removeFromList(VcsFileHandler.ListWithFiles.RM_LIST, fileNames);
        vcsFileHandler.addToList(VcsFileHandler.ListWithFiles.ADD_LIST, fileNames);
    }

}
