package util;

import eu.ecodex.dss.model.ECodexContainer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class ContainerToFilesystem {
    public static void writeFiles(String path, ECodexContainer myContainer) {
        try {
            String dirPath = path + "\\";

            File dir = new File(path);
            dir.mkdir();

            InputStream is = myContainer.getAsicDocument().openStream();

            FileOutputStream out = new FileOutputStream(new File(dirPath + myContainer.getAsicDocument().getName()));

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = is.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }

            is.close();
            out.flush();
            out.close();

            is = myContainer.getTokenXML().openStream();

            out = new FileOutputStream(new File(dirPath + "Token.xml"));

            read = 0;
            bytes = new byte[1024];

            while ((read = is.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }

            is.close();
            out.flush();
            out.close();

            is = myContainer.getTokenPDF().openStream();

            out = new FileOutputStream(new File(dirPath + myContainer.getTokenPDF().getName()));

            read = 0;
            bytes = new byte[1024];

            while ((read = is.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }

            is.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            System.out.println("Fehler beim schreiben der Ergebnisse");
        }
    }
}
