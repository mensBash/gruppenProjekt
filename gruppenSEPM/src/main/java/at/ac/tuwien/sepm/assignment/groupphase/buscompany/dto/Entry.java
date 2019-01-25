package at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto;
public class Entry {
    private String depTime;
    private String arrTime;
    private Double price;
    private Long rid;

    public Entry(Long rid, String  depTime, String arrTime, Double price) {
        this.rid = rid;
        this.depTime = depTime;
        this.arrTime = arrTime;
        this.price = price;
    }

    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }

    public String getDepTime() {
        return depTime;
    }

    public String getArrTime() {
        return arrTime;
    }

    public Double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Entry{" +
            "depTime='" + depTime + '\'' +
            ", arrTime='" + arrTime + '\'' +
            '}';
    }
}