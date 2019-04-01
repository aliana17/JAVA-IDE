import java.awt.*;
import java.awt.event.*;
import java.util.regex.*;
import javax.swing.*;

class Find implements ActionListener
{
	JDialog jd;		int flag=0,cp;
	TextField t;	JTextArea ta2;
	
	public Find(JFrame f,JTextArea ta)
	{
		ta2=ta;
		
		jd=new JDialog(f,"Find");
		jd.setSize(390,160);
		jd.setLayout(new GridBagLayout());
		jd.setResizable(false);
		
		GridBagConstraints gbc=new GridBagConstraints();
		Insets i;
		
		gbc.anchor=GridBagConstraints.WEST;				
		JLabel l1=new JLabel("Find what:");
		jd.add(l1,gbc);
		
		gbc.gridy=1;
		t=new TextField(16);			//+++++++++++%%%%%%%% DOUBT: JTextField(TxtListnr) %%%%%%%%%%++++++++++++
		jd.add(t,gbc);
		
		JButton b1=new JButton("Find Next");
		JButton b2=new JButton("Close");
		
		b1.addActionListener(this);
		b2.addActionListener(this);
		
		FindFlag ff=new FindFlag(t,b1);
		t.addTextListener(ff);
		
		i=new Insets(40,0,0,120);
		gbc.insets=i;
		gbc.gridy=2;
		jd.add(b1,gbc);	
		
		i=new Insets(40,100,0,0);
		gbc.insets=i;
		gbc.gridx=1;
		jd.add(b2,gbc);
		
		jd.setVisible(true);
	}

	public void actionPerformed(ActionEvent e)
	{
		String str=e.getActionCommand();
		
		if(str=="Close")
		{
			jd.setVisible(false);
			jd.dispose();
		}
		else if(str=="Find Next")
		{
			String fp=t.getText();				//fp is find pattern.
			
			if(flag==0)
			{
				Pattern p2=Pattern.compile("\r");
				Matcher m2=p2.matcher(ta2.getText());
				String s2=m2.replaceAll("");
				
				int tempc=ta2.getCaretPosition();
				ta2.setText(s2);
				ta2.setCaretPosition(tempc);
				
				flag=1;
			}
			
			Pattern p=Pattern.compile(fp);
			Matcher m=p.matcher(ta2.getText());
			
			cp=ta2.getCaretPosition();//+ta2.getSelectedText().length();	//++++++++%%%%%%%%%%%++++ DOUBT +++++%%%%%%%%%%%%%%++++++
			
			if(m.find(cp))
				ta2.select(m.start(),m.end());
			else
			{
			/*	JDialog warn=new JDialog(jd,"Search Complete");
				warn.setSize(200,100);
				warn.setLayout(new FlowLayout());
				
				JLabel l=new JLabel("No match Found");
				warn.add(l);
				
				warn.setResizable(false);
				jd.setEnabled(false);
				warn.setVisible(true);
				
				warn.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				warn.addWindowListener(this);	*/	//++++++++++++++++++++++%%%%%%%%%%%%%%%%  DOUBT  %%%%%%%%%%%%++++++++++++++++++++++
			
			/*	WarnCloser wc=new WarnCloser(jd);
				warn.addWindowListener(wc);		*/
				
				JOptionPane.showMessageDialog(jd,"No Match Found!","Search Complete",JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	
/*	public void WindowClosing(WindowEvent we)
	{
		Window w=we.getWindow();
		
		w.setVisible(false);
		w.dispose();
		//jd.setEnabled(true);
	}	*/
}