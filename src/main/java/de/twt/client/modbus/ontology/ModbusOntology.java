package de.twt.client.modbus.ontology;

import java.io.File;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import de.twt.client.modbus.common.constants.ModbusConstants;
import eu.arrowhead.common.Utilities;

public class ModbusOntology {
	// final IRI baseIri = IRI.create("http://www.semanticweb.org/anwender/ontologies/2020/1/untitled-ontology-23");
	final IRI baseIri = IRI.create("http://www.twt-gmbh.de/productive40/demonstrator");
	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	OWLDataFactory df = manager.getOWLDataFactory();
	OWLOntology ontology;
	OWLReasoner reasoner;
	NodeSet<OWLNamedIndividual> subSystemIndividuals;
	
	public void loadOntology(String filename) {
		try {
			ontology = manager.loadOntologyFromOntologyDocument(new File(filename));
			reasoner = new StructuralReasonerFactory().createReasoner(ontology);
			for(OWLClass cls : ontology.getClassesInSignature()) {
				if  (cls.getIRI().getFragment().equals("ProductionSubSystem")){
					subSystemIndividuals = reasoner.getInstances(cls, true);
				}
			}
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getSubscriptionModule(String subSystemsName) {
		OWLNamedIndividual subSystemIndividual = getSubsystemIndividualWithName(subSystemsName);
		OWLObjectProperty headModuleObjProperty = df.getOWLObjectProperty(IRI.create(baseIri + "#headModule"));
		OWLObjectProperty nextModuleObjProperty = df.getOWLObjectProperty(IRI.create(baseIri + "#nextModule"));
		OWLObjectProperty fulfilledObjProperty = df.getOWLObjectProperty(IRI.create(baseIri + "#isFulfilledBy"));
		OWLNamedIndividual headModuleIndividual = reasoner.getObjectPropertyValues(subSystemIndividual, headModuleObjProperty).getFlattened().stream().findFirst().get();
		OWLNamedIndividual subscriptionModuleIndividual = getOWLNamedIndividualWithObjectProperty(headModuleIndividual, nextModuleObjProperty);
		System.out.println("---------------------");
		System.out.println(subscriptionModuleIndividual);
		return subscriptionModuleIndividual.getIRI().getFragment();
	}
	
	public ModbusOntologyModule getInputModuleFromController(String subSystemsName) {
		ModbusOntologyModule inputModule = getInputOutputModuleFromController(subSystemsName, true);
		inputModule.name = getInputModule(subSystemsName);
		// System.out.println(Utilities.toJson(inputModule));
		return inputModule;
	}
	
	public ModbusOntologyModule getOutputModuleFromController(String subSystemsName) {
		ModbusOntologyModule outputModule = getInputOutputModuleFromController(subSystemsName, false);
		outputModule.name = getOutputModule(subSystemsName);
		// System.out.println(Utilities.toJson(outputModule));
		return outputModule;
	}
	
	public ModbusOntologyModule getInputOutputModuleFromController(String subSystemsName, boolean isInput) {
		OWLNamedIndividual subSystemIndividual = getSubsystemIndividualWithName(subSystemsName);
		OWLObjectProperty controllsObjProperty = df.getOWLObjectProperty(IRI.create(baseIri + "#controlls"));
		OWLNamedIndividual controllerModule = getOWLNamedIndividualWithObjectProperty(subSystemIndividual, controllsObjProperty);
		OWLObjectProperty isFulfilledByObjProperty = df.getOWLObjectProperty(IRI.create(baseIri + "#isFulfilledBy"));
		OWLNamedIndividual controller = reasoner.getObjectPropertyValues(controllerModule, isFulfilledByObjProperty).getFlattened().stream().findFirst().get();
		OWLObjectProperty offersServiceObjProperty = df.getOWLObjectProperty(IRI.create(baseIri + "#offersService"));
		String className = isInput ? "#AH_ModbusTCP_Client_InputConnector" : "#AH_ModbusTCP_Client_OutputConnector";
		OWLClass inputClass = df.getOWLClass(IRI.create(baseIri + className));
		OWLNamedIndividual inputIndividual = getOWLNamedIndividualWithObjectProperty(controller, offersServiceObjProperty, inputClass);
		
		OWLDataProperty hasAddressDataProperty = df.getOWLDataProperty(IRI.create(baseIri + "#hasAddress"));
		OWLDataProperty hasConnectorTypeDataProperty = df.getOWLDataProperty(IRI.create(baseIri + "#hasConnectorType"));
		OWLDataProperty hasConnectorTypeAddressDataProperty = df.getOWLDataProperty(IRI.create(baseIri + "#hasConnectorTypeAddress"));
		OWLDataProperty hasConnectorDefaultValueDataProperty = df.getOWLDataProperty(IRI.create(baseIri + "#hasConnectorDefaultValue"));
		ModbusOntologyModule module = new ModbusOntologyModule();
		module.ip = reasoner.getDataPropertyValues(inputIndividual, hasAddressDataProperty).stream().findFirst().get().getLiteral();
		module.memoryType = ModbusConstants.MODBUS_DATA_TYPE.valueOf(
				reasoner.getDataPropertyValues(inputIndividual, hasConnectorTypeDataProperty).stream().findFirst().get().getLiteral()
				);
		module.memoryTypeAddress = Integer.parseInt(
				reasoner.getDataPropertyValues(inputIndividual, hasConnectorTypeAddressDataProperty).stream().findFirst().get().getLiteral()
				);
		module.defaultValue = reasoner.getDataPropertyValues(inputIndividual, hasConnectorDefaultValueDataProperty).stream().findFirst().get().getLiteral();
		
		return module;
	}
	
	@SuppressWarnings("deprecation")
	public String getInputModule(String subSystemsName) {
		OWLNamedIndividual subSystemIndividual = getSubsystemIndividualWithName(subSystemsName);
		OWLObjectProperty headModuleObjProperty = df.getOWLObjectProperty(IRI.create(baseIri + "#headModule"));
		OWLNamedIndividual headModuleIndividual = reasoner.getObjectPropertyValues(subSystemIndividual, headModuleObjProperty).getFlattened().stream().findFirst().get();
		OWLObjectProperty previousModuleObjProperty = df.getOWLObjectProperty(IRI.create(baseIri + "#previousModule"));
		OWLNamedIndividual inputIndividual = reasoner.getObjectPropertyValues(headModuleIndividual, previousModuleObjProperty).getFlattened().stream().findFirst().get();
		String inputName = inputIndividual.getIRI().getFragment();
		
		return inputName;
	}
	
	public String getOutputModule(String subSystemsName) {
		OWLNamedIndividual subSystemIndividual = getSubsystemIndividualWithName(subSystemsName);
		OWLObjectProperty tailModuleObjProperty = df.getOWLObjectProperty(IRI.create(baseIri + "#tailModule"));
		OWLNamedIndividual tailModuleIndividual = reasoner.getObjectPropertyValues(subSystemIndividual, tailModuleObjProperty).getFlattened().stream().findFirst().get();
		String tailModule = tailModuleIndividual.getIRI().getFragment();
		
		return tailModule;
	}
	
	private OWLClass getOWLClassFromOWLNamedIndividual(OWLNamedIndividual individual) {
		OWLClass individualCls = null;
		for(OWLClass cls : ontology.getClassesInSignature()) {
			NodeSet<OWLNamedIndividual> instances = reasoner.getInstances(cls, true);
			if (instances.containsEntity(individual)) {
				individualCls = cls;
				break;
			}
		}
		return individualCls;
	}
	
	private OWLNamedIndividual getSubsystemIndividualWithName(String subSystemsName) {
		OWLNamedIndividual subSystemIndividual = null;
		for (OWLNamedIndividual ind : subSystemIndividuals.getFlattened()) {
            if (ind.getIRI().getFragment().equals(subSystemsName)) {
            	subSystemIndividual = ind;
            	break;
            }
		}
		return subSystemIndividual;
	}

	private OWLNamedIndividual getOWLNamedIndividualWithObjectProperty(OWLNamedIndividual individual, OWLObjectProperty objProperty) {
		OWLNamedIndividual individualFound = null;
		for (OWLNamedIndividual ind : ontology.getIndividualsInSignature()) {
			NodeSet<OWLNamedIndividual> filterdInds = reasoner.getObjectPropertyValues(ind, objProperty);
			if (filterdInds.containsEntity(individual)) {
				individualFound = ind;
				break;
			}
		}
		return individualFound;
	}
	
	private OWLNamedIndividual getHeadComponentIdividual(OWLNamedIndividual individual) {
		OWLObjectProperty headComponentObjProperty = df.getOWLObjectProperty(IRI.create(baseIri + "#headComponent"));
		NodeSet<OWLNamedIndividual> headComponentIndividuals = reasoner.getObjectPropertyValues(individual, headComponentObjProperty);
		if (headComponentIndividuals.getFlattened().size() > 0) {
			return getHeadComponentIdividual(headComponentIndividuals.getFlattened().stream().findFirst().get());
		}
		return individual;
	}
	
	private OWLNamedIndividual getTailComponentIdividual(OWLNamedIndividual individual) {
		OWLObjectProperty tailComponentObjProperty = df.getOWLObjectProperty(IRI.create(baseIri + "#tailComponent"));
		NodeSet<OWLNamedIndividual> tailComponentIndividuals = reasoner.getObjectPropertyValues(individual, tailComponentObjProperty);
		if (tailComponentIndividuals.getFlattened().size() > 0) {
			return getHeadComponentIdividual(tailComponentIndividuals.getFlattened().stream().findFirst().get());
		}
		return individual;
	}

	private OWLNamedIndividual getOWLNamedIndividualWithObjectProperty(OWLNamedIndividual individual, OWLObjectProperty objProperty, OWLClass individualFoundClass) {
		OWLNamedIndividual individualFound = null;
		String individualFoundClassName = individualFoundClass.getIRI().getFragment();
		NodeSet<OWLNamedIndividual> filterdInds = reasoner.getObjectPropertyValues(individual, objProperty);
		for (Node<OWLNamedIndividual> individualnode : filterdInds) {
			String individualClassName = getOWLClassFromOWLNamedIndividual(individualnode.getRepresentativeElement()).getIRI().getFragment();
			if (individualFoundClassName.equals(individualClassName)) {
				individualFound = individualnode.getRepresentativeElement();
				break;
			}
		}
		
		return individualFound;
	}
}

