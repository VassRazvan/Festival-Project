package domain;

public class Buyer extends Identifiable<Integer> {
    private String name;
    private int numberOfSeats;
    private int idShow;

    public Buyer(String name, int numberOfSeats, int idShow) {
        this.name = name;
        this.numberOfSeats = numberOfSeats;
        this.idShow = idShow;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public int getIdShow() {
        return idShow;
    }

    public void setIdShow(int idShow) {
        this.idShow = idShow;
    }

    @Override
    public String toString() {
        return "Buyer{" +
                "name='" + name + '\'' +
                ", numberOfSeats=" + numberOfSeats +
                ", idShow=" + idShow +
                '}';
    }
}
