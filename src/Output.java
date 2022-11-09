import java.io.Serializable;

public class Output implements Serializable {
    private String status_description;
    private Status status;

    public Output(Status status, String status_description){
        this.status=status;
        this.status_description=status_description;
    }

    public Status getStatus() {
        return status;
    }

    public String getStatus_description() {
        return status_description;
    }
}
