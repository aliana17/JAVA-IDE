import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class FindFlag implements TextListener
{
	TextField tt;
	JButton bb;
	
	public FindFlag(TextField t,JButton b)
	{
		tt=t;	bb=b;
		
		bb.setEnabled(false);
	}
	public void textValueChanged(TextEvent e)
	{
		if(!tt.getText().equals(""))
			bb.setEnabled(true);
		
		else
			bb.setEnabled(false);
	}
}