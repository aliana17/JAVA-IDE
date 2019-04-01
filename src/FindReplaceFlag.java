import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class FindReplaceFlag implements TextListener
{
	TextField tt;
	JButton bb1,bb2,bb3;
	
	public FindReplaceFlag(TextField t,JButton b1,JButton b2,JButton b3)
	{
		tt=t;	bb1=b1;	  bb2=b2;	bb3=b3;
		
		bb1.setEnabled(false);	bb2.setEnabled(false);	bb3.setEnabled(false);
	}
	public void textValueChanged(TextEvent e)
	{
		if(!tt.getText().equals(""))
		{
			bb1.setEnabled(true);	bb2.setEnabled(true);	bb3.setEnabled(true);
		}
		else
		{
			bb1.setEnabled(false);	bb2.setEnabled(false);	bb3.setEnabled(false);
		}
	}
}