package agents;

import jade.core.Agent;
import behaviours.RequestPerformer;
import gui.BookBuyerGui;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class BookBuyerAgent extends Agent {

    private String bookTitle;
    private AID[] sellerAgents;
    private int ticker_timer = 10000;
    private BookBuyerAgent this_agent = this;

    public BookBuyerGui gui;

    protected void setup() {

        gui = new BookBuyerGui(this);
        gui.setVisible(true);

        System.out.println("Buyer agent " + getAID().getName() + " is ready");
    }

    protected void takeDown() {
        gui.dispose();
        System.out.println("Buyer agent " + getAID().getName() + " terminating");
    }

    public void initiateBookPurchase(String bookTitle) {
        this.bookTitle = bookTitle;

        System.out.println("Trying to buy " + bookTitle);

        addBehaviour(new TickerBehaviour(this, ticker_timer) {
            protected void onTick() {
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("book-selling");
                template.addServices(sd);

                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    System.out.println("Found the following seller agents:");
                    sellerAgents = new AID[result.length];
                    for (int i = 0; i < result.length; i++) {
                        sellerAgents[i] = result[i].getName();
                        System.out.println(sellerAgents[i].getName());
                    }

                } catch (FIPAException fe) {
                    fe.printStackTrace();
                }
                myAgent.addBehaviour(new RequestPerformer(this_agent));
            }
        });
    }

    public AID[] getSellerAgents() {
        return sellerAgents;
    }

    public String getBookTitle() {
        return bookTitle;
    }
}
