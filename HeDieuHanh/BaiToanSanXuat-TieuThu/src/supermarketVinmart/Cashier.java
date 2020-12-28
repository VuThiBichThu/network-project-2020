package supermarketVinmart;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Cashier extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final int DEMO_APP_WIDTH = 700;
	private static final int DEMO_APP_HEIGHT = 500;
	private static final String DEMO_APP_TITLE = "PRODUCER-CONSUMER-SEMAPHORE";

	private JFrame frame;
	private JTextField txtChair;
	private JTextField txtCustomer;
	private JButton btnStart;
	private JButton btnStop;
	private JButton btnReset;
	private JButton btnExit;

	private JLabel lbHead;
	private JLabel lbChair;
	private JLabel lbCustomer;
	private JLabel lbResult;

	private Semaphore payment_chair; // ghế thanh toán tiền // tiêu dùng
	private Semaphore chairs; // ghế chờ - bộ đệm dùng chung
	private Semaphore cashier; // nhân viên thanh toán - tiêu dùng
	private int waiting_customers; // theo dõi số lượng khách hàng chờ đợi
	private int number_of_chairs; // đếm số lượng ghế chờ
	private static final int PAYMENT_TIME = 1000; // thời gian thanh toán
	private static JTextArea txtResult = new JTextArea();

	public Cashier(int chair_number) {
		payment_chair = new Semaphore(1, true);
		chairs = new Semaphore(0, true);
		cashier = new Semaphore(0, true);

		txtResult = new JTextArea();
		Font font = txtResult.getFont();
		float size = font.getSize() + 2.0f;
		txtResult.setFont(font.deriveFont(size));

		number_of_chairs = chair_number;
		waiting_customers = 0;
		paymentReady();
	}

	/**
	 * Chỉ ra rằng nhân viên thanh toán đã sẵn sàng phục vụ. Thông báo
	 * 
	 */
	public void paymentReady() {
		System.out.println("*Nhân viên sẵn sàng thanh toán");
		txtResult.append("  *Nhân viên sẵn sàng thanh toán\n");
		cashier.release();

		// nếu có khách hàng xếp trên ghế thì người đầu tiên sẽ tiếp tục
		chairs.release();
		System.out.println("=>Lượt thanh toán thứ :" + chairs.availablePermits() + ",Ghế thanh toán:"
				+ payment_chair.availablePermits());
		txtResult.append("  =>Lượt thanh toán tiếp theo, Ghế thanh toán=" + payment_chair.availablePermits() + "\n");
	}

	public void customerReady(Customer c) {
		System.out.println(c + " muốn thanh toán");
		txtResult.append(c + " muốn thanh toán\n");
		if (payment_chair.availablePermits() <= 0)
			customerSitDown(c);

		if (c.wantToPayment()) {
			try {
				payment_chair.acquire();
				cashier(c);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void customerSitDown(Customer c) {
		if (waiting_customers < number_of_chairs) {
			try {
				waiting_customers++;
				System.out.println(c + " ngồi xuống ghế chờ. Có " + waiting_customers + " người đang chờ");
				txtResult.append(c + " ngồi xuống ghế chờ.Có " + waiting_customers + " người đang chờ\n");
				chairs.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();

			}
		} else {
			System.out.println(c + " rời đi vì hàng đợi thanh toán đã đầy khách");
			txtResult.append(c + "  rời đi vì hàng đợi thanh toán đã đầy khách\n");
			c.wantToLeave();
		}
	}

	public void cashier(Customer c) {

		if (waiting_customers > 0)
			waiting_customers--;

		try {
			cashier.acquire(); // grab the cashier
			System.out.println(c + " đang thanh toán");
			txtResult.append(c + " đang thanh toán\n");

			Thread.sleep(PAYMENT_TIME);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		payment_chair.release();
		System.out.println(c + " đã thanh toán xong, lấy hàng và trả tiền cho nhân viên");
		txtResult.append(c + " đã thanh toán xong, lấy hàng và trả tiền cho nhân viên\n");

		paymentReady();
	}

	/**
	 * Launch the application.
	 */

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Cashier window = new Cashier();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */

	public Cashier() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */

	private void initialize() {
		frame = new JFrame();
		setFrame(frame);

		lbHead = new JLabel("QUẢN LÍ THANH TOÁN TẠI SIÊU THỊ VINMART");
		lbHead.setForeground(Color.BLUE);
		lbHead.setFont(new Font("Times New Roman", Font.BOLD, 16));
		lbHead.setBounds(180, 11, 563, 22);
		frame.getContentPane().add(lbHead);

		JSeparator separator = new JSeparator();
		separator.setBounds(10, 43, 672, 2);
		frame.getContentPane().add(separator);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 93, 672, 2);
		frame.getContentPane().add(separator_1);

		btnStart = new JButton("START");
		btnStart.setBounds(400, 56, 73, 26);
		setButton(btnStart);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int chair = 0;
				int customer_number = 0;
				if (txtChair.getText().equals("") || txtCustomer.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "Bạn chưa nhập thông tin", "Error", JOptionPane.ERROR_MESSAGE);
				} else {

					try {
						chair = Integer.parseInt(txtChair.getText());
						customer_number = Integer.parseInt(txtCustomer.getText());
						if (chair <= 0 || customer_number <= 0) {
							throw new Exception("Vui lòng nhập số nguyên lớn hơn 0");
						}

						Cashier medicalShop = new Cashier(chair);
						Thread[] cust = new Thread[customer_number];
						for (int i = 0; i < customer_number; i++)
							cust[i] = new Customer(medicalShop, "    Khách hàng " + i);

						for (int i = 0; i < customer_number; i++)
							cust[i].start();

						JScrollPane scrollPane = new JScrollPane(txtResult);
						scrollPane.setBounds(10, 122, 662, 300);
						frame.getContentPane().add(scrollPane);
					} catch (NumberFormatException e2) {
						JOptionPane.showMessageDialog(null, "Vui lòng nhập số nguyên", "Error",
								JOptionPane.ERROR_MESSAGE);
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}

				}

			}
		});
		
		btnStop = new JButton("STOP");
		btnStop.setBounds(475, 56, 67, 26);
		setButton(btnStop);
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});


		btnReset = new JButton("RESET");
		btnReset.setBounds(544, 56, 72, 26);
		setButton(btnReset);
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				txtChair.setText("");
				txtCustomer.setText("");
				txtResult.setText("");
			}
		});

		btnExit = new JButton("EXIT");
		btnExit.setBounds(618, 56, 62, 26);
		setButton(btnExit);
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		lbChair = new JLabel("Số vị trí trong hàng đợi:");
		lbChair.setFont(new Font("Tahoma", Font.BOLD, 12));
		lbChair.setBounds(10, 60, 150, 22);
		frame.getContentPane().add(lbChair);

		txtChair = new JTextField();
		txtChair.setBounds(160, 62, 50, 20);
		frame.getContentPane().add(txtChair);
		txtChair.setColumns(10);

		lbCustomer = new JLabel("Số người mua hàng:");
		lbCustomer.setFont(new Font("Tahoma", Font.BOLD, 12));
		lbCustomer.setBounds(215, 60, 130, 22);
		frame.getContentPane().add(lbCustomer);

		txtCustomer = new JTextField();
		txtCustomer.setColumns(10);
		txtCustomer.setBounds(345, 62, 50, 20);
		frame.getContentPane().add(txtCustomer);

		lbResult = new JLabel("KẾT QUẢ");
		lbResult.setForeground(new Color(255, 0, 0));
		lbResult.setFont(new Font("Tahoma", Font.BOLD, 13));
		lbResult.setBounds(300, 100, 100, 22);
		frame.getContentPane().add(lbResult);

	}

	private void setFrame(JFrame frame) {
		frame.getContentPane().setBackground(new Color(230, 230, 250));
		frame.setTitle(DEMO_APP_TITLE);
		frame.setSize(DEMO_APP_WIDTH, DEMO_APP_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
	}

//	private void setLabel(JLabel jLabel) {
//
//	}

	private void setButton(JButton jButton) {
		jButton.setFocusPainted(false);
		jButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		jButton.setForeground(new Color(0, 0, 255));
		jButton.setBackground(Color.WHITE);
		frame.getContentPane().add(jButton);
	}
}
