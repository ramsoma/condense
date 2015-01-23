package newshog.twitter;

import org.datasift.*;

public class ConsumeStream implements IStreamConsumerEvents {
	public static void main(String[] args) {
		try {
			User user = new User("ramsoma", "013d9f3ca36fc8574645b0e7c7795b20");
			StreamConsumer consumer = user.getConsumer(StreamConsumer.TYPE_HTTP, "4229049a6e95a0c95a1beea0bff12682", new ConsumeStream());
			consumer.consume();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void onInteraction(StreamConsumer c, Interaction i) throws EInvalidData {
		System.out.print(i.getStringVal("interaction.author.username") + ": ");
		System.out.println(i.getStringVal("interaction.content"));
//		System.out.println(i.get)
		
		System.out.println("--");
	}
	public void onStopped(StreamConsumer consumer, String reason) {
		System.out.print("Stopped: " + reason);
	}

	@Override
	public void onConnect(StreamConsumer arg0) {
		// TODO Auto-generated method stub
		System.out.println("Connected");
		
	}

	@Override
	public void onDeleted(StreamConsumer arg0, Interaction arg1)
			throws EInvalidData {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnect(StreamConsumer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(StreamConsumer arg0, String arg1) throws EInvalidData {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatus(StreamConsumer arg0, String arg1, JSONdn arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWarning(StreamConsumer arg0, String arg1) throws EInvalidData {
		// TODO Auto-generated method stub
		
	}
}