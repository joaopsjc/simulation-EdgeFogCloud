package de.nec.nle.siafu.edgeFogCloud.ontology;


import java.util.Iterator;
import java.util.Random;
import java.util.stream.Stream;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * This class handles the Ontology.
 * 
 * @author João Pedro de Souza Jardim da Costa
 */
public class DiseaseOntologyController {
	
	private static final DiseaseOntologyController instance = new DiseaseOntologyController();
	private OWLOntology diseaseOntology,symptomOntology,transmissionOntology,diseaseDriverOntology;
	private String namespace;
	
	/**
	 * The class constructor. Initializes the ontology by loading a pre-made one.
	 * 
	 */
	public DiseaseOntologyController() {
		loadOntology();
	}
	
    public static DiseaseOntologyController getInstance() {
        return instance;
    }
	
	/**
	 * Load the ontology Human-Disease-Ontology-Compact.owl from the resources folder.
	 */
	private void loadOntology()
	{
		 OWLOntologyManager om = OWLManager.createOWLOntologyManager();
		 
		 OWLOntologyDocumentSource diseaseOntologySource = 
				 new StreamDocumentSource(getClass().getResourceAsStream("/ontology/Human-Disease-Ontology.owl"));
		 try {
			System.out.println("Loading ontologies");
	
			diseaseOntology = om.loadOntologyFromOntologyDocument(diseaseOntologySource);
			Iterator<OWLOntology> ontologiesIterator = om.ontologies().iterator();
			
			while(ontologiesIterator.hasNext())
			{
				OWLOntology currentOntology = ontologiesIterator.next();
				if(currentOntology.getOntologyID().getOntologyIRI().toString()
						.equals("Optional[http://purl.obolibrary.org/obo/doid/imports/disdriv_import.owl]"))
				{
					diseaseDriverOntology = currentOntology;
				}
				else if(currentOntology.getOntologyID().getOntologyIRI().toString()
						.equals("Optional[http://purl.obolibrary.org/obo/doid/imports/symp_import.owl]"))
				{
					symptomOntology = currentOntology;
				}
				else if(currentOntology.getOntologyID().getOntologyIRI().toString()
						.equals("Optional[http://purl.obolibrary.org/obo/doid/imports/trans_import.owl]"))
				{
					transmissionOntology = currentOntology;
				}
			}
			
			namespace = "http://purl.obolibrary.org/obo/";
			
			System.out.println("Loading Complete");
		} catch (OWLOntologyCreationException e) {
			throw new RuntimeException("Couldn't create the provided ontology ", e);
		}
	}
	/**
	 * returns a combination of random values from Disease, Symptom, Disease Drive and Transmission
	 * classes from the Ontology
	 * 
	 * @return a String with the set of random values.
	 */
	public String getRandomSetOfValues()
	{
		OWLOntologyManager om = diseaseOntology.getOWLOntologyManager();
		OWLDataFactory df = om.getOWLDataFactory();
		
		Stream<OWLClass> diseaseSubClasses = diseaseOntology.classesInSignature()
				.filter(classe-> classe.getIRI().toString().contains("DOID"))
				.filter(classe -> diseaseOntology.subClassAxiomsForSuperClass(classe).count()==0);
		Stream<OWLClass> diseaseDriverSubClasses = diseaseDriverOntology.classesInSignature()
				.filter(classe-> diseaseDriverOntology.subClassAxiomsForSuperClass(classe).count()==0);
		Stream<OWLClass> symptomSubClasses = symptomOntology.classesInSignature()
				.filter(classe-> symptomOntology.subClassAxiomsForSuperClass(classe).count()==0);
		Stream<OWLClass> transmissionSubClasses = transmissionOntology.classesInSignature()
				.filter(classe-> transmissionOntology.subClassAxiomsForSuperClass(classe).count()==0);

		int diseaseRandomPosistion = getRandomInt(11347);
		int diseaseDriverRandomPosistion = getRandomInt(9);
		int symptomRandomPosistion = getRandomInt(713);
		int transmissionRandomPosistion = getRandomInt(20);
		
		OWLClass diseaseRandomSubCLass = (OWLClass) diseaseSubClasses.toArray()[diseaseRandomPosistion];
		OWLClass diseaseDriverRandomSubClass = (OWLClass) diseaseDriverSubClasses.toArray()[diseaseDriverRandomPosistion];
		OWLClass symptomRandomSubClass = (OWLClass) symptomSubClasses.toArray()[symptomRandomPosistion];
		OWLClass transmissionRandomSubClass = (OWLClass) transmissionSubClasses.toArray()[transmissionRandomPosistion];
		

		Iterator<OWLAnnotationAssertionAxiom> diseaseSubClassAnnotationsIterator =
				diseaseOntology.annotationAssertionAxioms(diseaseRandomSubCLass.getIRI()).
				filter(annotation -> annotation.getProperty().equals(df.getRDFSLabel())).iterator();
		Iterator<OWLAnnotationAssertionAxiom> diseaseDriverSubClassAnnotationsIterator =
				diseaseDriverOntology.annotationAssertionAxioms(diseaseDriverRandomSubClass.getIRI()).
				filter(annotation -> annotation.getProperty().equals(df.getRDFSLabel())).iterator();
		Iterator<OWLAnnotationAssertionAxiom> symptomSubClassAnnotationsIterator =
				symptomOntology.annotationAssertionAxioms(symptomRandomSubClass.getIRI()).
				filter(annotation -> annotation.getProperty().equals(df.getRDFSLabel())).iterator();
		Iterator<OWLAnnotationAssertionAxiom> transmissionSubClassAnnotationsIterator =
				transmissionOntology.annotationAssertionAxioms(transmissionRandomSubClass.getIRI()).
				filter(annotation -> annotation.getProperty().equals(df.getRDFSLabel())).iterator();

		OWLAnnotationAssertionAxiom randomDiseaseLabel = diseaseSubClassAnnotationsIterator.next();
		OWLAnnotationAssertionAxiom randomDiseaseDriveLabel = diseaseDriverSubClassAnnotationsIterator.next();
		OWLAnnotationAssertionAxiom randomSymptomLabel = symptomSubClassAnnotationsIterator.next();
		OWLAnnotationAssertionAxiom randomTransmissionLabel = transmissionSubClassAnnotationsIterator.next();
		
		String randomDiseaseString = randomDiseaseLabel.getValue().components()
				.filter(value-> !value.toString().contains("http")).iterator().next().toString();
		String randomDiseaseDriveString = randomDiseaseDriveLabel.getValue().components()
				.filter(value-> !value.toString().contains("http")).iterator().next().toString();
		String randomSymptomString = randomSymptomLabel.getValue().components()
				.filter(value-> !value.toString().contains("http")).iterator().next().toString();
		String randomTransmissionString = randomTransmissionLabel.getValue().components()
				.filter(value-> !value.toString().contains("http")).iterator().next().toString();
		
		String setRandomValue = randomDiseaseString.concat(";;").concat(randomDiseaseDriveString)
				.concat(";;").concat(randomSymptomString).concat(";;").concat(randomTransmissionString);
		
		getReducedDiseaseValue(randomDiseaseString);
		
		return setRandomValue;
	}
	
	public String getReducedValue(String packageOriginalData)
	{
		String[] packageSplitData = packageOriginalData.split(";;");
		String diseaseLabel= packageSplitData[0];
		String diseaseDriverLabel= packageSplitData[1];
		String symptomLabel= packageSplitData[2];
		String transmissionLabel= packageSplitData[3];
		
		int reducedDiseaseData = getReducedDiseaseValue(diseaseLabel);
		int reducedDiseaseDriverData = getReducedDiseaseDriverValue(diseaseDriverLabel);
		int reducedSymptomData = getReducedSymptomValue(symptomLabel);
		int reducedTransmissionData = getReducedTranmissionValue(transmissionLabel);
		
		String reducedData = "";
		reducedData = reducedData.concat(String.valueOf(reducedDiseaseData)).concat(";;")
				.concat(String.valueOf(reducedDiseaseDriverData)).concat(";;")
				.concat(String.valueOf(reducedSymptomData)).concat(";;")
				.concat(String.valueOf(reducedTransmissionData));
		return reducedData;
	}
	/**
	 * returns the id of the subclass of the class Disease, 
	 * corresponding with the label provided,
	 * inside the diseaseOntology
	 * 
	 * @param diseaseLabel the label of a subclass of the class Disease.
	 * 
	 * @return The value of the id as an Int.
	 */
	public int getReducedDiseaseValue(String diseaseLabel)
	{
		OWLOntologyManager om = diseaseOntology.getOWLOntologyManager();
		OWLDataFactory df = om.getOWLDataFactory();

		Stream<OWLClass> diseaseClasses = diseaseOntology.classesInSignature()
		.filter(classe -> diseaseOntology.annotationAssertionAxioms(classe.getIRI())
				.filter(annotation -> annotation.getProperty().equals(df.getRDFSLabel()))
				.filter(annotation -> annotation.getValue().components()
						.filter(component -> !component.toString().contains("http"))
						.iterator().next().toString().equals(diseaseLabel)
						)
				.count() != 0
				);
		
		String classIRI = diseaseClasses.findFirst().get().getIRI().toString();
		classIRI = classIRI.replaceFirst(namespace, "");
		classIRI = classIRI.replaceFirst("DOID_", "1");
		int reducedValue = Integer.parseInt(classIRI, 10);
		

		return reducedValue;
	}
	
	/**
	 * returns the id of the subclass of the class DiseaseDriver, 
	 * corresponding with the label provided,
	 * inside the diseaseDriverOntology
	 * 
	 * @param diseaseDriverLabel the label of a subclass of the class DiseaseDriver.
	 * 
	 * @return The value of the id as an Int.
	 */
	public int getReducedDiseaseDriverValue(String diseaseDriverLabel)
	{
		
		OWLOntologyManager om = diseaseDriverOntology.getOWLOntologyManager();
		OWLDataFactory df = om.getOWLDataFactory();

		Stream<OWLClass> diseaseDriveClasses = diseaseDriverOntology.classesInSignature()
		.filter(classe -> diseaseDriverOntology.annotationAssertionAxioms(classe.getIRI())
				.filter(annotation -> annotation.getProperty().equals(df.getRDFSLabel()))
				.filter(annotation -> annotation.getValue().components()
						.filter(component -> !component.toString().contains("http"))
						.iterator().next().toString().equals(diseaseDriverLabel)
						)
				.count() != 0
				);
		String classIRI = diseaseDriveClasses.findFirst().get().getIRI().toString();
		classIRI = classIRI.replaceFirst(namespace, "");
		classIRI = classIRI.replaceFirst("DISDRIV_", "2");
		classIRI = classIRI.replaceFirst("CHEBI_", "3");
		classIRI = classIRI.replaceFirst("ExO_", "4");
		classIRI = classIRI.replaceFirst("NCIT_C", "5");
		int reducedValue = Integer.parseInt(classIRI, 10);
		
		return reducedValue;
	}
	
	/**
	 * returns the id of the subclass of the class Symptom,
	 * corresponding with the label provided,
	 * inside the symptomOntology
	 * 
	 * @param symptomLabel the label of a subclass of the class Symptom.
	 * 
	 * @return The value of the id as an Int.
	 */
	public int getReducedSymptomValue(String symptomLabel)
	{
		
		OWLOntologyManager om = symptomOntology.getOWLOntologyManager();
		OWLDataFactory df = om.getOWLDataFactory();

		Stream<OWLClass> symptomClasses = symptomOntology.classesInSignature()
		.filter(classe -> symptomOntology.annotationAssertionAxioms(classe.getIRI())
				.filter(annotation -> annotation.getProperty().equals(df.getRDFSLabel()))
				.filter(annotation -> annotation.getValue().components()
						.filter(component -> !component.toString().contains("http"))
						.iterator().next().toString().equals(symptomLabel)
						)
				.count() != 0
				);
		String classIRI = symptomClasses.findFirst().get().getIRI().toString();
		classIRI = classIRI.replaceFirst(namespace, "");
		classIRI = classIRI.replaceFirst("SYMP_", "6");
		int reducedValue = Integer.parseInt(classIRI, 10);
		
		return reducedValue;
	}
	
	/**
	 * returns the id of the subclass of the class Tranmission, 
	 * corresponding with the label provided, 
	 * inside the transmissionOntology
	 * 
	 * @param transmissionLabel the label of a subclass of the class Tranmission.
	 * 
	 * @return The value of the id as an Int.
	 */
	public int getReducedTranmissionValue(String transmissionLabel)
	{
		
		OWLOntologyManager om = transmissionOntology.getOWLOntologyManager();
		OWLDataFactory df = om.getOWLDataFactory();

		Stream<OWLClass> transmissionClasses = transmissionOntology.classesInSignature()
		.filter(classe -> transmissionOntology.annotationAssertionAxioms(classe.getIRI())
				.filter(annotation -> annotation.getProperty().equals(df.getRDFSLabel()))
				.filter(annotation -> annotation.getValue().components()
						.filter(component -> !component.toString().contains("http"))
						.iterator().next().toString().equals(transmissionLabel)
						)
				.count() != 0
				);
		String classIRI = transmissionClasses.findFirst().get().getIRI().toString();
		classIRI = classIRI.replaceFirst(namespace, "");
		classIRI = classIRI.replaceFirst("TRANS_", "7");
		int reducedValue = Integer.parseInt(classIRI, 10);
		
		return reducedValue;
	}
	
	/**
	 * returns the label of the subclass 
	 * corresponding with the ID provided.
	 * 
	 * @param id the label of a subclass of the class Disease.
	 * 
	 * @return The value of the id as an Int.
	 */
	public String getOriginalValue(int id)
	{	
		String idString = String.valueOf(id);
		char classIdentifier = idString.charAt(0);
		idString = idString.substring(1);
		String classIri = namespace;
		String classLabel=null;
		switch (classIdentifier)
		{
			case '1':
				classIri = classIri.concat("DOID_").concat(idString);
				classLabel = getOriginalValueFromDiseaseOntology(classIri);
				break;
			case '2':
				classIri = classIri.concat("DISDRIV_").concat(idString);
				classLabel = getOriginalValueFromDiseaseDriverOntology(classIri);
				break;
			case '3':
				classIri = classIri.concat("CHEBI_").concat(idString);
				classLabel = getOriginalValueFromDiseaseDriverOntology(classIri);
				break;
			case '4':
				classIri = classIri.concat("ExO_").concat(idString);
				classLabel = getOriginalValueFromDiseaseDriverOntology(classIri);
				break;
			case '5':
				classIri = classIri.concat("NCIT_C").concat(idString);
				classLabel = getOriginalValueFromDiseaseDriverOntology(classIri);
				break;
			case '6':
				classIri = classIri.concat("SYMP_").concat(idString);
				classLabel = getOriginalValueFromSymptomOntology(classIri);
				break;
			default:
				classIri = classIri.concat("TRANS_").concat(idString);
				classLabel = getOriginalValueFromDiseaseTransmissionOntology(classIri);
		}
		
		return classLabel;
	}
	
	private String getOriginalValueFromDiseaseOntology(String IriString)
	{	
		OWLOntologyManager om = diseaseOntology.getOWLOntologyManager();
		OWLDataFactory df = om.getOWLDataFactory();
		
		IRI classIRI = IRI.create(IriString);
		
		Iterator<OWLAnnotationAssertionAxiom> diseaseSubClassAnnotationsIterator =
				diseaseOntology.annotationAssertionAxioms(classIRI).
				filter(annotation -> annotation.getProperty().equals(df.getRDFSLabel())).iterator();
		
		OWLAnnotationAssertionAxiom diseaseLabel = diseaseSubClassAnnotationsIterator.next();
		
		String diseaseLabelString = diseaseLabel.getValue().components()
				.filter(value-> !value.toString().contains("http")).iterator().next().toString();
		
		
		return diseaseLabelString;
	}
	
	private String getOriginalValueFromDiseaseDriverOntology(String IriString)
	{
		OWLOntologyManager om = diseaseDriverOntology.getOWLOntologyManager();
		OWLDataFactory df = om.getOWLDataFactory();
		
		IRI classIRI = IRI.create(IriString);
		
		Iterator<OWLAnnotationAssertionAxiom> diseaseDriverSubClassAnnotationsIterator =
				diseaseDriverOntology.annotationAssertionAxioms(classIRI).
				filter(annotation -> annotation.getProperty().equals(df.getRDFSLabel())).iterator();
		
		OWLAnnotationAssertionAxiom diseaseDriverLabel = diseaseDriverSubClassAnnotationsIterator.next();
		
		String diseaseDriverLabelString = diseaseDriverLabel.getValue().components()
				.filter(value-> !value.toString().contains("http")).iterator().next().toString();
		
		
		return diseaseDriverLabelString;
	}
	
	private String getOriginalValueFromSymptomOntology(String IriString)
	{
		OWLOntologyManager om = symptomOntology.getOWLOntologyManager();
		OWLDataFactory df = om.getOWLDataFactory();
		
		IRI classIRI = IRI.create(IriString);
		
		Iterator<OWLAnnotationAssertionAxiom> symptomSubClassAnnotationsIterator =
				symptomOntology.annotationAssertionAxioms(classIRI).
				filter(annotation -> annotation.getProperty().equals(df.getRDFSLabel())).iterator();
		
		OWLAnnotationAssertionAxiom symptomLabel = symptomSubClassAnnotationsIterator.next();
		
		String symptomLabelString = symptomLabel.getValue().components()
				.filter(value-> !value.toString().contains("http")).iterator().next().toString();
		
		
		return symptomLabelString;
	}
	
	private String getOriginalValueFromDiseaseTransmissionOntology(String IriString)
	{
		OWLOntologyManager om = transmissionOntology.getOWLOntologyManager();
		OWLDataFactory df = om.getOWLDataFactory();
		
		IRI classIRI = IRI.create(IriString);
		
		Iterator<OWLAnnotationAssertionAxiom> transmissionSubClassAnnotationsIterator =
				transmissionOntology.annotationAssertionAxioms(classIRI).
				filter(annotation -> annotation.getProperty().equals(df.getRDFSLabel())).iterator();
		
		OWLAnnotationAssertionAxiom transmissionLabel = transmissionSubClassAnnotationsIterator.next();
		
		String transmissionLabelString = transmissionLabel.getValue().components()
				.filter(value-> !value.toString().contains("http")).iterator().next().toString();
		
		
		return transmissionLabelString;
	}
	
	private int getRandomInt(int upperLimit)
	{
		Random numberGenerator = new Random();
		int randomNumber = (numberGenerator.nextInt(upperLimit));
		return randomNumber;
	}
}
