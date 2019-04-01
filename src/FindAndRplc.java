import java.awt.*;
import java.awt.event.*;
import java.util.regex.*;
import javax.swing.*;

class FindAndRplc implements ActionListener
{
	JDialog d;
	JTextArea ta2;			
	TextField t1,t2;
	int cp,flag=0;
	
	public FindAndRplc(JFrame f,JTextArea ta)
	{
		ta2=ta;
		
		d=new JDialog(f,"Find And Replace");
		d.setSize(440,180);
		d.setLayout(new GridBagLayout());
		d.setResizable(false);
		
		GridBagConstraints gbc=new GridBagConstraints();
		Insets i;
		
		gbc.anchor=GridBagConstraints.WEST;				
		JLabel l1=new JLabel("Find what:");
		d.add(l1,gbc);
		
		gbc.gridy=1;
		t1=new TextField(16);
		d.add(t1,gbc);
		
		gbc.gridy=2;
		JLabel l2=new JLabel("Replace with:");
		d.add(l2,gbc);
		
		gbc.gridy=3;
		t2=new TextField(16);
		d.add(t2,gbc);
		
		JButton b1=new JButton("Find Next");
		JButton b2=new JButton("Replace");
		JButton b3=new JButton("Replace All");
		JButton b4=new JButton("Close");
		
		FindReplaceFlag frf=new FindReplaceFlag(t1,b1,b2,b3);
		t1.addTextListener(frf);
		
		b1.addActionListener(this);
		b2.addActionListener(this);
		b3.addActionListener(this);
		b4.addActionListener(this);
		
		i=new Insets(20,0,0,0);
		gbc.insets=i;
		
		gbc.gridy=4;
		d.add(b1,gbc);	
		
		i=new Insets(20,0,0,10);
		gbc.insets=i;
		
		gbc.gridx=1;
		d.add(b2,gbc);
		
		gbc.gridx=2;
		d.add(b3,gbc);
		
		i=new Insets(20,25,0,0);
		gbc.insets=i;
		
		gbc.gridx=3;
		d.add(b4,gbc);
		
		d.setVisible(true);
	}
	public void actionPerformed(ActionEvent e)
	{
		String str=e.getActionCommand();
		
		if(str=="Close")
		{
			d.setVisible(false);
			d.dispose();
		}
		else
			if(str=="Find Next")
			{
				findText();
			}
		else
			if(str=="Replace")
			{
				if(ta2.getSelectedText().isEmpty())
				{
					findText();
				}
				else
				{
					String rt=t2.getText();				 //rt=replacement text
					ta2.replaceRange(rt,ta2.getSelectionStart(),ta2.getSelectionEnd());
					
					findText();
				}
			}
		else
			if(str=="Replace All")
			{
				String gs=ta2.getText();								 //gs=get String
				String rs=gs.replaceAll(t1.getText(),t2.getText());		//rs=replaced string
				
				ta2.setText(rs);
			}
	}
	public void findText()
	{
		String fp=t1.getText();				//fp is find pattern.
				
		if(flag==0)
		{
			Pattern p=Pattern.compile("\r");
			Matcher m=p.matcher(ta2.getText());
			String s=m.replaceAll("");
			
			int tempc=ta2.getCaretPosition();
			ta2.setText(s);
			ta2.setCaretPosition(tempc);
			
			flag=1;
		}
		
		Pattern p=Pattern.compile(fp);
		Matcher m=p.matcher(ta2.getText());
		
		cp=ta2.getCaretPosition();
		
		if(m.find(cp))
			ta2.select(m.start(),m.end());
		else
			JOptionPane.showMessageDialog(d,"No Match Found!","Search Complete",JOptionPane.WARNING_MESSAGE);
	}
}