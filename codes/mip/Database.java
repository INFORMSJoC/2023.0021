public class Database {
    private int id;
    private int size;
    private int deadline;
    private int releaseTime;
    private int testingCost;

    public Database(int id, int size) {
        this.id = id;
        this.size = size;
    }

    public Database(int id, int size, int deadline, int releaseTime) {
        this.id = id;
        this.size = size;
        this.deadline = deadline;
        this.releaseTime = releaseTime;
    }

    public int getSize() {
        return size;
    }

    public int getDeadline() {
        return deadline;
    }

    public int getReleaseTime() {
        return releaseTime;
    }

    public int getTestingCost() {
        return testingCost;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Database))
            return  false;

        Database other = (Database) obj;
        if (other.id == this.id)
            return true;
        else
            return false;

    }

    @Override
    public String toString() {
        return "id: " + id + " rt: " + releaseTime + " deadline: " + deadline;
    }
}
