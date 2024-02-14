package fr.abes.cidemis.webstats;

public class ExportException extends Exception {
    private static final long serialVersionUID = 5875471802660745163L;

    public ExportException(Exception e) {
        super(e);
    }

}
