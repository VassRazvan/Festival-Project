package domain;

public class Artist extends Identifiable<Integer> {
    private String name;

    public Artist() {

    }

    public Artist(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "id=" + super.getId() +
                ", name='" + name + '\'' +
                '}';
    }
}

