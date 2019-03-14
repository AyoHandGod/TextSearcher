
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * The FileSearchApp class is a command line program that allows a user
 * to search for and zip all files containing a target pattern.
 */
public class FileSearchApp {

    // Fields
    String path, pattern, zipFileName;
    Pattern regEx;
    List<File> zipFiles = new ArrayList<>();


    public static void main(String[] args) {
        FileSearchApp app = new FileSearchApp().argChecker(args);
        if(app.getPath() == null) return;
        try {
            app.walkDirectory(app.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The argChecker method, checks the arguments supplied to this command
     * application and applies them to construct a FileSearchApp object
     * @param args String[] of 3 arguments. Path [pattern] [zipFileName]
     * @return FileSearchApp object
     */
    public FileSearchApp argChecker(String[] args) {
        FileSearchApp app = new FileSearchApp();
        switch (Math.min(args.length, 3)) { // makes sure no more than three args
            case 0:
                System.out.println("USAGE: FileSearchApp path [pattern] " +
                        "[zipFile]");
                return app;
            case 3:
                app.setZipFileName(args[2]);
            case 2:
                app.setPattern(args[1]);
            case 1:
                app.setPath(args[0]);
        }
        return app;
    }

    /**
     * The walkDirectory method moves along the specified directory
     * path and process each file.
     * @param path target Directory path for search
     * @throws IOException if no file specified.
     */
    public void walkDirectory(String path) throws IOException {
        // Search through directory Java8 method
        Files.walk(Paths.get(path)).forEach(f -> processFile(f.toFile()));
        zipperFiles();
//        System.out.println("walkDirectory: " + path);
//        searchFile(null);
//        addFileToZip(null);
    }

    /**
     * the ProcessFile method determines if a file fits our target
     * search or not.
     * @param file The file to be checked for target
     */
    private void processFile(File file) {
        System.out.println("processFile: " + file);

        try {
            if (searchFile(file)) {
                addFileToZip(file);
            }
        } catch (IOException | UncheckedIOException io) {
            System.out.println("Error processing file: " + file +
                    " : " + io);
        }
    }

    /**
     * Reads through the content of a file and determines if it contains
     * the search target.
     * @param file - the file to be searched.
     * @return boolean whether or not target found.
     * @throws IOException - If an I/O error occurs opening the file
     */
    private boolean searchFile(File file) throws IOException {
        return Files.lines(file.toPath(), StandardCharsets.UTF_8)
                .anyMatch(t -> searchText(t));
    }

    /**
     * The searchText method searches a String for a regEx pattern.
     * @param text - the string to be searched.
     * @return boolean whether or not target pattern found.
     */
    private boolean searchText(String text) {
        return this.getPattern() == null ||
                this.regEx.matcher(text).matches();
    }

    /**
     * the addFileToZip method adds a file to zip
     * @param file - the file to be added to zip.
     */
    public void addFileToZip(File file) {
        if(getZipFileName() != null) {
            zipFiles.add(file);
        }
        System.out.println("addFileToZip: " + file);
    }

    /**
     * The zipperFiles method performs the process of zipping the
     * files into a single .zip file
     * @throws IOException - If an I/O error in File management.
     */
    public void zipperFiles() throws IOException {
        try(ZipOutputStream out = new ZipOutputStream(
                new FileOutputStream(getZipFileName())
        )) {
            File baseDir = new File(getPath());
            
            for(File file: zipFiles) {
                String fileName = getRelativeFileName(file, baseDir);
                
                ZipEntry zipEntry = new ZipEntry(fileName);
                zipEntry.setTime(file.lastModified());
                out.putNextEntry(zipEntry);
                
                Files.copy(file.toPath(), out);
                
                out.closeEntry();
            }
        }
    }

    /**
     * the getRelativeFileName method modifies our fileName so that it is prep.
     * for use with the zipper method.
     * @param file - File to be modified
     * @param baseDir - base directory of file
     * @return String of modified fileName.
     */
    private String getRelativeFileName(File file, File baseDir) {
        String fileName = file.getAbsolutePath().substring(
                baseDir.getAbsolutePath().length());
        fileName = fileName.replace('\\', '/');

        while (fileName.startsWith("/")) {
            fileName = fileName.substring(1);
        }

        return fileName;
    }


    // Getter/Setters
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
        this.regEx = Pattern.compile(pattern);
    }

    public String getZipFileName() {
        return zipFileName;
    }

    public void setZipFileName(String zipFileName) {
        this.zipFileName = zipFileName;
    }

}
