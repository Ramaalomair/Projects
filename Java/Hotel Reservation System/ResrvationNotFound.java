

public class ResrvationNotFound extends Exception{

	public ResrvationNotFound() {
		super("can not be canceled");
	}
	public ResrvationNotFound(String sMessage) {
		super(sMessage);
	}

}
