//import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;



public class SemanticBeerBase extends JFrame implements ActionListener {

	/**
	 * 
	 */
	JButton bAddMainBase, bAddBreweryBase, bPrintBeers;
	JLabel lMainBase, lBreweryBase;
	JTextField tMainBase, tBreweryBase;
	JTree tBeers;
	Ontology ont = new Ontology();
	private static final long serialVersionUID = 1L;
	static SemanticBeerBase frame;
	static int ile = 0;
	Container cp;
	/**
	 * @param args
	 */
	SemanticBeerBase()
	{
		setSize(600,400);
		setTitle("Semantic Beer Base");
		//FlowLayout flow = new FlowLayout();
		setLayout(null);
		lMainBase = new JLabel("Adres bazy g³ównej");
		lMainBase.setBounds(20, 10, 140, 20);
		add(lMainBase);
		tMainBase = new JTextField("https://dl.dropboxusercontent.com/u/87890572/beer1.4.rdf");
		tMainBase.setBounds(150, 10, 200, 20);
		tMainBase.setToolTipText("Adres g³ównego repozytorium");
		add(tMainBase);
		bAddMainBase = new JButton("Dodaj");
		bAddMainBase.setBounds(360, 10, 70, 20);
		add(bAddMainBase);
		bAddMainBase.addActionListener(this);


		lBreweryBase = new JLabel("Adres bazy firmowej");
		lBreweryBase.setBounds(20, 40, 140, 20);
		add(lBreweryBase);
		tBreweryBase = new JTextField("https://dl.dropboxusercontent.com/u/87890572/Zywiec.owl");
		tBreweryBase.setBounds(150, 40, 200, 20);
		tBreweryBase.setToolTipText("Adres firmowego repozytorium");
		add(tBreweryBase);
		bAddBreweryBase = new JButton("Dodaj");
		bAddBreweryBase.setBounds(360, 40, 70, 20);
		add(bAddBreweryBase);
		bAddBreweryBase.addActionListener(this);
		
		bPrintBeers = new JButton("Wyœwietl piwa");
		bPrintBeers.setBounds(50, 100, 150, 20);
		add(bPrintBeers);
		bPrintBeers.addActionListener(this);
		String [] row = {"Firma", "Nazwa piwa", "Styl"};
		String [][] data = {{"¯ywiec", "¯ywiec Bia³e", "White"}};
		
		
		
		
		
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		frame = new SemanticBeerBase();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object source = e.getSource();
		if (source.equals(bAddMainBase))
		{
			ont.addMainBase(tMainBase.getText());
			if (ont.isMainBaseOpen())
				infoBox("Dodano bazê " + tMainBase.getText(), "Sukces");
		}
		else if (source.equals(bAddBreweryBase))
		{
			ont.addBreweryBase(tBreweryBase.getText());
			infoBox("Dodano bazê " + tBreweryBase.getText(),"Sukces");
		}
		else if (source.equals(bPrintBeers))
		{
			// Tutaj trzeba jakoœ wyœwietlaæ w JTable trójki (firma,nazwa,styl)
			// Musisz zobaczyæ sobie printBeers w Ontology.java - tam wszystko wyœwietlam
			// w konsoli, trzeba to wrzuciæ do jakieœ struktury i wyœwietliæ za pomoc¹ JTable 
			// (lub w inny sposób) byle bêdzie dzia³aæ
			ont.printBeers();
			
			if(ile > 0)
			{
				tBeers.setModel(null);
				frame.getContentPane().remove(tBeers);
				frame.getContentPane().repaint(50L);
			}
			
			
			
			tBeers = new JTree(ont.drzewko.getModel());
			tBeers.setBounds(20, 130, 550, 200);
			
			
			//JScrollPane treePane = new JScrollPane(tBeers);
			//JOptionPane.showMessageDialog(null,new JScrollPane(tBeers));
        
			

			frame.getContentPane().add(tBeers);
			frame.getContentPane().repaint();
			ile++;
		}

	}
	
	private static void infoBox(String infoMessage, String location)
    {
        JOptionPane.showMessageDialog(null, infoMessage, location, JOptionPane.INFORMATION_MESSAGE);
    }

}
