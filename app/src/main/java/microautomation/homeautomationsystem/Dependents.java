package microautomation.homeautomationsystem;

public class Dependents {
    String dependents_id;

    public String getDependents_id() {
        return dependents_id;
    }

    public void setDependents_id(String dependents_id) {
        this.dependents_id = dependents_id;
    }

    public Dependents(String dependents_id) {
        this.dependents_id = dependents_id;
    }
    public Dependents() {

    }
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Dependents)) {
            return false;
        }

        Dependents that = (Dependents) other;
        // Custom equality check here.
        return this.dependents_id.equals(that.dependents_id);
    }
}
