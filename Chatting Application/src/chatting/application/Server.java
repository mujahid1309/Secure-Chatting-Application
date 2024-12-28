package chatting.application;
/**
 *
 * @author Mujahid
 */

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import java.net.*;
import java.io.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

public class Server implements ActionListener {
    
    JTextField text;
    JPanel a1;
    static Box vertical = Box.createVerticalBox();
    static JFrame f = new JFrame();
    static DataInputStream din;
    static DataOutputStream dout;
    static SecretKey aesKey;
    
    Server(){
        f.setLayout(null);
        JPanel p1 =new JPanel();
        p1.setBackground(new Color(7, 94, 84));
        p1.setBounds(0, 0, 350, 60);
        p1.setLayout(null);
        f.add(p1);
        
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/3.png"));
        Image i2 = i1.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel back = new JLabel(i3);
        back.setBounds(5, 17, 25, 25);
        p1.add(back);
        
        back.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent ae){
                f.setVisible(false);
            }
        });
       
        ImageIcon i4 = new ImageIcon(ClassLoader.getSystemResource("icons/1.png"));
        Image i5 = i4.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT);
        ImageIcon i6 = new ImageIcon(i5);
        JLabel profile = new JLabel(i6);
        profile.setBounds(40, 10, 50, 50);
        p1.add(profile);
        
        ImageIcon i7 = new ImageIcon(ClassLoader.getSystemResource("icons/video.png"));
        Image i8 = i7.getImage().getScaledInstance(27, 27, Image.SCALE_DEFAULT);
        ImageIcon i9 = new ImageIcon(i8);
        JLabel video = new JLabel(i9);
        video.setBounds(250, 15, 27, 27);
        p1.add(video);
        
        ImageIcon i10 = new ImageIcon(ClassLoader.getSystemResource("icons/phone.png"));
        Image i11 = i10.getImage().getScaledInstance(30, 25, Image.SCALE_DEFAULT);
        ImageIcon i12 = new ImageIcon(i11);
        JLabel phone = new JLabel(i12);
        phone.setBounds(290, 16, 30, 25);
        p1.add(phone); 
        
        ImageIcon i13 = new ImageIcon(ClassLoader.getSystemResource("icons/3icon.png"));
        Image i14 = i13.getImage().getScaledInstance(10, 23, Image.SCALE_DEFAULT);
        ImageIcon i15 = new ImageIcon(i14);
        JLabel morevert = new JLabel(i15);
        morevert.setBounds(330, 16, 10, 23);
        p1.add(morevert);
        
        JLabel name = new JLabel("Turtles");
        name.setBounds(110, 16, 100, 18);
        name.setForeground(Color.white);
        name.setFont(new Font("SAN_SERIF",Font.BOLD, 18));
        p1.add(name);
        
        JLabel status = new JLabel("Active Now");
        status.setBounds(110, 35, 100, 18);
        status.setForeground(Color.white);
        status.setFont(new Font("SAN_SERIF",Font.BOLD, 12));
        p1.add(status);
        
        a1 = new JPanel();
        a1.setBounds(3, 62, 343, 490);
        f.add(a1);
        
        text = new JTextField();
        text.setBounds(5, 555, 225, 40);
        text.setFont(new Font("SAN_SERIF",Font.PLAIN, 16));
        f.add(text);
        
        JButton send = new JButton("Send");
        send.setBounds(230, 555, 120, 40);
        send.setFont(new Font("SAN_SERIF",Font.BOLD, 20));
        send.setForeground(Color.WHITE);
        send.setBackground(new Color(7,94,84));
        send.addActionListener(this);
        f.add(send);
        
        f.setSize(350,600);
        f.setLocation(250,50);
        f.setUndecorated(true);
        f.getContentPane().setBackground(Color.white);
        
        
        f.setVisible(true);
    }
    public void actionPerformed(ActionEvent ae){
        try{
            String out = text.getText();
            
            JPanel p2 = formatLabel(out);

            a1.setLayout(new BorderLayout());

            JPanel right = new JPanel(new BorderLayout());
            right.add(p2, BorderLayout.LINE_END);
            vertical.add(right);
            vertical.add(Box.createVerticalStrut(15));

            a1.add(vertical, BorderLayout.PAGE_START);

            
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encryptedMessage = aesCipher.doFinal(out.getBytes());
            dout.writeInt(encryptedMessage.length);
            dout.write(encryptedMessage);

            text.setText("");

            f.repaint();
            f.invalidate();
            f.validate();
        } catch (Exception e){
            
            e.printStackTrace();
        }
    }
    
    public static JPanel formatLabel(String out){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel output =new JLabel("<html><p style=\"width: 150px\">" + out + "</p></html");
        output.setFont(new Font("Tahoma", Font.PLAIN, 16));
        output.setBackground(new Color(37, 211, 102));
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(15, 15, 15, 50));
        
        panel.add(output);
        
        Calendar cal =Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        
        JLabel time = new JLabel();
        time.setText(sdf.format(cal.getTime()));
        
        panel.add(time);
        
        
        return panel;
    }
    
    public static void main(String[] agrs){
        new Server();
        
        try {
            ServerSocket skt = new ServerSocket(6001);
            while(true) {
                Socket s = skt.accept();
                din = new DataInputStream(s.getInputStream());
                dout = new DataOutputStream(s.getOutputStream());
                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

                // Generate RSA key pair
                KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
                keyPairGen.initialize(2048);
                KeyPair pair = keyPairGen.generateKeyPair();
                PrivateKey privateKey = pair.getPrivate();
                PublicKey publicKey = pair.getPublic();

                // Send public key to the client
                oos.writeObject(publicKey);

                // Receive and decrypt AES key
                byte[] encryptedAesKey = new byte[256];
                din.readFully(encryptedAesKey);
                Cipher rsaCipher = Cipher.getInstance("RSA");
                rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] aesKeyBytes = rsaCipher.doFinal(encryptedAesKey);
                aesKey = new SecretKeySpec(aesKeyBytes, "AES");
             
                while(true){
                    if (din.available() > 0) {
                        int length = din.readInt();
                        byte[] encryptedMessage = new byte[length];
                        din.readFully(encryptedMessage);

                        Cipher aesCipher = Cipher.getInstance("AES");
                        aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
                        byte[] decryptedMessage = aesCipher.doFinal(encryptedMessage);
                        String msg = new String(decryptedMessage);

                        JPanel panel = formatLabel(msg);
                        JPanel left = new JPanel(new BorderLayout());
                        left.add(panel, BorderLayout.LINE_START);
                        vertical.add(left);
                        f.validate();
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
