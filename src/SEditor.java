//Java IDE

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.Element;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SEditor extends JFrame implements ActionListener,KeyListener,WindowListener
{
	//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ Fields ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	
	String fi[]={"New","Open","Save","SaveAs","Exit"};				// fi= File items
	String ei[]={"Cut","Copy","Paste","Find","Find & Replace"};		// ei= Edit items
	String bi[]={"Compile","Run"};									// bi= Build items
	String ci[]={"Current Tab","All Tabs"};						// ci= Close items
	
	JMenu fm,em,bm,cm;							//fm= File Menu, em= Edit Menu, bm= Build Menu, cm=Close Menu
	JMenuItem em_item[]=new JMenuItem[5];		//to enable/disable edit menu items
	JMenuItem fm_item[]=new JMenuItem[5];		//to enable/disable file menu items
	JMenuItem bm_item[]=new JMenuItem[2];		//to enable/disable build menu items
	JMenuItem cm_item[]=new JMenuItem[2];		//to enable/disable close menu items
	
	JTabbedPane jtp;							//To open tabs with jscrollpane--->jtextarea
	JScrollPane jsp;
	JTextArea jta;								//To show Compile and Run button messages
	
	HashMap map1=new HashMap();		//key=tab index, value=File instance
	HashMap map2=new HashMap();		//key=tab index, value=JTextArea instance
	HashMap map3=new HashMap();		//key=tab index, value=Boolean value
									
	String str;						// str will hold ActionCommand
	
	
	//^^^^^^^^^^^^^^^^^^^^^^^^^^ Default Constructor ^^^^^^^^^^^^^^^^^^^^^^^^^^^
	
	public SEditor()
	{
		fm=new JMenu("File");		
		em=new JMenu("Edit");	
		bm=new JMenu("Build");		
		cm=new JMenu("CloseTab");
		
		for(int i=0;i<fi.length;i++)
		{
			fm_item[i]=new JMenuItem(fi[i]);
			fm_item[i].addActionListener(this);
			fm.add(fm_item[i]);
		}
		fm_item[2].setEnabled(false);
		fm_item[3].setEnabled(false);
		
		for(int i=0;i<ei.length;i++)
		{
			em_item[i]=new JMenuItem(ei[i]);
			em_item[i].addActionListener(this);
			em.add(em_item[i]);
			
			em_item[i].setEnabled(false);
		}
		
		for(int i=0;i<bi.length;i++)
		{
			bm_item[i]=new JMenuItem(bi[i]);
			bm_item[i].addActionListener(this);
			bm.add(bm_item[i]);
			
			bm_item[i].setEnabled(false);
		}
		
		for(int i=0;i<ci.length;i++)
		{
			cm_item[i]=new JMenuItem(ci[i]);
			cm_item[i].addActionListener(this);
			cm.add(cm_item[i]);
			
			cm_item[i].setEnabled(false);
		}
		
		fm.insertSeparator(4);
		em.insertSeparator(3);
		
		JMenuBar jmb=new JMenuBar();
		jmb.add(fm);		jmb.add(em);		jmb.add(bm);		jmb.add(cm);
		
		setJMenuBar(jmb);
		setSize(800,680);
		setTitle("Java-IDE");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		
		jta=new JTextArea();
		jta.setEditable(false);
		jta.setLineWrap(true);
		jta.setWrapStyleWord(true);
		jsp=new JScrollPane(jta);
		
		jtp=new JTabbedPane(JTabbedPane.BOTTOM);
		jtp.setPreferredSize(new Dimension(800,440));
		jtp.setMinimumSize(new Dimension(800,440));
		//jsp.setMinimumSize(new Dimension(800,240));
		
		JSplitPane sp=new JSplitPane(JSplitPane.VERTICAL_SPLIT,jtp,jsp);
		
		add(sp);
		setVisible(true);
	}
	
	//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ Action Handling ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	
	public void actionPerformed(ActionEvent e)
	{
		str=e.getActionCommand();
		
		if(str.equals("Compile"))		//-------------------------------------Compile----------------------------------
		{
			Object val=map3.get(jtp.getSelectedIndex());
			Boolean b=(Boolean)val;
			if(b)
				save();				//calling save() method before compiling to save changes if made
				
			try
			{
				Object ob=map1.get(jtp.getSelectedIndex());
				File f=(File)ob;
				
				Process pr=Runtime.getRuntime().exec("cmd /c javac "+f);
				InputStream is=pr.getErrorStream();
				
				if(is.read()==-1)
				{
					jta.setForeground(Color.DARK_GRAY);
					jta.setFont(new Font(Font.SERIF,Font.BOLD,13));
					jta.setText("Compilation successful!");
					
					bm_item[1].setEnabled(true);
				}
				else
				{
					bm_item[1].setEnabled(false);
					
					int ch;
					jta.setText("");
					jta.setForeground(Color.red);
					jta.setFont(new Font(Font.SERIF,Font.ITALIC,14));
					while((ch=is.read())!=-1)
						jta.append((char)ch+"");
				}
			}
			catch(IOException ex) { System.out.println(ex.getMessage()); }
		}
		else
			if(str.equals("Run"))		//--------------------------------------Run--------------------------------------
			{
				try
				{
					Object ob=map1.get(jtp.getSelectedIndex());
					File f=(File)ob;
					String fname=f.getName();								//fname= file name
					String cfname=fname.substring(0,fname.indexOf("."));	//cfname= cut file name(i.e., without extension)
						
					Process pr=Runtime.getRuntime().exec("cmd /c start cmd /k java "+cfname,null,new File(f.getParent()));
				}
				catch(IOException ex) { System.out.println(ex.getMessage()); }
			}
		else
			if(str.equals("New"))		//--------------------------------------New----------------------------------------
			{
				JFileChooser jfc=new JFileChooser();
				int s_rv=jfc.showSaveDialog(this);			// s_rv= save_return value
				
				if(s_rv==JFileChooser.APPROVE_OPTION)
				{
					JTextArea ln_jta=new JTextArea("1");	//ln_jta= linenumber_jta to show line numbers
					ln_jta.setEditable(false);
					ln_jta.setFont(new Font(Font.SANS_SERIF,Font.BOLD,11));
					ln_jta.setBackground(Color.LIGHT_GRAY);
					ln_jta.setForeground(Color.BLUE);
					Border border = BorderFactory.createLineBorder(Color.BLACK);
					ln_jta.setBorder(BorderFactory.createCompoundBorder(border,BorderFactory.createEmptyBorder(0,8,0,8)));
					
					JTextArea jta=new JTextArea();
					jta.setFont(new Font(Font.SANS_SERIF,Font.BOLD,11));
					jta.addKeyListener(this);
					jta.getDocument().addDocumentListener(new DocumentListener()
					{
						String getText()
						{
							//Element root=jta.getDocument().getDefaultRootElement();
							int lines=jta.getLineCount();//root.getElementCount();
							String str="1";
							
							for(int i=2;i<=lines;i++)
								str+="\n"+i;
							
							return str;
						}
						
						@Override
						public void changedUpdate(DocumentEvent e)
						{
							ln_jta.setText(getText());
						}
						
						@Override
						public void insertUpdate(DocumentEvent e)
						{
							ln_jta.setText(getText());
						}
						
						@Override
						public void removeUpdate(DocumentEvent e)
						{
							ln_jta.setText(getText());
						}
					});
					
					JScrollPane jsp=new JScrollPane(jta);
					jsp.setRowHeaderView(ln_jta);
					File f=new File(jfc.getSelectedFile().getPath()+".java");
					
					jtp.addTab(f.getName(),jsp);
					jtp.setSelectedIndex(jtp.getTabCount()-1);		//to select the new opening tab
					
					map1.put(jtp.getSelectedIndex(),f);
					map2.put(jtp.getSelectedIndex(),jta);
					map3.put(jtp.getSelectedIndex(),false);
					
					try
					{
						FileOutputStream fos=new FileOutputStream(f.getPath());
					}
					catch(IOException ex) { System.out.println(ex.getMessage()); }
					
					if(jtp.getTabCount()==1)
					{
						for(int i=0;i<ei.length;i++)
							em_item[i].setEnabled(true);
						
						fm_item[3].setEnabled(true);
						
						bm_item[0].setEnabled(true);
						
						cm_item[0].setEnabled(true);
					}
					if(jtp.getTabCount()>1)
						cm_item[1].setEnabled(true);
				}
			}
		else
			if(str.equals("Open"))		//---------------------------------------Open-------------------------------------
			{
				JFileChooser jfc=new JFileChooser();
				
				FileNameExtensionFilter fltr=new FileNameExtensionFilter("*.java","java");
    			jfc.setFileFilter(fltr);
				
				int op_rv=jfc.showOpenDialog(this);			// op_rv= open_return value
				
				if(op_rv==JFileChooser.APPROVE_OPTION)
				{
					int existFileIndex=0;
					Boolean fileExist=false;
					File f=jfc.getSelectedFile();
					
					if(!map1.isEmpty())
						for(int i=0;i<jtp.getTabCount();i++)
						{
							Object val=map1.get(i);
							File ff=(File)val;
							
							if(f.getPath().equals(ff.getPath()))
							{
								fileExist=true;
								existFileIndex=i;
								break;
							}
						}
					
					if(fileExist)
						jtp.setSelectedIndex(existFileIndex);
					else
					{
						if(f.exists())
						{
							JTextArea ln_jta=new JTextArea("1");	//ln_jta= linenumber_jta to show line numbers
							ln_jta.setEditable(false);
							ln_jta.setFont(new Font(Font.SANS_SERIF,Font.BOLD,11));
							ln_jta.setBackground(Color.LIGHT_GRAY);
							ln_jta.setForeground(Color.BLUE);
							Border border = BorderFactory.createLineBorder(Color.BLACK);
							ln_jta.setBorder(BorderFactory.createCompoundBorder(border,BorderFactory.createEmptyBorder(0,8,0,8)));
					
							JTextArea jta=new JTextArea();
							jta.setFont(new Font(Font.SANS_SERIF,Font.BOLD,11));
							jta.addKeyListener(this);
							jta.getDocument().addDocumentListener(new DocumentListener()
							{
								String getText()
								{
									
									//Element root=jta.getDocument().getDefaultRootElement();
									int lines=jta.getLineCount();//root.getElementCount();
									String str="1";
									
									for(int i=2;i<=lines;i++)
										str+="\n"+i;
									
									return str;
								}
								
								@Override
								public void changedUpdate(DocumentEvent e)
								{
									ln_jta.setText(getText());
								}
								
								@Override
								public void insertUpdate(DocumentEvent e)
								{
									ln_jta.setText(getText());
								}
								
								@Override
								public void removeUpdate(DocumentEvent e)
								{
									ln_jta.setText(getText());
								}
							});
							
							JScrollPane jsp=new JScrollPane(jta);
							jsp.setRowHeaderView(ln_jta);
							jtp.addTab(f.getName(),jsp);
							jtp.setSelectedIndex(jtp.getTabCount()-1);		//to select the new opening tab
							
							map1.put(jtp.getSelectedIndex(),f);
							map2.put(jtp.getSelectedIndex(),jta);
							map3.put(jtp.getSelectedIndex(),false);
							
							try
							{
								FileInputStream fis=new FileInputStream(f);
								int ch;
								while((ch=fis.read())!=-1)
									jta.append(""+(char)ch);	
								fis.close();
							}
							catch(IOException ex) { System.out.println(ex.getMessage()); }
							
							jta.setCaretPosition(0);						//The caret(cursor) is always at 0 position for an opened file 
							Boolean b=jta.requestFocusInWindow();
							//System.out.println(b);						//------%%%%%%% DOUBT (it always prints false, why?) %%%%%%%------
						}
					}	
					
					if(jtp.getTabCount()==1)
					{
						for(int i=0;i<ei.length;i++)
							em_item[i].setEnabled(true);
						
						fm_item[3].setEnabled(true);
						
						bm_item[0].setEnabled(true);
						
						cm_item[0].setEnabled(true);
					}
					if(jtp.getTabCount()>1)
						cm_item[1].setEnabled(true);
				}
			}
		else
			if(str.equals("Save"))		//-------------------------------Save------------------------------
			{
				save();
			}
		else
			if(str.equals("SaveAs"))	//------------------------------SaveAs-------------------------------
			{
				if(!map1.isEmpty())
				{
					JFileChooser jfc=new JFileChooser();
					int s_rv=jfc.showSaveDialog(this);			// s_rv= save_return value
					
					if(s_rv==JFileChooser.APPROVE_OPTION)
					{
						File f=new File(jfc.getSelectedFile().getPath()+".java");
						
						map1.put(jtp.getSelectedIndex(),f);			//to update the new file(with the same tab index but, may differ in Name & Location)
						
						Object val2=map2.get(jtp.getSelectedIndex());
						JTextArea ta=(JTextArea)val2;
						String s=ta.getText();
						
						try
						{
							FileOutputStream fos=new FileOutputStream(f);
							fos.write(s.getBytes());
							fos.flush();
							fos.close();
						}
						catch(IOException ex) { System.out.println(ex.getMessage()); }
						
						jtp.setTitleAt(jtp.getSelectedIndex(),f.getName());		//to change the title of the existing tab with the new file name
						
						map3.put(jtp.getSelectedIndex(),false);
						fm_item[2].setEnabled(false);
					}
				}
			}
		else
			if(str.equals("Exit"))		//--------------------------------Exit----------------------------
			{
				exit();
			}
		else
			if(str.equals("Cut"))		//--------------------------------Cut------------------------------
			{
				Object val=map2.get(jtp.getSelectedIndex());
				JTextArea ta=(JTextArea)val;
				
				ta.cut();
			}
		else
			if(str.equals("Copy"))		//------------------------------Copy-------------------------------
			{
				Object val=map2.get(jtp.getSelectedIndex());
				JTextArea ta=(JTextArea)val;
				
				ta.copy();
			}
		else
			if(str.equals("Paste"))		//-----------------------------Paste-------------------------------
			{
				Object val=map2.get(jtp.getSelectedIndex());
				JTextArea ta=(JTextArea)val;
				
				ta.paste();
			}
		else
			if(str.equals("Find"))		//-----------------------------Find--------------------------------
			{
				Object val=map2.get(jtp.getSelectedIndex());
				JTextArea ta=(JTextArea)val;
				
				Find fnd=new Find(this,ta);
			}
		else
			if(str.equals("Find & Replace"))	//--------------------Find & Replace-----------------------
			{
				Object val=map2.get(jtp.getSelectedIndex());
				JTextArea ta=(JTextArea)val;
				
				FindAndRplc fnd=new FindAndRplc(this,ta);
			}
			else
				if(str.equals("Current Tab"))			//--------------------------Current Tab--------------------------
				{
					int index=jtp.getSelectedIndex();
					
					Object val=map3.get(index);
					Boolean b=(Boolean)val;
					
					if(b)
					{
						// r means result
						JOptionPane jop=new JOptionPane();
						int r=jop.showConfirmDialog(this,"Do you want to save file?","Close Tab",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
						
						if(r==JOptionPane.YES_OPTION)
						{
							save();
							jtp.remove(index);
						}
						else
							if(r==JOptionPane.NO_OPTION)
							{
								jtp.remove(index);
							}
						else
						{
							jop.setVisible(false);
						}
					}
					else
					{
						jtp.remove(index);
					}
					
					if(jtp.getTabCount()==0)
					{
						cm_item[0].setEnabled(false);
						
						for(int i=0;i<ei.length;i++)
							em_item[i].setEnabled(false);
						
						fm_item[2].setEnabled(false);
						fm_item[3].setEnabled(false);
						
						bm_item[0].setEnabled(false);
						bm_item[1].setEnabled(false);
					}
					if(jtp.getTabCount()==1)
						cm_item[1].setEnabled(false);
				}
			else
				if(str.equals("All Tabs"))		//-----------------------All Tabs-------------------------
				{
					while(jtp.getTabCount()!=0)
					{
						int lastTab=jtp.getTabCount()-1;			//lastTab means last tab's index
						jtp.setSelectedIndex(lastTab);
						
						Object val=map3.get(lastTab);
						Boolean b=(Boolean)val;
						
						if(b)
						{
							// r means result
							JOptionPane jop=new JOptionPane();
							int r=jop.showConfirmDialog(this,"Do you want to save file?","Close All Tab",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
							
							if(r==JOptionPane.YES_OPTION)
							{
								save();
								jtp.remove(lastTab);
							}
							else
								if(r==JOptionPane.NO_OPTION)
								{
									jtp.remove(lastTab);
								}
							else
							{
								jop.setVisible(false);
								break;
							}
						}
						else
						{
							jtp.remove(lastTab);
						}
					}
					
					if(jtp.getTabCount()==0)
					{
						cm_item[0].setEnabled(false);
						cm_item[1].setEnabled(false);
						
						for(int i=0;i<ei.length;i++)
							em_item[i].setEnabled(false);
						
						fm_item[2].setEnabled(false);
						fm_item[3].setEnabled(false);
						
						bm_item[0].setEnabled(false);
						bm_item[1].setEnabled(false);
					}
					if(jtp.getTabCount()==1)
						cm_item[1].setEnabled(false);	
				}
	}
	
	//^^^^^^^^^^^^^^^^^^^^^^ Key Handling ^^^^^^^^^^^^^^^^^^^^^^^^
	
	public void keyTyped(KeyEvent e)
	{
		int index=jtp.getSelectedIndex();
		String str=jtp.getTitleAt(index);
	
		if(str.charAt(0)!='*')
			jtp.setTitleAt(index,"*"+str);
		
		map3.put(index,true);
		fm_item[2].setEnabled(true);
	}
	
	public void keyPressed(KeyEvent e)  {}
	public void keyReleased(KeyEvent e) {}
		
	//^^^^^^^^^^^^^^^^^^^^^^^^^ Window Handling ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	
	public void windowActivated(WindowEvent e)   {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowIconified(WindowEvent e)   {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e)      {}
	
	public void windowClosing(WindowEvent e) { exit(); }
	public void windowClosed(WindowEvent e)  { exit(); }
	
	//^^^^^^^^^^^^^^^^^^^^^^^^^^^ save() method ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	
	public void save()
	{
		if(!map1.isEmpty())
		{
			int index=jtp.getSelectedIndex();
			
			Object val1=map1.get(index);
			File f=(File)val1;
			
			Object val2=map2.get(index);
			JTextArea ta=(JTextArea)val2;
			String s=ta.getText();
			
			try
			{
				FileOutputStream fos=new FileOutputStream(f);
				fos.write(s.getBytes());
				fos.flush();
				fos.close();
			}
			catch(IOException ex) { System.out.println(ex.getMessage()); }
			
			String str=jtp.getTitleAt(index);
			jtp.setTitleAt(index,str.substring(1,str.length()));
			
			map3.put(jtp.getSelectedIndex(),false);
			fm_item[2].setEnabled(false);
		}
	}
	
	//^^^^^^^^^^^^^^^^^^^^^^^^^^^ exit() method ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	
	public void exit()
	{
		while(jtp.getTabCount()!=0)
		{
			int lastTab=jtp.getTabCount()-1;			//lastTab means last tab's index
			jtp.setSelectedIndex(lastTab);
			
			Object val=map3.get(lastTab);
			Boolean b=(Boolean)val;
			
			if(b)
			{
				// r means result
				JOptionPane jop=new JOptionPane();
				int r=jop.showConfirmDialog(this,"Do you want to save file?","Close All Tab",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
				
				if(r==JOptionPane.YES_OPTION)
				{
					save();
					jtp.remove(lastTab);
				}
				else
					if(r==JOptionPane.NO_OPTION)
					{
						jtp.remove(lastTab);
					}
				else
				{
					jop.setVisible(false);
					break;
				}
			}
			else
			{
				jtp.remove(lastTab);
			}
		}
		
		if(jtp.getTabCount()==0)
		{
			cm_item[0].setEnabled(false);
			cm_item[1].setEnabled(false);
			
			for(int i=0;i<ei.length;i++)
				em_item[i].setEnabled(false);
			
			fm_item[2].setEnabled(false);
			fm_item[3].setEnabled(false);
			
			bm_item[0].setEnabled(false);
			bm_item[1].setEnabled(false);
		}
		if(jtp.getTabCount()==1)
			cm_item[1].setEnabled(false);
		
		if(jtp.getTabCount()==0)
			System.exit(1);			// to Terminate the application
	}
	
	//^^^^^^^^^^^^^^^^^^^^ main() method ^^^^^^^^^^^^^^^^^^^^^^^^
	
	public static void main(String[] args)
	{
		SEditor se=new SEditor();
	}
}