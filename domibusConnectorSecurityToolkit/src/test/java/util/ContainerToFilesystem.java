package util;

import eu.ecodex.dss.model.ECodexContainer;
import java.io.File;
import java.io.FileOutputStream;

@SuppressWarnings("checkstyle:MissingJavadocType")
public class ContainerToFilesystem {
    /**
     * Write the ASiC container files to the specified directory.
     *
     * @param path        The path of the directory to write the files to.
     * @param myContainer The ASiC container to write the files from.
     */
    public static void writeFiles(String path, ECodexContainer myContainer) {
        try {
            var dirPath = path + "\\";

            var directory = new File(path);
            directory.mkdir();

            var asicDocument = myContainer.getAsicDocument().openStream();

            var fileOutputStream = new FileOutputStream(
                dirPath + myContainer.getAsicDocument().getName()
            );

            int read;
            var bytes = new byte[1024];

            while ((read = asicDocument.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, read);
            }

            asicDocument.close();
            fileOutputStream.flush();
            fileOutputStream.close();

            asicDocument = myContainer.getTokenXML().openStream();

            fileOutputStream = new FileOutputStream(dirPath + "Token.xml");

            bytes = new byte[1024];

            while ((read = asicDocument.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, read);
            }

            asicDocument.close();
            fileOutputStream.flush();
            fileOutputStream.close();

            asicDocument = myContainer.getTokenPDF().openStream();

            fileOutputStream = new FileOutputStream(dirPath + myContainer.getTokenPDF().getName());

            bytes = new byte[1024];

            while ((read = asicDocument.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, read);
            }

            asicDocument.close();
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            System.out.println("Fehler beim schreiben der Ergebnisse");
        }
    }
}
