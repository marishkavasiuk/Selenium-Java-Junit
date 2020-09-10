package com.ui.helpers;

import com.ui.webdriver.DriverHelper;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import static com.ui.common.Config.USE_GRID;
import static com.ui.common.Constants.PLG_TEST_FILES;
import static com.ui.helpers.PerformanceHelper.repeat;
import static java.lang.Runtime.getRuntime;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileHelper {
    /**
     * @param filePath - absolute path or must be in ClassPath (config for now)
     * @return
     * @throws IOException
     */

    private static final Logger logger = LoggerFactory. getLogger(FileHelper. class);

    public static String getFileContent(String filePath) throws IOException {
        logger.info("File path is " + filePath);
        ClassLoader classLoader = FileHelper.class.getClassLoader();
        InputStream in = classLoader.getResourceAsStream(filePath);
        StringBuilder out = new StringBuilder();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1; ) {
            out.append(new String(b, 0, n));
        }
        in.close();
        logger.info("File content is \n" + out.toString());
        return out.toString();
    }

    public static void createLargeXSDFile(String filePath) throws IOException {
        FileWriter fw = new FileWriter(filePath);
        try {
            fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n");
            fw.write("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n");

            fw.write("<xs:simpleType name=\"stringtype\">\n");
            fw.write("  <xs:restriction base=\"xs:string\"/>\n");
            fw.write("</xs:simpleType>\n");

            for (int x = 0; x < 9000; x++) {
                fw.write("<xs:complexType name=\"shiptotype-" + x + "\">\n");
                fw.write("  <xs:sequence>\n");
                fw.write("  <xs:element name=\"name-" + x + "\" type=\"stringtype\"/>\n");
                fw.write(" </xs:sequence>\n");
                fw.write("</xs:complexType>\n");
            }
        } catch (Exception e) {
            logger.info("Unexpected error", e);
        } finally {
            fw.write("</xs:schema>");
            fw.flush();
            fw.close();
            logger.info(filePath.concat(" File written Succesfully, size: ")
                    .concat(String.valueOf(new File(filePath).length() / 1000)).concat(" KB"));
        }
    }

    public static void writeEnvDetails(Map<String, String> details, boolean append) throws IOException {
        Map<String, String> res = new HashMap<>(details);
        BufferedWriter writer = null;
        try {
            File file = new File("env_info.txt");
            writer = new BufferedWriter(new FileWriter(file, append));
            for (String key : details.keySet()) {
                writer.write(String.format("<b>%s: </b>%s<br/>\n", key, details.get(key)));
            }
        } catch (Exception e) {
            logger.warn("Unable to write Environment details", e);
        } finally {
            if (writer != null) writer.close();
        }
    }

    public static void writeAllureEnvProperties(Map<String, String> details, boolean append) throws IOException {
        BufferedWriter writer = null;
        try {
            File file = new File("target/allure-results/environment.properties");
            file.getParentFile().mkdirs();
            writer = new BufferedWriter(new FileWriter(file, append));
            for (String key : details.keySet()) {
                writer.write(String.format("%s=%s", key, details.get(key)) + "\n");
            }
        } catch (Exception e) {
            logger.warn("Unable to write Allure properties", e);
        } finally {
            if (writer != null) writer.close();
        }
    }

    public static void saveAllureHistory() throws IOException {
        String source = "allure-report/history";
        File srcDir = new File(source);

        String destination = "target/allure-results/history";
        File destDir = new File(destination);

        try {
            FileUtils.copyDirectory(srcDir, destDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getDownloadDir() {
        String downloadDir = System.getProperty("java.io.tmpdir");
        if (USE_GRID) {
            downloadDir = "\\\\localhost\\dev\\temp\\";
        }
        File dir = new File(downloadDir);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return downloadDir;
    }

    public static void assertFileExistsInFolder(String attachmentName, String folder) {
        File file = new File(folder + attachmentName);
        assertTrue(file.exists(), "The file " + attachmentName + " was not found at directory: " + folder);
        logger.info(String.format("%s file is located at %s", attachmentName, file.getPath()));
    }

    public static void clearDirectory(String path) {
        // delete all files from dir
        try {
            FileUtils.cleanDirectory(new File(path));
        } catch (Exception e) {
            logger.warn("Unable to clear directory", e);
        }
    }

    public static void deleteFileFromDownloadDir(String fileName) {
        // delete file from dir
        try {
            String srcFile = getDownloadDir() + fileName;
            FileUtils.forceDeleteOnExit(new File(srcFile));
        } catch (Exception e) {
            logger.warn("Unable to delete file", e);
        }
    }

    public static void executeShellScript(String file) {
        String path = System.getProperty("user.dir") + file;
        try {
            InputStreamReader reader = new InputStreamReader(getRuntime().exec("bash " + path).getInputStream());

            String line;
            BufferedReader input = new BufferedReader(reader);
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
            input.close();
        } catch (Exception e) {
            logger.warn(String.format("Unable to execute Shell script - '%s'", path), e);
        }
    }

    public static String copyScriptFileToLocal(String fileName) {
        String to = getDownloadDir() + fileName;

        try {
            InputStream fileAsStream = FileHelper.class.getClassLoader().
                    getResourceAsStream(PLG_TEST_FILES.concat(fileName));

            Files.copy(fileAsStream, Paths.get(to), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error("Unable to copy script file from master hub to local download directory: ", e);
        }

        repeat(() -> DriverHelper.delay(1), () -> new File(to).exists());

        return to;
    }
}

