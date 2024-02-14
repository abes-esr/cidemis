package fr.abes.cidemis.exception;

import lombok.Getter;

@Getter
public class DaoException extends Exception {
    private static final long serialVersionUID = 1L;
    private final String tableOfException;
    private final String typeOfException;
    private final String tierOfException;

    public DaoException(String message, String tableOfException) {
        super(message);
        this.tableOfException = tableOfException;
        this.typeOfException = this.getClass().getSimpleName();
        this.tierOfException = "accès à la base de données";
    }
}
