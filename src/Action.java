import java.io.Serializable;

public class Action implements Serializable{

	private String op;
	private int amount;
	
	public Action(String op, int amount) {
		super();
		this.op = op;
		this.amount = amount;
	}

	public String getOp() {
		return op;
	}

	public int getAmount() {
		return amount;
	}

	@Override
	public String toString() {
		return "Action [op=" + op + ", amount=" + amount + "]";
	}	
}
