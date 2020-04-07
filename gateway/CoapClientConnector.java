/**
 * this is a simple wrap of CoAP client, provide GET 
 * POST and OBSERVE method
 */


import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;

public class CoapClientConnector {
	CoapClient client = null;
	CoapObserveRelation relation = null;

	/**
	 * constructor, client can be instantiated with URI then use all methods without
	 * specifying URI
	 */
	public CoapClientConnector(String uri) {
		super();
		this.client = new CoapClient(uri);
	}

	/**
	 * default constructor
	 */
	public CoapClientConnector() {
		super();
		this.client = new CoapClient();
	}

	/**
	 * GET method, return the text of response
	 * 
	 * @return String of response
	 */
	public String GET_TEXT() {
		return client.get().getResponseText();
	}

	/**
	 * GET method, can define handler to handle response
	 * 
	 * @param handler :CoapHandler
	 */
	public void GET(CoapHandler handler) {
		client.get(handler);
	}

	/**
	 * OBSERVE method, can define handler to handle response
	 * 
	 * @param handler :CoapHandler
	 */
	public void OBSERVE(CoapHandler handler) {
		relation = client.observe(handler);
	}

	/**
	 * cancel the observe request
	 */
	public void cancel_OBSERVE() {
		if (relation != null) {
			relation.proactiveCancel();
		}
	}

	/**
	 * POST method, can define handler to handle response
	 * 
	 * @param handler :CoapHandler
	 * @param payload :POST payload
	 * @param format  :format
	 */
	public void POST(CoapHandler handler, String payload, int format) {
		client.post(handler, payload, format);
	}

}
