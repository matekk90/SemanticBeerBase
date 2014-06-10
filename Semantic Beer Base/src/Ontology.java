import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.ontology.UnionClass;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;


public class Ontology {

	/**
	 * @param args
	 */
	OntModel MainOnt = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_TRANS_INF);;
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
		OntModel BrewOnt = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_TRANS_INF);
		BrewerySource.add(source + "#");
		BrewOnt.read(source + "#");
		BreweryOnt.add(BrewOnt);
	}
	
	
	public void printBeers()
	{
		drzewko = new JTree(root);
		DefaultMutableTreeNode firmy = null;
		
		String last = " ";
		String nameCompany = " ";	
		
		DefaultMutableTreeNode styleExample=null;
		DefaultMutableTreeNode instancje=null;
		DefaultMutableTreeNode ingredients=null;
		DefaultMutableTreeNode ingredientsExample=null;
		for (int i = 0; i < BreweryOnt.size(); i++)
		{
			
			skladnik="";
			/****************** Wyciagamy nazwe bazy - to nazwa firmy ****************************/
			nameCompany = BrewerySource.get(i);
			
			
			int start = nameCompany.lastIndexOf("/")+1;
			int end = nameCompany.lastIndexOf(".");

			nameCompany = nameCompany.substring(start,end);
			
			firmy = new DefaultMutableTreeNode(nameCompany);
			root.add(firmy);
			/**************************************************************************************/
			
			OntClass BreweryClass = MainOnt.getOntClass("http://www.csd.abdn.ac.uk/research/AgentCities/ontologies/beer#Beer");
			ExtendedIterator<OntClass> iBrew = BreweryClass.listSubClasses();
			while ( iBrew.hasNext()) 
	        {
	        	OntClass c = iBrew.next();
	        	// Pobieramy przyklady danego stylu i do drzewa 

				Resource res = ResourceFactory.createResource(c.getURI());
				
				ExtendedIterator<Individual> ind = BreweryOnt.get(i).listIndividuals(res);
				while (ind.hasNext())
				{
					String instName = ind.next().getLocalName();
					//System.out.println("Nazwa: " + instName);
					instancje = new DefaultMutableTreeNode(instName);
					firmy.add(instancje);
					//System.out.println(c.getLocalName());
					styleExample = new DefaultMutableTreeNode(c.getLocalName());
	        		instancje.add(styleExample);
	        		
					ingredients = new DefaultMutableTreeNode("Sk³adniki");
					styleExample.add(ingredients);
		        	OntClass artefact = MainOnt.getOntClass( NS + c.getLocalName() );
		        	ExtendedIterator<OntClass> iArtefact = artefact.listSuperClasses();
		        	while ( iArtefact.hasNext() ) 
		        	{
		        		displayType( iArtefact.next() );
		        		// Pobieramy i obcinamy dany skladnik 
		        		if(skladnik != "" && skladnik != last)
		        		{
		        			//String[] parts = skladnik.split(" ");
		        			ingredientsExample = new DefaultMutableTreeNode(skladnik);
		        			ingredients.add(ingredientsExample);
		        			last = skladnik;
		        		}
		        	}
				} // while (ind.hasNext())
	        } // while ( iBrew.hasNext()) 
		} // for (int i = 0; i < BreweryOnt.size(); i++)
		BreweryOnt.clear();
		BrewerySource.clear();
		
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
        String out = String.format( "%s",
                                    renderConstraint( constraint ) );
        //System.out.println( "Ingredients: " + out );
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
