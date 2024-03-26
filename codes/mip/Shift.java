public class Shift {
    private int id;
    private int capacity;

    public Shift(int id, int capacity) {
        this.id = id;
        this.capacity = capacity;
    }

    public int getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return "id: "+id + " cap:" + capacity;
    }
}
