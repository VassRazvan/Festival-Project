package domain;

import java.io.Serializable;

public class Identifiable<Tid> implements Serializable {
    private Tid id;

    public Tid getId() {
        return id;
    }

    public void setId(Tid id) {
        this.id = id;
    }
}
