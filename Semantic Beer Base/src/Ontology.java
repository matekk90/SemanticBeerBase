import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.ontology.UnionClass;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;


public class Ontology {

	/**
	 * @param args
	 */
	OntModel MainOnt = ModelFactory.createOntologyModel();;
	List <OntModel> BreweryOnt = new LinkedList<OntModel>();
	String MainSource;
	List <String> BrewerySource = new LinkedList<String>();
	String NS = "http://www.csd.abdn.ac.uk/research/AgentCities/ontologies/beer#";
	DefaultMutableTreeNode root = new DefaultMutableTreeNode("Piwo");
	JTree drzewko=null;
	
	
	public void addMainBase(String source)
	{
		MainSource = source + "#";
		MainOnt.read(MainSource);
	}
	
	public void addBreweryBase(String source)
	{
		OntModel BrewOnt = ModelFactory.createOntologyModel();
		BrewerySource.add(source + "#");
		BrewOnt.read(source + "#");
		BreweryOnt.add(BrewOnt);
	}
	
	
	
	public void printBeers()
	{
		//List<String[]> data = new LinkedList<String[]>();
		//tutaj tworzymy liste styli piwa poki co nie wiem skad widzalem ze na sztywno wspiales
	
		
		drzewko = new JTree(root);
		DefaultMutableTreeNode firmy = null;
		
		String last = " ";
		String nameComapany = " ";
			//style = new DefaultMutableTreeNode(beerStyles.get(i));
			//root.add(style);
		
		
		
		
		String row[] = {"0","0","0"};
		//System.out.println("Style warzone:");
		DefaultMutableTreeNode styleExample=null;
		DefaultMutableTreeNode instancje=null;
		DefaultMutableTreeNode ingredients=null;
		DefaultMutableTreeNode ingredientsExample=null;
		for (int i = 0; i < BreweryOnt.size(); i++)
		{
			skladnik="";
			//System.out.println(BrewerySource.get(i));
			/****************** Wyciagamy nazwe bazy - to nazwa firmy ****************************/
			nameComapany = BrewerySource.get(i);
			
			int start = 0;
			int end = 0;
			
			for(int ii=(nameComapany.length()-1); ii>=0; ii--)
			{
				
				String c =Character.toString(nameComapany.charAt(ii));
				
				if(start == 0)
					if(c.equals("/"))
						start=ii+1;
				if(end == 0)
					if(c.equals("."))
						end=ii;
			}
			nameComapany = nameComapany.substring(start,end);
			
			firmy = new DefaultMutableTreeNode(nameComapany);
			root.add(firmy);
			/**************************************************************************************/
			//String NS = BrewerySource.get(i);
			OntClass BreweryClass = BreweryOnt.get(i).getOntClass(NS + "Beer");
			for (Iterator<OntClass> iBrew = BreweryClass.listSubClasses(); iBrew.hasNext();) 
	        {
	        	OntClass c = iBrew.next();
	        	/***** Pobieramy przyklady danego stylu i do drzewa ************/
	        	// Wyœwietlamy nazwê klasy (stylu)
	        	System.out.print(c.getLocalName() + " - ");
	        	row[0] = c.getLocalName();
	        	styleExample = new DefaultMutableTreeNode(c.getLocalName());
	        	//firmy.add(styleExample);
	        	/***************************************************************/
	        	// oraz nazwê piwa/piw (instancjê klasy) danego stylu
	        	ExtendedIterator<? extends OntResource> instances = c.listInstances();
	        	while (instances.hasNext())
	        	{
	        		/** tutaj pobierana jest konkretna instancja danego stylu ***/ 
	        		Individual thisInstance = (Individual) instances.next();
	        		System.out.println(thisInstance.getLocalName() + " ");
	        		instancje = new DefaultMutableTreeNode(thisInstance.getLocalName());
	        		firmy.add(instancje);
	        		instancje.add(styleExample);
	        		row[1] = thisInstance.getLocalName();
	        	}
	        	// oraz sk³adniki
	        	System.out.println("Sk³adniki:");
	        	ingredients = new DefaultMutableTreeNode("Sk³adniki");
	        	styleExample.add(ingredients);
	        	OntClass artefact = MainOnt.getOntClass( NS + c.getLocalName() );

	        	for (Iterator<OntClass> iArtefact = artefact.listSuperClasses(); iArtefact.hasNext(); ) 
	        	{
	        		displayType( iArtefact.next() );
	        		/********* Pobieramy i obcinamy dany skladnik **************/
	        		if(skladnik != "" && skladnik != last)
	        		{
	        			String[] parts = skladnik.split(" ");
	        			ingredientsExample = new DefaultMutableTreeNode(parts[2]);
	        			ingredients.add(ingredientsExample);
	        			last = skladnik;
	        		}
	        	}
	        }
		}
		BreweryOnt.clear();
		
	}
	static String skladnik="";
	public Boolean isMainBaseOpen()
	{
		return !MainOnt.isClosed();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

	
    protected static void displayType( OntClass sup ) {
        if (sup.isRestriction()) {
            displayRestriction( sup.asRestriction() );
        }
    }

    protected static void displayRestriction( Restriction sup ) {
        if (sup.isAllValuesFromRestriction()) {
            displayRestriction( "all", sup.getOnProperty(), sup.asAllValuesFromRestriction().getAllValuesFrom() );
        }
        else if (sup.isSomeValuesFromRestriction()) {
            displayRestriction( "some", sup.getOnProperty(), sup.asSomeValuesFromRestriction().getSomeValuesFrom() );
        }
    }

    protected static void displayRestriction( String qualifier, OntProperty onP, Resource constraint ) {
        String out = String.format( "%s %s %s",
                                    qualifier, renderURI( onP ), renderConstraint( constraint ) );
        System.out.println( "Ingredients: " + out );
        skladnik = out;
        
    }

    protected static Object renderConstraint( Resource constraint ) {
        if (constraint.canAs( UnionClass.class )) {
            UnionClass uc = constraint.as( UnionClass.class );
            // this would be so much easier in ruby ...
            String r = "union{ ";
            for (Iterator<? extends OntClass> i = uc.listOperands(); i.hasNext(); ) {
                r = r + " " + renderURI( i.next() );
            }
            return r + "}";
        }
        else {
            return renderURI( constraint );
        }
    }

    protected static Object renderURI( Resource onP ) {
        String qName = onP.getModel().qnameFor( onP.getURI() );
        return qName == null ? onP.getLocalName() : qName;
    }
}
