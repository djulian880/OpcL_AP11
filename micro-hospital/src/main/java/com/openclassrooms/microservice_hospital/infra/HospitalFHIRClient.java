package com.openclassrooms.microservice_hospital.infra;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import com.openclassrooms.microservice_hospital.infra.DTO.OrganizationDTO;
import com.openclassrooms.microservice_hospital.infra.model.HospitalBDD;
import com.openclassrooms.microservice_hospital.infra.repository.HospitalRepository;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class HospitalFHIRClient {

    @Autowired
    HospitalRepository hospitalRepository;


    IGenericClient client;

    public HospitalFHIRClient(){
        FhirContext ctx = FhirContext.forR4();
        client = ctx.newRestfulGenericClient("https://gateway.api.esante.gouv.fr/fhir");

        // Ajout de la clé API à chaque requête
        client.registerInterceptor(new IClientInterceptor() {
            @Override
            public void interceptRequest(IHttpRequest request) {
                request.addHeader("ESANTE-API-KEY", "2637adef-a6d3-4852-b7ad-efa6d34852dc");
            }
            @Override
            public void interceptResponse(IHttpResponse response) {}
        });
    }

    public void retrieveAll() {


        List<OrganizationDTO> listOrganization=new ArrayList<>();

        // Rechercher les établissements hospitaliers par département
        Bundle bundle = client.search()
                .forResource(Organization.class)
                .where(Organization.TYPE.exactly().code("355")) // Centre Hospitalier
                .and(Organization.ADDRESS_POSTALCODE.matches().value("68"))  // Bas-Rhin
                .and(Organization.ACTIVE.exactly().code("true"))
                .count(20)
                .returnBundle(Bundle.class)
                .execute();

        String idHospital="";
        int count = 0;
        // Boucle sur toutes les pages
        while (bundle != null) {
            // Extraire les résultats de la page courante
            for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
                Organization org = (Organization) entry.getResource();
                OrganizationDTO organizationDTO = new OrganizationDTO();

                //System.out.println("Nom       : " + org.getName());
                String adresse = "";
                String rue = org.getAddressFirstRep().getLine().getFirst().getValue();
                if (rue.contains("BP")) {
                    rue = rue.substring(0, rue.indexOf("BP") - 1);
                }

               /* getExtension().get(0)+" "+org.getAddressFirstRep().getExtension().get(1)+
                        org.getAddressFirstRep().getExtension().get(2);*/
                String ville = org.getAddressFirstRep().getCity();
                if (ville.contains("CEDEX")) {
                    ville = ville.substring(0, ville.indexOf("CEDEX") - 1);
                }

                adresse = rue + " " + ville;
                //System.out.println("Adresse   : " + org.getAddressFirstRep().getCity()+" "+org.getAddressFirstRep().getPostalCode()+" "+org.getAddressFirstRep().getLine());
                //System.out.println("Adresse   : " + adresse);
                idHospital = org.getIdElement().getIdPart();
                //System.out.println("Id  : " + idHospital);
                organizationDTO.setName(org.getName());
                organizationDTO.setId(idHospital);
                organizationDTO.setAdress(adresse);
                listOrganization.add(organizationDTO);
                //count++;
            }
            // Vérifier s'il existe une page suivante
            if (bundle.getLink(Bundle.LINK_NEXT) != null) {
                bundle = client.loadPage()
                        .next(bundle)
                        .execute();
            } else {
                bundle = null; // fin de la pagination
            }
        }

        for(OrganizationDTO organizationDTO : listOrganization){
            ArrayList<String> specialties=retrieveSpecialtiesFromOrganization(organizationDTO);
            if(specialties.size()>0){
                HospitalBDD hospitalBDD = new HospitalBDD();
                hospitalBDD.setAddress(organizationDTO.getAdress());
                hospitalBDD.setName(organizationDTO.getName());
                hospitalBDD.setSpecialities(specialties);
                System.out.println(hospitalBDD.toString());
                hospitalRepository.save(hospitalBDD);
            }

        }
        System.out.println("Terminé ");

/*
        List<String> listePraticiens=new ArrayList<String>();
        String idPraticient="";
        String idTotal="";
        // Utilisation directe dans la requête suivante
        Bundle praticiensRole = client.search()
                .forResource(PractitionerRole.class)
                .where(PractitionerRole.ORGANIZATION.hasId(idHospital))
                .include(PractitionerRole.INCLUDE_PRACTITIONER)
                .count(10)
                .returnBundle(Bundle.class)
                .execute();

        while (praticiensRole != null) {
            // Extraire les résultats de la page courante
            for (Bundle.BundleEntryComponent entry : praticiensRole.getEntry()) {
                try{
                    PractitionerRole practitionerRole = (PractitionerRole) entry.getResource();
                    System.out.println("****** Début ******: ");
                    String idpractitionerRole=practitionerRole.getIdPart();
                    System.out.println("idpractitionerRole: " + idpractitionerRole);
                    String idRole=idpractitionerRole.substring(4);
                    idRole=idRole.substring(0,idRole.indexOf("-"));
                    System.out.println("idRole: " + idRole);

                    idPraticient=practitionerRole.getPractitioner().getReferenceElement().getIdPart();
                    System.out.println("getIdPart  : " + idPraticient);
                    System.out.println("ID complet  : " + idPraticient+"-"+idRole);
                    idTotal=idPraticient+"-"+idRole;


                    listePraticiens.add(idTotal);
                }
                catch(Exception e){

                }




            }
            // Vérifier s'il existe une page suivante
            if (praticiensRole.getLink(Bundle.LINK_NEXT) != null) {
                praticiensRole = client.loadPage()
                        .next(praticiensRole)
                        .execute();
            } else {
                praticiensRole = null; // fin de la pagination
            }


        }
*/



        //System.out.println("------///// idPraticienTotal retenu : " + idTotal);
        // Utilisation directe dans la requête suivante
        /*Practitioner practitioner = client.read()
                .resource(Practitioner.class)
                .withId(idTotal)
                .execute();*/
        /*
        for(String idpersonne : listePraticiens) {
            Practitioner practitioner = client.read()
                    .resource(Practitioner.class)
                    .withUrl("https://gateway.api.esante.gouv.fr/fhir/v2/Practitioner/"+idpersonne)
                    .execute();

            for (Practitioner.PractitionerQualificationComponent qualif : practitioner.getQualification()) {

                for (Coding code : qualif.getCode().getCoding()) {
                    if(code.getSystem().equals("https://mos.esante.gouv.fr/NOS/TRE_R38-SpecialiteOrdinale/FHIR/TRE-R38-SpecialiteOrdinale")){
                        String spe=code.getDisplay();
                        spe=spe.substring(0,spe.indexOf(" (SM)"));
                        System.out.println("Système  : " + code.getSystem());
                        System.out.println("Code     : " + code.getCode());
                        System.out.println("Libellé  : " + spe);
                        System.out.println("---");
                    }

                }

            }
        }*/
    }

    public ArrayList<String> retrieveSpecialtiesFromOrganization(OrganizationDTO organizationDTO){
        //List<String> result;
        Set<String> set = new HashSet<String>(); // List → Set (supprime les doublons)


        //List<String> listePraticiens=new ArrayList<String>();
        //String idPraticient="";
        //String idTotal="";
        // Utilisation directe dans la requête suivante
        Bundle praticiensRole = client.search()
                .forResource(PractitionerRole.class)
                .where(PractitionerRole.ORGANIZATION.hasId(organizationDTO.getId()))
                .include(PractitionerRole.INCLUDE_PRACTITIONER)
                .count(10)
                .returnBundle(Bundle.class)
                .execute();

        while (praticiensRole != null) {
            // Extraire les résultats de la page courante
            for (Bundle.BundleEntryComponent entry : praticiensRole.getEntry()) {
                try{
                    PractitionerRole practitionerRole = (PractitionerRole) entry.getResource();
                    //System.out.println("****** Début ******: ");
                    String idpractitionerRole=practitionerRole.getIdPart();
                    //System.out.println("idpractitionerRole: " + idpractitionerRole);
                    String idRole=idpractitionerRole.substring(4);
                    idRole=idRole.substring(0,idRole.indexOf("-"));
                    //System.out.println("idRole: " + idRole);

                    String idPractionner=practitionerRole.getPractitioner().getReferenceElement().getIdPart();
                    //System.out.println("getIdPart  : " + idPraticient);
                    //System.out.println("ID complet  : " + idPraticient+"-"+idRole);
                    String idTotal=idPractionner+"-"+idRole;

                    String specialty=retrieveSpecialtyFromPractictionner(idTotal);
                    if(!specialty.isEmpty()){
                        set.add(retrieveSpecialtyFromPractictionner(idTotal));
                    }

                    //listePraticiens.add(idTotal);
                }
                catch(Exception e){

                }

            }
            // Vérifier s'il existe une page suivante
            if (praticiensRole.getLink(Bundle.LINK_NEXT) != null) {
                praticiensRole = client.loadPage()
                        .next(praticiensRole)
                        .execute();
            } else {
                praticiensRole = null; // fin de la pagination
            }


        }

        //List<String> result = new ArrayList<>(set);
        return new ArrayList<>(set);

    }



    public String retrieveSpecialtyFromPractictionner(String idPractionner ){
        //List<String> result=new ArrayList<>();
        //Set<String> set = new HashSet<String>(); // List → Set (supprime les doublons)

        String result="";


        //for(String idpersonne : listePraticiens) {
            Practitioner practitioner = client.read()
                    .resource(Practitioner.class)
                    .withUrl("https://gateway.api.esante.gouv.fr/fhir/v2/Practitioner/"+idPractionner)
                    .execute();

            for (Practitioner.PractitionerQualificationComponent qualif : practitioner.getQualification()) {

                for (Coding code : qualif.getCode().getCoding()) {
                    if(code.getSystem().equals("https://mos.esante.gouv.fr/NOS/TRE_R38-SpecialiteOrdinale/FHIR/TRE-R38-SpecialiteOrdinale")){
                        String spe=code.getDisplay();
                        result=spe.substring(0,spe.indexOf(" (SM)"));
                        /*System.out.println("Système  : " + code.getSystem());
                        System.out.println("Code     : " + code.getCode());
                        System.out.println("Libellé  : " + spe);
                        System.out.println("---");*/
                        //result=spe;
                    }

                }

            }
        //}

       // List<String> result = new ArrayList<>(set);
        return result;
    }

}
