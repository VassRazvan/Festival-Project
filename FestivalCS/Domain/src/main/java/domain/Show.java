package domain;

public class Show extends Identifiable<Integer> {
    private String place;
    private long date;
    private Integer idArtist;
    private int soldSeats;
    private int totalSeats;

    public Show(String place, long date, Integer idArtist, int soldSeats, int totalSeats) {
        this.place = place;
        this.date = date;
        this.idArtist = idArtist;
        this.soldSeats = soldSeats;
        this.totalSeats = totalSeats;
    }

    public String getPlace() {
        return place;
    }

    public long getDate() {
        return date;
    }

    public Integer getIdArtist() {
        return idArtist;
    }

    public int getSoldSeats() {
        return soldSeats;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setIdArtist(Integer idArtist) {
        this.idArtist = idArtist;
    }

    public void setSoldSeats(int soldSeats) {
        this.soldSeats = soldSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    @Override
    public String toString() {
        return "Show{" +
                "id=" + super.getId() +
                ", place='" + place + '\'' +
                ", date=" + date +
                ", idArtist=" + idArtist +
                ", soldSeats=" + soldSeats +
                ", totalSeats=" + totalSeats +
                '}';
    }
}
