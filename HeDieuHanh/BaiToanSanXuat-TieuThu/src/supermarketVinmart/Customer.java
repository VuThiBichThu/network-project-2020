package supermarketVinmart;

import java.util.Random;

public class Customer extends Thread {
	private String name;
	private Cashier cashier;

	private Random rd;
	private boolean paymentRequest;

	public Customer(Cashier cashier, String name) {
		this.name = name;
		this.cashier = cashier;
		paymentRequest = true;
		rd = new Random();
	}

	public void run() {
		wasteTime();
		cashier.customerReady(this);
	}

	public boolean wantToPayment() {
		return paymentRequest;
	}

	public void wantToLeave() {
		paymentRequest = false;
	}

	public void wasteTime() {
		try {
			Customer.sleep(Math.abs(rd.nextInt(1000)));
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	public String toString() {
		return name;
	}
}
