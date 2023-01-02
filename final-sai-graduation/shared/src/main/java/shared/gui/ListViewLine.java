package shared.gui;


/**
 * This class is an item/line for a ListViewLine.
 * It makes it possible to put both a request and a reply object in one item in a ListViewLine, e.g. in loan-client:
 *
 *     @FXML
 *     private ListView<ListViewLine<GraduationClientRequest, GraduationClientReply>> lvGraduationClientRequestReply;
 */
public class ListViewLine<REQUEST, REPLY> {
	
	private REQUEST request;
	private REPLY reply;
	
	public ListViewLine(REQUEST request) {
		setRequest(request);
		setReply(null);
	}	
	
	public REQUEST getRequest() {
		return request;
	}
	
	private void setRequest(REQUEST request) {
		this.request = request;
	}

	
	public void setReply(REPLY reply) {
		this.reply = reply;
	}

	public boolean hasReply(){
		return reply != null;
	}

    /**
     * This method defines how one line is shown in the ListViewLine.
     * @return
     *  a) if reply is null, then this item will be shown as "request.toString ---> waiting for loan reply..."
     *  b) if reply is not null, then this item will be shown as "request.toString ---> GraduationClientReply.toString"
     */
	@Override
	public String toString() {
	   return request.toString() + "  --->  " + ((reply !=null)? reply.toString():"waiting for reply...");
	}
	
}
