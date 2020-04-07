/**
 * This is the main entry of the Gateway APP, will instantiate an adaptor instance
 * and create a separate thread to run the main function of gateway.
 */


public class gatewayApp {

	// main entry
	public static void main(String[] args) {
		// set up logging format
		System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT,%1$tL:%4$s:%5$s %n");
		// instantiate adaptor
		GatewayAdaptor ad = new GatewayAdaptor();
		Thread app = new Thread(ad);
		app.start();
		// let main thread sleep
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
