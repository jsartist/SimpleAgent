package Agent;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.security.Key;

import javax.crypto.Cipher;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")

public class AgentClient extends JFrame{
	
	private byte[] mac;
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private JPanel panel;
	private JButton button;
	private JTextField textfield1;
	private JTextField textfield2;
	private JLabel img;
	private JLabel label1;
	private JLabel label2;
	private JCheckBox ok;
	private ImageIcon icon;
	private Key publicKey;
	private String SERVER_IP;
	
	public AgentClient() {
		GUISetting();
		SERVER_IP = "ip?"
	}
	
	private void GUISetting() {
		  // JFrame 크기 및 이름
		  setSize(300, 400);
		  setTitle("보안경진대회 AGENT");
		  
		  //icon 변경
		  setIconImage(Toolkit());
		  
		  // 화면 중앙
		  Dimension frameSize = this.getSize();
		  Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		  setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		  
		  // X 활성화
		  setDefaultCloseOperation(EXIT_ON_CLOSE);

		  // panel 생성, 레이아웃은 사용자지정으로 하겠다.
		  panel = new JPanel();
		  panel.setLayout(null);
		  
		  // 체크박스
		  ok = new JCheckBox("본 대회에 참여합니다.");
		  ok.setBounds(65, 250, 200, 30);
		  checkBoxClick();
		  
		  // Button 생성 및 크기와 위치 설정
		  button = new JButton("확인");
		  button.setBounds(45, 300, 200, 30);
		  buttonClick();
		  button.setEnabled(false);
		  
		  // 라벨
		  label1 = new JLabel("    팀 이름 :   ");
		  label2 = new JLabel("  참가자 이름 :");
		  label1.setBounds(20, 150, 80, 30);
		  label2.setBounds(10, 200, 80, 30);
		  setIcon();
		  img = new JLabel(icon);
		  img.setBounds(43, 20, 200, 100);
		  
		  // 텍스트 필드
		  textfield1 = new JTextField();
		  textfield2 = new JTextField();
		  textfield1.setBounds(110, 150, 150, 30);
		  textfield2.setBounds(110, 200, 150, 30);
		  textfield1.setDocument(new JTextFieldLimit(40));
		  textfield2.setDocument(new JTextFieldLimit(6));
		  
		  // panel에 붙이기
		  panel.add(img);
		  panel.add(label1);
		  panel.add(textfield1);
		  panel.add(label2);
		  panel.add(textfield2);
		  panel.add(ok);
		  panel.add(button);
		  
		  // panel을 frame에 붙이기
		  add(panel);
		  // 사이즈 변경 불가능
		  setResizable(false); 
		  // 띄우기
		  setVisible(true);
	}
	
	private int ConnectServer() {
		try {
			socket = new Socket(SERVER_IP, 12347);
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
			return 1;
		}catch(Exception e) {		
			return -1;
		}
	}
	
	private void DisconnectServer() {
		try {
			dos.close();
			dis.close();
			ois.close();
			oos.close();
			socket.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// 여러가지 함수들
	
	private void checkBoxClick() {
		ok.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					button.setEnabled(true);
				}
				else {
					button.setEnabled(false);
				}
				
			}
		});
	}
	
	private void buttonClick() {
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(checkBlank() == 0) {
					JOptionPane.showMessageDialog(null, "팀 이름 또는 참가자 이름을 잘못 입력하셨습니다.", "보안경진대회 AGENT", 
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				else {
					if(ConnectServer() == -1) {
						JOptionPane.showMessageDialog(null, "현재 서버가 닫혀있습니다.", "보안경진대회 AGENT", 
								JOptionPane.ERROR_MESSAGE);
					}
					else {
						int c = Check();
						if(c == 0){
							JOptionPane.showMessageDialog(null, "팀 이름 또는 참가자 이름을 잘못 입력하셨습니다.", "보안경진대회 AGENT", 
									JOptionPane.ERROR_MESSAGE);
							DisconnectServer();
							return;
						}
						else if(c == 2) { 
							JOptionPane.showMessageDialog(null, "이미 등록되어있거나 중복된 값이 존재합니다. 수정을 원할 경우, 관리자에게 문의하시기 바랍니다. (010-4923-1132)", "보안경진대회 AGENT",
									JOptionPane.ERROR_MESSAGE); 
							DisconnectServer();
							return;
						}
					 
						else {
							String last_check = getMacAddress();
							DisconnectServer();
							if(last_check.equals("1")) {
								JOptionPane.showMessageDialog(null, "완료되었습니다.", "보안경진대회 AGENT", 
										JOptionPane.INFORMATION_MESSAGE);
								ok.setEnabled(false);
								button.setEnabled(false);
								textfield1.setEnabled(false);
								textfield2.setEnabled(false);
							}
							else {
								JOptionPane.showMessageDialog(null, "이미 등록되어있거나 중복된 값이 존재합니다. 수정을 원할 경우, 관리자에게 문의하시기 바랍니다. (010-4923-1132)", "보안경진대회 AGENT",
										JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				}
			}
		});
	}
	
	private void setIcon() {
		ImageIcon iconSet = new ImageIcon(AgentClient.class.getResource("EBL.png"));
		Image im = iconSet.getImage();
		Image im2 = im.getScaledInstance(200, 100, Image.SCALE_DEFAULT);
		icon = new ImageIcon(im2);
	}
	
	private Image Toolkit() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		return toolkit.getImage(AgentClient.class.getResource("EBL.png"));
		
	}
	
	private int checkBlank() {
		if(textfield1.getText().trim().isEmpty() || textfield2.getText().trim().isEmpty())
			return 0;
		else {
			return 1;
		}
	}
	
	private int Check() {
		try {
			dos.writeUTF(textfield1.getText());
			dis.readUTF();
			dos.writeUTF(textfield2.getText());
			return Integer.parseInt(dis.readUTF());
		}catch(Exception e) {
			return 0;
		}
	}
	
	private byte[] incode(String mac) {  // 수정부분 1
		try {
			Cipher ci = Cipher.getInstance("RSA");
			ci.init(Cipher.ENCRYPT_MODE, publicKey);
			return ci.doFinal(mac.getBytes());
		}catch(Exception e) {
			e.printStackTrace();
			return "-1".getBytes();
		}
	}
	
	private void encryptRSA(String mac) {
		try {
			KeyData key = (KeyData) ois.readObject();
			publicKey = key.getKey();
			EncryptedData data = new EncryptedData(incode(mac));
			oos.writeObject(data);
			oos.flush();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getMacAddress() {
		try {
			InetAddress ip = InetAddress.getLocalHost();
			NetworkInterface netif = NetworkInterface.getByInetAddress(ip);
			mac = netif.getHardwareAddress();
			StringBuilder str_mac = new StringBuilder();
			for(byte k : mac) {
				str_mac.append(String.format("%02X:", k));
			}
			str_mac.delete(str_mac.length() - 1, str_mac.length());
			encryptRSA(str_mac.toString());
			return dis.readUTF();
		}catch(Exception e) {
			return "1";
		}
	}
}