package fr.abes.cidemis.components;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
@Getter
@Setter
public class Fichier {
    private File file;
    private String filename;

    public File getFile() {
        return file;
    }

    public String getFilename() {
        return filename;
    }
}
