package test.ru.spbau;

import static org.junit.Assert.assertEquals;

public class WorkingCopyComandTest {

//    @Rule
//    public TemporaryFolder folder = new TemporaryFolder();
//
//    @Test
//    public void saveAndRestore() throws IOException {
//        File workFolder = folder.newFolder();
//        Vcs.setCurrentFolder(workFolder.getAbsolutePath());
//        fillFolder(workFolder);
//        List<Path> filesOld = Files.walk(Paths.get(workFolder.getPath())).sorted(FileSystem.compByLengthRev).collect(Collectors.toList());
//        Vcs.saveWorkingCopy();
//        for (Path path : filesOld) {
//            if (path.equals(Paths.get(workFolder.getPath()))) continue;
//            Files.deleteIfExists(path);
//        }
//        Vcs.restoreWorkingCopy();
//        filesOld = filesOld.stream().sorted().collect(Collectors.toList());
//        List<Path> filesNew = Files.walk(Paths.get(workFolder.getPath())).sorted().collect(Collectors.toList());
//        assertEquals(filesOld, filesNew);
//        for (Path path : filesNew) {
//            if (Files.isDirectory(path)) continue;
//            String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
//            assertEquals(content, path.getFileName().toString());
//        }
//
//    }
//
//    private void fillFolder(File workFolder) throws IOException {
//        folder.newFolder(workFolder.getName() + File.separator + "dir1");
//        folder.newFolder(workFolder.getName() + File.separator + "dir1" + File.separator + "dir2");
//        folder.newFolder(workFolder.getName() + File.separator + "dir3");
//        File a = folder.newFile(workFolder.getName() + File.separator + "a");
//        File b = folder.newFile(workFolder.getName() + File.separator + "b");
//        File c = folder.newFile(workFolder.getName() + File.separator + "dir1" + File.separator + "dir2" + File.separator + "c");
//        File d = folder.newFile(workFolder.getName() + File.separator + "dir3" + File.separator + "d");
//        for (File file : Arrays.asList(a, b, c, d)) {
//            Files.write(Paths.get(file.getPath()), file.getName().getBytes());
//        }
//    }

}
